package com.shahul3d.indiasatelliteweather.helpers;

import com.shahul3d.indiasatelliteweather.events.DownloadProgressUpdateEvent;
import com.shahul3d.indiasatelliteweather.events.DownloadStatusEvent;

import de.greenrobot.event.EventBus;

public class MAPDownloadBroadcastHelper {
    EventBus bus;

    public MAPDownloadBroadcastHelper(EventBus eventBus) {
        this.bus = eventBus;
    }

    public void broadcastDownloadProgress(int mapType, int mapID, int downloadedPercentage) {
        bus.post(new DownloadProgressUpdateEvent(mapType, mapID, downloadedPercentage));
    }

    public void broadcastDownloadStatus(int mapType, int mapID, boolean status) {
        bus.post(new DownloadStatusEvent(mapType, mapID, status));
    }
}
