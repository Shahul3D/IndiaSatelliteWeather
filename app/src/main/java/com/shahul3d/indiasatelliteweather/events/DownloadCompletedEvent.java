package com.shahul3d.indiasatelliteweather.events;

public class DownloadCompletedEvent {
    public int mapID;

    public DownloadCompletedEvent(int mapIDToDownload) {
        mapID = mapIDToDownload;
    }

    public int getDownloadedMapType() {
        return mapID;
    }
}