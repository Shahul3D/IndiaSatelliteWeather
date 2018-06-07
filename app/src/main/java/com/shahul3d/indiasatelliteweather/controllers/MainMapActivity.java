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

package com.shahul3d.indiasatelliteweather.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.noveogroup.android.log.Log;
import com.shahul3d.indiasatelliteweather.R;
import com.shahul3d.indiasatelliteweather.adapters.TouchImagePageAdapter;
import com.shahul3d.indiasatelliteweather.data.AppConstants;
import com.shahul3d.indiasatelliteweather.events.DownloadProgressUpdateEvent;
import com.shahul3d.indiasatelliteweather.events.DownloadStatusEvent;
import com.shahul3d.indiasatelliteweather.preferences.GeneralPreference;
import com.shahul3d.indiasatelliteweather.service.DownloaderService_;
import com.shahul3d.indiasatelliteweather.utils.AnimationUtil;
import com.shahul3d.indiasatelliteweather.utils.CrashUtils;
import com.shahul3d.indiasatelliteweather.utils.PreferenceUtil;
import com.shahul3d.indiasatelliteweather.widgets.SlidingTabLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.codechimp.apprater.AppRater;

import java.util.concurrent.ConcurrentHashMap;

import de.cketti.library.changelog.ChangeLog;
import de.greenrobot.event.EventBus;

@EActivity(R.layout.activity_main_map)
public class MainMapActivity extends AppCompatActivity {

    @ViewById(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @ViewById(R.id.navdrawer)
    ListView mDrawerList;
    @ViewById(R.id.toolbar)
    Toolbar toolbar;
    @ViewById
    NumberProgressBar number_progress_bar;
    @ViewById(R.id.viewpager)
    ViewPager pager;
    @ViewById(R.id.sliding_tabs)
    SlidingTabLayout slidingTabLayout;

    EventBus bus = EventBus.getDefault();
    ActionBarDrawerToggle drawerToggle;
    private MenuItem refreshItem;
    private boolean isLoading = Boolean.FALSE;
    Integer currentPage = 0;
    AppConstants.MapType currentMapType;
    ConcurrentHashMap<String, Integer> activeDownloadsList;
    ChangeLog changeLogLib;

    final String BUNDLE_MAP_TYPE = "MAP_TYPE";
    final String BUNDLE_MAP_ID = "MAP_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Making Live map as default.
        currentMapType = AppConstants.MapType.LIVE;

        if (savedInstanceState != null) {
            // Restore values from saved state
            currentPage = savedInstanceState.getInt(BUNDLE_MAP_ID, 0);
            currentMapType = AppConstants.MapType.values()[savedInstanceState.getInt(BUNDLE_MAP_TYPE, 0)];
        }

        activeDownloadsList = new ConcurrentHashMap<String, Integer>();
//        WeatherApplication.analyticsHandler.trackScreen(getString(R.string.home_page));
        AppRater.app_launched(this);

        changeLogLib = new ChangeLog(this);
        if (changeLogLib.isFirstRun()) {
//            changeLogLib.getLogDialog().show();
            changeLogLib.getFullLogDialog().show();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        //Saving map type & current while recreating the activity.
        savedInstanceState.putInt(BUNDLE_MAP_TYPE, currentMapType.value);
        savedInstanceState.putInt(BUNDLE_MAP_ID, currentPage);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        //App going background. stopping all download notifications.
        stopRefreshAnimation();
        hideProgress();

        super.onPause();
    }

    @AfterViews
    protected void init() {
        initToolbar();
        reInitializeTabs();
        initDrawer();
        hideProgress();
    }

    private void initDrawer() {
        final Context context = this;
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(drawerToggle);
        String[] values = new String[]{
                "Live Weather","Temperature Forecast", "Rainfall Forecast", "Settings", "What's New", "Do you like this Work ?", "About"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.row_navbar, values);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDrawerLayout.closeDrawer(GravityCompat.START);

                switch (position) {
                    case 0:
                        toggleMapView(AppConstants.MapType.LIVE);
                        break;
                    case 1:
                        toggleMapView(AppConstants.MapType.TEMP_FORECAST);
                        break;
                    case 2:
                        toggleMapView(AppConstants.MapType.FORECAST);
                        break;
                    case 3:
                        Intent intent = new Intent(context, GeneralPreference.class);
                        startActivity(intent);
                        break;
                    case 4:
                        changeLogLib.getLogDialog().show();
                        break;
                    case 5:
                        showRateAppDialog(context);
                        break;
                    case 6:
                        showAboutDeveloperPage(context);
                        break;
                }
            }
        });
    }

    private void toggleMapView(AppConstants.MapType mapType){
        currentMapType = mapType;
        reInitializeTabs();
    }

    private void initToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_ab_drawer);
            toolbar.inflateMenu(R.menu.menu_main_map);
            toolbar.setTitleTextColor(Color.WHITE);
        }
    }

    private void reInitializeTabs() {
        //Hiding the progress from the previous map type.
        hideProgress();

        currentPage = 0;
        updateToolbarTitle(currentMapType);
        pager.setAdapter(new TouchImagePageAdapter(getSupportFragmentManager(), getTabTitles(currentMapType), currentMapType));
        slidingTabLayout.setViewPager(pager);
        slidingTabLayout.setDistributeEvenly(true);
        number_progress_bar.setSuffix("% Downloading ");
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }
        });

        //Check for update while launching the MAP view
        autoRefreshMAP();

        slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int mapID) {
                Log.d("onPageSelected:" + mapID);
                syncDownloadProgress(mapID);
                currentPage = mapID;

                WeatherApplication.analyticsHandler.trackScreen(AppConstants.getMapType(mapID, currentMapType.value));

                //Check for update while switching MAPs
                autoRefreshMAP();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void autoRefreshMAP() {
        int autoRefreshInterval = PreferenceUtil.getAutoRefreshInterval();
        if (autoRefreshInterval == -1) {
            return;
        }

        final long lastUpdatedDateTime = PreferenceUtil.getLastModifiedTime(AppConstants.getMapType(currentPage, currentMapType.value));
        if (lastUpdatedDateTime < 1) {
            return;
        }

        final int SECOND_MILLIS = 1000;
        final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        final int DAY_MILLIS = 24 * HOUR_MILLIS;

        final long now = System.currentTimeMillis();
        long diff = now - lastUpdatedDateTime;
        boolean status = false;

        if (currentMapType != AppConstants.MapType.LIVE) {
            //Forecast maps will be updated only once a day.
            //So setting its default update interval as 1 day.
            autoRefreshInterval = 1;
        }

        switch (autoRefreshInterval) {
            case 1:// 1 day
                status = diff > DAY_MILLIS;
                break;
            case 2://6 hours
                status = diff > (6 * HOUR_MILLIS);
                break;
            case 3://1 hour
                status = diff > (HOUR_MILLIS);
                break;
            case 4://30 mins
                status = diff > (30 * MINUTE_MILLIS);
                break;
            case 5://15 mins
                status = diff > (15 * MINUTE_MILLIS);
//                status = true; //Always refresh
                break;
        }

        if (!status) {
            return;
        }

        initiateDownload();
    }

    private String[] getTabTitles(AppConstants.MapType mapType) {
        if (mapType == AppConstants.MapType.LIVE) {
            return AppConstants.LIVE_MAP_TAB_LABELS;
        } else if (mapType == AppConstants.MapType.TEMP_FORECAST) {
            return AppConstants.TEMP_FORECAST_TAB_LABELS;
        } else {
            return AppConstants.FORECAST_TAB_LABELS;
        }
    }

    private void updateToolbarTitle(AppConstants.MapType mapType) {
        String title;
        //TODO: avoid if else with key value pairs
        if (mapType == AppConstants.MapType.LIVE) {
            title = getString(R.string.title_live_map);
        } else if (mapType == AppConstants.MapType.TEMP_FORECAST) {
            title = getString(R.string.title_temp_forecast_map);
        } else {
            title = getString(R.string.title_forecast_map);
        }
        try {
            getSupportActionBar().setTitle(title);
        } catch (Exception e) {
            CrashUtils.trackException("Error on setting toolbar title", e);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_map, menu);
        refreshItem = menu.findItem(R.id.action_refresh);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            Log.d("Refresh called for page number:" + currentPage);
            initiateDownload();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initiateDownload() {
        startRefreshAnimation();
        Intent downloaderIntent = new Intent(getApplicationContext(), DownloaderService_.class);
        downloaderIntent.putExtra(AppConstants.DOWNLOAD_INTENT_NAME, currentPage);
        downloaderIntent.putExtra(AppConstants.DOWNLOAD_MAP_TYPE, currentMapType.value);
        getApplicationContext().startService(downloaderIntent);
    }

    @UiThread
    public void updateProgress(int progress) {
        if (number_progress_bar != null) {
            if (progress >= 100) {
                hideProgress();
                return;
            }

            if (number_progress_bar.getVisibility() == View.GONE) {
                startRefreshAnimation();
                number_progress_bar.setVisibility(View.VISIBLE);
            }

            number_progress_bar.setProgress(progress);
        }
    }

    @UiThread
    public void hideProgress() {
        if (number_progress_bar != null) {
            stopRefreshAnimation();
            number_progress_bar.setVisibility(View.GONE);
        }
    }

    @UiThread
    public void startRefreshAnimation() {
        if (!isLoading) {
            AnimationUtil.startRefreshAnimation(this, refreshItem);
            isLoading = Boolean.TRUE;
        }
    }

    @UiThread
    public void stopRefreshAnimation() {
        if (isLoading) {
            AnimationUtil.stopRefreshAnimation(this, refreshItem);
            isLoading = Boolean.FALSE;
        }
    }

    public void syncDownloadProgress(int currentPage) {
        final String key = constructActiveDownloadMAPKey(currentMapType.value, currentPage);
        if (!activeDownloadsList.containsKey(key)) {
            hideProgress();
            return;
        }
        updateProgress(activeDownloadsList.get(key));
    }

    public void updateActiveDownloadsList(int mapType, int downloadingMapID, int lastKnownProgress) {
        final String key = constructActiveDownloadMAPKey(mapType, downloadingMapID);
        if (lastKnownProgress == -1 && activeDownloadsList.containsKey(key)) {
            activeDownloadsList.remove(key);
            return;
        }
        activeDownloadsList.put(key, lastKnownProgress);
    }

    private String constructActiveDownloadMAPKey(int mapType, int downloadingMapID) {
        return mapType + ":" + downloadingMapID;
    }

    private void showAboutDeveloperPage(Context context) {
        new LibsBuilder()
                .withFields(R.string.class.getFields())
                .withActivityTitle(getString(R.string.about_heading))
                .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                .withLibraries("androidAnnotations")
                .start(context);
        WeatherApplication.analyticsHandler.trackScreen(getString(R.string.about_page));
    }

    private void showRateAppDialog(Context context) {
        AppRater.setDontRemindButtonVisible(true);
        AppRater.showRateDialog(context);
        WeatherApplication.analyticsHandler.trackScreen(getString(R.string.rating_page));
    }

    public void onEvent(DownloadProgressUpdateEvent downloadProgress) {
        if (currentMapType.value == downloadProgress.getMapType() && currentPage == downloadProgress.getMapID()) {
            updateProgress(downloadProgress.getProgress());
        }
        updateActiveDownloadsList(downloadProgress.getMapType(), downloadProgress.getMapID(), downloadProgress.getProgress());
    }

    public void onEvent(DownloadStatusEvent downloadStatus) {
        int completedMapID = downloadStatus.mapID;
        Log.d("UI: Download complete event received. mapid:" + completedMapID + " status:" + downloadStatus.status);
        updateActiveDownloadsList(downloadStatus.mapType, completedMapID, -1);

        if (currentMapType.value == downloadStatus.mapType && currentPage == completedMapID) {
            hideProgress();
        }
    }
}