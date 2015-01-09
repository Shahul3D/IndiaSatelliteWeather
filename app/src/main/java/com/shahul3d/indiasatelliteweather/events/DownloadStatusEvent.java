package com.shahul3d.indiasatelliteweather.events;

public class DownloadStatusEvent {
    public int mapID;
    public boolean status;

    public DownloadStatusEvent(int mapID, boolean status) {
        this.mapID = mapID;
        this.status = status;
    }
}