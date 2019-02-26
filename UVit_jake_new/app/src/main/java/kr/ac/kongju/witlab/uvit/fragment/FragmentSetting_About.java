package kr.ac.kongju.witlab.uvit.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.ac.kongju.witlab.uvit.R;

public class FragmentSetting_About extends Fragment {
    private View view = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_setting_about, container, false);
        }
        return view;
    }
}