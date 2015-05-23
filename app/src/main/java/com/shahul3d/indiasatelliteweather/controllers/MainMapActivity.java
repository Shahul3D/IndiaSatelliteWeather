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
import android.widget.Toast;

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
import com.shahul3d.indiasatelliteweather.utils.StorageUtils;
import com.shahul3d.indiasatelliteweather.widgets.SlidingTabLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.codechimp.apprater.AppRater;

import java.util.concurrent.ConcurrentHashMap;

import de.greenrobot.event.EventBus;

@EActivity(R.layout.activity_main_map)
public class MainMapActivity extends AppCompatActivity {
    private String titles[] = new String[]{"Ultra Violet", "Color Composite", "Infra Red", "Heat Map", "Wind Direction"};
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

    @Bean
    StorageUtils storageUtils;
    @Bean
    AppConstants appConstants;

    EventBus bus = EventBus.getDefault();
    private MenuItem refreshItem;
    private boolean isLoading = Boolean.FALSE;
    Integer currentPage = 0;
    ConcurrentHashMap<Integer, Integer> downloadingMapsList;
    WeatherApplication applicationContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        downloadingMapsList = new ConcurrentHashMap<Integer, Integer>();

        applicationContext = (WeatherApplication) getApplicationContext();
//        applicationContext.sendAnalyticsScreen(getString(R.string.home_page));
        AppRater.app_launched(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
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
                "Weather Maps", "Weather Animation", "Do you like this Work ?", "About"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.row_navbar, values);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDrawerLayout.closeDrawer(Gravity.START);

                switch (position) {
                    case 1:
                        Toast.makeText(context, "Coming Soon..!", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        AppRater.setDontRemindButtonVisible(true);
                        AppRater.showRateDialog(context);
                        applicationContext.sendAnalyticsScreen(getString(R.string.rating_page));
                        break;
                    case 3:
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

    private void initToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_ab_drawer);
            toolbar.inflateMenu(R.menu.menu_main_map);
        }

        pager.setAdapter(new TouchImagePageAdapter(getSupportFragmentManager(), titles));
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

                applicationContext.sendAnalyticsScreen(appConstants.getMapType(mapID));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
            downloaderIntent.putExtra(appConstants.DOWNLOAD_INTENT_NAME, currentPage);
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
        if (!downloadingMapsList.containsKey(currentPage)) {
            hideProgress();
            return;
        }
        updateProgress(downloadingMapsList.get(currentPage));
    }

    public void updateActiveDownloadsList(int downloadingMapID, int lastKnownProgress) {
        if (lastKnownProgress == -1 && downloadingMapsList.containsKey(downloadingMapID)) {
            downloadingMapsList.remove(downloadingMapID);
            return;
        }
        downloadingMapsList.put(downloadingMapID, lastKnownProgress);

    }

    public void onEvent(DownloadProgressUpdateEvent downloadProgress) {
        if (currentPage == downloadProgress.getMapType()) {
            updateProgress(downloadProgress.getProgress());
        }
        updateActiveDownloadsList(downloadProgress.getMapType(), downloadProgress.getProgress());
    }

    public void onEvent(DownloadStatusEvent downloadStatus) {
        int completedMapID = downloadStatus.mapID;
        updateActiveDownloadsList(completedMapID, -1);

        if (currentPage == completedMapID) {
            hideProgress();
        }
    }
}