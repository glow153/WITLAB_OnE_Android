package com.witlab.glowsoft.whomakesnoise.model;

import android.graphics.drawable.Drawable;

/**
 * Created by WitLab on 2017-11-15.
 */

public class NoiseEventType {
    public static final int CAUTION = 0;
    public static final int WARNING = 1;
    private int type;
    private String desc;
    private Drawable icon;

    public NoiseEventType(int type, String desc, Drawable icon) {
        this.type = type;
        this.desc = desc;
        this.icon = icon;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
