package kr.ac.kongju.witlab.kket_controller.activity;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kr.ac.kongju.witlab.kket_controller.R;
import kr.ac.kongju.witlab.kket_controller.daemons.ThAutoMode;
import kr.ac.kongju.witlab.kket_controller.adapter.LevelListAdapter;
import kr.ac.kongju.witlab.kket_controller.adapter.ListViewItem;
import kr.ac.kongju.witlab.kket_controller.bluetooth_le.BluetoothLeService;
import kr.ac.kongju.witlab.kket_controller.callback.TimeSequenceChangeCallback;
import kr.ac.kongju.witlab.kket_controller.model.DataRepository;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();
    public static final String EXTRAS_DEVICE_LIST = "DEVICE_LIST";

    private BluetoothDevice mSelectedDevice;
    private BluetoothLeService mBluetoothLeService;
    private boolean manualOrAuto = true;
    private DataRepository dr;
    private ThAutoMode th;
    private TextView tvStatus;
    private ListView listview;
    private Button btnConn;
    private TextView tvName;
    private TextView tvAddress;
    private LevelListAdapter listAdapter1 = null;
    private Button btnAutoMode;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.d(TAG, "onServiceConnected()");
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();

            // Automatically connects to devices upon successful start-up initialization.
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }

            if (mSelectedDevice != null) {
                mBluetoothLeService.initConnection(mSelectedDevice);
                mBluetoothLeService.connect();
                initConnectedDeviceView();
            } else {
                Log.d(TAG, "mSelectedDevice == null");
                return;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService.disconnect();
            mBluetoothLeService.close();
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "broadcast received : " + action);

            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                // TODO: process when device GATT connected.
                updateConnectionState();

                Toast.makeText(MainActivity.this,
                        "BLE 장치가 연결되었습니다.",
                        Toast.LENGTH_SHORT).show();

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                // TODO: process when device GATT disconnected.
                updateConnectionState();

                Toast.makeText(MainActivity.this,
                        "BLE 장치의 연결이 끊어졌습니다.",
                        Toast.LENGTH_SHORT).show();

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // TODO: process when device GATT services discovered.

                // Show all the supported services and characteristics on the user interface.
//                displayGattServices(mBluetoothLeService.getSupportedGattServices());

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                // TODO: process when there is available data of device GATT.

//                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));

            } else if (BluetoothLeService.CUSTOM_BLE_SERVICE_NOT_FOUND.equals(action)) {
                // TODO: process when GATT service not found.
                Toast.makeText(MainActivity.this,
                        "BLE 장치를 인식할 수 없습니다.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate()");

        manualOrAuto = true;
        dr = DataRepository.getInstance();

        final Intent intent = getIntent();
        mSelectedDevice = intent.getParcelableExtra(EXTRAS_DEVICE_LIST);

        // init views
        listview = findViewById(R.id.levelListView1);
//        listview2 = findViewById(R.id.levelListView2);
        tvStatus = findViewById(R.id.status_caption);
        btnAutoMode = findViewById(R.id.btnAutoMode);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initBleConnection();
        initAdapter();
        setListeners();
    }

    private void initBleConnection() {
        // init ble and bind service
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        Log.d(TAG, "bindService called");

        //register receiver
        registerReceiver(mBroadcastReceiver, makeBroadcastReceiverIntentFilter());
        Log.d(TAG, "registerReceiver called");
    }

    private void initConnectedDeviceView() {
        btnConn = findViewById(R.id.btnConnect1);
        tvName = findViewById(R.id.connected_device_name1);
        tvAddress = findViewById(R.id.connected_device_address1);

        tvName.setText(mSelectedDevice.getName());
        tvAddress.setText(mSelectedDevice.getAddress());

        btnConn.setOnClickListener(v -> {
            if (!mBluetoothLeService.isDeviceConnected()) {
                mBluetoothLeService.connect();
            } else {
                mBluetoothLeService.disconnect();
            }

        });
    }

    private void initAdapter() {
        ArrayList<ListViewItem> listViewItems1 = new ArrayList<>();

        for (int i = 0; i < dr.getTotalCount(0); i++) {
            ListViewItem item = new ListViewItem();
            item.setIndex(i);
            item.setPacket(dr.getPacket(i));
            item.setInfo(dr.getInfo(i));
            listViewItems1.add(item);
        }

        listAdapter1 = new LevelListAdapter(this, listViewItems1);
        listview.setAdapter(listAdapter1);
    }

    public TimeSequenceChangeCallback timeCallback = (int pktIndex) -> {
        Log.d(TAG, "timeCallback");
        runOnUiThread(() -> {
            listAdapter1.checkOneItem(pktIndex);
            listview.setSelectionFromTop(pktIndex, 700);
            Log.d("ThAutoMode.run()","auto packet send : " +
                    listAdapter1.getItem(pktIndex).toString());
        });
        sendPacket(dr.getPacket(pktIndex));
    };

    private void setListeners() {
        btnAutoMode.setOnClickListener(v -> {
            Log.d(TAG, "btnAutomode clicked");
            manualOrAuto = !manualOrAuto; // switch

            // listview enable disable toggle
            listview.setEnabled(manualOrAuto);
            listAdapter1.notifyDataSetChanged();

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
            runOnUiThread(() -> {
                listAdapter1.checkOneItem(position); // it has notifyDataSetChanged()
            });
            sendPacket(dr.getPacket(position));
        });
    }

    public void sendPacket(byte[] packet) {
        if(mBluetoothLeService.isDeviceConnected()) {
            mBluetoothLeService.sendPacket(packet);
        } else {
            Log.d(TAG, "device is not connected.");
            Toast.makeText(this, "device is not connected.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void updateConnectionState() {
        Log.d(TAG, "device conn: " + mBluetoothLeService.isDeviceConnected());
        if (!mBluetoothLeService.isDeviceConnected()) {
            btnConn.setBackground(getDrawable(R.drawable.disconnected));
        } else {
            btnConn.setBackground(getDrawable(R.drawable.connected));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        if(th == null) {
            th = new ThAutoMode(timeCallback);
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

        unregisterReceiver(mBroadcastReceiver);
        Log.d(TAG, "unregisterReceiver called");

        unbindService(mServiceConnection);
        Log.d(TAG, "unbindService called");

        Log.d(TAG, "unbindService called, stop thread");
        th.killThread();
        th = null;

        mBluetoothLeService.disconnect();
        mBluetoothLeService.close();
        mBluetoothLeService = null;

        //하위 액티비티도 전부 종료
//        System.exit(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_device_control, menu);
//        if (mBluetoothLeService.isAllConnected()) {
//            menu.findItem(R.id.menu_connect_all).setVisible(false);
//            menu.findItem(R.id.menu_disconnect_all).setVisible(true);
//        } else {
//            menu.findItem(R.id.menu_connect_all).setVisible(true);
//            menu.findItem(R.id.menu_disconnect_all).setVisible(false);
//        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_connect_all:
//                Log.d(TAG, "connectDevice menu clicked");
//                if (!mBluetoothLeService.connectAll()) {
//                    Log.d(TAG,"connect all failed :(");
//                } else {
//                    th.startThread();
//                }
//                return true;
//            case R.id.menu_disconnect_all:
//                Log.d(TAG, "disconnectDevice menu clicked");
//                mBluetoothLeService.disconnectAll();
//                th.pauseThread();
//                return true;
//            case android.R.id.home:
//                finish();
//                return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    private static IntentFilter makeBroadcastReceiverIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.EXTRA_DATA);
        intentFilter.addAction(BluetoothLeService.CUSTOM_BLE_SERVICE_NOT_FOUND);
        intentFilter.addAction(BluetoothLeService.EXTRA_DEVICE_IDX);
        return intentFilter;
    }


    private long backKeyPressedTime = 0L;
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
}
