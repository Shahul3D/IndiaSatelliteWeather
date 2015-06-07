package com.shahul3d.indiasatelliteweather.utils;

import android.content.Context;
import android.os.Environment;
import android.support.v4.content.ContextCompat;

import com.shahul3d.indiasatelliteweather.controllers.WeatherApplication;

import java.io.File;

public class StorageUtils {
    public static File getAppSpecificFolder() {
        Context context = WeatherApplication.getContext();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // External directory
            File[] files = ContextCompat.getExternalFilesDirs(context, null);

            for (File file : files) {
                if (file != null && file.exists() && file.canRead() && file.canWrite()) {
                    return file;
                }
            }
            return context.getFilesDir();
        } else {
            // Internal directory
            return context.getFilesDir();
        }
    }

    public static File getAppSpecificCacheFolder() {
        Context context = WeatherApplication.getContext();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // External cache directory
            File[] files = ContextCompat.getExternalCacheDirs(context);
            for (File file : files) {
                if (file != null && file.exists() && file.canRead() && file.canWrite()) {
                    return file;
                }
            }
            return context.getCacheDir();
        } else {
            // Internal cache directory
            return context.getCacheDir();
        }
    }

    public static void createNoMediaFile() {
        final String NOMEDIA = " .nomedia";
        try {
            File storageLocation = getAppSpecificFolder();
//            if (storageLocation.mkdir()) {
            if (storageLocation.exists()) {
                File nomediaFile = new File(getAppSpecificFolder() + File.separator + NOMEDIA);
                if (!nomediaFile.exists()) {
                    nomediaFile.createNewFile();
                }
            }
//            }
        } catch (Exception e) {
            CrashUtils.trackException("Error while creating noMedia file", e);
        }

    }

    public static boolean fileExists(String filename) {
        File file = new File(filename);
        if (file == null || !file.exists()) {
            return false;
        }
        return true;
    }
}
