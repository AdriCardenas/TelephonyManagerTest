package adrian.com.telephonymanagertest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyLogAdapter extends RecyclerView.Adapter<MyLogAdapter.MyViewHolder> {
    private List<String> mDataset;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView logText;

        public MyViewHolder(View v) {
            super(v);
            logText = v.findViewById(R.id.log_text);
        }
    }

    public void addItem(String item) {
        mDataset.add(0, item);
    }

    public MyLogAdapter() {
        mDataset = new ArrayList<>();
    }

    @Override
    public MyLogAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_simple_list, parent, false);
        MyLogAdapter.MyViewHolder vh = new MyLogAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyLogAdapter.MyViewHolder holder, int position) {
        holder.logText.setText("Wifi Name:" + mDataset.get(position));

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
