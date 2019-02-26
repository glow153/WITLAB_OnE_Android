package com.witlab.glowsoft.whomakesnoise.view;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/**
 * Created by WitLab on 2017-12-01.
 */

public class JakeChart {
    ArrayList<String> labels = new ArrayList<>();
    ArrayList<Entry> entries = new ArrayList<>();
    LineChart linechart;
    public JakeChart(LineChart linechart) {
        this.linechart = linechart;
        labels.add("31.5Hz");
        labels.add("63Hz");
        labels.add("125Hz");
        labels.add("250Hz");
        labels.add("500Hz");
        labels.add("1000Hz");
        labels.add("2000Hz");
    }

    public void addEntryValue(float[] data) {
        for(int i=0;i<data.length;i++)
            entries.add(new Entry(data[i], i));
    }

    public void invalidateChartView() {
        LineDataSet lineDataSet = new LineDataSet(entries, "# of Ex-Rates");
        lineDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        lineDataSet.setDrawCubic(true);
        lineDataSet.setDrawFilled(true); //선아래로 색상표시
        lineDataSet.setDrawValues(false);

        LineData lineData = new LineData(labels, lineDataSet);

        linechart.setData(lineData); // set the data and list of lables into chart

        linechart.getAxisLeft().setTextColor(Color.GRAY);
        linechart.getXAxis().setTextColor(Color.GRAY);
        linechart.getLegend().setTextColor(Color.GRAY);

        linechart.animateXY(1000, 1000); //애니메이션 기능 활성화
        linechart.invalidate();
    }
}
