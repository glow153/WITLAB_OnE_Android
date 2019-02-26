package com.witlab.glowsoft.whomakesnoise.controller;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.witlab.glowsoft.whomakesnoise.R;
import com.witlab.glowsoft.whomakesnoise.model.NoiseEventDetail;
import com.witlab.glowsoft.whomakesnoise.model.NoiseEventType;
import com.witlab.glowsoft.whomakesnoise.model.SettingVO;
import com.witlab.glowsoft.whomakesnoise.view.JakeChart;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * Created by WitLab on 2017-11-27.
 */

public class ExListViewAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<NoiseEventType> groupList = null;
    private ArrayList<ArrayList<NoiseEventDetail>> childList = null;
    private SettingVO settings;
    private String sc, sw;
    private Drawable dc, dw;

    public ExListViewAdapter(Context context, TreeMap<String, NoiseEventDetail> eventMap) {
        super();
        this.context = context;
        settings = SettingVO.getInstance();
        groupList = new ArrayList<>();
        childList = new ArrayList<>();

        String sc = context.getString(R.string.chart_caution);
        String sw = context.getString(R.string.chart_warning);
        Drawable dc = context.getDrawable(R.drawable.ic_caution);
        Drawable dw = context.getDrawable(R.drawable.ic_warning);

        Iterator<NoiseEventDetail> iterator = eventMap.values().iterator();
        Log.d("exlistviewdapter()","eventlist exist? " + eventMap.size());

        while(iterator.hasNext()) {
            NoiseEventDetail item = iterator.next();
            ArrayList<NoiseEventDetail> childEntity = new ArrayList<>();

            switch(item.getType()){
                case NoiseEventType.CAUTION :
                    groupList.add(new NoiseEventType(item.getType(), sc, dc));
                    break;
                case NoiseEventType.WARNING :
                    groupList.add(new NoiseEventType(item.getType(), sw, dw));
                    break;
            }
            childEntity.add(item);
            childList.add(childEntity);
            Log.d("exlistviewadapter()", "eventlist:" + childList.get(childList.size()-1));
        }

        settings.setEventList(childList);
    }

    public void addEvent(NoiseEventDetail item) {
        ArrayList<NoiseEventDetail> entity = new ArrayList<>();
        groupList.add(0, new NoiseEventType(item.getType(), sc, dc));
        entity.add(item);
        childList.add(0, entity);
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View root = convertView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(root == null) {
            root = inflater.inflate(R.layout.list_item_group, parent, false);
            ImageView imgGroupIcon = (ImageView)root.findViewById(R.id.list_item_icon);
            TextView tv = (TextView) root.findViewById(R.id.list_item_content);
            NoiseEventType item = groupList.get(groupPosition);

            tv.setText(item.getDesc());
            imgGroupIcon.setImageDrawable(item.getIcon());
        }
        return root;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View root = null;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView tvDatetime, tvMagnitude, tvSource, tvVictims;
        JakeChart jakeChart;
        NoiseEventDetail item = childList.get(groupPosition).get(0);

        Log.d("test", "groupPosition:" + groupPosition + ", childPosition : " + childPosition);

        if(item.getType() == NoiseEventType.CAUTION) {
            root = inflater.inflate(R.layout.list_item_caution, parent, false);
            tvDatetime = (TextView) root.findViewById(R.id.list_item_datetime);
            tvMagnitude = (TextView) root.findViewById(R.id.list_item_magnitude);
            tvSource = (TextView) root.findViewById(R.id.list_item_source);
            LineChart linechart = (LineChart) root.findViewById(R.id.jakeChart);
            jakeChart = new JakeChart(linechart);

            tvDatetime.setText("발생 일시 : " + item.getDatetime());
            tvMagnitude.setText("층간소음 발생 크기 : " + Float.toString(item.getMagnitude()));
            tvSource.setText("발생 세대 : " + item.getSource());
            jakeChart.addEntryValue(item.getSpectrum());
            jakeChart.invalidateChartView();

        } else if (item.getType() == NoiseEventType.WARNING) {
            root = inflater.inflate(R.layout.list_item_warning, parent, false);
            tvDatetime = (TextView) root.findViewById(R.id.list_item_datetime);
            tvMagnitude = (TextView) root.findViewById(R.id.list_item_magnitude);
            tvVictims = (TextView) root.findViewById(R.id.list_item_victims);
            LineChart linechart = (LineChart) root.findViewById(R.id.jakeChart);
            jakeChart = new JakeChart(linechart);

            tvDatetime.setText("발생 일시 : " + item.getDatetime());
            tvMagnitude.setText("층간소음 발생 크기 : " + Float.toString(item.getMagnitude()));
            tvVictims.setText("피해 세대 : " + item.getVictimsString());
            jakeChart.addEntryValue(item.getSpectrum());
            jakeChart.invalidateChartView();
        }
        return root;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
