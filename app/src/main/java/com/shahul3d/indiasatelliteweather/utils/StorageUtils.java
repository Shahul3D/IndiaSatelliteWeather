package com.shahul3d.indiasatelliteweather.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.io.File;

@EBean
public class StorageUtils {

    @RootContext
    Context context;

    public String getExternalStoragePath() {

        File[] dir = ContextCompat.getExternalFilesDirs(context, null);
        String location = dir[dir.length - 1].toString();
        return location ;
    }
    public boolean fileExists(String filename) {
        File file = new File(filename);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }
}
