package kr.ac.kongju.witlab.kket_controller;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import kr.ac.kongju.witlab.kket_controller.callback.TimeSequenceChangeCallback;
import kr.ac.kongju.witlab.kket_controller.model.DataRepository;

public class ThAutoMode extends Thread {
    private final static String TAG = ThAutoMode.class.getSimpleName();

    private volatile boolean bRunning;
    private volatile boolean kill;
    private TimeSequenceChangeCallback callback;
    private SimpleDateFormat sdf;
    private int currentLevel;

    public ThAutoMode(TimeSequenceChangeCallback callback) {
        this.callback = callback;
        bRunning = false;
        kill = false;
        sdf = new SimpleDateFormat("HH:mm");
        currentLevel = 0;
    }

    @Override
    public void run() {
        DataRepository dr = DataRepository.getInstance();
        Date[] timetable = dr.getDateObjTable();
        while (!kill) {
            if(bRunning) {
                // TODO: running process here
                Log.d("ThAutoMode.run()","automode thread is running");

                for (int i = 1; i < timetable.length - 1; i++) {
                    Calendar calnow = Calendar.getInstance();
                    calnow.set(0, 0, 0);

                    long start = timetable[i].getTime();
                    long now = calnow.getTime().getTime();
                    long end = timetable[i+1].getTime();

                    if (start <= now && now < end & i != currentLevel) {
                        callback.changeTimeSeq(0, i);
                        callback.changeTimeSeq(1, i);
                        currentLevel = i; // inhibit double occurrence
                        break;
                    }
                }
            }

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        bRunning = false;
        kill = true;
    }

    public synchronized void startThread() {
        kill = false;
        bRunning = true;
        Log.d(TAG,"automode thread has been started.");
    }

    public boolean isRunning() {
        return bRunning;
    }

    public void killThread() {
        Log.d(TAG,"automode thread has been killed.");
        kill = true;
        currentLevel = -1;
    }

    public void pauseThread() {
        Log.d(TAG,"automode thread has been paused.");
        bRunning = false;
        currentLevel = -1;
    }

    public boolean isKilled() {
        return kill;
    }
}
