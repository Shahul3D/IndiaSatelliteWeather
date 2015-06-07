package com.shahul3d.indiasatelliteweather.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.shahul3d.indiasatelliteweather.controllers.WeatherApplication;
import com.shahul3d.indiasatelliteweather.data.AppConstants;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.util.concurrent.TimeUnit;

public class HttpClient {
    private static OkHttpClient okHttpClient;

    public enum CacheType {
        NO_CACHE, USE_CACHE, FORCE_CACHE
    }

    private HttpClient() {
        okHttpClient = getInstance();
    }

    public static synchronized OkHttpClient getInstance() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
            configureDefaultTimeouts();
            initializeHttpCache();
            //TODO: Stetho  library should be included.
//            okHttpClient.networkInterceptors().add(new StethoInterceptor());
        }
        return okHttpClient;
    }

    private static void configureDefaultTimeouts() {
        okHttpClient.setConnectTimeout(AppConstants.HTTP_DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        okHttpClient.setReadTimeout(AppConstants.HTTP_DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        okHttpClient.setWriteTimeout(AppConstants.HTTP_DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
    }

    private static void initializeHttpCache() {
        try {
            int cacheSize = 10 * 1024 * 1024; // 10 MiB
            Cache responseCache = new Cache(WeatherApplication.getContext().getCacheDir(), cacheSize);
            okHttpClient.setCache(responseCache);
        } catch (Exception e) {
            CrashUtils.trackException("Can't set HTTP cache", e);
        }
    }

    public static Request generateRequest(String url) {
        return generateRequest(url, null);
    }

    public static Request generateRequest(String url, CacheType cacheType) {
        Request.Builder builder = new Request.Builder().url(url);
        if (cacheType == CacheType.USE_CACHE) {
            //Using cache, so the same call wont happen again for next 1 day.
            builder.cacheControl(new CacheControl.Builder()
                    .maxStale(1, TimeUnit.DAYS)
                    .build());
        }
        return builder.build();
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) WeatherApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        }
        return false;
    }
}
