package com.witlab.glowsoft.euvbmonitor.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.witlab.glowsoft.euvbmonitor.MainActivity;
import com.witlab.glowsoft.euvbmonitor.R;

/**
 * Created by WitLab on 2018-03-28.
 */

public class IntroActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(IntroActivity.this, DeviceScanActivity.class);
                startActivity(intent);
                finish();
            }
        }, 800);
    }
}
