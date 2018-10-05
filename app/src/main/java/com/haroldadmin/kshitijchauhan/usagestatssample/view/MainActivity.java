package com.haroldadmin.kshitijchauhan.usagestatssample.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import com.google.android.material.tabs.TabLayout;
import com.haroldadmin.kshitijchauhan.usagestatssample.R;
import com.haroldadmin.kshitijchauhan.usagestatssample.adapter.PagerAdapter;
import com.haroldadmin.kshitijchauhan.usagestatssample.viewmodel.MainViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {

    MainViewModel mainViewModel;
    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabs;
    AlertDialog alertDialog;
    FragmentPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.my_toolbar);
        viewPager = findViewById(R.id.viewpager);
        tabs = findViewById(R.id.tab_layout);

        setSupportActionBar(toolbar);

        mainViewModel = ViewModelProviders
                .of(this)
                .get(MainViewModel.class);

        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabs.setupWithViewPager(viewPager);

        alertDialog = new AlertDialog.Builder(this)
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
                .create();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainViewModel.checkForPermission(this);
        if (!mainViewModel.getPermissionStatus().getValue()) {
            askForUsageStatsPermission();
        }
    }

    private void askForUsageStatsPermission() {
        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }
}
