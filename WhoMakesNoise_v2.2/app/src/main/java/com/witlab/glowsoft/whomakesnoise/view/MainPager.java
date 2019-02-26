package com.witlab.glowsoft.whomakesnoise.view;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.witlab.glowsoft.whomakesnoise.R;
import com.witlab.glowsoft.whomakesnoise.model.NoiseEventDetail;
import com.witlab.glowsoft.whomakesnoise.pages.FrgChart;
import com.witlab.glowsoft.whomakesnoise.pages.FrgHome;
import com.witlab.glowsoft.whomakesnoise.pages.FrgSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WitLab on 2017-11-15.
 */

public class MainPager {
    private FragmentManager fm;
    private List<Fragment> pageList;
    private int curPageNo;

    public MainPager(FragmentManager fm) {
        this.fm = fm;
        pageList = new ArrayList<>();
        pageList.add(new FrgHome());
        pageList.add(new FrgChart());
        pageList.add(new FrgSettings());
        curPageNo = 0;
    }

    public void switchPageTo(int pageNo) {
        FragmentTransaction frgTrs = fm.beginTransaction();
        frgTrs.replace(R.id.pager, pageList.get(pageNo));
        frgTrs.commit();
        curPageNo = pageNo;
    }

    public int getCurrentPageNo() {
        return curPageNo;
    }

    public void updatePageHome(String spl) {
        ((FrgHome) pageList.get(0)).onUpdate(spl);
    }

    public void changeEmotion(int res) {
        ((FrgHome) pageList.get(0)).onChangeEmotion(res);
    }

    public void addEvent(NoiseEventDetail item) {
        Log.d("MainPager", "addEvent() entered");
        ((FrgChart) pageList.get(1)).onAddEvent(item);
    }
}