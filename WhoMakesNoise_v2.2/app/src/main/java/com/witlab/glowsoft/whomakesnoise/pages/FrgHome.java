package com.witlab.glowsoft.whomakesnoise.pages;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.witlab.glowsoft.whomakesnoise.R;

/**
 * Created by WitLab on 2017-10-23.
 */

public class FrgHome extends Fragment {
    private TextView tvSPL = null;
    private ImageView iv = null;

    public FrgHome() { }

    private void bindView(LinearLayout layout) {
        tvSPL = (TextView) layout.findViewById(R.id.tvSPL);
        iv = (ImageView) layout.findViewById(R.id.ivStatus);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.page_home, container, false);
        bindView(layout);

        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void onUpdate(String msg) {
        tvSPL.setText(msg);
    }

    public void onChangeEmotion(int res) {
        iv.setImageResource(res);
    }
}
