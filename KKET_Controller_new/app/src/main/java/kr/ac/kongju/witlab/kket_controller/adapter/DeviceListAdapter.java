package kr.ac.kongju.witlab.kket_controller.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import kr.ac.kongju.witlab.kket_controller.R;

public class DeviceListAdapter extends BaseAdapter {
    public final static String TAG = DeviceListAdapter.class.getSimpleName();

    private ArrayList<BluetoothDevice> mLeDevices;
    private ArrayList<Boolean> mChecked;
    private LayoutInflater mInflater;

    public DeviceListAdapter(Context context) {
        super();
        mLeDevices = new ArrayList<>();
        mChecked = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
    }

    public void addDevice(BluetoothDevice device) {
        if (!mLeDevices.contains(device)) {
            mLeDevices.add(device);
            mChecked.add(false);
        }
    }

    public BluetoothDevice getDevice(int position) {
        return mLeDevices.get(position);
    }

    public ArrayList<BluetoothDevice> getCheckedDevices() {
        ArrayList<BluetoothDevice> retlist = new ArrayList<>();
        for (int i = 0; i < mChecked.size(); i++) {
            if (mChecked.get(i)) {
                Log.d(TAG, "checked device : " + mLeDevices.get(i));
                retlist.add(mLeDevices.get(i));
            }
        }

        return retlist;
    }

    public BluetoothDevice getCheckedDevice() {
        for (int i = 0; i < mChecked.size(); i++) {
            if (mChecked.get(i)) {
                Log.d(TAG, "checked device : " + mLeDevices.get(i));
                return mLeDevices.get(i);
            }
        }
        return null;
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
            view = mInflater.inflate(R.layout.device_list_item, null);
            viewHolder = new DeviceListViewHolder();
            viewHolder.cbx = view.findViewById(R.id.cbxdevice);
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
        viewHolder.cbx.setChecked(mChecked.get(i));
        viewHolder.cbx.setClickable(false);
        viewHolder.cbx.setFocusable(false);

        return view;
    }

    public void switchChecked(int position) {
        boolean b = mChecked.get(position);
        mChecked.set(position, !b);
        notifyDataSetChanged();
    }
}