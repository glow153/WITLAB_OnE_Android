
/*
 * Copyright (c) 2015, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.nordicsemi.nrfUARTv2;


import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


import com.nordicsemi.nrfUARTv2.database.DatabaseManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements RadioGroup.OnCheckedChangeListener {
    private static final int REQUEST_SELECT_DEVICE_1 = 1;
    private static final int REQUEST_SELECT_DEVICE_2 = 2;
    private static final int REQUEST_ENABLE_BT_1 = 3;
    private static final int REQUEST_ENABLE_BT_2 = 4;
    private static final int UART_PROFILE_READY = 10;
    public static final String TAG = "nRFUART";
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private static final int STATE_OFF = 10;

    TextView mRemoteRssiVal;
    RadioGroup mRg;
    private int mState_1 = UART_PROFILE_DISCONNECTED;
    private int mState_2 = UART_PROFILE_DISCONNECTED;

    private UartService mService_1 = null;
    private UartService2 mService_2 = null;

    private BluetoothDevice mDevice_1 = null;
    private BluetoothDevice mDevice_2 = null;
    private BluetoothAdapter mBtAdapter_1 = null;
    private BluetoothAdapter mBtAdapter_2 = null;
    private ListView messageListView_1;
    private ListView messageListView_2;
    private ArrayAdapter<String> listAdapter_1;
    private ArrayAdapter<String> listAdapter_2;
    private Button btnConnectDisconnect_1;
    private Button btnConnectDisconnect_2;
    private Button btnSend_1;
    private Button btnSend_2;
    private EditText edtMessage_1;
    private EditText edtMessage_2;






    /*****************    edited by DaeHwan Park : 2019-02-20    *****************/
    private DatabaseManager dbmgr;

    // calibration properties
    private final double uviSunCalibConstant = 1.817973;  // based on nl measurement result at 2019-02-21
    private final double uviUvbGeneralLightingCalibConstant = 1.595692;  // based on uvb general lighting measurement result at 2019-02-14
    private final double euvbRatio = 0.76;  // based on nldc
    private final double lightingEuvbIrrd = 0.001921;
    // user profile data
    private double medf = 2.0;
    private double exposure = 0.15;
    private double recommendedVitdAmount = 400;
    // calculated data
    private double euvbAmount = 0.0;
    private double vitdAmount = 0.0;

    private void calcEuvbVitd(boolean isOutdoor, double measured_uvi) {  // 누적 EUVB양과 비타민 D 합성량 계산
        double calibration_constant = (isOutdoor) ? 1 : uviUvbGeneralLightingCalibConstant;
        double euvb = ((measured_uvi * calibration_constant) / 40) * euvbRatio;
        euvbAmount += euvb;
        vitdAmount = ((40 * exposure) / medf) * euvbAmount;
    }

    private int calcLightingTime() {  // 권장 조명 노출시간 산출하여 리턴
        double deficient = recommendedVitdAmount - vitdAmount;
        return (int) ((deficient * medf) / (40 * exposure * lightingEuvbIrrd));
    }
    /*****************    edited by DaeHwan Park : 2019-02-20    *****************/







    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        dbmgr = new DatabaseManager(this);
        dbmgr.open();
        dbmgr.create();

        mBtAdapter_1 = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter_1 == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        mBtAdapter_2 = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter_2 == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        messageListView_1 = (ListView) findViewById(R.id.listMessage_1);
        messageListView_2 = (ListView) findViewById(R.id.listMessage_2);

        listAdapter_1 = new ArrayAdapter<String>(this, R.layout.message_detail);
        listAdapter_2 = new ArrayAdapter<String>(this, R.layout.message_detail);
        messageListView_1.setAdapter(listAdapter_1);
        messageListView_2.setAdapter(listAdapter_2);
        messageListView_1.setDivider(null);
        messageListView_2.setDivider(null);
        btnConnectDisconnect_1 = (Button) findViewById(R.id.btn_select_1);
        btnConnectDisconnect_2 = (Button) findViewById(R.id.btn_select_2);
        btnSend_1 = (Button) findViewById(R.id.sendButton_1);
        btnSend_2 = (Button) findViewById(R.id.sendButton_2);
        edtMessage_1 = (EditText) findViewById(R.id.sendText_1);
        edtMessage_2 = (EditText) findViewById(R.id.sendText_2);

        UVBAND_service_init();
        UVLIGHT_service_init();


        // Handle Disconnect & Connect button
        btnConnectDisconnect_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBtAdapter_1.isEnabled()) {
                    Log.i(TAG, "onClick - BT not enabled yet");
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT_1);
                } else {
                    if (btnConnectDisconnect_1.getText().equals("UVBAND Connect")) {

                        //Connect button pressed, open DeviceListActivity class, with popup windows that scan for devices

                        Intent newIntent = new Intent(MainActivity.this, DeviceListActivity.class);
                        startActivityForResult(newIntent, REQUEST_SELECT_DEVICE_1);
                    } else {
                        //Disconnect button pressed
                        if (mDevice_1 != null) {
                            mService_1.disconnect();

                        }
                    }
                }
            }
        });
        // Handle Disconnect & Connect button
        btnConnectDisconnect_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBtAdapter_2.isEnabled()) {
                    Log.i(TAG, "onClick - BT not enabled yet");
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT_2);
                } else {
                    if (btnConnectDisconnect_2.getText().equals("UVLIGHT Connect")) {

                        //Connect button pressed, open DeviceListActivity class, with popup windows that scan for devices

                        Intent newIntent = new Intent(MainActivity.this, DeviceListActivity.class);
                        startActivityForResult(newIntent, REQUEST_SELECT_DEVICE_2);
                    } else {
                        //Disconnect button pressed
                        if (mDevice_2 != null) {
                            mService_2.disconnect();

                        }
                    }
                }
            }
        });

        btnSend_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.sendText_1);
                String message = editText.getText().toString();
                byte[] value;
                try {
                    //send data to service
                    value = message.getBytes("UTF-8");
                    mService_1.writeRXCharacteristic(value);
                    //Update the log with time stamp
                    String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                    listAdapter_1.add("[" + currentDateTimeString + "] TX: " + message);
                    messageListView_1.smoothScrollToPosition(listAdapter_1.getCount() - 1);
                    edtMessage_1.setText("");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        // Handle Send button
        btnSend_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.sendText_2);
                String message = editText.getText().toString();
                byte[] value;
                try {
                    //send data to service
                    value = message.getBytes("UTF-8");
                    mService_2.writeRXCharacteristic(value);
                    //Update the log with time stamp
                    String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                    listAdapter_2.add("[" + currentDateTimeString + "] TX: " + message);
                    messageListView_2.smoothScrollToPosition(listAdapter_2.getCount() - 1);
                    edtMessage_2.setText("");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });
        // Set initial UI state
    }

    //UART service connected/disconnected
    private ServiceConnection mServiceConnection_1 = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService_1 = ((UartService.LocalBinder) rawBinder).getService();
            Log.d(TAG, "onServiceConnected mService= " + mService_1);
            if (!mService_1.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
        }
        public void onServiceDisconnected(ComponentName classname) {
            ////     mService.disconnect(mDevice);
            mService_1 = null;
        }
    };

    //UART service connected/disconnected
    private ServiceConnection mServiceConnection_2 = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService_2 = ((UartService2.LocalBinder) rawBinder).getService();
            Log.d(TAG, "onServiceConnected mService= " + mService_2);
            if (!mService_2.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
        }
        public void onServiceDisconnected(ComponentName classname) {
            ////     mService.disconnect(mDevice);
            mService_2 = null;
        }
    };

    private final BroadcastReceiver Device_1_UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            final Intent mIntent = intent;
            //*********************//
            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {

                        boolean _Null_check = TextUtils.isEmpty(mDevice_1.getName());
                        if (!_Null_check) //Null 체크
                        {
                            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                            Log.d(TAG, "UART_CONNECT_MSG");
                            btnConnectDisconnect_1.setText("UVBAND Disconnect");
                            edtMessage_1.setEnabled(true);
                            btnSend_1.setEnabled(true);
                            ((TextView) findViewById(R.id.deviceName_1)).setText(mDevice_1.getName() + " - ready");
                            listAdapter_1.add("[" + currentDateTimeString + "] Connected to: " + mDevice_1.getName());
                            messageListView_1.smoothScrollToPosition(listAdapter_1.getCount() - 1);
                            mState_1 = UART_PROFILE_CONNECTED;
                        }

                    }
                });
            }

            //*********************//
            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.d(TAG, "UART_DISCONNECT_MSG");
                        btnConnectDisconnect_1.setText("UVBAND Connect");
                        edtMessage_1.setEnabled(false);
                        btnSend_1.setEnabled(false);
                        ((TextView) findViewById(R.id.deviceName_1)).setText("Not Connected");
                        listAdapter_1.add("[" + currentDateTimeString + "] Disconnected to: " + mDevice_1.getName());
                        mState_1 = UART_PROFILE_DISCONNECTED;
                        mService_1.close();
                        //setUiState();
                    }
                });
            }

            //*********************//
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mService_1.enableTXNotification();
            }






            /*****************    edited by DaeHwan Park : 2019-02-20    *****************/
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {
                final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
                runOnUiThread(() -> {
                    try {
                        String text = new String(txValue, "UTF-8");  // format: "UV:x.xx"
                        String currentDateTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                        // discard trash value
                        text = text.split("\n")[0];
                        Log.d(TAG, "text= " + text);
                        if (text.split(":")[0].equals("S_UV")) {
                            Log.d(TAG, "S_UV has been discarded");
                            return;
                        }

                        double measured_uvi = Float.parseFloat(text.split(":")[1]);

                        //TODO: db insert
                        boolean insertionSuccess = dbmgr.insertLog(currentDateTimeString, measured_uvi);
                        if (insertionSuccess) {
                            Log.d(TAG, "insertion succeed.");
                        } else {
                            Log.d(TAG, "insertion failed.");
                        }

                        //TODO: calc : EUVB, Vit D, recommended exposure time of uvb general lighting
                        calcEuvbVitd(true, measured_uvi);
                        int lightingTime = calcLightingTime();

                        listAdapter_1.add("[" + currentDateTimeString + "] RX: " + text);
                        listAdapter_1.add("EUVB Amount = " + String.format("%.2f", euvbAmount) + " J/m2");
                        listAdapter_1.add("VitD Amount = " + String.format("%.2f", vitdAmount) + " IU");
                        listAdapter_1.add("LightingTime = " + lightingTime + " sec");
                        messageListView_1.smoothScrollToPosition(listAdapter_1.getCount() - 1);

                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                });
            }
            /*****************    edited by DaeHwan Park : 2019-02-20    *****************/








            if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)) {
                showMessage("Device doesn't support UART. Disconnecting");
                mService_1.disconnect();
            }
        }
    };
    private final BroadcastReceiver Device_2_UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            final Intent mIntent = intent;
            //*********************//
            if (action.equals(UartService2.ACTION_GATT_CONNECTED2)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        boolean _Null_check = TextUtils.isEmpty(mDevice_2.getName());
                        if (!_Null_check) //Null 체크
                        {
                            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                            Log.d(TAG, "UART_CONNECT_MSG");
                            btnConnectDisconnect_2.setText("UVLIGHT Disconnect");
                            edtMessage_2.setEnabled(true);
                            btnSend_2.setEnabled(true);
                            ((TextView) findViewById(R.id.deviceName_2)).setText(mDevice_2.getName() + " - ready");
                            listAdapter_2.add("[" + currentDateTimeString + "] Connected to: " + mDevice_2.getName());
                            messageListView_2.smoothScrollToPosition(listAdapter_2.getCount() - 1);
                            mState_2 = UART_PROFILE_CONNECTED;
                        }
                    }
                });
            }

            //*********************//
            if (action.equals(UartService2.ACTION_GATT_DISCONNECTED2)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.d(TAG, "UART_DISCONNECT_MSG");
                        btnConnectDisconnect_2.setText("UVLIGHT Connect");
                        edtMessage_2.setEnabled(false);
                        btnSend_2.setEnabled(false);
                        ((TextView) findViewById(R.id.deviceName_2)).setText("UVLIGHT Not Connected");
                        listAdapter_2.add("[" + currentDateTimeString + "] Disconnected to: " + mDevice_2.getName());
                        mState_2 = UART_PROFILE_DISCONNECTED;
                        mService_2.close();
                        //setUiState();

                    }
                });
            }


            //*********************//
            if (action.equals(UartService2.ACTION_GATT_SERVICES_DISCOVERED2)) {
                mService_2.enableTXNotification();
            }
            //*********************//
            if (action.equals(UartService2.ACTION_DATA_AVAILABLE2)) {
                final byte[] txValue = intent.getByteArrayExtra(UartService2.EXTRA_DATA2);
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            String text = new String(txValue, "UTF-8");
                            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                            listAdapter_2.add("[" + currentDateTimeString + "] RX: " + text);
                            messageListView_2.smoothScrollToPosition(listAdapter_2.getCount() - 1);

                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                });
            }
            //*********************//
            if (action.equals(UartService2.DEVICE_DOES_NOT_SUPPORT_UART2)) {
                showMessage("Device doesn't support UART. Disconnecting");
                mService_2.disconnect();
            }


        }
    };

    private void UVBAND_service_init() {
        Intent bindIntent = new Intent(this, UartService.class);
        bindService(bindIntent, mServiceConnection_1, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(Device_1_UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    private void UVLIGHT_service_init() {
        Intent bindIntent = new Intent(this, UartService2.class);
        bindService(bindIntent, mServiceConnection_2, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(Device_2_UARTStatusChangeReceiver, makeGattUpdateIntentFilter2());
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }

    private static IntentFilter makeGattUpdateIntentFilter2() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService2.ACTION_GATT_CONNECTED2);
        intentFilter.addAction(UartService2.ACTION_GATT_DISCONNECTED2);
        intentFilter.addAction(UartService2.ACTION_GATT_SERVICES_DISCOVERED2);
        intentFilter.addAction(UartService2.ACTION_DATA_AVAILABLE2);
        intentFilter.addAction(UartService2.DEVICE_DOES_NOT_SUPPORT_UART2);
        return intentFilter;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(Device_1_UARTStatusChangeReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(Device_2_UARTStatusChangeReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }

        unbindService(mServiceConnection_1);
        mService_1.stopSelf();
        mService_1 = null;

        unbindService(mServiceConnection_2);
        mService_2.stopSelf();
        mService_2 = null;

        dbmgr.close();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (!mBtAdapter_1.isEnabled()) {
            Log.i(TAG, "onResume - BT not enabled yet");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT_1);
        }
        if (!mBtAdapter_2.isEnabled()) {
            Log.i(TAG, "onResume - BT not enabled yet");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT_2);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {


            case REQUEST_SELECT_DEVICE_1:
                //When the DeviceListActivity return, with the selected device address
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    mDevice_1 = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
                    Log.d(TAG, "... onActivityResultdevice.address==" + mDevice_1 + "mserviceValue" + mService_1);
                    ((TextView) findViewById(R.id.deviceName_1)).setText(mDevice_1.getName() + " - connecting");
                    mService_1.connect(deviceAddress);

                }

                break;
            case REQUEST_SELECT_DEVICE_2:
                //When the DeviceListActivity return, with the selected device address
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    mDevice_2 = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
                    Log.d(TAG, "... onActivityResultdevice.address==" + mDevice_2 + "mserviceValue" + mService_2);
                    ((TextView) findViewById(R.id.deviceName_2)).setText(mDevice_2.getName() + " - connecting");
                    mService_2.connect(deviceAddress);

                }

                break;
            case REQUEST_ENABLE_BT_1:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Bluetooth has turned on ", Toast.LENGTH_SHORT).show();

                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "Problem in BT Turning ON ", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case REQUEST_ENABLE_BT_2:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Bluetooth has turned on ", Toast.LENGTH_SHORT).show();

                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "Problem in BT Turning ON ", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                Log.e(TAG, "wrong request code");
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }


    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBackPressed() {
        if (mState_1 == UART_PROFILE_CONNECTED) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            showMessage("nRFUART's running in background.\n             Disconnect to exit");
        } else {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.popup_title)
                    .setMessage(R.string.popup_message)
                    .setPositiveButton(R.string.popup_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.popup_no, null)
                    .show();
        }

        if (mState_2 == UART_PROFILE_CONNECTED) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            showMessage("nRFUART's running in background.\n             Disconnect to exit");
        } else {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.popup_title)
                    .setMessage(R.string.popup_message)
                    .setPositiveButton(R.string.popup_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.popup_no, null)
                    .show();
        }

    }
}
