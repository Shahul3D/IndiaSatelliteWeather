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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.noveogroup.android.log.Log;
import com.shahul3d.indiasatelliteweather.data.AppConstants;
import com.shahul3d.indiasatelliteweather.utils.HttpClient;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EService;

import de.greenrobot.event.EventBus;

@EService
public class DownloaderService extends Service {
    EventBus bus = EventBus.getDefault();

    MAPDownloadController mapDownloadController;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Service On Create");
        bus.register(this);
        mapDownloadController = new MAPDownloadController(bus);
    }

    @Override
    public void onDestroy() {
        Log.d("Service On Destroy");
        //TODO: Need to check this is required or not?
        mapDownloadController = null;
        bus.unregister(this);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int startOption = START_NOT_STICKY;
        if (intent == null) {
            return startOption;
        }

        int mapID = intent.getIntExtra(AppConstants.DOWNLOAD_INTENT_NAME, 0);
        int mapType = intent.getIntExtra(AppConstants.DOWNLOAD_MAP_TYPE, 0);

        if (!HttpClient.isNetworkAvailable()) {
            mapDownloadController.networkUnavailableHandling(mapID, mapType);
            return startOption;
        }

        //Forwarding the new MAP download request to MAP Download Controller.
        //Calling it only on background thread. Since service also works only on MainThread.
        handleDownloadRequestOnBackgroundThread(mapID, mapType);

        // NOT_STICKY: No need to restart the service if it get killed by user or by system.
        return startOption;
    }

    @Background
    public void handleDownloadRequestOnBackgroundThread(int mapID, int mapType) {
        //Doing all the complex downloading activities only on background thread. Since service also works only on MainThread.
        mapDownloadController.downloadMAPRequest(mapID, mapType);
    }

    //Dummy event. For the stupid requirement of EventBus.
    public void onEvent(int dummyEvent) {
    }
}
