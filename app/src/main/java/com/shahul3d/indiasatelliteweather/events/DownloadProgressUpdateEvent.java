package com.shahul3d.indiasatelliteweather.events;

public class DownloadProgressUpdateEvent {
    public int mapID;
    public int progress;

    public DownloadProgressUpdateEvent(int mapID, int downloadProgress) {
        this.mapID = mapID;
        progress = downloadProgress;
    }

    public int getMapType() {
        return mapID;
    }

    public int getProgress(){
        return progress;
    }
}