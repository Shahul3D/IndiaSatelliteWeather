package com.shahul3d.indiasatelliteweather.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

public class CommonUtils {
	
	public static Boolean isNullorEmpty(String... vars)
	{
		for (String var : vars)
		{
			if (var == null || var.trim().equals(""))
				return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public static boolean storageReady() {
		
		String cardstatus = Environment.getExternalStorageState();
		if (cardstatus.equals(Environment.MEDIA_REMOVED)
				|| cardstatus.equals(Environment.MEDIA_UNMOUNTABLE)
				|| cardstatus.equals(Environment.MEDIA_UNMOUNTED)
				|| cardstatus.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
			return false;
		} else {
			return true;
		}
	}
		
	public static void showToast(Context context, String msg)
	{
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public static void printLog(String msg)
	{
		//Log.v("IndiaSatelliteWeather", msg);
	}
	
	public static void trackException(String log, Exception e)
	{
		Crashlytics.log(log);
		Crashlytics.logException(e);
	}
}