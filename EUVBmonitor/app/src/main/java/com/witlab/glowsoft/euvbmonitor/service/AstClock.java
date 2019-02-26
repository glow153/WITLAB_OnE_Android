package com.witlab.glowsoft.euvbmonitor.service;

import android.os.AsyncTask;
import android.util.Log;

import com.witlab.glowsoft.euvbmonitor.listener.RefreshClockViewListener;
import com.witlab.glowsoft.euvbmonitor.model.ValueObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by WitLab isOn 2018-03-29.
 */

public class AstClock extends AsyncTask<Integer, Integer, Integer> {
    private String datetime;
    private SimpleDateFormat sdf;
    private ValueObject vo;

    private RefreshClockViewListener refreshListener;

    public AstClock() {
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA);
        vo = ValueObject.getInstance();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        getDatetimeText();
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected Integer doInBackground(Integer... integers) {
        while(!isCancelled()) {
            Log.d("AstClock.doInBkgrd", "datetime : "+ datetime);
            getDatetimeText();
            publishProgress();
            refreshListener.onRefreshClock();

            try {
                Thread.sleep(1000);
            } catch(InterruptedException e) { }
        }

        return null;
    }

    private void getDatetimeText() {
        datetime = sdf.format(new Date());
        vo.setDatetime(datetime);
    }

    public synchronized String getDatetime() {
        return datetime;
    }

    public void setRefreshClockViewListener(RefreshClockViewListener listener) {
        this.refreshListener = listener;
    }
}
