package com.witlab.glowsoft.euvbmonitor.model;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.witlab.glowsoft.euvbmonitor.listener.RefreshEUVBChartListener;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by WitLab on 2018-03-29.
 */

public class JakeLineChartModel {
    private RefreshEUVBChartListener listener;

    //LineChart
    private static final int DATA_LENGTH_LIMIT = 60;
    private String dataname = "";

    private List<Entry> entry = null;
    private List<String> xaxis = null;
    private LineDataSet lds = null;
    private List<LineDataSet> dataSets = null;
    private LineData linedata = null;

    public JakeLineChartModel(String dataname) {
        this.dataname = dataname;
        resetChartData();
    }

    public void resetChartData() {
        entry = new LinkedList<>();
        xaxis = new LinkedList<>();
        dataSets = new LinkedList<>();

        lds = new LineDataSet(entry, dataname);
        lds.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSets.add(lds);
        linedata = new LineData(xaxis, dataSets);
    }

    public void appendData(String time, float e) {
        if (dataSets.size() >= DATA_LENGTH_LIMIT) {
            entry.remove(0);
            xaxis.remove(0);
        }

        xaxis.add(time);
        entry.add(new Entry(e, entry.size()));
    }

    public LineData getLineData() {
        return linedata;
    }
}
