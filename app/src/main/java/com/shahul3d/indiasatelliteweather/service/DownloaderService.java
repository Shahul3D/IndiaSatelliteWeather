package com.shahul3d.indiasatelliteweather.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.noveogroup.android.log.Log;
import com.shahul3d.indiasatelliteweather.events.TestEvent;

import org.androidannotations.annotations.EService;

import de.greenrobot.event.EventBus;

@EService
public class DownloaderService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        Log.a("Service On Create");
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        Log.a("Service On Destroy");

        super.onDestroy();
    }

    public void onEvent(TestEvent event) {
        Log.a("Service  got the message: " + event.getData());
    }
}
