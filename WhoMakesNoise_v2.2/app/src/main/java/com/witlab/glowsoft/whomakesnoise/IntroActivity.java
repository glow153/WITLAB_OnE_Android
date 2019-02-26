package com.witlab.glowsoft.whomakesnoise;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.witlab.glowsoft.whomakesnoise.controller.NetworkManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by WitLab on 2017-09-05.
 */

public class IntroActivity extends Activity {
    private Handler handler;
    private FrameLayout mainLayout;
    private LinearLayout loginForm;
    private ImageView imgLogo;
    private Button btnLogin;
    private EditText edtId, edtPw;

    private String id, pw, sid;

    private Runnable animationFloatLogin = new Runnable() {
        @Override
        public void run() {
            final Animation animFadeOutLogo = AnimationUtils.loadAnimation(IntroActivity.this, R.anim.fade_out_logo);
            final Animation animFadeInLogin = AnimationUtils.loadAnimation(IntroActivity.this, R.anim.fade_in_login);
            imgLogo.startAnimation(animFadeOutLogo);
            imgLogo.setVisibility(View.INVISIBLE);
            loginForm.setVisibility(View.VISIBLE);
            loginForm.startAnimation(animFadeInLogin);
        }
    };

    private Runnable authenticate = new Runnable() {
        @Override
        public void run() {
            NetworkManager net = NetworkManager.getInstance();
            Log.d("authenticate", "trying login - id:"+id+", pw:"+pw);
            sid = net.loginRequest(id, pw);
            Log.d("authenticate", "authentication result=" + sid);
            if(!sid.trim().equals("wrong")) {
                handler.post(switchActivity);
            } else {
                Log.d("authenticate","login failed :(");

                handler.post(loginDeniedMsg);
            }
        }
    };

    private Runnable loginDeniedMsg = new Runnable() {
        @Override
        public void run() {
            AlertDialog.Builder alertLoginFailed = new AlertDialog.Builder(IntroActivity.this);
            alertLoginFailed.setTitle("로그인 실패");
            alertLoginFailed.setMessage("아이디가 없거나 패스워드가 틀렸습니다.")
                    .setCancelable(false)
                    .setNegativeButton("종료",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                    .setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {dialog.cancel();
                                }
                            });
            alertLoginFailed.show();
        }
    };

    private Runnable switchActivity = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(IntroActivity.this, MainActivity.class);
            intent.putExtra("sid", sid);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    };

    private void bindView() {
        mainLayout = (FrameLayout) findViewById(R.id.mainIntroLayout);
        loginForm = (LinearLayout) findViewById(R.id.loginForm);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        edtId = (EditText) findViewById(R.id.edtId);
        edtPw = (EditText) findViewById(R.id.edtPw);
    }

    private void setListener() {
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = edtId.getText().toString();
                pw = edtPw.getText().toString();
                new Thread(authenticate).start();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_layout);
        handler = new Handler();

        bindView();
        setListener();

        loginForm.setVisibility(View.INVISIBLE);
        handler.postDelayed(animationFloatLogin, 1500);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(switchActivity);
    }
}