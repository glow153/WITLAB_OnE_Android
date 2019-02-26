package kr.ac.kongju.witlab.dailycontrol.adapter;

import android.widget.RadioButton;
import android.widget.TextView;

public class ListItemViewHolder {
    public RadioButton rdb;
    public TextView tvControlValues;
    public TextView tvInfo;

    public void setItemEnabled(boolean enabled) {
        rdb.setEnabled(enabled);
        tvControlValues.setEnabled(enabled);
        tvInfo.setEnabled(enabled);
    }
}
