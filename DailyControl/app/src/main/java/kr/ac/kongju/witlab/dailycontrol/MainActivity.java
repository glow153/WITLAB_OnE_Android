package kr.ac.kongju.witlab.dailycontrol;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.ac.kongju.witlab.dailycontrol.adapter.LevelListAdapter;
import kr.ac.kongju.witlab.dailycontrol.ble.service.BluetoothLeService;
import kr.ac.kongju.witlab.dailycontrol.adapter.ListViewItem;
import kr.ac.kongju.witlab.dailycontrol.listener.OnTimetableChangedListener;
import kr.ac.kongju.witlab.dailycontrol.model.DataRepository;

public class MainActivity extends Activity {
    private final static String TAG = MainActivity.class.getSimpleName();
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private boolean manualOrAuto = true;
    private DataRepository dr;
    private ThAutoMode th;
    private TextView tvStatus;
    private ListView listview;
    private LevelListAdapter listAdapter = null;
    private Button btnAutoMode;

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
    private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
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

    public OnTimetableChangedListener timeListener = (int index) -> {
        runOnUiThread(() -> listAdapter.checkOneItem(index));
        mBluetoothLeService.writeCustomCharacteristic(dr.getPacket(index));
        Log.d("ThAutoMode.run()","auto click listitem : " +
                listAdapter.getItem(index).toString());
    };

    private void initBle() {
        // init ble and bind service
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        Log.d(TAG, "bindService called");

        //register receiver
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        Log.d(TAG, "registerReceiver called");

        //connect device
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    private void initAdapter() {
        ArrayList<ListViewItem> listViewItems = new ArrayList<>();

        for (int i = 0; i < dr.getTotalCount(); i++) {
//            Log.d("initAdapter()", "adding items:" + i);
            ListViewItem item = new ListViewItem();

            item.setIndex(i);
            item.setPacket(dr.getPacket(i));
            item.setInfo(dr.getInfo(i));

            Log.d("initAdapter()", item.toString());

            listViewItems.add(item);
        }

        listAdapter = new LevelListAdapter(this, listViewItems);
        listview.setAdapter(listAdapter);
    }

    private void setListeners() {
        btnAutoMode.setOnClickListener((View v) -> {
            manualOrAuto = !manualOrAuto; // switch

            // listview enable disable toggle
            listview.setEnabled(manualOrAuto);
            listAdapter.notifyDataSetChanged();

            if(manualOrAuto) { // manual mode (true)
                tvStatus.setText(R.string.txt_manualmode);
                btnAutoMode.setText(R.string.manual_to_auto);
                // stop automode thread
                th.pauseThread();

            } else { // auto mode (false)
                tvStatus.setText(R.string.txt_automode);
                btnAutoMode.setText(R.string.auto_to_manual);
                // start automode thread
                th.startThread();
            }
        });

        listview.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            Log.d("listview.setOnClickListener", "listview clicked : " + position);
            runOnUiThread(() -> listAdapter.checkOneItem(position));
            mBluetoothLeService.writeCustomCharacteristic(dr.getPacket(position));
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate()");

        manualOrAuto = true;
        dr = DataRepository.getInstance();

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // init views
        listview = findViewById(R.id.levelListView);
        tvStatus = findViewById(R.id.status_caption);
        btnAutoMode = findViewById(R.id.btnAutoMode);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        initBle();
        initAdapter();
        setListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        if(th == null) {
            th = new ThAutoMode(timeListener);
            th.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");

        // start thread
        if(th != null) {
            if (!manualOrAuto)
                th.startThread();
            else
                th.pauseThread();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");

        unregisterReceiver(mGattUpdateReceiver);
        Log.d(TAG, "unregisterReceiver called");

        unbindService(mServiceConnection);
        Log.d(TAG, "unbindService called");
        mBluetoothLeService = null;

        Log.d(TAG, "unbindService called, stop thread");
        th.killThread();
        th = null;

        //하위 액티비티도 전부 종료
//        System.exit(0);
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
                Log.d(TAG, "connect menu clicked");
                mBluetoothLeService.connect(mDeviceAddress);
                th.startThread();
                return true;
            case R.id.menu_disconnect:
                Log.d(TAG, "disconnect menu clicked");
                mBluetoothLeService.disconnect();
                th.pauseThread();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(() -> {
            if (resourceId == R.string.connected)
                setTitle(getString(resourceId) + " : " + mDeviceName);
            else
                setTitle(getString(resourceId));
        });
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
