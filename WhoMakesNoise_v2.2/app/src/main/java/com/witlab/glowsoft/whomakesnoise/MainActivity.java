package com.witlab.glowsoft.whomakesnoise;

import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.witlab.glowsoft.whomakesnoise.controller.NetworkManager;
import com.witlab.glowsoft.whomakesnoise.controller.PushNoti;
import com.witlab.glowsoft.whomakesnoise.model.NoiseEventDetail;
import com.witlab.glowsoft.whomakesnoise.model.NoiseEventType;
import com.witlab.glowsoft.whomakesnoise.model.SettingVO;
import com.witlab.glowsoft.whomakesnoise.view.MainPager;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navigation;
    private BackgroundTask task;
    private MainPager mp;
    private SettingVO settings;

    private String spl = "0";
    private boolean isRefreshHomeRunning = true;

    private void changeTitle(int pageNo) {
        int resId;
        switch (pageNo) {
            case 0:
                resId = R.string.title_home;
                break;
            case 1:
                resId = R.string.title_chart;
                break;
            case 2:
                resId = R.string.title_settings;
                break;
            default:
                resId = R.string.app_name;
                break;
        }
        setTitle(getString(resId));
    }

    private void setListeners() {
        navigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int navId;
                        switch (item.getItemId()) {
                            case R.id.navigation_home:
                                navId = 0;
                                break;
                            case R.id.navigation_chart:
                                navId = 1;
                                break;
                            case R.id.navigation_settings:
                                navId = 2;
                                break;
                            default:
                                return false;
                        }
                        Log.d("navlistener", "navigation " + navId + " selected");

                        if (navId == 0) {
                            if (task == null)
                                startRefreshHomeTask();
                            else if (!isRefreshHomeRunning)
                                restartRefreshHomeTask();
                        } else {
                            if (isRefreshHomeRunning)
                                pauseRefreshHomeTask();
                        }

                        //page selecting logic here
                        mp.switchPageTo(navId);
                        changeTitle(navId);
                        return true;
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = SettingVO.getInstance();
        settings.setSid(Integer.parseInt(getIntent().getStringExtra("sid").trim()));
        Log.d("MainActivity", "onCreate(), device id:" + settings.getSid());

        navigation = (BottomNavigationView) findViewById(R.id.navigation); // bind view
        setListeners();

        FragmentManager fm = getFragmentManager();
        mp = new MainPager(fm);

        task = new BackgroundTask();
        task.execute();
    }

    @Override
    protected void onStart() {
        Log.d("MainActivity", "onStart()");
        super.onStart();
        mp.switchPageTo(0);
        startRefreshHomeTask();
    }

    @Override
    protected void onPause() {
        Log.d("MainActivity", "onPause()");
        pauseRefreshHomeTask();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("MainActivity", "onStop()");
        pauseRefreshHomeTask();
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.d("MainActivity", "onRestart()");
        super.onRestart();
        restartRefreshHomeTask();
    }

    @Override
    protected void onPostResume() {
        Log.d("MainActivity", "onPostResume()");
        super.onPostResume();
        restartRefreshHomeTask();
    }

    @Override
    protected void onDestroy() {
        Log.d("MainActivity", "onDestroy()");
        killRefreshHomeTask();
        super.onDestroy();
    }

    private void startRefreshHomeTask() {
        if (task == null) {
            task = new BackgroundTask();
        }
        if (task.getStatus() != AsyncTask.Status.RUNNING) {
            task.execute();
        }
        if (!isRefreshHomeRunning) {
            isRefreshHomeRunning = true;
        }
        Log.d("startTask()", "task started");
    }

    private void pauseRefreshHomeTask() {
        isRefreshHomeRunning = false;
    }

    private void restartRefreshHomeTask() {
        isRefreshHomeRunning = true;
    }

    private void killRefreshHomeTask() {
        task.cancel(true);
        task = null;
        isRefreshHomeRunning = false;
        Log.d("killTask()", "task died");
    }

    private class BackgroundTask extends AsyncTask<Integer, Integer, Integer> {
        private boolean kill;
        private int dBad, dGood;
        private Handler handler;
        private NetworkManager net;
        private SettingVO vo;

        @Override
        protected void onPreExecute() {
            handler = new Handler();
            net = NetworkManager.getInstance();
            vo = SettingVO.getInstance();
            dBad = R.drawable.ic_noise_detected;
            dGood = R.drawable.ic_noise_good;
            kill = false;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            kill = true;
            Log.d("asynctask", "onCancelled()");
        }

        @Override
        protected void onPostExecute(Integer integer) {
            Log.d("asynctask", "onPostExecute()");
            super.onPostExecute(integer);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //update view logic here
            if (isRefreshHomeRunning)
                mp.updatePageHome(spl);

            if (spl != null && spl.equals("")) {
                if (Float.parseFloat(spl) > 52.0f) {
                    mp.changeEmotion(dBad);
                } else {
                    mp.changeEmotion(dGood);
                }
            }
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            long lastMilsec = System.currentTimeMillis();
            boolean bRefresh = vo.isAlwaysRefresh();
            int interval = vo.getRefreshInterval();
            Log.d("doInBackground", "backgroundtask start");

            while (!kill) {
                if (System.currentTimeMillis() - lastMilsec >= interval * 1000) {
                    lastMilsec = System.currentTimeMillis();
                    if(mp.getCurrentPageNo() == 0) {
                        if (isRefreshHomeRunning && bRefresh) {
                            // send http request to Cloud Platform (AWS EC2)
                            spl = net.getSPL(vo.getSid());
                            Log.d("doInBkgrd()", "spl:" + spl);
                            publishProgress(); // refresh views
                        }

                    } else if(mp.getCurrentPageNo() == 1) {
                        String datetime = "";
                        NoiseEventDetail item;

                        if (vo.getEventList() != null && vo.getEventList().size() != 0) {
                            datetime = vo.getEventList().get(0).get(0).getDatetime(); //get newest datetime
                        }
                        Log.d("doinBkgrd()", "datetime : " + datetime);
                        if(!datetime.equals("")) {
                            item = net.getNewestEvent(datetime, vo.getSid());

                            if (item != null) {
                                Log.d("push", "event type : " + item.getType());
                                if (item.getType() == NoiseEventType.CAUTION) {
                                    PushNoti.push(MainActivity.this, "주의!", "다른 세대에서 층간소음이 발생하였습니다.");
                                } else if (item.getType() == NoiseEventType.WARNING) {
                                    PushNoti.push(MainActivity.this, "경고!", "당신의 집에서 층간소음이 발생하였습니다.");
                                }
                                mp.addEvent(item);
                            }
                        }
                    } else {
                        interval = vo.getRefreshInterval();
                    }
                }
            }
            return null;
        }
    }
}
