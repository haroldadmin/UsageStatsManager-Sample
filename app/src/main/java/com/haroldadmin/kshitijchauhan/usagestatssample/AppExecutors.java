package com.haroldadmin.kshitijchauhan.usagestatssample;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutors {

    private AppExecutors() {
        // No one should be allowed to create an object of this class
    }

    public static ExecutorService workExecutor = Executors.newSingleThreadExecutor();

    public static Executor mainThreadExecutor = new Executor() {

        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        @Override
        public void execute(@NonNull Runnable runnable) {
            mainThreadHandler.post(runnable);
        }
    };

}
