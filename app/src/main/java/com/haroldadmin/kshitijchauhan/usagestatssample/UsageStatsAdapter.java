package com.haroldadmin.kshitijchauhan.usagestatssample;

import android.app.usage.UsageStats;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class UsageStatsAdapter extends RecyclerView.Adapter<UsageStatsAdapter.ViewHolder> {

    private List<UsageStatistic> list;
    private Context context;

    public UsageStatsAdapter(List<UsageStatistic> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.usage_stats_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindValues(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(List<UsageStatistic> newList) {
        DiffUtilCallback diffUtilCallback = new DiffUtilCallback(this.list, newList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilCallback);
        diffResult.dispatchUpdatesTo(this);
        this.list.clear();
        this.list.addAll(newList);
    }

    public void clearAdapter() {
        updateList(new ArrayList<UsageStatistic>());
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView appIconImageView;
        private TextView appNameTextView;
        private TextView startTimeTextview;
        private TextView endTimeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appIconImageView = itemView.findViewById(R.id.app_icon_image_view);
            appNameTextView = itemView.findViewById(R.id.app_name_textview);
            startTimeTextview = itemView.findViewById(R.id.start_time_value);
            endTimeTextView = itemView.findViewById(R.id.end_time_value);
        }

        public void bindValues(UsageStatistic stat) {
            Glide.with(context)
                    .load(stat.getIcon())
                    .into(appIconImageView);
            appNameTextView.setText(stat.getName());
            startTimeTextview.setText(stat.getStartTime());
            endTimeTextView.setText(stat.getEndTime());
        }
    }

    class DiffUtilCallback extends DiffUtil.Callback {

        List<UsageStatistic> oldList;
        List<UsageStatistic> newList;

        public DiffUtilCallback(List<UsageStatistic> oldList, List<UsageStatistic> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition) == newList.get(newItemPosition);
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            UsageStatistic oldItem = oldList.get(oldItemPosition);
            UsageStatistic newItem = newList.get(newItemPosition);
            return oldItem.equals(newItem);
        }
    }
}
