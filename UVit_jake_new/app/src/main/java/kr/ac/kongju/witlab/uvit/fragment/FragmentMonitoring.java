package kr.ac.kongju.witlab.uvit.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import kr.ac.kongju.witlab.uvit.R;
import kr.ac.kongju.witlab.uvit.model.ValueObject;
import kr.ac.kongju.witlab.uvit.service.MySQLiteOpenHelper;

public class FragmentMonitoring extends Fragment {

    private static final String TAG = "FragmentMonitoring";

    private View view = null;
    private MySQLiteOpenHelper db;
//    private GpsInfo gpsService;
    private MySQLiteOpenHelper dbHelper;


    TextView locationText, tempText, humText, uviText, uvbText, syncText;

    TextView actionText, vitamindText;

    Button synButton;

    RelativeLayout monitoring;

    String ctime;
    double temp, hum, uvi, uvb;
    String action;
    double vitamind;

    public FragmentMonitoring() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) view = inflater.inflate(R.layout.fragment_monitoring, container, false);
        db = new MySQLiteOpenHelper(view.getContext());
//        gpsService = new GpsInfo(view.getContext());
        dbHelper = new MySQLiteOpenHelper(view.getContext());

        monitoring = view.findViewById(R.id.monitoring);

        locationText = view.findViewById(R.id.monitoring_location);
        tempText = view.findViewById(R.id.monitoring_temp);
        humText = view.findViewById(R.id.monitoring_hum);
        uviText = view.findViewById(R.id.monitoring_UVI);
        uvbText = view.findViewById(R.id.monitoring_UVB);
        vitamindText = view.findViewById(R.id.monitoring_D);
//        actionText = view.findViewById(R.id.monitoring_action);
        syncText = view.findViewById(R.id.lastsync);

        synButton = view.findViewById(R.id.bottombtn_sync);

//        if (gpsService.isGetLocation()) {
//        } else {
//            gpsService.showSettingsAlert();
//        }
        locationText.setText(setGPS());

        ValueObject vo = new ValueObject("p");
        Intent intent = new Intent("PUSH_SYN");
        intent.putExtra("SYNCHECK", vo.getSyncheck());
        getActivity().sendBroadcast(intent);

        String[] lastArr = dbHelper.getData();

        ctime = lastArr[0];
        temp = Double.valueOf(lastArr[1]);
        hum = Double.valueOf(lastArr[2]);
        uvi = Double.valueOf(lastArr[3]);
        uvb = Double.valueOf(lastArr[4]);
//        action = lastArr[5];
        vitamind = Double.valueOf(lastArr[6]);

        setValues();
        synButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValueObject vo = new ValueObject("p");
                Intent intent = new Intent("PUSH_SYN");
                intent.putExtra("SYNCHECK", vo.getSyncheck());
                getActivity().sendBroadcast(intent);
                setValues();
            }
        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(myReceiver, new IntentFilter("SEND_DATA"));
        getActivity().registerReceiver(myReceiver_gps, new IntentFilter("SEND_GPS"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(myReceiver);
        getActivity().unregisterReceiver(myReceiver_gps);
    }

    BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ctime = intent.getStringExtra("TIME");
            temp = intent.getDoubleExtra("TEMP", 0.0);
            hum = intent.getDoubleExtra("HUM", 0.0);
            uvi = intent.getDoubleExtra("UVI", 0.0);
            uvb = intent.getDoubleExtra("UVB", 0.0);
//            action = intent.getStringExtra("ACTION");
            vitamind = intent.getDoubleExtra("VITAMIND", 0.0);

            Log.d(TAG,"vitamin DDDDDDDDDDDDDDD : "+vitamind);

            //UVI 자외선 지수에 따라 배경이미지 변경
            if (uvi <= 2.0) {
                monitoring.setBackgroundResource(R.drawable.back_1);
                setValues();
            } else if (uvi <= 5.0) {
                monitoring.setBackgroundResource(R.drawable.back_2);
                setValues();
            } else if (uvi <= 7.0) {
                monitoring.setBackgroundResource(R.drawable.back_3);
                setValues();
            } else if (uvi <= 10.0) {
                monitoring.setBackgroundResource(R.drawable.back_4);
                setValues();
            } else if (uvi > 11.0) {
                monitoring.setBackgroundResource(R.drawable.back_5);
                setValues();
            }
        }
    };

    BroadcastReceiver myReceiver_gps = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String adr = intent.getStringExtra("ADDRESS");
            locationText.setText(adr);
        }
    };


    public void setValues() {
        int end = 3;
        tempText.setText(String.valueOf(temp));
        humText.setText(String.valueOf(hum));

        if (uvi >= 10)
            end = 4;
        else
            end = 3;

        uviText.setText(String.valueOf(uvi).substring(0, end));

        if (vitamind >= 10)
            end = 4;
        else
            end = 3;

        if (String.valueOf(vitamind).equals("100.0")) {
            vitamindText.setText("100");
        }

        String vitaminD_str = String.valueOf(vitamind);

//        Log.d(TAG, "vitaminD_str : "+vitaminD_str);
//
//        Log.d(TAG, "substring : "+vitaminD_str.substring(1,2));
//        Log.d(TAG, "substring : "+vitaminD_str.substring(2,3));
//        Log.d(TAG, "substring : "+vitaminD_str.substring(3,4));

        if (vitaminD_str.substring(1,2).equals(".")){
            vitamindText.setText(vitaminD_str.substring(0,1));
        }else if (vitaminD_str.substring(2,3).equals(".")){
            vitamindText.setText(vitaminD_str.substring(0,2));
        }else if (vitaminD_str.substring(3,4).equals(".")){
            vitamindText.setText(vitaminD_str.substring(0,3));
        }

        if (uvi < 0 || String.valueOf(uvi).equals("-")) {
            uviText.setText("0.0");
        }
        syncText.setText(ctime);
    }

    public String setGPS() {
        String address = null;
        double lat, lon;
//        lon = gpsService.getLongitude();
//        lat = gpsService.getLatitude();
//        address = gpsService.getAddress(lat, lon);
        return address;
    }
}
