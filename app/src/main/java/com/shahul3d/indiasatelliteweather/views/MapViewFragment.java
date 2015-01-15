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

package com.shahul3d.indiasatelliteweather.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.noveogroup.android.log.Log;
import com.shahul3d.indiasatelliteweather.R;
import com.shahul3d.indiasatelliteweather.controllers.MainMapActivity_;
import com.shahul3d.indiasatelliteweather.data.AppConstants;
import com.shahul3d.indiasatelliteweather.events.DownloadStatusEvent;
import com.shahul3d.indiasatelliteweather.utils.StorageUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;

import de.greenrobot.event.EventBus;

@EFragment(R.layout.map_view_fragment)
public class MapViewFragment extends Fragment {
    @Bean
    AppConstants appConstants;

    @FragmentArg
    int pageNumber;

    @Bean
    StorageUtils storageUtils;

    @ViewById
    SubsamplingScaleImageView touchImage;

    EventBus bus = EventBus.getDefault();
    MainMapActivity_ activityContext = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreate:" + pageNumber);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
        Log.d("OnResume:" + pageNumber);
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        Log.d("OnPause:" + pageNumber);
        super.onPause();
    }

    @AfterViews
    void calledAfterViewInjection() {
        activityContext = (MainMapActivity_) getActivity();
        renderImage();
        Log.d("ViewAfterInjection:" + pageNumber);
    }

    @UiThread
    void renderImage() {
        //TODO: Check file exits before render.
        touchImage.setMaxScale(5f);
        touchImage.setMinimumScaleType(touchImage.SCALE_TYPE_CENTER_CROP);
        touchImage.setScaleAndCenter(2f, touchImage.getCenter());
        touchImage.setImageFile(storageUtils.getExternalStoragePath() + File.separator + appConstants.getMapType(pageNumber) + ".jpg");
        Log.d("Map refreshed");
    }

    public void onEvent(DownloadStatusEvent downloadStatus) {
        int mapID = downloadStatus.mapID;
        if (pageNumber != mapID) {
            return;
        }

        activityContext.startRefreshAnimation();

        if (!downloadStatus.status) {
            //TODO: Handle download failure scenerio.
            Log.d("Received download failed event for:" + mapID);
            return;
        }

        renderImage();
    }
}
