package com.haroldadmin.kshitijchauhan.usagestatssample.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.haroldadmin.kshitijchauhan.usagestatssample.R;
import com.haroldadmin.kshitijchauhan.usagestatssample.model.UsageEvent;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class UsageEventAdapter extends RecyclerView.Adapter<UsageEventAdapter.ViewHolder> {

    private List<UsageEvent> list;
    private RequestManager glide;

    public UsageEventAdapter(List<UsageEvent> list, RequestManager glide) {
        this.list = list;
        this.glide = glide;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.usage_event_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindValues(this.list.get(position));
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public void updateList(final List<UsageEvent> newList) {
        DiffUtilCallback diffUtilCallback = new DiffUtilCallback(UsageEventAdapter.this.list, newList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilCallback);
        diffResult.dispatchUpdatesTo(UsageEventAdapter.this);
        list.clear();
        list.addAll(newList);
    }

    public void clearAdapter() {
        updateList(new ArrayList<UsageEvent>());
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView appIconImageView;
        private TextView appNameTextView;
        private TextView eventTimeTextView;
        private TextView eventTypeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appIconImageView = itemView.findViewById(R.id.event_app_icon_imageView);
            appNameTextView = itemView.findViewById(R.id.event_app_name_textview);
            eventTimeTextView = itemView.findViewById(R.id.event_time_value);
            eventTypeTextView = itemView.findViewById(R.id.event_type_value);
        }

        public void bindValues(UsageEvent event) {
            glide.load(event.getIcon()).into(appIconImageView);
            appNameTextView.setText(event.getAppName());
            eventTimeTextView.setText(event.getEventTime());
            eventTypeTextView.setText(event.getEventType());
        }
    }

    class DiffUtilCallback extends DiffUtil.Callback {

        List<UsageEvent> oldList;
        List<UsageEvent> newList;

        public DiffUtilCallback(List<UsageEvent> oldList, List<UsageEvent> newList) {
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
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }

}
