package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyBackgroundService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Move your background logic from DashboardActivity's onCreate here.
        // You can also remove DashboardActivity's background logic.

        return START_STICKY; // If you want the service to be restarted if it's killed.
    }
}
