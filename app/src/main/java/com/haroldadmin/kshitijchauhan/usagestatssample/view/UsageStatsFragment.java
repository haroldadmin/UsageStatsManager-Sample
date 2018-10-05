package com.haroldadmin.kshitijchauhan.usagestatssample.view;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.haroldadmin.kshitijchauhan.usagestatssample.R;
import com.haroldadmin.kshitijchauhan.usagestatssample.adapter.UsageStatsAdapter;
import com.haroldadmin.kshitijchauhan.usagestatssample.model.UsageStatistic;
import com.haroldadmin.kshitijchauhan.usagestatssample.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;


public class UsageStatsFragment extends Fragment {

    private MainViewModel mainViewModel;
    private UsageStatsAdapter adapter;
    private RecyclerView statsRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Snackbar refreshingSnackbar;

    public UsageStatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_usage_stats, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainViewModel = ViewModelProviders
                .of(getActivity())
                .get(MainViewModel.class);

        mainViewModel.loadUsageStats();

        mainViewModel.getStatsLoadingStatus()
                .observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean isLoading) {
                        swipeRefreshLayout.setRefreshing(isLoading);
                        if (!isLoading) {
                            refreshingSnackbar.dismiss();
                        }
                    }
                });

        mainViewModel.getUsageStatsList()
                .observe(this, new Observer<List<UsageStatistic>>() {
                    @Override
                    public void onChanged(List<UsageStatistic> usageStatistics) {
                        adapter.updateList(usageStatistics);
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        statsRecyclerView = view.findViewById(R.id.stats_recycler_view);

        adapter = new UsageStatsAdapter(new ArrayList<UsageStatistic>(), getContext(), Glide.with(this));
        statsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        statsRecyclerView.setAdapter(adapter);
        statsRecyclerView.setNestedScrollingEnabled(false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.list_divider));
        statsRecyclerView.addItemDecoration(dividerItemDecoration);

        refreshingSnackbar = Snackbar.make(view, "This might take some time...", Snackbar.LENGTH_INDEFINITE);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshingSnackbar.show();
                adapter.clearAdapter();
                mainViewModel.loadUsageStats();
            }
        });
    }
}
