package com.shahul3d.indiasatelliteweather.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;

import com.noveogroup.android.log.Log;
import com.shahul3d.indiasatelliteweather.data.AppConstants;
import com.shahul3d.indiasatelliteweather.events.DownloadCompletedEvent;
import com.shahul3d.indiasatelliteweather.events.TestEvent;
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
    AppConstants appConstants;

    OkHttpClient httpClient;

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
            Log.e("Unable to set http cache");
        }
        httpClient.setReadTimeout(90, TimeUnit.SECONDS);
        httpClient.setConnectTimeout(30, TimeUnit.SECONDS);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String requestedMapType = getMapType(intent.getIntExtra(appConstants.DOWNLOAD_INTENT_NAME, 0));
            Log.d("Download requested for map type: " + requestedMapType);
            downloadMap(requestedMapType);
        }
        // NOT_STICKY: No need to restart the service if it get killed by user or by system.
        return START_NOT_STICKY;
    }

    public String getMapType(int type) {
        String mapType = appConstants.MAP_UV;
        switch (type) {
            case 1:
                mapType = appConstants.MAP_COLOR;
                break;
            case 2:
                mapType = appConstants.MAP_IR;
                break;
            case 3:
                mapType = appConstants.MAP_WIND_FLOW;
                break;
        }
        return mapType;
    }

    @Background
    public void downloadMap(String mapType) {
        //TODO: to check the same map type is already downloading.
        //TODO: Check internet
        final String URL = appConstants.MAP_URL.get(mapType);

        try {
            Call call = httpClient.newCall(new Request.Builder().url(URL).get().build());
            Response response = call.execute();

            //TODO: These caching headers should be stored on preferences.
            String eTagHeader = response.header("ETag", "");
            String lastModifiedHeader = response.header("Last-Modified", "");

            Log.d("eTagHeader: " + eTagHeader);
            Log.d("last modified: " + lastModifiedHeader);
            Log.d("\nN/W counts: " + httpClient.getCache().getNetworkCount() + "\nReq Counts: " + httpClient.getCache().getRequestCount() + "\nCache Hits: " + httpClient.getCache().getHitCount());

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
                        if (statusUpdateTrigger > appConstants.STATUS_UPDATE_THRESHOLD) {
//                            Thread.sleep(3000);
                            statusUpdateTrigger = 0;
                            Log.d("downloaded percent: " + downloaded * appConstants.MAX_DOWNLOAD_PROGRESS / responseSize);
                        }
                    }

                    byte[] responseImage = outArrrayIPStream.toByteArray();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inMutable = true;
                    Bitmap bmp = BitmapFactory.decodeByteArray(responseImage, 0, responseImage.length, options);

                    if (mapType.equals(appConstants.MAP_UV)) {
                        //Trim the unwanted area from the Ultra Violet Map.
                        bmp = Bitmap.createBitmap(bmp, 110, 230, 800, 800);
                    }

                    //Save downloaded image for offline use.
                    saveDownlaodedMap(mapType, bmp);
                } catch (IOException ignore) {
                    //TODO: Exception handling
                    //Error on fetching & organizing the binary data.
                    ignore.printStackTrace();
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
                //TODO: Exception handling
                Log.d("res code other than 200 " + response.code());
                return;
            }
        } catch (IOException e) {
            //TODO: Exception handling
            //Error in download call
            e.printStackTrace();
            return;
        }

        publishDownloadComplete(mapType);
    }

    private void saveDownlaodedMap(String mapType, Bitmap bmp) throws IOException {
        String storagePath = storageUtils.getExternalStoragePath();

        File temp_file = new File(storagePath + File.separator + mapType + "_temp.jpg");
        FileOutputStream fileOutStream = new FileOutputStream(temp_file.getPath());

        // Compression Quality set to 100. ie. NO COMPRESSION.
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, fileOutStream);
        fileOutStream.flush();
        fileOutStream.close();

        boolean success = temp_file.renameTo(new File(storagePath, mapType + ".jpg"));
        Log.d("Map  saved to: " + temp_file.getAbsolutePath() + ". Overwritten? = " + success);
    }

    //Notify the Views to refresh to get the updated map.
    public void publishDownloadComplete(String mapType) {
        bus.post(new DownloadCompletedEvent(mapType));
    }

    //TODO: Test event. to be removed.
    public void onEvent(TestEvent event) {
        Log.d("Service  got the message: " + event.getData());
    }
}
