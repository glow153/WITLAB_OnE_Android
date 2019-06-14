package kr.ac.kongju.witlab.kket_controller.activity;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kr.ac.kongju.witlab.kket_controller.R;
import kr.ac.kongju.witlab.kket_controller.daemons.ThAutoMode;
import kr.ac.kongju.witlab.kket_controller.adapter.LevelListAdapter;
import kr.ac.kongju.witlab.kket_controller.adapter.LevelListItem;
import kr.ac.kongju.witlab.kket_controller.bluetooth_le.BluetoothLeService;
import kr.ac.kongju.witlab.kket_controller.callback.TimeSequenceChangeCallback;
import kr.ac.kongju.witlab.kket_controller.model.DataRepository;
import kr.ac.kongju.witlab.kket_controller.model.ModeSelectorVO;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();
    public static final String EXTRAS_DEVICE_LIST = "DEVICE_LIST";

    private BluetoothDevice mSelectedDevice;
    private BluetoothLeService mBluetoothLeService;
    private boolean manualOrAuto = true;
    private DataRepository dr;
    private ThAutoMode th;

    // views
    private Button btnAutoMode;
    private TextView tvStatus;
    private Button btnConn;
    private TextView tvName;
    private TextView tvAddress;
    private RadioGroup rgModeSelector;
    private LinearLayout manualPacketSender;

    // manual mode
    private TextView tvManualSum;
    private EditText edtM0;
    private EditText edtM1;
    private EditText edtM2;
    private EditText edtM3;
    private Button btnManualSend;



    private ListView listview;
    private LevelListAdapter listAdapter = null;

    private ModeSelectorVO ms;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.d(TAG, "onServiceConnected()");
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();

            // Automatically connects to devices upon successful start-up initialization.
            if (!mBluetoothLeService.initialize()) {
                Toast.makeText(getApplicationContext(),
                        "Unable to initialize Bluetooth.",
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Unable to initialize Bluetooth!!!");
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

        // init mode selector
        ms = ModeSelectorVO.getInstance();

        // get data (connected ble device) from DeviceScanActivity as an object
        final Intent intent = getIntent();
        mSelectedDevice = intent.getParcelableExtra(EXTRAS_DEVICE_LIST);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        bindView();
        initBleConnection();
        initAdapter(ms.getMode());
        setListeners();
    }

    private void bindView() {
        // init views
        tvStatus = findViewById(R.id.status_caption);
        btnAutoMode = findViewById(R.id.btnAutoMode);

        rgModeSelector = findViewById(R.id.rdgroup);
        manualPacketSender = findViewById(R.id.manualmode);
        manualPacketSender.setVisibility(View.GONE);

        tvManualSum = findViewById(R.id.tv_manual_value_sum);
        edtM0 = findViewById(R.id.edtM0);
        edtM1 = findViewById(R.id.edtM1);
        edtM2 = findViewById(R.id.edtM2);
        edtM3 = findViewById(R.id.edtM3);
        btnManualSend = findViewById(R.id.btn_manual_send);

        listview = findViewById(R.id.levelListView);
    }

    private void initBleConnection() {
        // init ble and bind service
        Log.d(TAG, "initBleConnection() : bindService called");
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        //register receiver
        Log.d(TAG, "initBleConnection() : registerReceiver called");
        registerReceiver(mBroadcastReceiver, makeBroadcastReceiverIntentFilter());
    }

    private void initConnectedDeviceView() {
        btnConn = findViewById(R.id.btnConnect);
        tvName = findViewById(R.id.connected_device_name);
        tvAddress = findViewById(R.id.connected_device_address);

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

    private void initAdapter(int mode) {
        // TODO: process when LevelListAdapter be initiated with selected mode
        Log.d(TAG, "initAdapter() start : make LevelListAdapter");
        ArrayList<LevelListItem> levelListItems = new ArrayList<>();

        for (int i = 0; i < dr.getPacketTableCount(mode); i++) {
            LevelListItem item = new LevelListItem();
            item.setIndex(i);
            item.setPacket(dr.getPacket(mode, i));
            item.setTitle(); // set default title
            item.setInfo(); // set default info
            levelListItems.add(item);
        }

        listAdapter = new LevelListAdapter(this, levelListItems);
        listview.setAdapter(listAdapter);
        Log.d(TAG, "initAdapter() end : LevelListAdapter made");
    }

    public TimeSequenceChangeCallback timeCallback = (int pktIndex) -> {
        Log.d(TAG, "timeCallback");
        runOnUiThread(() -> {
            listAdapter.checkOneItem(pktIndex);
            listview.setSelectionFromTop(pktIndex, 700);
            Log.d("ThAutoMode.run()","auto packet send : " +
                    listAdapter.getItem(pktIndex).toString());
        });
        sendPacket(dr.getPacket(ms.getMode(), pktIndex));
    };

    private void setListeners() {
        btnAutoMode.setOnClickListener(v -> {
            // TODO: auto mode process => TO BE CONTINUED...
            Log.d(TAG, "btnAutomode clicked");
            manualOrAuto = !manualOrAuto; // switch

//            // listview enable disable toggle
//            listview.setEnabled(manualOrAuto);
//            listAdapter.notifyDataSetChanged();

            if(manualOrAuto) { // manual mode (true)
                tvStatus.setText(R.string.txt_manualmode);
                btnAutoMode.setText(R.string.manual_to_auto);

                // stop automode thread
//                th.pauseThread();

            } else { // auto mode (false)
                tvStatus.setText(R.string.txt_automode);
                btnAutoMode.setText(R.string.auto_to_manual);

                // start automode thread
//                th.startThread();
            }
        });

        listview.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            Log.d(TAG, "listview clicked : " + position);
            runOnUiThread(() -> {
                listAdapter.checkOneItem(position); // it has notifyDataSetChanged()
            });
            sendPacket(dr.getPacket(ms.getMode(), position));
        });

        rgModeSelector.setOnCheckedChangeListener((RadioGroup group, int checkedId) -> {
            // TODO: process when mode selector (radio button) clicked
            Log.d(TAG, "rgModeSelector id changed: " + checkedId);

            switch(checkedId) {
            case R.id.rdbtn_15:
                ms.setMode(0);
                break;
            case R.id.rdbtn_69:
                ms.setMode(1);
                break;
            case R.id.rdbtn_test:
                ms.setMode(2);
                break;
            case R.id.rdbtn_manual:
                ms.setMode(3);
                break;
            default:
                break;
            }

            if (checkedId == R.id.rdbtn_manual) {
                manualPacketSender.setVisibility(View.VISIBLE);
            } else {
                manualPacketSender.setVisibility(View.GONE);
                initAdapter(ms.getMode());
            }
        });

        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                int sum = calcManualPacketSum();
                tvManualSum.setText(String.format("%d", sum));
                if (sum >= 256) {
                    tvManualSum.setTextColor(Color.RED);
                } else {
                    tvManualSum.setTextColor(Color.BLACK);
                }
            }
        };

        edtM0.addTextChangedListener(tw);
        edtM1.addTextChangedListener(tw);
        edtM2.addTextChangedListener(tw);
        edtM3.addTextChangedListener(tw);

        btnManualSend.setOnClickListener(v -> {
            if (calcManualPacketSum() >= 256) {
                Toast.makeText(MainActivity.this, "채널 값 총합이 256 이상입니다.",
                        Toast.LENGTH_LONG).show();
            } else {
                sendPacket(makeCustomPacket());
            }
        });
    }

    private byte[] makeCustomPacket() {
        int m0 = 0, m1 = 0, m2 = 0, m3 = 0;

        try {
            m0 = Integer.parseInt(edtM0.getText().toString());
        } catch (NumberFormatException | NullPointerException e) {}

        try {
            m1 = Integer.parseInt(edtM1.getText().toString());
        } catch (NumberFormatException | NullPointerException e) {}

        try {
            m2 = Integer.parseInt(edtM2.getText().toString());
        } catch (NumberFormatException | NullPointerException e) {}

        try {
            m3 = Integer.parseInt(edtM3.getText().toString());
        } catch (NumberFormatException | NullPointerException e) {}


        return new byte[] {(byte)0x01, (byte)0x02, (byte)m0, (byte)m1, (byte)m2, (byte)m3};
    }

    private int calcManualPacketSum() {
        int m0 = 0, m1 = 0, m2 = 0, m3 = 0;

        try {
            m0 = Integer.parseInt(edtM0.getText().toString());
        } catch (NumberFormatException | NullPointerException e) {}

        try {
            m1 = Integer.parseInt(edtM1.getText().toString());
        } catch (NumberFormatException | NullPointerException e) {}

        try {
            m2 = Integer.parseInt(edtM2.getText().toString());
        } catch (NumberFormatException | NullPointerException e) {}

        try {
            m3 = Integer.parseInt(edtM3.getText().toString());
        } catch (NumberFormatException | NullPointerException e) {}


        return m0 + m1 + m2 + m3;
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

        Log.d(TAG, "kill thread");
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home: //toolbar의 back키 눌렀을 때 동작
            th.pauseThread();
            th.killThread();
            th = null;
            finish();
            return true;

        case R.id.menu_about:
            Log.d(TAG, "options menu - about touched");
            th.pauseThread();

            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
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
