package com.shahul3d.indiasatelliteweather.controllers;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.noveogroup.android.log.Log;
import com.shahul3d.indiasatelliteweather.R;
import com.shahul3d.indiasatelliteweather.adapters.TouchImagePageAdapter;
import com.shahul3d.indiasatelliteweather.utils.AnimationUtil;
import com.shahul3d.indiasatelliteweather.utils.StorageUtils;
import com.shahul3d.indiasatelliteweather.widgets.SlidingTabLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main_map)
public class MainMapActivity extends ActionBarActivity {
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
    private MenuItem refreshItem;
    private boolean isLoading = Boolean.FALSE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Starting the service when the app starts
//        DownloaderService_.intent(getApplication()).start();
    }

    @Override
    protected void onDestroy() {
        //Stopping the service when the app exists.
//        DownloaderService_.intent(getApplication()).stop();
        super.onDestroy();
    }


    @AfterViews
    protected void init() {
        initToolbar();
        initDrawer();
        //TODO: To be removed.
        Log.d("Storage path: %s", storageUtils.getExternalStoragePath());
    }

    private void initDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(drawerToggle);
        String[] values = new String[]{
                "Weather Maps", "Weather Animation", "Settings", "About"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Selected :" + position);
                mDrawerLayout.closeDrawer(Gravity.START);
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
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }
        });
        number_progress_bar.setSuffix("% Downloading ");
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
            finishRefreshAnimation();
            number_progress_bar.setVisibility(View.GONE);
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

    public void startRefreshAnimation() {
        if(!isLoading){
            AnimationUtil.startRefreshAnimation(this, refreshItem);
            isLoading = Boolean.TRUE;
        }
    }

    public void finishRefreshAnimation() {
        AnimationUtil.stopRefreshAnimation(this, refreshItem);
        isLoading = Boolean.FALSE;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_refresh) {
//            Log.d("refresh clicked..");
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
