package kr.ac.kongju.witlab.dailycontrol;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import kr.ac.kongju.witlab.dailycontrol.listener.OnTimetableChangedListener;
import kr.ac.kongju.witlab.dailycontrol.model.DataRepository;

public class ThAutoMode extends Thread {
    private volatile boolean bRunning;
    private volatile boolean kill;
    private OnTimetableChangedListener listener;
    private SimpleDateFormat sdf;
    private int currentLevel;

    public ThAutoMode(OnTimetableChangedListener listener) {
        this.listener = listener;
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
                        listener.onTimeChanged(i);
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
        Log.d("ThAutoMode.startThread()","automode thread has been started.");
    }

    public boolean isRunning() {
        return bRunning;
    }

    public void killThread() {
        Log.d("ThAutoMode.killThread()","automode thread has been killed.");
        kill = true;
        currentLevel = -1;
    }

    public void pauseThread() {
        Log.d("ThAutoMode.pauseThread()","automode thread has been paused.");
        bRunning = false;
        currentLevel = -1;
    }

    public boolean isKilled() {
        return kill;
    }
}
