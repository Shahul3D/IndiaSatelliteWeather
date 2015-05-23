/*
 * ******************************************************************************
 *  * Copyright (c) 2015.  Shahul Hameed.
 *  *
 *  * Licensed under GNU GENERAL PUBLIC LICENSE;
 *  * you may not use this file except in compliance with the License.
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  ******************************************************************************
 */

package com.shahul3d.indiasatelliteweather.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.noveogroup.android.log.Log;
import com.shahul3d.indiasatelliteweather.R;
import com.shahul3d.indiasatelliteweather.data.AppConstants;
import com.shahul3d.indiasatelliteweather.events.DownloadProgressUpdateEvent;
import com.shahul3d.indiasatelliteweather.events.DownloadStatusEvent;
import com.shahul3d.indiasatelliteweather.utils.PreferenceUtil;
import com.shahul3d.indiasatelliteweather.utils.StorageUtils;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;

@EService
public class DownloaderService extends Service {
    EventBus bus = EventBus.getDefault();
    @Bean
    StorageUtils storageUtils;
    @Bean
    PreferenceUtil preferenceUtil;

    OkHttpClient httpClient;

    Boolean activeDownloadsList[] = new Boolean[AppConstants.MAP_URL.size()];
    private SharedPreferences preference_General = null;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Service On Create");
        bus.register(this);
        initializeHTTPClient();
        preference_General = this.getSharedPreferences("BackgroundPreference", Activity.MODE_PRIVATE);
    }

    @Override
    public void onDestroy() {
        Log.d("Service On Destroy");
        bus.unregister(this);
        super.onDestroy();
    }

    void initializeHTTPClient() {
        httpClient = new OkHttpClient();
        try {
            int cacheSize = 10 * 1024 * 1024; // 10 MiB
            Cache responseCache = new Cache(getApplicationContext().getCacheDir(), cacheSize);
            httpClient.setCache(responseCache);
        } catch (Exception e) {
            trackException("Can't set HTTP cache", e);
        }
        httpClient.setReadTimeout(90, TimeUnit.SECONDS);
        httpClient.setConnectTimeout(30, TimeUnit.SECONDS);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int startOption = START_NOT_STICKY;
        if (intent == null) {
            return startOption;
        }

        int mapID = intent.getIntExtra(AppConstants.DOWNLOAD_INTENT_NAME, 0);
        int mapType = intent.getIntExtra(AppConstants.DOWNLOAD_MAP_TYPE, 0);
        if (!isNetworkAvailable()) {
            Toast.makeText(this, this.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            broadcastDownloadStatus(mapType, mapID, false);
            return startOption;
        }

        if (activeDownloadsList[mapID] != null && activeDownloadsList[mapID]) {
            Log.d("Duplicate download request for the same map type");
            return startOption;
        }

        activeDownloadsList[mapID] = true;
        downloadMap(mapID, mapType);
        // NOT_STICKY: No need to restart the service if it get killed by user or by system.
        return startOption;
    }

    @Background
    public void downloadMap(int mapID, int mapType) {
        //TODO: Check internet
        String mapFileName = AppConstants.getMapType(mapID, mapType);
        String lastModifiedHeader = "";
        Log.d("Download requested for map type: " + mapFileName);
        updateDownloadStatus(mapType, mapID, 0);

        final String URL = AppConstants.MAP_URL.get(mapFileName);

        try {
            Call call = httpClient.newCall(new Request.Builder().url(URL).get().build());
            Response response = call.execute();

            //TODO: These caching headers should be stored on preferences.
//            String eTagHeader = response.header("ETag", "");
            lastModifiedHeader = response.header("Last-Modified", "");

//            Log.d("eTagHeader: " + eTagHeader);
            Log.d("last modified: " + lastModifiedHeader);
//            Log.d("\nN/W counts: " + httpClient.getCache().getNetworkCount() + "\nReq Counts: " + httpClient.getCache().getRequestCount() + "\nCache Hits: " + httpClient.getCache().getHitCount());

            if (response.code() == 200) {
                InputStream inputStream = null;
                ByteArrayOutputStream outArrrayIPStream = null;
                try {
                    inputStream = response.body().byteStream();
                    outArrrayIPStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int statusUpdateTrigger = 0;
                    long downloaded = 0;
                    long responseSize = response.body().contentLength();

                    Log.d("Total size: " + responseSize);

                    for (int count; (count = inputStream.read(buffer)) != -1; ) {
                        outArrrayIPStream.write(buffer, 0, count);
                        downloaded += count;
                        statusUpdateTrigger++;
//                        Log.d(String.format("%d / %d", downloaded, responseSize));

                        //Update download status
                        if (statusUpdateTrigger > AppConstants.STATUS_UPDATE_THRESHOLD) {
//                            Thread.sleep(3000);
                            statusUpdateTrigger = 0;
                            Long downloadedPercent = downloaded * AppConstants.MAX_DOWNLOAD_PROGRESS / responseSize;
                            Log.d("downloaded percent: " + downloadedPercent);
                            updateDownloadStatus(mapType, mapID, downloadedPercent.intValue());
                        }
                    }

                    byte[] responseImage = outArrrayIPStream.toByteArray();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inMutable = true;
                    Bitmap bmp = BitmapFactory.decodeByteArray(responseImage, 0, responseImage.length, options);

                    bmp = removeMapBorders(mapFileName, bmp);

                    //Save downloaded image for offline use.
                    saveDownloadedMap(mapFileName, bmp);
                    updateDownloadStatus(mapType, mapID, 100);
                } catch (IOException ignore) {
                    trackException("MAP download & parser error", ignore);
                    broadcastDownloadStatus(mapType, mapID, false);
                    //Error on fetching & organizing the binary data.
                    return;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outArrrayIPStream != null) {
                        outArrrayIPStream.close();
                    }
                }
            } else {
                trackException("res code other than 200: " + response.code(), null);
                Log.d("res code other than 200 " + response.code());
                return;
            }
        } catch (IOException e) {
            trackException("MAP download connection error", e);
            broadcastDownloadStatus(mapType, mapID, false);
            return;
        }

        preferenceUtil.updateLastModifiedTime(preference_General, mapFileName, lastModifiedHeader);
        broadcastDownloadStatus(mapType, mapID, true);
    }

    private Bitmap removeMapBorders(String mapType, Bitmap bmp) {

        //Try to remove unwanted parts from MAP. return the original MAP in case of any errors.
        try {
            //Trim the unwanted area from the Ultra Violet Map.
            if (mapType.equals(AppConstants.MAP_UV)) {
                return Bitmap.createBitmap(bmp, 110, 230, 800, 800);
            } else if (mapType.equals(AppConstants.MAP_HEAT)) {
                return Bitmap.createBitmap(bmp, 0, 180, 1250, 1400);
            }
        } catch (Exception e) {
            Crashlytics.log("trim MAP Error");
            Crashlytics.setString("MapType", mapType);
            try {
                Crashlytics.setInt("mapWidth", bmp.getWidth());
                Crashlytics.setInt("mapHeight", bmp.getHeight());
            } catch (Exception e1) {
                Crashlytics.logException(e1);
            }
            Crashlytics.logException(e);
        }
        return bmp;
    }

    private void saveDownloadedMap(String mapType, Bitmap bmp) throws IOException {
        File temp_file = new File(Environment.getExternalStorageDirectory() + File.separator + mapType + "_temp.jpg");
        FileOutputStream fileOutStream = new FileOutputStream(temp_file.getPath());

        // Compression Quality set to 100. ie. NO COMPRESSION.
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, fileOutStream);
        fileOutStream.flush();
        fileOutStream.close();

        boolean success = temp_file.renameTo(new File(Environment.getExternalStorageDirectory(), mapType + ".jpg"));
        Log.d("Map  saved to: " + temp_file.getAbsolutePath() + ". Overwritten? = " + success);
    }

    public void updateDownloadStatus(int mapType, int mapID, int downloadedPercentage) {
        bus.post(new DownloadProgressUpdateEvent(mapType, mapID, downloadedPercentage));
    }

    //Notify the Views to refresh to get the updated map.
    public void broadcastDownloadStatus(int mapType, int mapID, boolean status) {
        activeDownloadsList[mapID] = false;
        bus.post(new DownloadStatusEvent(mapType, mapID, status));
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        }
        return false;
    }


    public static void trackException(String log, Exception e) {
        Crashlytics.log(log);
        if (e != null) {
            Crashlytics.logException(e);
        }
    }

    //TODO: Test event. to be removed.
    public void onEvent(int dummyEvent) {
    }
}
