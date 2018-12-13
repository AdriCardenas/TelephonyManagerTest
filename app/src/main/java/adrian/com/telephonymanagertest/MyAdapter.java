package adrian.com.telephonymanagertest;

import android.net.wifi.ScanResult;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<ScanResult> mDataset;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView wifiName;
        public TextView wifiFreq;
        public TextView wifiBssid;
        public TextView wifiLevel;
        public TextView wifiTimestamp;

        public MyViewHolder(View v) {
            super(v);
            wifiName = v.findViewById(R.id.wifi_name);
            wifiFreq = v.findViewById(R.id.wifi_freq);
            wifiBssid = v.findViewById(R.id.wifi_bssid);
            wifiLevel = v.findViewById(R.id.wifi_level);
            wifiTimestamp = v.findViewById(R.id.wifi_timestamp);
        }
    }

    public void setmDataset(List<ScanResult> list) {
        this.mDataset = list;
    }

    public MyAdapter() {
        mDataset = new ArrayList<>();
    }

    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.wifiName.setText("Wifi Name:" + mDataset.get(position).SSID);
        holder.wifiFreq.setText("Frequency:" + Integer.toString(mDataset.get(position).frequency));
        holder.wifiBssid.setText("Bssid:" + mDataset.get(position).BSSID);
        holder.wifiLevel.setText("Level:" + Integer.toString(mDataset.get(position).level));
        holder.wifiTimestamp.setText("Timestamp:" + Long.toString(mDataset.get(position).timestamp));

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}