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
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
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
import com.shahul3d.indiasatelliteweather.service.DownloaderService_;
import com.shahul3d.indiasatelliteweather.utils.AnimationUtil;
import com.shahul3d.indiasatelliteweather.utils.CrashUtils;
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
    private String map_tabs[] = new String[]{"Ultra Violet", "Color Composite", "Infra Red", "Heat Map", "Wind Direction"};
    private String forecast_tabs[] = new String[]{"24 Hours", "48 Hours", "72 Hours", "96 Hours", "120 Hours", "144 Hours", "168 Hours"};
    @ViewById(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    ActionBarDrawerToggle drawerToggle;

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
    private MenuItem refreshItem;
    private boolean isLoading = Boolean.FALSE;
    Integer currentPage = 0;
    AppConstants.MapType currentMapType;
    ConcurrentHashMap<String, Integer> downloadingMapsList;
    WeatherApplication applicationContext;
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
        downloadingMapsList = new ConcurrentHashMap<String, Integer>();
        applicationContext = (WeatherApplication) getApplicationContext();
//        applicationContext.sendAnalyticsScreen(getString(R.string.home_page));
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
        //TODO: To be removed.
//        Log.d("Storage path: %s", Environment.getExternalStorageDirectory());
        hideProgress();
    }

    private void initDrawer() {
        final Context context = this;
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(drawerToggle);
        String[] values = new String[]{
                "Live Weather", "Forecast Rainfall", "What's New", "Do you like this Work ?", "About"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.row_navbar, values);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDrawerLayout.closeDrawer(Gravity.START);

                switch (position) {
                    case 0:
                        showLiveMAPs();
                        break;
                    case 1:
                        showForcastMAPs();
                        break;
                    case 2:
                        changeLogLib.getLogDialog().show();
                        break;
                    case 3:
                        AppRater.setDontRemindButtonVisible(true);
                        AppRater.showRateDialog(context);
                        applicationContext.sendAnalyticsScreen(getString(R.string.rating_page));
                        break;
                    case 4:
                        new LibsBuilder()
                                .withFields(R.string.class.getFields())
                                .withActivityTitle(getString(R.string.about_heading))
                                .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                                .withLibraries("androidAnnotations")
                                .start(context);
                        applicationContext.sendAnalyticsScreen(getString(R.string.about_page));
                        break;
                }
            }
        });
    }

    private void showForcastMAPs() {
        currentMapType = AppConstants.MapType.FORECAST;
        reInitializeTabs();
    }

    private void showLiveMAPs() {
        currentMapType = AppConstants.MapType.LIVE;
        reInitializeTabs();
    }

    private void initToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_ab_drawer);
            toolbar.inflateMenu(R.menu.menu_main_map);
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
        slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int mapID) {
                Log.d("onPageSelected:" + mapID);
                syncDownloadProgress(mapID);
                currentPage = mapID;

                applicationContext.sendAnalyticsScreen(AppConstants.getMapType(mapID, currentMapType.value));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private String[] getTabTitles(AppConstants.MapType mapType) {
        String[] tabs;
        if (mapType == AppConstants.MapType.LIVE) {
            tabs = map_tabs;
        } else {
            tabs = forecast_tabs;
        }
        return tabs;
    }

    private void updateToolbarTitle(AppConstants.MapType mapType) {
        String title;
        if (mapType == AppConstants.MapType.LIVE) {
            title = "LIVE Weather";
        } else {
            title = "Forecast Rainfall";
        }
        try {
            getSupportActionBar().setTitle(title);
        } catch (Exception e) {
            CrashUtils.trackException("", e);
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
            Log.d("Refresh clicked:-> with page number:" + currentPage);
            startRefreshAnimation();
            Intent downloaderIntent = new Intent(getApplicationContext(), DownloaderService_.class);
            downloaderIntent.putExtra(AppConstants.DOWNLOAD_INTENT_NAME, currentPage);
            downloaderIntent.putExtra(AppConstants.DOWNLOAD_MAP_TYPE, currentMapType.value);
            getApplicationContext().startService(downloaderIntent);
        }

        return super.onOptionsItemSelected(item);
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
        if (!downloadingMapsList.containsKey(key)) {
            hideProgress();
            return;
        }
        updateProgress(downloadingMapsList.get(key));
    }

    public void updateActiveDownloadsList(int mapType, int downloadingMapID, int lastKnownProgress) {
        final String key = constructActiveDownloadMAPKey(mapType, downloadingMapID);
        if (lastKnownProgress == -1 && downloadingMapsList.containsKey(key)) {
            downloadingMapsList.remove(key);
            return;
        }
        downloadingMapsList.put(key, lastKnownProgress);
    }

    private String constructActiveDownloadMAPKey(int mapType, int downloadingMapID) {
        return mapType + ":" + downloadingMapID;
    }

    public void onEvent(DownloadProgressUpdateEvent downloadProgress) {
        if (currentMapType.value == downloadProgress.getMapType() && currentPage == downloadProgress.getMapID()) {
            updateProgress(downloadProgress.getProgress());
        }
        updateActiveDownloadsList(downloadProgress.getMapType(), downloadProgress.getMapID(), downloadProgress.getProgress());
    }

    public void onEvent(DownloadStatusEvent downloadStatus) {
        int completedMapID = downloadStatus.mapID;
        updateActiveDownloadsList(downloadStatus.mapType, completedMapID, -1);

        if (currentMapType.value == downloadStatus.mapType && currentPage == completedMapID) {
            hideProgress();
        }
    }
}