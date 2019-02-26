package com.witlab.glowsoft.euvbmonitor;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.witlab.glowsoft.euvbmonitor.fragment.FrgAccount;
import com.witlab.glowsoft.euvbmonitor.fragment.FrgCurrent;
import com.witlab.glowsoft.euvbmonitor.fragment.FrgVitamind;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private Context context;
    private String[] title;
    private List<Fragment> pager;

    public SectionsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        title = context.getResources().getStringArray(R.array.tab_title);
        pager = new ArrayList<>();

        pager.add(new FrgCurrent());
        pager.add(new FrgVitamind());
        pager.add(new FrgAccount());
    }

    @Override
    public Fragment getItem(int position) {
        return pager.get(position);
    }

    @Override
    public int getCount() {
        // Show total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return title[position];
    }
}