package kr.ac.kongju.witlab.dailycontrol.ble.adapter;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import kr.ac.kongju.witlab.dailycontrol.R;

public class DeviceListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<BluetoothDevice> mLeDevices;
    private LayoutInflater mInflator;

    public DeviceListAdapter(Context context) {
        super();
        this.context = context;
        mLeDevices = new ArrayList<>();
        mInflator = ((Activity)context).getLayoutInflater();
    }

    public void addDevice(BluetoothDevice device) {
        if(!mLeDevices.contains(device)) {
            mLeDevices.add(device);
        }
    }

    public BluetoothDevice getDevice(int position) {
        return mLeDevices.get(position);
    }

    public void clear() {
        mLeDevices.clear();
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        DeviceListViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = mInflator.inflate(R.layout.device_list_item, null);
            viewHolder = new DeviceListViewHolder();
            viewHolder.deviceAddress = view.findViewById(R.id.device_address);
            viewHolder.deviceName = view.findViewById(R.id.device_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (DeviceListViewHolder) view.getTag();
        }

        BluetoothDevice device = mLeDevices.get(i);
        final String deviceName = device.getName();
        if (viewHolder.deviceName == null)
            Log.d("devscanactv", "viewholder.deviceName null");
        if (deviceName != null && deviceName.length() > 0)
            viewHolder.deviceName.setText(deviceName);
        else
            viewHolder.deviceName.setText(R.string.unknown_device);
        viewHolder.deviceAddress.setText(device.getAddress());

        return view;
    }
}