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

package kr.ac.kongju.witlab.kket_controller;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import android.widget.Toast;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends Activity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;

    //jake's code
    private boolean kke_lamp_status = true;

    private ListView listview;
    private List<String> packetlistitems = null;
    private ArrayAdapter<String> adapter = null;

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
//                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
//                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
//                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    TextView tvCurrent;
    LinearLayout manualmode_input;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_activity);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        final PacketRepository packetlist = new PacketRepository();
        listview = findViewById(R.id.packet_list_view);
        final RadioGroup rg = findViewById(R.id.rdgroup);
//        final Button btnAutoMode = findViewById(R.id.btnAutoMode);

        tvCurrent = findViewById(R.id.current);
        manualmode_input = findViewById(R.id.manualmode);
        manualmode_input.setVisibility(View.GONE);

        //initialize packet list view
        packetlistitems = new ArrayList<>();
        for (int j = 0; j < packetlist.getLength(0); j++)
            packetlistitems.add(packetlist.getPacketTitle(j)
                    + " ( " + packetlist.getPacketString(0, j) + ")");
        adapter = new ArrayAdapter<>(DeviceControlActivity.this,
                android.R.layout.simple_list_item_1, packetlistitems);
        listview.setAdapter(adapter);

        rg.setOnCheckedChangeListener((RadioGroup radioGroup, int i) -> {
            int type = 0;
            if (i == R.id.rdbtn_15) {
                if (manualmode_input.getVisibility() == View.VISIBLE)
                    manualmode_input.setVisibility(View.GONE);
                type = 0;
            } else if (i == R.id.rdbtn_69) {
                if (manualmode_input.getVisibility() == View.VISIBLE)
                    manualmode_input.setVisibility(View.GONE);
                type = 1;
            } else if (i == R.id.rdbtn_test) {
                if (manualmode_input.getVisibility() == View.VISIBLE)
                    manualmode_input.setVisibility(View.GONE);
                type = 2;
            } else if (i == R.id.rdbtn_manual) {
                manualmode_input.setVisibility(View.VISIBLE);
                return;
            }

            Log.d("onCheckedChanged()", "radio btn changed : " + type);

            //데이터를 저장하게 되는 리스트
            packetlistitems = null;
            adapter = null;
            packetlistitems = new ArrayList<>();

            for (int j = 0; j < packetlist.getLength(type); j++)
                packetlistitems.add(packetlist.getPacketTitle(j)
                        + " (" + packetlist.getPacketString(type, j) + ")");

            //리스트뷰와 리스트를 연결하기 위해 사용되는 어댑터
            adapter = new ArrayAdapter<>(DeviceControlActivity.this,
                    android.R.layout.simple_list_item_1, packetlistitems);

            //리스트뷰의 어댑터를 지정해준다.
            listview.setAdapter(adapter);
        });

        //리스트뷰의 아이템을 클릭시 해당 아이템의 문자열을 가져오기 위한 처리
        listview.setOnItemClickListener((AdapterView<?> adapterView, View view, int position, long id) -> {
            int i = rg.getCheckedRadioButtonId();
            int type = -1;
            if (i == R.id.rdbtn_15)
                type = 0;
            else if (i == R.id.rdbtn_69)
                type = 1;
            else if (i == R.id.rdbtn_test)
                type = 2;

            Log.d("onItemClick()", "checked radio btn : " + type);

            //클릭한 아이템의 문자열을 가져옴
            String selected_item = (String) adapterView.getItemAtPosition(position);
            Toast.makeText(DeviceControlActivity.this, selected_item, Toast.LENGTH_SHORT).show();
            mBluetoothLeService.writeCustomCharacteristic(packetlist.getPacket(type, position));
            tvCurrent.setText(selected_item);
        });

        initManualModeViews();

        getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private void initManualModeViews() {
        // manual mode views
        final EditText[] edtManualValues = {
                findViewById(R.id.edtM0),
                findViewById(R.id.edtM1),
                findViewById(R.id.edtM2),
                findViewById(R.id.edtM3),
        };
        final Button btnManualSend = findViewById(R.id.btn_manual_send);

        // send packet on manual mode
        btnManualSend.setOnClickListener((View v) -> {
            byte[] packet = new byte[6];
            packet[0] = (byte) 0x01;
            packet[1] = (byte) 0x02;
            for (int i = 0; i < 4; i++)
                packet[2 + i] = (byte) Integer.parseInt(edtManualValues[i].getText().toString());

            String packet_name = "custom packet( " + PacketRepository.customPacketToStr(packet) + ")";
            Toast.makeText(DeviceControlActivity.this, packet_name, Toast.LENGTH_SHORT).show();
            mBluetoothLeService.writeCustomCharacteristic(packet);
            tvCurrent.setText(packet_name);
        });

        for (int i = 0; i < 4; i++) {
            edtManualValues[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    int sum = 0;
                    for (int i = 0; i < 4; i++) {
                        try {
                            sum += Integer.parseInt(edtManualValues[i].getText().toString());
                        } catch (NumberFormatException nfe) {
                            nfe.printStackTrace();
                            sum += 0;
                        }
                    }
                    setManualSumValue(sum);
                }
            });
        }
    }

    private void setManualSumValue(int sum) {
        final TextView tvSumValue = findViewById(R.id.tv_manual_value_sum);
        tvSumValue.setText(Integer.toString(sum));
        if (sum > 255) {
            tvSumValue.setTextColor(Color.RED);
//            if (tvSumValue.getTypeface().getStyle() == Typeface.NORMAL)
            tvSumValue.setTypeface(null, Typeface.BOLD);
        } else {
            tvSumValue.setTextColor(Color.BLACK);
//            if (tvSumValue.getTypeface().isBold())
            tvSumValue.setTypeface(null, Typeface.NORMAL);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
        System.exit(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                //mConnectionState.setText(resourceId);
//            }
//        });
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
