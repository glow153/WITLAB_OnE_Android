package kr.ac.kongju.witlab.uvit.activity.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import kr.ac.kongju.witlab.uvit.fragment.FragmentLED;
import kr.ac.kongju.witlab.uvit.fragment.FragmentMonitoring;
import kr.ac.kongju.witlab.uvit.fragment.FragmentSetting;
import kr.ac.kongju.witlab.uvit.fragment.FragmentTrends;

/**
 * Created by user on 2016-08-13.
 */
public class UvitPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public UvitPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FragmentMonitoring monitoringFragment = new FragmentMonitoring();
                return monitoringFragment;
//            case 1:
//                FragmentTrends trendsFragment = new FragmentTrends();
//                return trendsFragment;
            case 1:
                FragmentLED ledFragment = new FragmentLED();
                return ledFragment;
            case 2:
                FragmentSetting settingFragment = new FragmentSetting();
                return settingFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
