package com.shahul3d.indiasatelliteweather.events;

public class DownloadCompletedEvent {
    public String mapType;

    public DownloadCompletedEvent(String mapTypeToDownload) {
        mapType = mapTypeToDownload;
    }

    public String getDownloadedMapType() {
        return mapType;
    }
}