package com.callpneck.Services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class job extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {/*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!isMyServiceRunning(this, LocationService.class)) {
                LocationService.enqueueWork(this, LocationService.class, 1000, new Intent());
            }
        } else {
            if (!isMyServiceRunning(this, LocationService.class)) {
                Intent service = new Intent(this, LocationService.class);
                this.startService(service);
            }
        }

        return true;*/
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        jobFinished(params, true);
        return false;
    }
}