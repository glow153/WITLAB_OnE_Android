package com.witlab.glowsoft.euvbmonitor.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.witlab.glowsoft.euvbmonitor.R;
import com.witlab.glowsoft.euvbmonitor.model.UserStatus;

import java.util.ArrayList;

/**
 * Created by WitLab on 2018-03-26.
 */

public class FrgAccount extends Fragment {
    private TextView edtVitdRes;

    private UserStatus us;
    private Button[] btnSkintypes;
    private Button[] btnExposureUpper;
    private Button[] btnExposureLower;

    private TextView tvExpArea;
    private SeekBar sbarUpper;
    private SeekBar sbarLower;
    private EditText edtExposureUpper;
    private EditText edtExposureLower;

    public FrgAccount() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btnSkintypes = new Button[6];
        btnExposureUpper = new Button[3];
        btnExposureLower = new Button[3];
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_account, container, false);
        //TODO: fragment Account process
        us = UserStatus.getInstance();

        bind(view);
        setListeners();

        return view;
    }

    private void bind(View view) {
        Log.d("FrgAccount", "bindView");
        btnSkintypes[0] = view.findViewById(R.id.btn_skintype1);
        btnSkintypes[1] = view.findViewById(R.id.btn_skintype2);
        btnSkintypes[2] = view.findViewById(R.id.btn_skintype3);
        btnSkintypes[3] = view.findViewById(R.id.btn_skintype4);
        btnSkintypes[4] = view.findViewById(R.id.btn_skintype5);
        btnSkintypes[5] = view.findViewById(R.id.btn_skintype6);

        edtVitdRes = (EditText) view.findViewById(R.id.edt_daily_resolution_amount);

        tvExpArea = view.findViewById(R.id.tv_exposure_area);

        btnExposureUpper[0] = view.findViewById(R.id.profile_exposure_10_upper);
        btnExposureUpper[1] = view.findViewById(R.id.profile_exposure_50_upper);
        btnExposureUpper[2] = view.findViewById(R.id.profile_exposure_90_upper);
        btnExposureLower[0] = view.findViewById(R.id.profile_exposure_10_lower);
        btnExposureLower[1] = view.findViewById(R.id.profile_exposure_50_lower);
        btnExposureLower[2] = view.findViewById(R.id.profile_exposure_90_lower);

        sbarUpper = view.findViewById(R.id.sb_exposure_up);
        sbarLower = view.findViewById(R.id.sb_exposure_lo);

        edtExposureUpper = view.findViewById(R.id.edt_exposure_up);
        edtExposureLower = view.findViewById(R.id.edt_exposure_lo);
    }

    private void setListeners() {
        final int[] exposurePerc = {5, 25, 45};
        Log.d("FrgAccount", "setListeners()");

        for (int i = 0; i < btnSkintypes.length; i++) {
            final int type = i + 1;
            btnSkintypes[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    us.setSkintype(type);
                }
            });
        }

        edtVitdRes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int value = Integer.parseInt(editable.toString());
                us.setVitd_res(value);
            }
        });

        for (int i = 0; i < btnExposureUpper.length; i++) {
            final int idx = i;
            btnExposureUpper[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    us.setExposeAreaUpper(exposurePerc[idx]);
                    sbarUpper.setProgress(exposurePerc[idx]);
                }
            });
        }

        for (int i = 0; i < btnExposureLower.length; i++) {
            final int idx = i;
            btnExposureLower[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    us.setExposeAreaLower(exposurePerc[idx]);
                    sbarLower.setProgress(exposurePerc[idx]);
                }
            });
        }

        sbarUpper.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                us.setExposeAreaUpper(i);
                edtExposureUpper.setText(Integer.toString(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                int i = seekBar.getProgress();
                edtExposureUpper.setText(Integer.toString(i));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbarLower.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                us.setExposeAreaLower(i);
                edtExposureLower.setText(Integer.toString(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                int i = seekBar.getProgress();
                edtExposureLower.setText(Integer.toString(i));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        edtExposureUpper.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String p = charSequence.toString();
                sbarUpper.setProgress(Integer.parseInt(p));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (Integer.parseInt(editable.toString()) < 0) {
                    edtExposureUpper.setText(Integer.toString(0));
                } else if (Integer.parseInt(editable.toString()) > 50) {
                    edtExposureUpper.setText(Integer.toString(50));
                }

                int exup = Integer.parseInt(edtExposureUpper.getText().toString());
                int exlo = Integer.parseInt(edtExposureLower.getText().toString());
                tvExpArea.setText(Integer.toString(exup + exlo));

                us.setExposeAreaLower(exup);
            }
        });

        edtExposureLower.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String p = charSequence.toString();
                sbarLower.setProgress(Integer.parseInt(p));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (Integer.parseInt(editable.toString()) < 0) {
                    edtExposureLower.setText(Integer.toString(0));
                } else if (Integer.parseInt(editable.toString()) > 50) {
                    edtExposureLower.setText(Integer.toString(50));
                }

                int exup = Integer.parseInt(edtExposureUpper.getText().toString());
                int exlo = Integer.parseInt(edtExposureLower.getText().toString());
                tvExpArea.setText(Integer.toString(exup + exlo));

                us.setExposeAreaLower(exlo);
            }
        });

    }

}
