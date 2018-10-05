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
import com.haroldadmin.kshitijchauhan.usagestatssample.adapter.UsageEventAdapter;
import com.haroldadmin.kshitijchauhan.usagestatssample.model.UsageEvent;
import com.haroldadmin.kshitijchauhan.usagestatssample.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class UsageEventFragment extends Fragment {

    private MainViewModel mainViewModel;
    private UsageEventAdapter adapter;
    private RecyclerView eventsRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Snackbar refreshingSnackbar;

    public UsageEventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_usage_event, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainViewModel = ViewModelProviders
                .of(getActivity())
                .get(MainViewModel.class);

        mainViewModel.loadUsageEvents();

        mainViewModel.getUsageEventsList()
                .observe(this, new Observer<List<UsageEvent>>() {
                    @Override
                    public void onChanged(List<UsageEvent> usageEvents) {
                        adapter.updateList(usageEvents);
                    }
                });

        mainViewModel.getEventsLoadingStatus()
                .observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean isLoading) {
                        swipeRefreshLayout.setRefreshing(isLoading);
                        if (!isLoading) {
                            refreshingSnackbar.dismiss();
                        }
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventsRecyclerView = view.findViewById(R.id.events_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.events_swipe_refresh_layout);

        adapter = new UsageEventAdapter(new ArrayList<UsageEvent>(), Glide.with(this));
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventsRecyclerView.setAdapter(adapter);
        eventsRecyclerView.setNestedScrollingEnabled(false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.list_divider));
        eventsRecyclerView.addItemDecoration(dividerItemDecoration);

        refreshingSnackbar = Snackbar.make(view, "Fetching Usage Events can take a lot of time", Snackbar.LENGTH_INDEFINITE);

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
