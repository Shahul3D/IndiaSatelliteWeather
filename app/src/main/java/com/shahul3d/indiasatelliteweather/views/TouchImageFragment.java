package com.shahul3d.indiasatelliteweather.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.noveogroup.android.log.Log;
import com.shahul3d.indiasatelliteweather.R;
import com.shahul3d.indiasatelliteweather.data.AppConstants;
import com.shahul3d.indiasatelliteweather.service.DownloaderService_;
import com.squareup.okhttp.OkHttpClient;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import de.greenrobot.event.EventBus;

@EFragment(R.layout.touch_image_fragment)
public class TouchImageFragment extends Fragment {
    @Bean
    AppConstants appConstants;

    @FragmentArg
    int pageNumber;

    @ViewById
    SubsamplingScaleImageView touchImage;

    EventBus bus = EventBus.getDefault();
    OkHttpClient httpClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    @AfterViews
    void calledAfterViewInjection() {
        touchImage.setImageAsset(chooseImage(pageNumber));
    }

    String chooseImage(int pageNumber) {
        String defaultImage = "map_temprature.jpg";
        if (pageNumber == 1) {
            defaultImage = "map_infra_red.jpg";
        } else if (pageNumber == 2) {
            defaultImage = "map_color_composite.jpg";
        } else if (pageNumber == 3) {
            defaultImage = "map_water_vapor.jpg";
        }
        Log.d("Loading: %s", defaultImage);
        return defaultImage;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            //TODO: to be refactored.
            Log.d("Refresh clicked:-> with page number:" + pageNumber);
            Intent downloaderIntent = new Intent(getActivity().getApplicationContext(), DownloaderService_.class);
            downloaderIntent.putExtra(appConstants.DOWNLOAD_INTENT_NAME, pageNumber);
            getActivity().getApplicationContext().startService(downloaderIntent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
        @Override
        public void onDestroyView() {
            super.onDestroyView();
        }*/
    public void onEvent(Object e) {
    }
}
