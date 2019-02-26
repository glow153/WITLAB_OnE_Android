package kr.co.glowsoft.todayshaksik;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import kr.co.glowsoft.todayshaksik.util.NetworkManager;

public class IntroActivity extends Activity {
    private Handler handler;
    private NetworkManager haksik;

    private Runnable switchActivity = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(IntroActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_layout);
        handler = new Handler();
        haksik = NetworkManager.getInstance(); // create instance for the first time and can reuse after all
        handler.postDelayed(switchActivity, 800);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(switchActivity);
    }
}