package com.shahul3d.indiasatelliteweather.utils;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.shahul3d.indiasatelliteweather.R;

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
		Log.v("IndiaSatelliteWeather", msg);
	}
	
	public static void trackException(String log, Exception e)
	{
		Crashlytics.log(log);
		Crashlytics.logException(e);
	}
	
	public static long getLastModifiedTime(SharedPreferences preference_General, Context context, String mapType) {
		if(preference_General == null)
		{
			preference_General = context.getSharedPreferences("BackgroundPreference", Activity.MODE_PRIVATE);
		}
		
		long lastModTime = 0;
		if (preference_General != null) {
			lastModTime = preference_General.getLong(mapType+"_update_time", 0l);
		}
		return lastModTime;
	}
	
	public static void updateLastModifiedTime(SharedPreferences preference_General, Context context, String mapType, final long timeInms) {
		if(preference_General == null)
		{
			preference_General = context.getSharedPreferences("BackgroundPreference", Activity.MODE_PRIVATE);
		}
		
		SharedPreferences.Editor editor = preference_General.edit();
		editor.putLong(mapType+"_update_time", timeInms);
		editor.commit();
	}
	
	public static String getFormattedLastModifiedTime(SharedPreferences preference_General, Context context, String mapType) {
		//TODO: need to handle with the mayType
		String formattedTime = "";
		long lastModTime = getLastModifiedTime(preference_General,null,mapType);
	
		if (lastModTime > 0) {
			Calendar indianTime = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
			indianTime.setTimeInMillis(lastModTime);
			formattedTime = context.getString(R.string.updated_as_on)+ String.format(Locale.US, "%tb %te, %tl:%tM %tp ",	indianTime, indianTime, indianTime, indianTime,indianTime);
			//For Debug. date with seconds
			// formattedTime = "As on "+ String.format(Locale.US,"%tb %te, %tl:%tM:%tS %tp  ",indianTime, indianTime, indianTime,indianTime,indianTime,indianTime);
			indianTime = null;
		}
		return formattedTime;
	}
	
	public static boolean isFileExists(String filePath) {
		try {
			File file = new File(filePath);
			if (file.exists()) {
				return true;
			} else
				return false;
		} catch (Exception e) {
			trackException("Problem on checing file Existence", e);
			return false;
		}
	}

}