package com.haroldadmin.kshitijchauhan.usagestatssample;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {

    MainViewModel mainViewModel;
    UsageStatsAdapter adapter;
    RecyclerView statsRecyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        statsRecyclerView = findViewById(R.id.stats_recycler_view);

        adapter = new UsageStatsAdapter(new ArrayList<UsageStatistic>(), this);
        statsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        statsRecyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.list_divider));
        statsRecyclerView.addItemDecoration(dividerItemDecoration);


        mainViewModel = ViewModelProviders
                .of(this)
                .get(MainViewModel.class);

        mainViewModel.checkForPermission(this);
        mainViewModel.loadUsageStats();

        mainViewModel.getUsageStatsList()
                .observe(this, new Observer<List<UsageStatistic>>() {
                    @Override
                    public void onChanged(List<UsageStatistic> usageStatistics) {
                        adapter.updateList(usageStatistics);
                    }
                });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clearAdapter();
                mainViewModel.loadUsageStats();
            }
        });

        mainViewModel.getLoadingStatus()
                .observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        swipeRefreshLayout.setRefreshing(aBoolean);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainViewModel.checkForPermission(this);
        mainViewModel.getPermissionStatus()
                .observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        if (!aBoolean) askForUsageStatsPermission();
                    }
                });
    }

    private void askForUsageStatsPermission() {
        new AlertDialog.Builder(this)
                .setTitle("Permission not granted")
                .setMessage("Please allow us to access Usage Stats from settings")
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(true)
                .show();
    }
}
