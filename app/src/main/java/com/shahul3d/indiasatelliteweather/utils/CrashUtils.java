package com.shahul3d.indiasatelliteweather.utils;

import com.crashlytics.android.Crashlytics;
import com.noveogroup.android.log.Log;

public class CrashUtils {
    public static void trackException(String log, Exception e) {
        if (log != null && !log.isEmpty()) {
            Log.e(log);
            Crashlytics.log(log);
        }
        if (e != null) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }
}
