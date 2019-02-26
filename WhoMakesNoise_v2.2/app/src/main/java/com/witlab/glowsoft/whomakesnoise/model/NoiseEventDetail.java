package com.witlab.glowsoft.whomakesnoise.model;

/**
 * Created by WitLab on 2017-11-15.
 */

public class NoiseEventDetail {
    private int type;
    private String datetime;
    private int source;
    private float magnitude;
    private float[] spectrum;
    private int[] victims = null;

    public NoiseEventDetail() { }

    public NoiseEventDetail(String datetime, int source, float[] spectrum) {
        this.datetime = datetime;
        this.source = source;
        this.spectrum = spectrum;
    }

    public NoiseEventDetail(String datetime, int[] victims, float[] spectrum) {
        this.datetime = datetime;
        this.victims = victims;
        this.spectrum = spectrum;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(float magnitude) {
        this.magnitude = magnitude;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public float[] getSpectrum() {
        return spectrum;
    }

    public void setSpectrum(float[] spectrum) {
        this.spectrum = spectrum;
    }

    public String getVictimsString() {
        StringBuilder ret = new StringBuilder();
        if(victims.length > 0) {
            for (int i = 0; i < victims.length - 1; i++)
                ret.append(victims[i] + ", ");
            ret.append(victims[victims.length - 1]);
        }
        return ret.toString();
    }

    public void setVictims(int[] victims) {
        this.victims = victims;
    }
}
