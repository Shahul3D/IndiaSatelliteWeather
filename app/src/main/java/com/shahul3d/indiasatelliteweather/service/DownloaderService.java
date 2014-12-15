package com.shahul3d.indiasatelliteweather.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.noveogroup.android.log.Log;
import com.shahul3d.indiasatelliteweather.data.AppConstants;
import com.shahul3d.indiasatelliteweather.events.DownloadCompletedEvent;
import com.shahul3d.indiasatelliteweather.events.TestEvent;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EService;

import de.greenrobot.event.EventBus;

@EService
public class DownloaderService extends Service {
    EventBus bus = EventBus.getDefault();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bus.register(this);
        Log.a("Service On Create");
    }

    @Override
    public void onDestroy() {
        bus.unregister(this);
        Log.a("Service On Destroy");

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String requestedMapType = intent.getStringExtra(AppConstants.DOWNLOAD_INTENT_NAME);
            downloadMap(requestedMapType);
        }

        // NOT_STICKY: No need to restart the service if it get killed by user or by system.
        return START_NOT_STICKY;
    }

    @Background
    private void downloadMap(String mapType) {
        //TODO: to check the same map type is already downloading.

//        try {
//            URL map_url = new URL(AppConstants.MAP_URL.get(mapType));
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
        //download logic here...
        publishDownloadComplete(mapType);
    }

    //Notify the Views to refresh to get the updated map.
    private void publishDownloadComplete(String mapType) {
        bus.post(new DownloadCompletedEvent(mapType));
    }

    //TODO: Test event. to be removed.
    public void onEvent(TestEvent event) {
        Log.a("Service  got the message: " + event.getData());
    }
}
