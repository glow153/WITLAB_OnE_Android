package com.witlab.glowsoft.whomakesnoise.model;

import java.util.ArrayList;

/**
 * Created by WitLab on 2017-11-15.
 */

public class SettingVO {
    private int sid;
    private boolean allowPush = false;
    private boolean alwaysRefresh = true;
    private int refreshInterval = 1;

    ArrayList<ArrayList<NoiseEventDetail>> eventList = null;

    /////////////////////// Singleton code start
    private SettingVO() {}
    private static class Singleton {
        private static final SettingVO instance = new SettingVO();
    }
    public static SettingVO getInstance() {
        return Singleton.instance;
    }
    /////////////////////// Singleton code end

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public boolean isAlwaysRefresh() {
        return alwaysRefresh;
    }

    public void setAllowPush(boolean allowPush) {
        this.allowPush = allowPush;
    }

    public void setAlwaysRefresh(boolean alwaysRefresh) {
        this.alwaysRefresh = alwaysRefresh;
    }

    public void setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public boolean isAllowPush() {
        return allowPush;
    }

    public int getRefreshInterval() {
        if(refreshInterval < 1)
            refreshInterval = 1;
        return refreshInterval;
    }

    public ArrayList<ArrayList<NoiseEventDetail>> getEventList() {
        return eventList;
    }

    public void setEventList(ArrayList<ArrayList<NoiseEventDetail>> eventList) {
        this.eventList = eventList;
    }
}
