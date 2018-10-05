package com.haroldadmin.kshitijchauhan.usagestatssample.adapter;

import com.haroldadmin.kshitijchauhan.usagestatssample.view.UsageEventFragment;
import com.haroldadmin.kshitijchauhan.usagestatssample.view.UsageStatsFragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new UsageStatsFragment();
            case 1:
                return new UsageEventFragment();
            default: return new UsageStatsFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0: return "Usage Statistics";
            case 1: return "Usage Events";
            default: return "Usage Statistics";
        }
    }
}
