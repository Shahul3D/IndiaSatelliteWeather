package com.shahul3d.indiasatelliteweather.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.noveogroup.android.log.Log;
import com.shahul3d.indiasatelliteweather.events.DownloadCompletedEvent;
import com.shahul3d.indiasatelliteweather.events.DownloadRequestEvent;
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

    public void onEvent(DownloadRequestEvent downloadRequest) {
        String mapType = downloadRequest.getMapTypeToDownload();
        downloadMap(mapType);
    }

    @Background
    private void downloadMap(String mapType) {
        //download logic here...
        publishDownloadComplete(mapType);
    }

    //Notify the Views to refresh to get the updated map.
    private void publishDownloadComplete(String mapType) {
        bus.post(new DownloadCompletedEvent(mapType));
    }

    public void onEvent(TestEvent event) {
        Log.a("Service  got the message: " + event.getData());
    }
}
