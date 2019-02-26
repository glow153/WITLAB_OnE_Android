/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.ac.kongju.witlab.dailycontrol_multi.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kr.ac.kongju.witlab.dailycontrol_multi.R;
import kr.ac.kongju.witlab.dailycontrol_multi.adapter.DeviceListAdapter;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends AppCompatActivity {
    public final static String TAG = DeviceScanActivity.class.getSimpleName();
    public static final int PERMISSION_REQUEST_COARSE_LOCATION = 7;
    public static final int REQUEST_ENABLE_BT = 1;
    private long backKeyPressedTime = 0L;
    private boolean mScanning;
    private Handler mHandler;
    private BluetoothAdapter mBluetoothAdapter;
    private DeviceListAdapter deviceListAdapter;
    private ListView deviceListView;

    // new api instead startLeScan(), stopLeScan() : MUST USE THIS!!!
    private BluetoothLeScanner leScanner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan);
        setTitle(R.string.title_devices);
        mHandler = new Handler();

        requestPermissions(new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);

        deviceListView = findViewById(R.id.device_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initBluetoothAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // Initializes list view adapter.
        deviceListAdapter = new DeviceListAdapter(this);
        deviceListView.setAdapter(deviceListAdapter);

        deviceListView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            // TODO:
            deviceListAdapter.switchChecked(position);
        });

        // start ble scan
        startScanBle();
    }

    private void startScanBle() {
        leScanner.startScan(mScanCallback);
        mHandler.postDelayed(() -> stopScanBle(), 10000);
        invalidateOptionsMenu();
        mScanning = true;
    }

    private void stopScanBle() {
        leScanner.stopScan(mScanCallback);
        invalidateOptionsMenu();
        mScanning = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScanBle();
        deviceListAdapter.clear();
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this,
                    "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.",
                    Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }

    private void initBluetoothAdapter() {
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported,
                            Toast.LENGTH_SHORT).show();
            finish();
        }

        // get BluetoothLeScanner
        leScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (leScanner == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported,
                            Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_device_scan, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "option menu clicked : " + item.getItemId());
        switch (item.getItemId()) {
            case R.id.menu_scan:
                deviceListAdapter.clear();
                startScanBle();
                break;
            case R.id.menu_stop:
                stopScanBle();
                break;
            case R.id.menu_goto_control:
                if (deviceListAdapter.getCheckedDevices().size() < 2) {
                    Toast.makeText(this, "2개 이상 선택해야합니다.", Toast.LENGTH_SHORT).show();
                    return false;
                }

                final ArrayList<BluetoothDevice> bleDevices = deviceListAdapter.getCheckedDevices();
                if (bleDevices == null)
                    return false;

                if (bleDevices.get(0).getAddress().equals("00:15:83:00:BC:EF") && // 2번조명
                        bleDevices.get(1).getAddress().equals("A8:1B:6A:9E:EC:0D")) { // 1번조명
                    BluetoothDevice d1 = bleDevices.get(1);
                    BluetoothDevice d2 = bleDevices.get(0);
                    // 바꿔서 보내기 (제어지표 달라서 순서바뀌면 큰일남)
                    bleDevices.clear();
                    bleDevices.add(d1);
                    bleDevices.add(d2);
                } else if (bleDevices.get(0).getAddress().equals("D4:36:39:C4:66:BA") && // HM-10
                    bleDevices.get(1).getAddress().equals("80:30:DC:E9:08:91")) { // BLUNO
                    BluetoothDevice d1 = bleDevices.get(1);
                    BluetoothDevice d2 = bleDevices.get(0);
                    // 바꿔서 보내기 테스트
                    bleDevices.clear();
                    bleDevices.add(d1);
                    bleDevices.add(d2);
                }

                final Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(MainActivity.EXTRAS_DEVICE_LIST, bleDevices);
                stopScanBle();
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            processResult(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            results.forEach((ScanResult result) -> processResult(result));
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }

        private void processResult(final ScanResult result) {
            runOnUiThread(() -> {
                deviceListAdapter.addDevice(result.getDevice());
                deviceListAdapter.notifyDataSetChanged();
            });
        }
    };
}