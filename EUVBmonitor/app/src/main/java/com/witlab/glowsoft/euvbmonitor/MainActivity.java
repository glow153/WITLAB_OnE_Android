package com.witlab.glowsoft.euvbmonitor;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.witlab.glowsoft.euvbmonitor.listener.RefreshEUVBChartListener;
import com.witlab.glowsoft.euvbmonitor.listener.RefreshCurrentInfoViewListener;
import com.witlab.glowsoft.euvbmonitor.listener.RefreshVitDChartListener;
import com.witlab.glowsoft.euvbmonitor.listener.RefreshVitdInfoViewListener;
import com.witlab.glowsoft.euvbmonitor.model.SampleGattAttributes;
import com.witlab.glowsoft.euvbmonitor.model.UserStatus;
import com.witlab.glowsoft.euvbmonitor.model.ValueObject;
import com.witlab.glowsoft.euvbmonitor.service.BluetoothLeService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRAS_DEVICE_NAME = "ARG_DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "ARG_DEVICE_ADDR";
    private final static String TAG = MainActivity.class.getSimpleName();

    private long backKeyPressedTime = 0L;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager = null;

    /**
     * Jake's sth
     */
    private ValueObject vo = ValueObject.getInstance();
    private UserStatus us = UserStatus.getInstance();
    private PacketManager pm = PacketManager.getInstance();
    private RefreshCurrentInfoViewListener refreshCurrentInfoListener;
    private RefreshEUVBChartListener refreshEUVBChartListener;
    private RefreshVitdInfoViewListener refreshVitdInfoViewListener;
    private RefreshVitDChartListener refreshVitDChartListener;

    /**
     *  Jake's Bluetooth LE code
     */
    public static final int REQUEST_BLE_DEVICE = 10;
//    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothDevice device = null;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic characteristicTX;
    private BluetoothGattCharacteristic characteristicRX;

    public final static UUID HM_RX_TX =
            UUID.fromString(SampleGattAttributes.HM_RX_TX);
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private boolean canSendPacket = false;
    private long lastPacketReceivedMillis = System.currentTimeMillis();

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
            mBluetoothLeService.setMaster(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    /**
     *  Jake's Broadcast Receiver
     */
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
                displayGattServices(mBluetoothLeService.getSupportedGattServices());

                // TODO: initial process, it's necessary!
                //enable receiving data from device
                mBluetoothLeService.setCharacteristicNotification(characteristicRX,true);
                canSendPacket = true;

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                // TODO: Data received from device
                byte[] receivedPacket = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                float uvi = pm.parseUvi(receivedPacket);
                float euvb = pm.parseEuvb(receivedPacket);
                //calculate vitd
                float durationSec = (System.currentTimeMillis() - lastPacketReceivedMillis) / 1000.0f;
                float vitd = 4000 * (euvb * durationSec) / (us.getMEDF() * 100) *
                        (us.getExposeAreaUpper() + us.getExposeAreaLower()) * us.getAgeFactor();

                vo.setUvi(uvi);
                vo.setEuvb(euvb);

                String hms = new SimpleDateFormat("hh:mm:ss", Locale.KOREA).format(new Date());

                Log.d("mGattUpdateReceiver","uvi=" + uvi + ", euvb=" + euvb + ", vitd=" + vitd);
                if(refreshCurrentInfoListener != null)
                    refreshCurrentInfoListener.onRefreshCurrentInfoView();

                if(refreshVitdInfoViewListener != null)
                    refreshVitdInfoViewListener.onRefreshVitdInfo(vitd);

//                if(refreshEUVBChartListener != null)
//                    refreshEUVBChartListener.onRefreshChart(hms, euvb);
//                if(refreshVitDChartListener != null)
//                    refreshVitDChartListener.onRefreshChart(hms, vitd);

                //send time sync packet
                if(canSendPacket)
                    sendPacket(pm.getPktTimesync());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final Intent intentParams = getIntent();
//        mDeviceName = intentParams.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intentParams.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        initBle();
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress); //connect device
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
        switch(item.getItemId()) {
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

    @Override
    protected void onResume() {
        super.onResume();
//        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
//        if (mBluetoothLeService != null) {
//            final boolean result = mBluetoothLeService.connect(mDeviceAddress); //connect device
//            Log.d(TAG, "Connect request result=" + result);
//        }
    }

    @Override
    public void onBackPressed() {
        Toast toast = new Toast(this);
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this,
                                    "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.",
                                    Toast.LENGTH_SHORT);
            toast.show();
        } else {
            finish();
            toast.cancel();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MainActivity", "onPause()");
//        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        Log.d("MainActivity", "onDestroy()");
        unregisterReceiver(mGattUpdateReceiver);
        unbindService(mServiceConnection);
        super.onDestroy();
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                invalidateOptionsMenu();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_BLE_DEVICE:

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();


        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));

            // If the service exists for HM 10 Serial, say so.
//            if(SampleGattAttributes.lookup(uuid, unknownServiceString).equals("HM 10 Serial")) {
//                isSerial.setText("Yes, serial :-)");
//            } else {
//                isSerial.setText("No, serial :-(");
//            }
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            // get characteristic when UUID matches RX/TX UUID
            characteristicTX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
            characteristicRX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public void sendPacket(final byte[] packet) {
        Log.d(TAG, "send packet : " + PacketManager.byteArrayToHex(packet));
        if(mConnected) {
            characteristicTX.setValue(packet);
            mBluetoothLeService.writeCharacteristic(characteristicTX);

            //enable to receive packet
//            mBluetoothLeService.setCharacteristicNotification(characteristicRX,true);
        }
    }

    public void setRefreshInfoViewListener(RefreshCurrentInfoViewListener listener) {
        this.refreshCurrentInfoListener = listener;
    }

    public void setRefreshEUVBChartListener(RefreshEUVBChartListener listener) {
        this.refreshEUVBChartListener = listener;
    }

    public void setRefreshVitdInfoViewListener(RefreshVitdInfoViewListener listener) {
        this.refreshVitdInfoViewListener = listener;
    }

    public void setRefreshVitDChartListener(RefreshVitDChartListener listener) {
        this.refreshVitDChartListener = listener;
    }
}
