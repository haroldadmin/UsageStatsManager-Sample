package com.haroldadmin.kshitijchauhan.usagestatssample.viewmodel;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.Application;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.haroldadmin.kshitijchauhan.usagestatssample.AppExecutors;
import com.haroldadmin.kshitijchauhan.usagestatssample.model.UsageEvent;
import com.haroldadmin.kshitijchauhan.usagestatssample.model.UsageStatistic;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static android.os.Process.myUid;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = "MainViewModel";

    private MutableLiveData<Boolean> isPermissionGranted;
    private MutableLiveData<Boolean> isStatsListLoading;
    private MutableLiveData<Boolean> isEventsListLoading;
    private MutableLiveData<List<UsageStatistic>> usageStatsList;
    private MutableLiveData<List<UsageEvent>> usageEventsList;
    private DisposableObserver<UsageEvent> usageEventDisposableObserver;
    private DisposableObserver<UsageStatistic> usageStatisticDisposableObserver;
    private CompositeDisposable compositeDisposable;

    private UsageStatsManager usageStatsManager;
    private PackageManager packageManager;
    private long startTime;
    private long endTime;

    @SuppressLint("WrongConstant")
    public MainViewModel(@NonNull Application application) {
        super(application);
        isStatsListLoading = new MutableLiveData<>();
        isEventsListLoading = new MutableLiveData<>();
        isPermissionGranted = new MutableLiveData<>();
        usageStatsList = new MutableLiveData<>();
        usageEventsList = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
        packageManager = getApplication().getPackageManager();

        // Context.USAGE_STATS_SERVICE was added in API 22
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            usageStatsManager = (UsageStatsManager) getApplication().getSystemService(Context.USAGE_STATS_SERVICE);
        } else {
            usageStatsManager = (UsageStatsManager) getApplication().getSystemService("usagestats");
        }

        if (usageStatsManager == null) {
            Log.d(TAG, "UsageStatsManager is null, maybe the Usage Access permission has not been granted to this app!");
        }
    }

    public LiveData<Boolean> getStatsLoadingStatus() {
        return isStatsListLoading;
    }

    public LiveData<Boolean> getEventsLoadingStatus() {
        return isEventsListLoading;
    }

    public LiveData<List<UsageStatistic>> getUsageStatsList() {
        return usageStatsList;
    }

    public LiveData<List<UsageEvent>> getUsageEventsList() {
        return usageEventsList;
    }

    public LiveData<Boolean> getPermissionStatus() {
        return isPermissionGranted;
    }

    public void loadUsageStats() {

        Calendar cal = Calendar.getInstance();
        endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_WEEK, -1);
        startTime = cal.getTimeInMillis();

        // Querying usage stats (without aggregation) may be a potentially long running operation
        AppExecutors.workExecutor.submit(new Runnable() {
            @Override
            public void run() {
                isStatsListLoading.postValue(true);
                List<UsageStats> list = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
                MainViewModel.this.convertUsageStatsToUsageStatistic(list);
            }
        });
    }

    private void convertUsageStatsToUsageStatistic(List<UsageStats> list) {

        String timeFormat = "HH:mm:ss a";
        final SimpleDateFormat formatter = new SimpleDateFormat(timeFormat);

        Observable<UsageStatistic> usageStatisticsObservable = Observable.fromIterable(list)
                .filter(new Predicate<UsageStats>() {
                    @Override
                    public boolean test(UsageStats usageStats) {
                        return (packageManager.getLaunchIntentForPackage(usageStats.getPackageName()) != null) && (usageStats.getLastTimeStamp() != usageStats.getFirstTimeStamp());
                    }
                })
                .sorted(new Comparator<UsageStats>() {
                    @Override
                    public int compare(UsageStats usageStats, UsageStats t1) {
                        return (int) (t1.getLastTimeUsed() - usageStats.getLastTimeUsed());
                    }
                })
                .map(new Function<UsageStats, UsageStatistic>() {
                    @Override
                    public UsageStatistic apply(UsageStats usageStats) {
                        ApplicationInfo info;
                        try {
                            info = packageManager.getApplicationInfo(usageStats.getPackageName(), 0);
                        } catch (PackageManager.NameNotFoundException e) {
                            info = null;
                        }
                        if (info != null) {
                            String name = (String) packageManager.getApplicationLabel(info);
                            String startTime = formatter.format(new Date(usageStats.getLastTimeUsed()));
                            String endTime = formatter.format(new Date(usageStats.getLastTimeUsed() + usageStats.getTotalTimeInForeground()));
                            Drawable icon = packageManager.getApplicationIcon(info);
                            return new UsageStatistic(name, startTime, endTime, icon);
                        } else {
                            return new UsageStatistic("Unknown", "Unknown", "Unknown", null);
                        }
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation());

        usageStatisticDisposableObserver = new DisposableObserver<UsageStatistic>() {

            List<UsageStatistic> usageStatisticList = new ArrayList<>();

            @Override
            public void onNext(UsageStatistic usageStatistic) {
                usageStatisticList.add(usageStatistic);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("MainViewModel", "onError: status of list = " + usageStatisticList.toString());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: usageStatisticList size = " + usageStatisticList.size());
                usageStatsList.postValue(usageStatisticList);
                isStatsListLoading.postValue(false);
            }
        };

        compositeDisposable.add(usageStatisticsObservable.subscribeWith(usageStatisticDisposableObserver));
    }

    public void loadUsageEvents() {

        Calendar cal = Calendar.getInstance();
        endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_WEEK, -1);
//        startTime = cal.getTimeInMillis();

        startTime = endTime - (1000 * 60 * 60);

        final List<UsageEvents.Event> eventList = new ArrayList<>();
        AppExecutors.workExecutor.submit(new Runnable() {
            @Override
            public void run() {
                isEventsListLoading.postValue(true);
                UsageEvents events = usageStatsManager.queryEvents(startTime, endTime);
                while (events.hasNextEvent()) {
                    UsageEvents.Event event = new UsageEvents.Event();
                    events.getNextEvent(event);
                    eventList.add(event);
                }
                MainViewModel.this.convertUsageEventsToUsageEvent(eventList);
            }
        });

    }

    private void convertUsageEventsToUsageEvent(List<UsageEvents.Event> list) {

        String timeFormat = "HH:mm:ss a dd/MM";
        final SimpleDateFormat formatter = new SimpleDateFormat(timeFormat);

        Observable<UsageEvent> observable = Observable.fromIterable(list)
                .filter(new Predicate<UsageEvents.Event>() {
                    @Override
                    public boolean test(UsageEvents.Event event) {
                        return event.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND || event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND;
                    }
                })
                .sorted(new Comparator<UsageEvents.Event>() {
                    @Override
                    public int compare(UsageEvents.Event event, UsageEvents.Event t1) {
                        return (int) (t1.getTimeStamp() - event.getTimeStamp());
                    }
                })
                .map(new Function<UsageEvents.Event, UsageEvent>() {
                    @Override
                    public UsageEvent apply(UsageEvents.Event event) {
                        ApplicationInfo info;
                        try {
                            info = packageManager.getApplicationInfo(event.getPackageName(), PackageManager.GET_META_DATA);
                        } catch (PackageManager.NameNotFoundException e) {
                            info = null;
                        }
                        if (info != null) {
                            String name = (String) packageManager.getApplicationLabel(info);
                            String eventTime = formatter.format(new Date(event.getTimeStamp()));
                            String eventType = event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND ? "Move to Foreground" : "Move to Background";
                            Drawable icon = packageManager.getApplicationIcon(info);
                            return new UsageEvent(name, eventTime, eventType, icon);
                        } else {
                            return new UsageEvent("Unknown", "Unknown", "Unknown", null);
                        }
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation());

        usageEventDisposableObserver = new DisposableObserver<UsageEvent>() {

            List<UsageEvent> usageEvents = new ArrayList<>();

            @Override
            public void onNext(UsageEvent usageEvent) {
                usageEvents.add(usageEvent);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("MainViewModel", "onError: status of events list = " + usageEvents);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: usageEventsList size = " + usageEvents.size());
                usageEventsList.postValue(usageEvents);
                isEventsListLoading.postValue(false);
            }
        };

        compositeDisposable.add(observable.subscribeWith(usageEventDisposableObserver));
    }

    public void checkForPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, myUid(), context.getPackageName());
        isPermissionGranted.setValue(mode == AppOpsManager.MODE_ALLOWED);
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }
}
