package com.witlab.glowsoft.whomakesnoise.pages;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.witlab.glowsoft.whomakesnoise.R;
import com.witlab.glowsoft.whomakesnoise.controller.ExListViewAdapter;
import com.witlab.glowsoft.whomakesnoise.controller.NetworkManager;
import com.witlab.glowsoft.whomakesnoise.model.NoiseEventDetail;
import com.witlab.glowsoft.whomakesnoise.model.NoiseEventType;
import com.witlab.glowsoft.whomakesnoise.model.SettingVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by WitLab on 2017-10-23.
 */

public class FrgChart extends Fragment {
    private LinearLayout root;
    private ExpandableListView listview;
    private ExListViewAdapter adapter;
    private NetworkManager net;
    private SettingVO settings;
    private int lastClicked;

    public FrgChart() { }

    private void setListener() {
        listview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                Boolean isExpand = !listview.isGroupExpanded(groupPosition);
                listview.collapseGroup(lastClicked);
                if (isExpand) {
                    listview.expandGroup(groupPosition);
                }
                lastClicked = groupPosition;
                return true;
            }
        });
    }

    private void initAdapter() {
        JSONArray jaEvents = net.getEventJson(settings.getSid());
        TreeMap<String, NoiseEventDetail> eventMap = new TreeMap<>(Collections.reverseOrder());

        for (int i = 0; i < jaEvents.length(); i++) {
            JSONObject jobj;
            String datetime = null;
            String type;
            JSONObject joDetail;
            NoiseEventDetail item = null;

            try {
                jobj = jaEvents.getJSONObject(i);
                datetime = jobj.getString("datetime");
                type = jobj.getString("type");
                joDetail = jobj.getJSONObject("detail");

                if (type.equals("nfm")) {
                    item = net.parseJsonWarningDetail(joDetail);
                    item.setType(NoiseEventType.WARNING);
                    item.setDatetime(datetime);
                } else if (type.equals("nfo")) {
                    item = net.parseJsonCautionDetail(joDetail);
                    item.setType(NoiseEventType.CAUTION);
                    item.setDatetime(datetime);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            eventMap.put(datetime, item);
        }

        adapter = new ExListViewAdapter(getActivity(), eventMap);
        listview.setAdapter(adapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        net = NetworkManager.getInstance();
        settings = SettingVO.getInstance();
        root = (LinearLayout) inflater.inflate(R.layout.page_chart, container, false);
        listview = (ExpandableListView) root.findViewById(R.id.eventList);

        initAdapter();
        setListener();

        return root;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void onAddEvent(NoiseEventDetail item) {
        adapter.addEvent(item);
    }
}
