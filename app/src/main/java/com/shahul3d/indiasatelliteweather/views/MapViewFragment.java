package com.shahul3d.indiasatelliteweather.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.noveogroup.android.log.Log;
import com.shahul3d.indiasatelliteweather.R;
import com.shahul3d.indiasatelliteweather.controllers.MainMapActivity_;
import com.shahul3d.indiasatelliteweather.data.AppConstants;
import com.shahul3d.indiasatelliteweather.events.DownloadProgressUpdateEvent;
import com.shahul3d.indiasatelliteweather.events.DownloadStatusEvent;
import com.shahul3d.indiasatelliteweather.service.DownloaderService_;
import com.shahul3d.indiasatelliteweather.utils.AnimationUtil;
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
    MenuItem refreshItem;
    boolean isLoading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        activityContext.hideProgress();
    }

    @UiThread
    void renderImage() {
        //TODO: Check file exits before render.
        touchImage.setImageFile(storageUtils.getExternalStoragePath() + File.separator + appConstants.getMapType(pageNumber) + ".jpg");
        Log.d("Map refreshed");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.i("create menu called on fragmetn");
        inflater.inflate(R.menu.menu_main_map, menu);
        refreshItem = menu.findItem(R.id.action_refresh);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            //TODO: to be refactored.
            Log.d("Refresh clicked:-> with page number:" + pageNumber);
            startRefreshAnimation();
            Intent downloaderIntent = new Intent(getActivity().getApplicationContext(), DownloaderService_.class);
            downloaderIntent.putExtra(appConstants.DOWNLOAD_INTENT_NAME, pageNumber);
            getActivity().getApplicationContext().startService(downloaderIntent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @UiThread
    public void startRefreshAnimation() {
        if (!isLoading) {
            AnimationUtil.startRefreshAnimation(getActivity().getApplicationContext(), refreshItem);
            isLoading = Boolean.TRUE;
        }
    }

    @UiThread
    public void stopRefreshAnimation() {
        if (isLoading) {
            AnimationUtil.stopRefreshAnimation(getActivity().getApplicationContext(), refreshItem);
            isLoading = Boolean.FALSE;
        }
    }

    public void onEvent(DownloadStatusEvent downloadStatus) {
        int mapID = downloadStatus.mapID;
        if (pageNumber != mapID) {
            return;
        }

        stopRefreshAnimation();

        if (!downloadStatus.status) {
            //TODO: Handle download failure scenerio.
            Log.d("Received download failed event for:" + mapID);
            return;
        }

        renderImage();
    }

    public void onEvent(DownloadProgressUpdateEvent downloadProgress) {
        if (activityContext == null) {
            return;
        }

        int mapID = downloadProgress.getMapType();
        if (pageNumber == mapID) {
            int progress = downloadProgress.getProgress();
            activityContext.updateProgress(progress);
        }
    }
}
