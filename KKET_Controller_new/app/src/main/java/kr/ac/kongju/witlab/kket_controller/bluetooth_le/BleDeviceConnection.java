package kr.ac.kongju.witlab.kket_controller.bluetooth_le;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import java.util.UUID;

import kr.ac.kongju.witlab.kket_controller.model.GattRepository;

public class BleDeviceConnection {
    private final static String TAG = BleDeviceConnection.class.getSimpleName();

    private boolean connected = false;

    private Context bleServiceContext;

    private BluetoothDevice device;
    private BluetoothGatt gatt;
    private BluetoothGattCallback gattCallback;

    private UUID service_uuid = null;
    private UUID write_char_uuid = null;
    private UUID read_char_uuid = null;

    public BleDeviceConnection(Context bleServiceContext,
                               BluetoothDevice device,
                               BluetoothGattCallback gattCallback) {
        this.bleServiceContext = bleServiceContext;
        this.device = device;
        this.gattCallback = gattCallback;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect() {
        Log.d(TAG, "connect():01 - gatt connect");
        // Previously connected device.  Try to reconnect.
        if(gatt != null) {
            Log.d(TAG, "connect():04 - gatt not null");
            if (gatt.connect()) {
                connected = true;
                return true;
            } else {
                return false;
            }
        } else {
            Log.d(TAG, "connect():12 - gatt null");
            // new device connection
            if (device == null) {
                Log.w(TAG, "Device not found.  Unable to connectDevice.");
                connected = false;
                return false;
            }
            // We want to directly connectDevice to the device, so we are setting the autoConnect
            // parameter to false.
            gatt = device.connectGatt(bleServiceContext, false, gattCallback);
            Log.d(TAG, "connect():22 - " + ((gatt == null) ? "gatt null" : "gatt not null"));
            Log.d(TAG, "Trying to create a new connection.");
            if (gatt != null) {  // successfully connected
//                gattService = gatt.getService(service_uuid);
                connected = true;

                //set available UUID for connected device
                setCustomUUID();
            } else {
                Log.w(TAG, "Bluetooth not initialized");
                return false;
            }
            return true;
        }
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        Log.d(TAG, "gatt disconnect");
        if (gatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        gatt.disconnect();
//        mConnectionState = STATE_DISCONNECTED;
        connected = false;
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        Log.d(TAG, "gatt close");
        if (gatt == null) {
            return;
        }
        gatt.close();
        gatt = null;
    }

    private void setCustomUUID() {
        if (device.getName().equals("Bluno")) {
            service_uuid = GattRepository.UUID_BLUNO_NANO_SERVICE;
            write_char_uuid = GattRepository.UUID_BLUNO_NANO_WRITE_CHAR;
            read_char_uuid = GattRepository.UUID_BLUNO_NANO_READ_CHAR;
        } else if(device.getName().equals("KKET") ||
                device.getName().equals("Mum") ||
                device.getName().equals("HM-10") ||
                device.getName().equals("BL1")) { // HM-10
            service_uuid = GattRepository.UUID_HM10_SERVICE;
            write_char_uuid = GattRepository.UUID_HM10_WRITE_CHAR;
            read_char_uuid = GattRepository.UUID_HM10_READ_CHAR;
        }
        Log.d(TAG,"device name: " + device.getName() +
                ", service UUID: " + service_uuid +
                ", write UUID: " + write_char_uuid +
                ", read UUID: " + read_char_uuid);
    }

    public void writeCustomCharacteristic(byte[] value) {
        if (gatt == null) {
            Log.d(TAG, "gatt is null");
            return;
        }
        /*check if the service is available on the device*/
        Log.d(TAG, "uuid: " + service_uuid + "," + write_char_uuid + "," + read_char_uuid);
        BluetoothGattService mCustomService = gatt.getService(service_uuid);
        if(mCustomService == null) {
            Log.w(TAG, "Custom BLE Service not found");
            return;
        }

        /*get the write characteristic from the service*/
        BluetoothGattCharacteristic mWriteCharacteristic = mCustomService.getCharacteristic(write_char_uuid);

        if (mWriteCharacteristic == null) {
            Log.w(TAG, "WriteCharacteristic Service not found");
            return;
        }

        mWriteCharacteristic.setValue(value);
        if(!gatt.writeCharacteristic(mWriteCharacteristic)){
            Log.w(TAG, "Failed to write characteristic");
        }
        Log.w(TAG, "send packet : " + packetToStr(value));
    }

    public void readCustomCharacteristic() {
        if (gatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        /*check if the service is available on the device*/
        BluetoothGattService mCustomService = gatt.getService(service_uuid);
        if(mCustomService == null){
            Log.w(TAG, "Custom BLE Service not found");
            return;
        }
        /*get the read characteristic from the service*/
        BluetoothGattCharacteristic mReadCharacteristic = mCustomService.getCharacteristic(UUID.fromString("00000002-0000-1000-8000-00805f9b34fb"));
        if(!gatt.readCharacteristic(mReadCharacteristic)){
            Log.w(TAG, "Failed to read characteristic");
        }
    }

    public static String packetToStr(byte[] packet) {
        final StringBuilder stringBuilder = new StringBuilder(packet.length);
        for(byte byteChar : packet)
            stringBuilder.append(String.format("%02X ", byteChar));
        return stringBuilder.toString();
    }

    public void setConnectionState(boolean b) {
        connected = b;
    }

    public boolean isConnected() {
        return connected;
    }
}
