package kr.ac.kongju.witlab.uvit.service;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.Vector;

import kr.ac.kongju.witlab.uvit.model.ValueObject;

/**
 * Created by user on 2016-08-13.
 */
public class GeneralBluetoothControllerJake {
    private static final String TAG = GeneralBluetoothControllerJake.class.getSimpleName();
    MySQLiteOpenHelper dbHelper;

    double uvi = 0, uvb = 0;
    String action = "t";
    double bandValue, vitamind = 0;
    double RssTemp = 20.0, RssHum = 30.0;

    private Activity mActivity;
    private Thread mWorkerThread = null;

    private int MED = 300, EXPOSURE = 50, TARGET_VITAMIN_D = 400;
    private final double CORRECTION_FACTOR_3 = 0.916817688;
    private final double CORRECTION_FACTOR_5 = 0.324731475;

    //added by jake
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    private List<ScanFilter> scanFilters;
    private ScanSettings.Builder mScanSettings;

    /**** singleton start ****/
    private GeneralBluetoothControllerJake() {
        Log.d(TAG, "bluetooth 시작!");

        //added by jake
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();

        mScanSettings = new ScanSettings.Builder();
        mScanSettings.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
//        mScanSettings.setScanMode(ScanSettings.SCAN_MODE_BALANCED);
        ScanSettings scanSettings = mScanSettings.build();

        scanFilters = new Vector<>();
        ScanFilter.Builder scanFilter = new ScanFilter.Builder();
        scanFilter.setDeviceAddress("CF:7B:90:E1:76:03"); //ex) 00:00:00:00:00:00 white
        ScanFilter scan = scanFilter.build();
        scanFilters.add(scan);
        mBluetoothLeScanner.startScan(scanFilters, scanSettings, mScanCallback);

        addData();
//        String[] RSSTnH = dbHelper.getRSS();
//        RssTemp = Double.parseDouble(RSSTnH[0]);
//        RssHum = Double.parseDouble(RSSTnH[1]);
        mWorkerThread.start();
    }
    private static GeneralBluetoothControllerJake INSTANCE = new GeneralBluetoothControllerJake();
    public static GeneralBluetoothControllerJake getInstance() { return INSTANCE; }
    /**** singleton end ****/

    // getter setter

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }




    public void initManager(Activity mainActivity) {
        this.mActivity = mainActivity;
        dbHelper = new MySQLiteOpenHelper(mActivity);
    }

    ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            try {
                ScanRecord scanRecordObj = result.getScanRecord();
                byte[] scanRecord = scanRecordObj.getBytes();
                Log.d("getTxPowerLevel()",scanRecordObj.getTxPowerLevel()+"");
                Log.d("onScanResult()",
                        result.getDevice().getAddress() + " " +
                                result.getRssi() + " " +
                                result.getDevice().getName() + " " +
                                result.getDevice().getBondState() + " " +
                                result.getDevice().getType());

                bandValue = convertByteToInt(scanRecord, 5, 6);   //밴드
                uvi = ((bandValue / 284.4) - 0.99) * 15 / (2.8 - 0.99) / 1000 * 10000;
                uvb = 0.0;

                vitamind = calcVitaminD(uvi / 100 * 83);
                action = String.valueOf(bandValue);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.d("onBatchScanResults", results.size() + "");
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d("onScanFailed()", errorCode+"");
        }

        // Byte -> Int 변환
        public int convertByteToInt(byte[] b, int start, int end) {
            int value = 0;
            for (int i = end; i >= start; i--) {
                value = (value << 8) | b[i];
            }
            return value;
        }
    };

    private void addData() {
        mWorkerThread = new Thread(() -> {
            while (true) {
                try {
                    mWorkerThread.sleep(2000); //2초에 한번씩
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ValueObject vo = new ValueObject(RssTemp, RssHum, uvi, uvb, action, vitamind);

                Intent intent = new Intent("SEND_DATA");
                intent.putExtra("TIME", vo.getTime());//key, value
                intent.putExtra("TEMP", vo.getTemp());
                intent.putExtra("HUM", vo.getHum());
                intent.putExtra("UVI", vo.getUvi());
                intent.putExtra("UVB", vo.getUvb());
                intent.putExtra("ACTION", vo.getAction());
                intent.putExtra("VITAMIND", vo.getVitamind());
                mActivity.sendBroadcast(intent);

                dbHelper.addData(vo);

                Log.d(TAG, "db : " + "DB 삽입 : " + vo.getTime() + ", " + RssTemp + ", " + RssHum + ", " + vo.getUvi() + ", " + vo.getUvb() + ", " + vo.getAction() + ", " + vo.getVitamind());
            }
        });
    }

    BroadcastReceiver myReceiver_profile = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MED = Integer.valueOf(dbHelper.getSkintypeData());
            String[] arr = dbHelper.getProfileData();
            int exposureSum = Integer.valueOf(arr[4]) + Integer.valueOf(arr[5]);
            EXPOSURE = exposureSum;
            TARGET_VITAMIN_D = Integer.valueOf(arr[6]);
        }
    };

    public double calcVitaminD(double uvb) {
        String[] arr = dbHelper.getData();
        double lastVitamind = Double.valueOf(arr[6]);
        double sumVitamind = 0.0;
        Log.d(TAG, "lasttt : " + String.valueOf(lastVitamind));

        double cvitamind = 0.0;

        if (uvb <= 0) {
            sumVitamind = 0;
            sumVitamind += lastVitamind;
        } else {
            cvitamind = ((((uvb * CORRECTION_FACTOR_5) * 5) / MED) * EXPOSURE * 40);
//            cvitamind = ((((uvb * CORRECTION_FACTOR_5) * 10) / MED) * EXPOSURE * 40);
            Log.d(TAG, String.valueOf(cvitamind));
            sumVitamind = (cvitamind / TARGET_VITAMIN_D) * 100;
            sumVitamind += lastVitamind;
        }
        Log.d(TAG, "return : " + String.valueOf(sumVitamind));
        if (sumVitamind >= 100)
            sumVitamind = 100;

        return sumVitamind;
    }
}