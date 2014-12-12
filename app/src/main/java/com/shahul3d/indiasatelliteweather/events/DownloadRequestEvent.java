package com.shahul3d.indiasatelliteweather.events;

public class DownloadRequestEvent {
    public String mapType;

    public DownloadRequestEvent(String mapTypeToDownload) {
        mapType = mapTypeToDownload;
    }

    public String getMapTypeToDownload() {
        return mapType;
    }
}