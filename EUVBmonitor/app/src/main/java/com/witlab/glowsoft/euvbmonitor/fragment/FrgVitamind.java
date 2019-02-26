package com.witlab.glowsoft.euvbmonitor.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.witlab.glowsoft.euvbmonitor.MainActivity;
import com.witlab.glowsoft.euvbmonitor.R;
import com.witlab.glowsoft.euvbmonitor.listener.RefreshVitdInfoViewListener;
import com.witlab.glowsoft.euvbmonitor.model.ValueObject;

import java.util.ArrayList;

/**
 * Created by WitLab on 2018-03-26.
 * 껍데기만 만듬
 * values are based on 150515 tocon e2 data
 */

public class FrgVitamind extends Fragment {
    private ValueObject vo = ValueObject.getInstance();

    private LineChart chart = null;
    private TextView tvVitd, tvExposureTime;

    public FrgVitamind() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_vitamind, container, false);
        //TODO: fragment VitaminD process
        bind(view);
        initChart();
        setListeners();
        return view;
    }

    private void bind(View view) {
        chart = view.findViewById(R.id.chart);
        tvVitd = view.findViewById(R.id.tvVitd);
        tvExposureTime = view.findViewById(R.id.tvExposureTime);

    }

    private void initChart() {
        ArrayList<Entry> data = new ArrayList<>();

        data.add(new Entry(8.0f, 0));
        data.add(new Entry(17.0f, 1));
        data.add(new Entry(27.2f, 2));
        data.add(new Entry(43.6f, 3));
        data.add(new Entry(65.3f, 4));
        data.add(new Entry(91.4f, 5));
        data.add(new Entry(118.8f, 6));
        data.add(new Entry(143.6f, 7));
        data.add(new Entry(169.3f, 8));
        data.add(new Entry(215.3f, 9));
        data.add(new Entry(267.1f, 10));
        data.add(new Entry(322.2f, 11));


        LineDataSet lds = new LineDataSet(data, "Vitamin D");
        lds.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lds);

        ArrayList<String> xaxis = new ArrayList<>();
        xaxis.add("07:30");
        xaxis.add("07:40");
        xaxis.add("07:50");
        xaxis.add("08:00");
        xaxis.add("08:10");
        xaxis.add("08:20");
        xaxis.add("08:30");
        xaxis.add("08:40");
        xaxis.add("08:50");
        xaxis.add("09:00");
        xaxis.add("09:10");
        xaxis.add("09:20");

        LineData ld = new LineData(xaxis, dataSets);

        chart.setData(ld);
        chart.invalidate();
    }

    private void setListeners() {
        ((MainActivity) getActivity()).setRefreshVitdInfoViewListener(new RefreshVitdInfoViewListener() {
            @Override
            public void onRefreshVitdInfo(float vitd) {
                tvVitd.setText(Double.toString(vitd));
            }
        });
    }
}
