package com.shahul3d.indiasatelliteweather.events;

public class TestEvent {
    public String data;

    public TestEvent(String tempData) {
        data = tempData;
    }

    public String getData() {
        return data;
    }
}