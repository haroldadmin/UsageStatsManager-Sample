package com.haroldadmin.kshitijchauhan.usagestatssample;

import android.graphics.drawable.Drawable;

import java.util.Objects;

public class UsageStatistic {

    public UsageStatistic(String name, String startTime, String endTime, Drawable icon) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.icon = icon;
    }

    private String name;
    private String startTime;
    private String endTime;
    private Drawable icon;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsageStatistic)) return false;
        UsageStatistic statistic = (UsageStatistic) o;
        return Objects.equals(name, statistic.name) &&
                Objects.equals(startTime, statistic.startTime) &&
                Objects.equals(endTime, statistic.endTime) &&
                Objects.equals(icon, statistic.icon);
    }
}
