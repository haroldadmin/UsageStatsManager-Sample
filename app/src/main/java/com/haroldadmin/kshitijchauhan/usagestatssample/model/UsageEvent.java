package com.haroldadmin.kshitijchauhan.usagestatssample.model;

import android.graphics.drawable.Drawable;

import java.util.Objects;

public class UsageEvent {

    private String appName;
    private String eventTime;
    private String eventType;
    private Drawable icon;

    public UsageEvent(String appName, String eventTime, String eventType, Drawable icon) {
        this.appName = appName;
        this.eventTime = eventTime;
        this.eventType = eventType;
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "UsageEvent{" +
                "appName='" + appName + '\'' +
                ", eventTime='" + eventTime + '\'' +
                ", eventType=" + eventType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsageEvent)) return false;
        UsageEvent that = (UsageEvent) o;

        return appName.equals(that.appName) &&
                eventTime.equals(that.eventTime) &&
                eventType.equals(that.eventType) &&
                Objects.equals(icon, that.icon);
    }
}

