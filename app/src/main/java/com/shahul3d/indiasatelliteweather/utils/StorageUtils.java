package com.shahul3d.indiasatelliteweather.utils;

import org.androidannotations.annotations.EBean;

import java.io.File;

@EBean
public class StorageUtils {
    public boolean fileExists(String filename) {
        File file = new File(filename);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }
}
