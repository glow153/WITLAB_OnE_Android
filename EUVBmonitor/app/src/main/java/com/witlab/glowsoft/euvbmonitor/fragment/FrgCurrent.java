package com.witlab.glowsoft.euvbmonitor.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.witlab.glowsoft.euvbmonitor.PacketManager;
import com.witlab.glowsoft.euvbmonitor.R;
import com.witlab.glowsoft.euvbmonitor.listener.RefreshEUVBChartListener;
import com.witlab.glowsoft.euvbmonitor.listener.RefreshClockViewListener;
import com.witlab.glowsoft.euvbmonitor.listener.RefreshCurrentInfoViewListener;
import com.witlab.glowsoft.euvbmonitor.model.ValueObject;
import com.witlab.glowsoft.euvbmonitor.model.JakeLineChartModel;
import com.witlab.glowsoft.euvbmonitor.service.AstClock;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by WitLab on 2018-03-26.
 */

public class FrgCurrent extends Fragment {
    private ValueObject vo = ValueObject.getInstance();
    private PacketManager pm = PacketManager.getInstance();

    private TextView tvTime = null;
    private TextView tvEuvb = null;
    private TextView tvUvi = null;
    private LineChart chartView = null;
    private JakeLineChartModel jchart = null;
    private AstClock clock;

    public FrgCurrent() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FrgCur", "onCreate()");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_current, container, false);
        //TODO: fragment Current process
        clock = new AstClock();
        bind(view);
//        setListeners(view);

        jchart = new JakeLineChartModel("EUVB");
//        chartView.setData(jchart.getLineData());
//        chartView.setAutoScaleMinMaxEnabled(true);
        initChart();
        vo.setJchart(jchart);

//        clock.execute();
        Log.d("FrgCur", "onCreateView()");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("FrgCur", "onResume()");
        if(clock == null) {
            clock = new AstClock();
            clock.execute();
        }
        Log.d("FrgCur", "onResume() end");
    }

    @Override
    public void onDestroyView() {
        clock.cancel(true);
        clock = null;
        vo.setJchart(null);
        jchart = null;
        Log.d("FrgCur", "onDestroyView()");
        super.onDestroyView();
    }

    private void bind(View view) {
        tvTime = view.findViewById(R.id.tvTime);
        tvEuvb = view.findViewById(R.id.tvEuvb);
        tvUvi = view.findViewById(R.id.tvUvi);
        chartView = view.findViewById(R.id.chart);
    }

    private void initChart() {
        ArrayList<Entry> data = new ArrayList<>();

        data.add(new Entry(0.007f, 0));
        data.add(new Entry(0.008f, 1));
        data.add(new Entry(0.008f, 2));
        data.add(new Entry(0.014f, 3));
        data.add(new Entry(0.018f, 4));
        data.add(new Entry(0.022f, 5));
        data.add(new Entry(0.023f, 6));
        data.add(new Entry(0.021f, 7));
        data.add(new Entry(0.021f, 8));
        data.add(new Entry(0.038f, 9));
        data.add(new Entry(0.043f, 10));
        data.add(new Entry(0.046f, 11));


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

        chartView.setData(ld);
        chartView.invalidate();
    }

    private void setListeners(View view) {
        clock.setRefreshClockViewListener(new RefreshClockViewListener() {
            @Override
            public void onRefreshClock() {
                refreshClockView();
            }
        });

        ((MainActivity) getActivity()).setRefreshInfoViewListener(new RefreshCurrentInfoViewListener() {
            @Override
            public void onRefreshCurrentInfoView() {
                float idx = vo.getUvi();
                float euvb = vo.getEuvb();
                tvEuvb.setText(String.format(Locale.KOREA,"%.3f W/m²", euvb));
//                if (idx < 0.0f)
//                    idx = 0.0f;
//                else if (idx > 11.0f)
//                    idx = 11.f;
                tvUvi.setText(String.format(Locale.KOREA, "%.1f UVI", idx));
            }
        });

        ((MainActivity) getActivity()).setRefreshEUVBChartListener(new RefreshEUVBChartListener() {
            @Override
            public void onRefreshChart(String strHms, float value) {
                jchart.appendData(strHms, value);
                vo.setJchart(jchart);
                chartView.setData(vo.getJchart().getLineData());
                chartView.notifyDataSetChanged();
                chartView.invalidate();
            }
        });
    }

    private void refreshClockView() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTime.setText(vo.getDatetime());
            }
        });
    }
}
