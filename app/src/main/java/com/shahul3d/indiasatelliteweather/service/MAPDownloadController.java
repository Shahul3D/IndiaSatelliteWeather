package com.shahul3d.indiasatelliteweather.service;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.noveogroup.android.log.Log;
import com.shahul3d.indiasatelliteweather.R;
import com.shahul3d.indiasatelliteweather.controllers.WeatherApplication;
import com.shahul3d.indiasatelliteweather.data.AppConstants;
import com.shahul3d.indiasatelliteweather.helpers.MAPDownloadBroadcastHelper;
import com.shahul3d.indiasatelliteweather.utils.CrashUtils;
import com.shahul3d.indiasatelliteweather.utils.HttpClient;
import com.shahul3d.indiasatelliteweather.utils.PreferenceUtil;
import com.shahul3d.indiasatelliteweather.utils.StorageUtils;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import de.greenrobot.event.EventBus;

public class MAPDownloadController {
    MAPDownloadBroadcastHelper mapDownloadBroadcastHelper;
    Boolean activeDownloadsList[] = new Boolean[AppConstants.MAP_URL.size()];
    Context context = WeatherApplication.getContext();
    private SharedPreferences preference_General = null;

    public MAPDownloadController(EventBus eventBus) {
        mapDownloadBroadcastHelper = new MAPDownloadBroadcastHelper(eventBus);
        preference_General = WeatherApplication.getContext().getSharedPreferences("BackgroundPreference", Activity.MODE_PRIVATE);
    }

    public void downloadMAPRequest(int mapID, int mapType) {
        if (!performDuplicateDownloadCheck(mapID)) {
            Log.d("Duplicate download request for the same map type");
            return;
        }
        String mapFileName = AppConstants.getMapType(mapID, mapType);
        Log.d("Download initiated for MAP type: " + mapFileName);

        //Marking current map type as in progress.
        activeDownloadsList[mapID] = true;

        //Notifying UI about download is starting, so start rotating the loader..
        mapDownloadBroadcastHelper.broadcastDownloadProgress(mapType, mapID, 0);

        //Downloading the map.
        String URL = AppConstants.getMapURL(mapFileName);
        final HashMap<String, Object> downloadResult = downloadMap(URL, mapID, mapType);
        if (downloadResult == null || downloadResult.get("map") == null) {
            //Download failed
            markDownloadComplete(mapType, mapID, false);
            return;
        }

        Bitmap downloadedMAP = (Bitmap) downloadResult.get("map");
        String lastModifiedHeader = (String) downloadResult.get("lost_modified");

        //Trimming the borders of some map types that are not meaningful to the user.
        mapDownloadBroadcastHelper.broadcastDownloadProgress(mapType, mapID, 92);
        downloadedMAP = removeMapBorders(mapFileName, downloadedMAP);
        mapDownloadBroadcastHelper.broadcastDownloadProgress(mapType, mapID, 95);

        //Save downloaded image for offline use.
        try {
            saveDownloadedMap(mapFileName, downloadedMAP);
        } catch (Exception e) {
            //Download failed
            CrashUtils.trackException("Error while storing map on DISK", e);
            markDownloadComplete(mapType, mapID, false);
            return;
        }
        //Download & Everything successful.
        mapDownloadBroadcastHelper.broadcastDownloadProgress(mapType, mapID, 100);

        PreferenceUtil.updateLastModifiedTime(mapFileName, lastModifiedHeader);
        markDownloadComplete(mapType, mapID, true);

        //check for .nomeida file and create it if it is not available.
        StorageUtils.createNoMediaFile();
    }

    public boolean performDuplicateDownloadCheck(int mapID) {
        if (activeDownloadsList[mapID] != null && activeDownloadsList[mapID]) {
            return false;
        }
        return true;
    }

    public HashMap<String, Object> downloadMap(String URL, int mapID, int mapType) {
        Bitmap downloadedBitmap = null;
        String lastModifiedHeader = "";
        try {
            //Invoking Http call to download the URL.
            //Calling through synchronous mode, since async wont suits on our requirement.
            OkHttpClient httpClient = HttpClient.getInstance();
            Call call = httpClient.newCall(HttpClient.generateRequest(URL));
            Response response = call.execute();

            //These caching headers should be stored on preferences.
            lastModifiedHeader = response.header("Last-Modified", "");
            Log.d("last modified: " + lastModifiedHeader);

//            Log.d("\nN/W counts: " + httpClient.getCache().getNetworkCount() + "\nReq Counts: " + httpClient.getCache().getRequestCount() + "\nCache Hits: " + httpClient.getCache().getHitCount());

            if (response.code() == 200) {
                InputStream inputStream;
                ByteArrayOutputStream outArrrayIPStream;
                try {
                    inputStream = response.body().byteStream();
                    outArrrayIPStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int statusUpdateTrigger = 0;
                    long downloaded = 0;
                    long responseSize = response.body().contentLength();

                    Log.d("Total file size to download: " + responseSize);

                    for (int count; (count = inputStream.read(buffer)) != -1; ) {
                        outArrrayIPStream.write(buffer, 0, count);
                        downloaded += count;
                        statusUpdateTrigger++;
//                        Log.d(String.format("%d / %d", downloaded, responseSize));

                        if (statusUpdateTrigger > AppConstants.STATUS_UPDATE_THRESHOLD) {
                            statusUpdateTrigger = 0;
                            Long downloadedPercent = downloaded * AppConstants.MAX_DOWNLOAD_PROGRESS / responseSize;
                            Log.d("downloaded percent: " + downloadedPercent);
                            mapDownloadBroadcastHelper.broadcastDownloadProgress(mapType, mapID, downloadedPercent.intValue());
                        }
                    }
                    byte[] responseImage = outArrrayIPStream.toByteArray();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inMutable = true;
                    downloadedBitmap = BitmapFactory.decodeByteArray(responseImage, 0, responseImage.length, options);
                } catch (Exception e) {
                    CrashUtils.trackException("MAP download IO exception", e);
                    return null;
                }
            } else {
                CrashUtils.trackException("res code other than 200: " + response.code(), null);
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        final HashMap<String, Object> result = new HashMap<>();
        result.put("map", downloadedBitmap);
        result.put("lost_modified", lastModifiedHeader);

        return result;
    }

    private Bitmap removeMapBorders(String mapFileName, Bitmap bmp) {
        //Try to remove unwanted parts from MAP. return the original MAP in case of any errors.
        try {
            //Trim the unwanted area from the Ultra Violet Map.
            if (mapFileName.equals(AppConstants.MAP_UV)) {
                return Bitmap.createBitmap(bmp, 110, 230, 800, 800);
            } else if (mapFileName.equals(AppConstants.MAP_HEAT)) {
                return Bitmap.createBitmap(bmp, 0, 180, 1250, 1400);
            }
        } catch (Exception e) {
            Crashlytics.log("trim MAP Error");
            Crashlytics.setString("MapType", mapFileName);
            try {
                if (bmp != null) {
                    Crashlytics.setInt("mapWidth", bmp.getWidth());
                    Crashlytics.setInt("mapHeight", bmp.getHeight());
                }
            } catch (Exception e1) {
                Crashlytics.logException(e1);
            }
            Crashlytics.logException(e);
        }
        return bmp;
    }

    private void saveDownloadedMap(String mapType, Bitmap bmp) throws Exception {
        File temp_file = new File(StorageUtils.getAppSpecificFolder() + File.separator + mapType + "_temp.jpg");
        FileOutputStream fileOutStream = new FileOutputStream(temp_file.getPath());

        // Compression Quality set to 100. ie. NO COMPRESSION.
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, fileOutStream);
        fileOutStream.flush();
        fileOutStream.close();

        boolean success = temp_file.renameTo(new File(StorageUtils.getAppSpecificFolder(), mapType + ".jpg"));
        Log.d("Map  saved to: " + temp_file.getAbsolutePath() + ". Overwritten? = " + success);
    }

    public void networkUnavailableHandling(int mapID, int mapType) {
        Toast.makeText(context, context.getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        markDownloadComplete(mapType, mapID, false);
    }

    public void markDownloadComplete(int mapType, int mapID, boolean status) {
        //Updating the controller flag about the specific map type is no more on progress.
        activeDownloadsList[mapID] = false;
        mapDownloadBroadcastHelper.broadcastDownloadStatus(mapType, mapID, status);
    }
}
