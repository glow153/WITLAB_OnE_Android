package kr.ac.kongju.witlab.uvit.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import kr.ac.kongju.witlab.uvit.activity.main.MainActivity;
import kr.ac.kongju.witlab.uvit.R;

/**
 * Created by user on 2016-08-13.
 */
public class IntroActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }
}
