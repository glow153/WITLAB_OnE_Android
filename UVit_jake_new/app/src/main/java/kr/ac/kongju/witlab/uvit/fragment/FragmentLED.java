package kr.ac.kongju.witlab.uvit.fragment;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;
import java.util.UUID;

import kr.ac.kongju.witlab.uvit.R;
import kr.ac.kongju.witlab.uvit.model.LEDControllerModel;
import kr.ac.kongju.witlab.uvit.service.GeneralBluetoothControllerJake;


/**
 * Plan A. BluetoothManagerJake를 블루투스 관련 통합 클래스로 merge
 * Plan B. LED fragment에 한시적으로 BLEScanner 등을 구현 (currently applied)
 */

public class FragmentLED extends Fragment {
    private static final String TAG = FragmentLED.class.getSimpleName();
    private View view = null;

    private SeekBar[] sbar;
    private int[] seekbar_id = {R.id.control_bar_ch01, R.id.control_bar_ch02,
                                R.id.control_bar_ch03, R.id.control_bar_ch04};
    private TextView[] tvChannelValue;
    private int[] tv_id = {R.id.control_tv_ch01, R.id.control_tv_ch02,
                           R.id.control_tv_ch03, R.id.control_tv_ch04};

    private LEDControllerModel ledCtrl;

    /***************** BLE code start *****************/
    private GeneralBluetoothControllerJake gbtCtrl = GeneralBluetoothControllerJake.getInstance();
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    private final String BLE_BLUNO_ADDRESS = "80:30:DC:E9:08:91";
    private final String BLE_BLUNO_UUID_SERVICE = "0000DFB0-0000-1000-8000-00805F9B34FB";
    private final String BLE_BLUNO_UUID_WRITE = "0000DFB1-0000-1000-8000-00805F9B34FB";
    private final String BLE_BLUNO_UUID_READ = "0000DFB2-0000-1000-8000-00805F9B34FB";

    private final String BLE_ENTEC_FLATLIGHT_ADDRESS = "D4:F5:13:15:42:87";
    private final String BLE_ENTEC_FLATLIGHT_UUID_SERVICE = "0000FF00-0000-1000-8000-00805F9B34FB";
    private final String BLE_ENTEC_FLATLIGHT_UUID_WRITE = "0000FF01-0000-1000-8000-00805F9B34FB";
    private final String BLE_ENTEC_FLATLIGHT_UUID_READ = "0000FF02-0000-1000-8000-00805F9B34FB";

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
//                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
//                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
//                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
//                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
//            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    private boolean connect() {
        if (gbtCtrl.getBluetoothAdapter()== null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        final BluetoothDevice device = gbtCtrl.getBluetoothAdapter().getRemoteDevice(BLE_ENTEC_FLATLIGHT_ADDRESS);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(getActivity(), false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");

        return true;
    }

    public void disconnect() {
        if (gbtCtrl.getBluetoothAdapter() == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
        Log.w(TAG, "Bluetooth LE device disconnected.");
    }

    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public void sendPacket(byte[] value) {
        if (gbtCtrl.getBluetoothAdapter() == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        BluetoothGattService mCustomService = mBluetoothGatt.getService(
                UUID.fromString(BLE_ENTEC_FLATLIGHT_UUID_SERVICE));
        if(mCustomService == null){
            Log.w(TAG, "Custom BLE Service not found");
            return;
        }
        BluetoothGattCharacteristic mWriteCharacteristic = mCustomService.getCharacteristic(
                UUID.fromString(BLE_ENTEC_FLATLIGHT_UUID_WRITE));
        mWriteCharacteristic.setValue(value);
        if(!mBluetoothGatt.writeCharacteristic(mWriteCharacteristic)){
            Log.w(TAG, "Failed to write characteristic");
        }
    }
    /***************** BLE code end *****************/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sbar = new SeekBar[4];
        tvChannelValue = new TextView[4];

        ledCtrl = LEDControllerModel.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_led, container, false);
        }

        bindView();
        setListeners();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        close();
    }

    private void bindView() {
        for (int i = 0; i < sbar.length; i++)
            sbar[i] = view.findViewById(seekbar_id[i]);

        for (int i = 0; i < tvChannelValue.length; i++)
            tvChannelValue[i] = view.findViewById(tv_id[i]);
    }

    private void setListeners() {
        for (int i = 0; i < sbar.length - 1; i++) {
            final int idx = i;
            sbar[idx].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    int value = sbar[idx].getProgress();
                    tvChannelValue[idx].setText(String.format(Locale.KOREA, "%d", value));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    ledCtrl.setValue(idx, sbar[idx].getProgress());
                    // send packet
                    byte[] packet = {(byte)0x02, (byte)0x11, (byte)0xFF,
                            (byte) ledCtrl.getRawValue(0),
                            (byte) ledCtrl.getRawValue(1),
                            (byte) ledCtrl.getRawValue(2),
                            (byte) ledCtrl.getRawValue(3),
                            (byte)0x03};
                    sendPacket(packet);
                }
            });
        }

        sbar[3].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ledCtrl.setUvOnOff(!ledCtrl.isUVOn());

                if(ledCtrl.isUVOn()) {
                    ledCtrl.setValue(3, LEDControllerModel.UV_CHANNEL_LIMIT);
                    Log.d(TAG, ledCtrl.isUVOn() + " " + ledCtrl.getRawValue(3));
                } else {
                    ledCtrl.setValue(3, 0);
                    Log.d(TAG, ledCtrl.isUVOn() + " " + ledCtrl.getRawValue(3));
                }

                tvChannelValue[3].setText((ledCtrl.isUVOn()) ? "ON" : "OFF");
                // send packet
                byte[] packet = {(byte)0x02, (byte)0x11, (byte)0xFF,
                        (byte) ledCtrl.getRawValue(0),
                        (byte) ledCtrl.getRawValue(1),
                        (byte) ledCtrl.getRawValue(2),
                        (byte) ledCtrl.getRawValue(3),
                        (byte)0x03};
                sendPacket(packet);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }


}
