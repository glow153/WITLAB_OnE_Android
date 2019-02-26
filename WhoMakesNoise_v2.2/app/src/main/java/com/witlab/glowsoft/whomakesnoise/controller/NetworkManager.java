package com.witlab.glowsoft.whomakesnoise.controller;

import android.os.Handler;
import android.util.Log;

import com.witlab.glowsoft.whomakesnoise.model.NoiseEventDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by WitLab on 2017-11-17.
 */

public class NetworkManager {
    /////////////////////// Singleton code start
    private NetworkManager() {
    }

    private static class Singleton {
        private static final NetworkManager instance = new NetworkManager();
    }

    public static NetworkManager getInstance() {
        return Singleton.instance;
    }
    /////////////////////// Singleton code end

    private Handler handler;
    private URL url = null;
    private HttpURLConnection conn;

    private ArrayList<ArrayList<NoiseEventDetail>> eventList = null;

    private String request(String urlStr, String queryParam) {
        StringBuilder output = new StringBuilder();
        try {
            url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();

            if (conn != null) {
                conn.setConnectTimeout(10000); //10s
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", Integer.toString(queryParam.length()));
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();

                OutputStream os = conn.getOutputStream();
                os.write(queryParam.getBytes());
                os.flush();
                os.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while (true) {
                    String line = br.readLine();
                    if (line == null) {
                        break;
                    }
                    output.append(line);
                    output.append("\n");
                }

                br.close();
                conn.disconnect();
            }
        } catch (Exception ex) {
            Log.e("SampleHTTP", "Exception in processing response.", ex);
            ex.printStackTrace();
        }

        return output.toString();
    }

    public String getSPL(int sid) {
        String queryParam = "sid=" + sid;
        return request("http://52.78.73.203/floornoise/selectSPL.php", queryParam);
    }


    public String loginRequest(String id, String pw) {
        String queryParam = "id=" + id + "&pw=" + pw;
        return request("http://52.78.73.203/floornoise/login.php", queryParam);
    }

    private JSONArray concatArray(JSONArray arr1, JSONArray arr2)
            throws JSONException {
        JSONArray result = new JSONArray();
        for (int i = 0; i < arr1.length(); i++) {
            result.put(arr1.get(i));
        }
        for (int i = 0; i < arr2.length(); i++) {
            result.put(arr2.get(i));
        }
        return result;
    }

    String jm;
    String jo;

    public JSONArray getEventJson(int sid) {
        final String queryParam = "sid=" + sid;
        Log.d("getEventJson()", queryParam);
        handler = new Handler();
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                jm = request("http://52.78.73.203/floornoise/getEvent_m.php", queryParam);
                jo = request("http://52.78.73.203/floornoise/getEvent_o.php", queryParam);
                Log.d("getEventJson()", jm + jo);
            }
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONArray jarrm;
        JSONArray jarro;
        JSONArray result = null;
        try {
            jarrm = new JSONArray(jm);
            jarro = new JSONArray(jo);
            result = concatArray(jarrm, jarro);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public NoiseEventDetail parseJsonWarningDetail(JSONObject jobj) {
        float mag = 0.0f;
        int[] victims = null;
        float[] spectrum = null;
        NoiseEventDetail item = new NoiseEventDetail();

        try {
            mag = Float.parseFloat(jobj.getString("magnitude"));
            JSONArray jaVictims = jobj.getJSONArray("victims");
            victims = new int[jaVictims.length()];
            JSONArray jaSpectrum = jobj.getJSONArray("spectrum");
            spectrum = new float[jaSpectrum.length()];

            for (int i = 0; i < jaVictims.length(); i++)
                victims[i] = jaVictims.getInt(i);
            for (int i = 0; i < jaSpectrum.length(); i++)
                spectrum[i] = Float.parseFloat(jaSpectrum.getString(i));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        item.setMagnitude(mag);
        item.setVictims(victims);
        item.setSpectrum(spectrum);

        return item;
    }

    public NoiseEventDetail parseJsonCautionDetail(JSONObject jobj) {
        float mag = 0.0f;
        int source = 0;
        float[] spectrum = null;
        NoiseEventDetail item = new NoiseEventDetail();

        try {
            mag = Float.parseFloat(jobj.getString("magnitude"));
            source = jobj.getInt("source");
            JSONArray jaSpectrum = jobj.getJSONArray("spectrum");
            spectrum = new float[jaSpectrum.length()];
            for (int i = 0; i < jaSpectrum.length(); i++)
                spectrum[i] = Float.parseFloat(jaSpectrum.getString(i));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        item.setMagnitude(mag);
        item.setSource(source);
        item.setSpectrum(spectrum);

        return item;
    }

    public NoiseEventDetail getNewestEvent(String newestDatetime, int sid) {
        final String queryParam = "datetime=" + newestDatetime + "&sid=" + sid;
        String result = request("http://52.78.73.203/floornoise/getEventNew.php", queryParam);
        Log.d("getNewestEvent()", "request result : " + result);

        if(result.equals(""))
            return null;

        JSONObject jo;
        NoiseEventDetail item = null;
        try {
            jo = new JSONObject(result);
            if(jo.getString("type").equals("nfm")) {
                item = parseJsonWarningDetail(jo);
            } else if(jo.getString("type").equals("nfo")) {
                item = parseJsonCautionDetail(jo);
            }
        } catch (JSONException e) {
            Log.e("getNewestEvent()", "error getting newest noise event");
        }

        return item;
    }
}
