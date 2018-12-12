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
        public TextView mTextView;
        public MyViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.text);
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
        holder.mTextView.setText(mDataset.get(position).toString());

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}