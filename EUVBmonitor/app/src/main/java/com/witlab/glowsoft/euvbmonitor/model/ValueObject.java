package com.witlab.glowsoft.euvbmonitor.model;

/**
 * Created by WitLab on 2018-03-29.
 */

public class ValueObject {
    private float euvb = 0.0f;
    private float uvi = 0.0f;
    private String datetime = "";
    private JakeLineChartModel jchart = null;

    private ValueObject() {}
    private static class Singleton {
        private static final ValueObject instance = new ValueObject();
    }
    public static ValueObject getInstance() {
        return Singleton.instance;
    }

    public void setEuvb(float euvb) {
        this.euvb = euvb;
    }

    public void setUvi(float uvi) {
        this.uvi = uvi;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public float getEuvb() {
        return euvb;
    }

    public float getUvi() {
        return uvi;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setJchart(JakeLineChartModel jlcm) {
        this.jchart = jlcm;
    }

    public JakeLineChartModel getJchart() {
        return jchart;
    }
}
