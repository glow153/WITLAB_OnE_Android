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

package kr.ac.kongju.witlab.dailycontrol_multi.bluetooth_le;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 *
 *
 * 인텍 UUID = FF00, Write = FF01 , Read = FF02
 */

public class BluetoothLeService extends Service {
    public final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
//    private LinkedList<BleDeviceConnection> deviceConnPool;
    ArrayList<BluetoothDevice> deviceList;
    private BleDeviceConnection bleDeviceConn1, bleDeviceConn2;

    private boolean allConnected = false;

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
    public final static String CUSTOM_BLE_SERVICE_NOT_FOUND = "Custom_ble_service_not_found";
    public final static String EXTRA_DEVICE_IDX = "device_index";

    public class BluetoothGattCallbackImpl extends BluetoothGattCallback {
        public int deviceIndex = -1;
        public BluetoothGattCallbackImpl(int index) {
            deviceIndex = index;
        }
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = BluetoothLeService.ACTION_GATT_CONNECTED;

                if(deviceIndex == 0){
                    bleDeviceConn1.setConnectionState(true);
                }else if(deviceIndex == 1){
                    bleDeviceConn2.setConnectionState(true);
                }

                broadcastUpdate(intentAction, deviceIndex);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                // MUST RUN!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                Log.i(TAG, "Attempting to start service discovery:" + gatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = BluetoothLeService.ACTION_GATT_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");

                if(deviceIndex == 0){
                    bleDeviceConn1.setConnectionState(false);
                }else if(deviceIndex == 1){
                    bleDeviceConn2.setConnectionState(false);
                }

                broadcastUpdate(intentAction, deviceIndex);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED, deviceIndex);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(BluetoothLeService.ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(BluetoothLeService.ACTION_DATA_AVAILABLE, characteristic);
        }
    }


    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        Log.d(TAG, "init bleservice for the firsttime!");
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    public void initConnection(ArrayList<BluetoothDevice> deviceList) {
        if(this.deviceList == null)
            this.deviceList = deviceList;
        bleDeviceConn1 = new BleDeviceConnection(
                this, deviceList.get(0), new BluetoothGattCallbackImpl(0));
        bleDeviceConn2 = new BleDeviceConnection(
                this, deviceList.get(1), new BluetoothGattCallbackImpl(1));
    }

    public boolean connectAll() {
        boolean b1 = bleDeviceConn1.connect();
        boolean b2 = bleDeviceConn2.connect();
        allConnected = (b1 && b2);
        return allConnected;
    }

    public void disconnectAll() {
        bleDeviceConn1.disconnect();
        bleDeviceConn2.disconnect();
    }

    public void closeAll() {
        bleDeviceConn1.close();
        bleDeviceConn2.close();
    }

    public boolean isDeviceConnected(int index) {
        if (index == 0) {
            return bleDeviceConn1.isConnected();
        } else if (index == 1) {
            return bleDeviceConn2.isConnected();
        }
        return false;
    }

    public boolean isAllConnected() {
        return allConnected;
    }

    public boolean connectDevice(int index) {
        if (index == 0) {
            return bleDeviceConn1.connect();
        } else if (index == 1) {
            return bleDeviceConn2.connect();
        }
        return false;
    }

    public void disconnectDevice(int index) {
        if (index == 0) {
            bleDeviceConn1.disconnect();
        } else if (index == 1) {
            bleDeviceConn2.disconnect();
        }
    }

    public void sendPacket(int deviceIndex, byte[] packet) {
        if (deviceIndex == 0) {
            Log.d(TAG, "send packet to device0");
            bleDeviceConn1.writeCustomCharacteristic(packet);
        } else if (deviceIndex == 1) {
            Log.d(TAG, "send packet to device1");
            bleDeviceConn2.writeCustomCharacteristic(packet);
        }
    }

    public void sendPacketToAll(byte[] packet) {
        bleDeviceConn1.writeCustomCharacteristic(packet);
        bleDeviceConn2.writeCustomCharacteristic(packet);
    }




    /************************ Jake: Service Binder *************************/
    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        disconnectAll();
        return super.onUnbind(intent);
    }


    /************************ Jake: broadcast sender *************************
     *  bluetoothLeService.sendBroadcast() -> MainActivity.BroadcastReceiver
     */
    public void broadcastUpdate(String action, int deviceIndex) {
        final Intent intent = new Intent(action);
        intent.putExtra("deviceIndex", deviceIndex);
        sendBroadcast(intent);
    }

    public void broadcastUpdate(String action, BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            intent.putExtra(BluetoothLeService.EXTRA_DATA,
                    new String(data) + "\n" + BleDeviceConnection.packetToStr(data));
        }
        sendBroadcast(intent);
    }
}
