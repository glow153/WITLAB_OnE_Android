package com.witlab.glowsoft.whomakesnoise.pages;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.witlab.glowsoft.whomakesnoise.R;
import com.witlab.glowsoft.whomakesnoise.model.SettingVO;

/**
 * Created by WitLab on 2017-10-23.
 */

public class FrgSettings extends Fragment {
    private FrameLayout layout = null;
    private LinearLayout refresh_submenu;
    private CheckBox chbxAllowPush, chbxAlwaysRefresh;
    private TextView tvAllowPush, tvAlwaysRefresh;
    private EditText edtRefreshInterval;
    private Button btnInc, btnDec;
    private SettingVO vo = SettingVO.getInstance();

    public FrgSettings() { }

    private void bindView() {
        refresh_submenu = (LinearLayout) layout.findViewById(R.id.refresh_submenu);
        edtRefreshInterval = (EditText) layout.findViewById(R.id.edtRefreshInterval);
        chbxAllowPush = (CheckBox) layout.findViewById(R.id.chbxAllowPush);
        chbxAlwaysRefresh = (CheckBox) layout.findViewById(R.id.chbxAlwaysRefresh);
        tvAllowPush = (TextView) layout.findViewById(R.id.tvAllowPush);
        tvAlwaysRefresh = (TextView) layout.findViewById(R.id.tvAlwaysRefresh);
        btnInc = (Button) layout.findViewById(R.id.intv_increment);
        btnDec = (Button) layout.findViewById(R.id.intv_decrement);
    }

    private void setListeners() {
        tvAllowPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vo.setAllowPush(!vo.isAllowPush());
                chbxAllowPush.setChecked(vo.isAllowPush());
            }
        });

        tvAlwaysRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vo.setAlwaysRefresh(!vo.isAlwaysRefresh());
                chbxAlwaysRefresh.setChecked(vo.isAlwaysRefresh());
            }
        });

        chbxAlwaysRefresh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                vo.setAlwaysRefresh(isChecked);
                refresh_submenu.setVisibility(!vo.isAlwaysRefresh() ? View.GONE : View.VISIBLE);
            }
        });

        btnInc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int intv = Integer.parseInt(edtRefreshInterval.getText().toString());
                edtRefreshInterval.setText(Integer.toString(++intv));
            }
        });

        btnDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int intv = Integer.parseInt(edtRefreshInterval.getText().toString());
                edtRefreshInterval.setText(Integer.toString(intv <= 1 ? 1 : --intv));
            }
        });
    }

    private void initSettingValues() {
        chbxAllowPush.setChecked(vo.isAllowPush());
        chbxAlwaysRefresh.setChecked(vo.isAlwaysRefresh());
        edtRefreshInterval.setText(Integer.toString(vo.getRefreshInterval()));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        layout = (FrameLayout) inflater.inflate(R.layout.page_settings, container, false);

        bindView();
        setListeners();
        initSettingValues();
        return layout;
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
        EditText edt = (EditText) layout.findViewById(R.id.edtRefreshInterval);
        String s = edt.getText().toString();
        Log.d("onDestroyView()", "frg settings destroyed!");
        Log.d("edt to string", "interval="+s);
        int sec = 1;
        if(!s.equals(""))
            sec = Integer.parseInt(s);
        vo.setRefreshInterval(sec);
        super.onDestroyView();
    }
}
