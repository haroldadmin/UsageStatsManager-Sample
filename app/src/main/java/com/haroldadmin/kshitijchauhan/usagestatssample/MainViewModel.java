package com.haroldadmin.kshitijchauhan.usagestatssample;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Application;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.os.Process.myUid;

public class MainViewModel extends AndroidViewModel {

    private MutableLiveData<Boolean> isPermissionGranted;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<List<UsageStatistic>> usageStatsList;
    private long startTime;
    private long endTime;

    public MainViewModel(@NonNull Application application) {
        super(application);
        isLoading = new MutableLiveData<>();
        usageStatsList = new MutableLiveData<>();
        isPermissionGranted = new MutableLiveData<>();
    }

    public LiveData<Boolean> getLoadingStatus() {
        return isLoading;
    }

    public LiveData<List<UsageStatistic>> getUsageStatsList() {
        return usageStatsList;
    }

    public LiveData<Boolean> getPermissionStatus() {
        return isPermissionGranted;
    }

    private void convertToUsageStatistic(List<UsageStats> list) {

        final List<UsageStatistic> usageStatisticList = new ArrayList<>();

        final PackageManager packageManager = getApplication().getPackageManager();
        String timeFormat = "HH:mm a";
        final SimpleDateFormat formatter = new SimpleDateFormat(timeFormat);

        // I just wanted a reason to use RxJava in this project
        Observable<UsageStatistic> observable = Observable.fromIterable(list)
                .map(new Function<UsageStats, UsageStatistic>() {
                    @Override
                    public UsageStatistic apply(UsageStats usageStats) throws Exception {
                        ApplicationInfo info;
                        try {
                            info = packageManager.getApplicationInfo(usageStats.getPackageName(), 0);
                        } catch (PackageManager.NameNotFoundException e) {
                            info = null;
                        }
                        if (info != null) {
                            String name = (String) packageManager.getApplicationLabel(info);
                            String startTime = formatter.format(new Date(usageStats.getFirstTimeStamp()));
                            String endTime = formatter.format(new Date(usageStats.getLastTimeStamp()));
                            Drawable icon = packageManager.getApplicationIcon(info);
                            return new UsageStatistic(name, startTime, endTime, icon);
                        } else {
                            return new UsageStatistic("Unknown", "Unknown", "Unknown", null);
                        }
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation());

        observable.subscribe(new Observer<UsageStatistic>() {
            @Override
            public void onSubscribe(Disposable d) {
                // Do Nothing
            }

            @Override
            public void onNext(UsageStatistic usageStatistic) {
                Log.d("MainViewModel", "onNext on thread " + Thread.currentThread());
                usageStatisticList.add(usageStatistic);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("MainViewModel", "onError: " + e.getLocalizedMessage());
            }

            @Override
            public void onComplete() {
                usageStatsList.postValue(usageStatisticList);
                isLoading.postValue(false);
            }
        });
    }

    @SuppressLint("WrongConstant")
    public void loadUsageStats() {

        final UsageStatsManager usageStatsManager;
        Calendar cal = Calendar.getInstance();
        endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_WEEK, -1);
        startTime = cal.getTimeInMillis();

        if (isPermissionGranted.getValue()) {
            // Context.USAGE_STATS_SERVICE was added in API 22
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                usageStatsManager = (UsageStatsManager) getApplication().getSystemService(Context.USAGE_STATS_SERVICE);
            } else {
                usageStatsManager = (UsageStatsManager) getApplication().getSystemService("usagestats");
            }

            // Querying usage stats (without aggregation) may be a potentially long running operation
            AppExecutors.workExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    isLoading.postValue(true);
                    List<UsageStats> list = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
                    // Using postValue() here because this code is not on Main thread.
                    convertToUsageStatistic(list);
                }
            });
        }
    }

    public void checkForPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, myUid(), context.getPackageName());
        isPermissionGranted.setValue(mode == AppOpsManager.MODE_ALLOWED);
    }
}
