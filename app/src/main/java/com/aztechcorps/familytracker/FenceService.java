package com.aztechcorps.familytracker;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FenceService extends Service {
    public FenceService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true) {
                        fenceAsync fenceAsync = new fenceAsync(FenceService.this);
                        fenceAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        Thread.sleep(3000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        })).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
