package com.shahul3d.indiasatelliteweather.controllers;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.shahul3d.indiasatelliteweather.R;
import com.shahul3d.indiasatelliteweather.adapters.TouchImagePageAdapter;
import com.shahul3d.indiasatelliteweather.widgets.FloatingActionButton;
import com.shahul3d.indiasatelliteweather.widgets.SlidingTabLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main_map)
public class MainMapActivity extends ActionBarActivity {
    private String titles[] = new String[]{"Ultra Violet", "Color Composite", "Infra Red", "Wind Direction"};
    @ViewById(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    ActionBarDrawerToggle drawerToggle;

    @ViewById(R.id.navdrawer)
    ListView mDrawerList;

    @ViewById(R.id.toolbar)
    Toolbar toolbar;

    @ViewById(R.id.viewpager)
    ViewPager pager;

    @ViewById(R.id.sliding_tabs)
    SlidingTabLayout slidingTabLayout;

    @AfterViews
    protected void init() {
        initToolbar();
        initDrawer();
    }

    private void initDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(drawerToggle);
        String[] values = new String[]{
                "Ultra Violet", "Color Composite", "Infra Red", "Wind Direction"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        mDrawerList.setAdapter(adapter);
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

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
