package com.shahul3d.indiasatelliteweather.ui;

import java.util.ArrayList;

import android.app.ActionBar;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.crashlytics.android.Crashlytics;
import com.shahul3d.indiasatelliteweather.R;
import com.shahul3d.indiasatelliteweather.adapter.NavDrawerListAdapter;
import com.shahul3d.indiasatelliteweather.model.NavDrawerItem;
import com.shahul3d.indiasatelliteweather.others.AppConstants;
import com.shahul3d.indiasatelliteweather.others.TrackedFragmentActivity;
import com.shahul3d.indiasatelliteweather.ui.Fragment_ViewMap.ActivityListenerInterface;

public class Activity_ViewImage extends TrackedFragmentActivity implements ActivityListenerInterface{

	//For Navigation Drawer.
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	
	private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;
	private ActionBar actionBar;
	
	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
   
	
	private Fragment_ViewMap mapFragment;
	private final String MAP_FRAGMENT_NAME="mapFragment";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_drawer_main);
		Crashlytics.start(this);
		
		mTitle = mDrawerTitle = getTitle();
		mPlanetTitles = getResources().getStringArray(R.array.navbar_array);
		navDrawerItems = new ArrayList<NavDrawerItem>();
		
		navDrawerItems.add(new NavDrawerItem(mPlanetTitles[0], -1));
		navDrawerItems.add(new NavDrawerItem(mPlanetTitles[1], -1));
		navDrawerItems.add(new NavDrawerItem(mPlanetTitles[2], -1));
		navDrawerItems.add(new NavDrawerItem(mPlanetTitles[3], -1));
//		navDrawerItems.add(new NavDrawerItem(mPlanetTitles[4], -1, true, "2 Updates"));
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        
        adapter = new NavDrawerListAdapter(getApplicationContext(),	navDrawerItems);
        
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		
     // enable ActionBar app icon to behave as action to toggle nav drawer
        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        
     // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.app_name,  /* "open drawer" description for accessibility */
                R.string.app_name  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        
        if (savedInstanceState == null) {
//            selectItem(0);
        }
        
		
		FragmentManager fm = getSupportFragmentManager();
		mapFragment = (Fragment_ViewMap) fm.findFragmentByTag(MAP_FRAGMENT_NAME);

		// If the Fragment is non-null, then it is retained across a configuration change.
		//since it is configured as RetainedInstance we no need handle if the instance is available.
//		CommonUtils.printLog("fragment state during onCreate()= " + mapFragment);
		if (mapFragment == null) {
			mapFragment = new Fragment_ViewMap();
			fm.beginTransaction().add(R.id.frame_mapfragment, mapFragment, MAP_FRAGMENT_NAME).commit();
		}
	}
	
	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.refresh_item).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	/* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	switch(position)
        	{
        	case 0:
        		mapFragment.updateMap(AppConstants.MAP_INDIA_WEATHER_UV);
        		break;
        	case 1:	
        		mapFragment.updateMap(AppConstants.MAP_INDIA_WEATHER_COLOR);
        		break;
        	case 2:	
        		mapFragment.updateMap(AppConstants.MAP_INDIA_WEATHER_IR);
        		break;
        	case 3:	
        		mapFragment.updateMap(AppConstants.MAP_INDIA_WIND_FLOW);
        		break;
        	}
        	mDrawerLayout.closeDrawer(mDrawerList);
        	updateProgress(100);
//            selectItem(position);
        }
    }
	
    private void selectItem(int position) {
        // update the main content by replacing fragments
//        Fragment fragment = new PlanetFragment();
//        Bundle args = new Bundle();
//        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//        fragment.setArguments(args);
//
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
    
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

	@Override
	public void updateProgress(int progress) {
		if (progress > 99) {
			// Auto hide progress when it completes
			setProgress(Window.PROGRESS_VISIBILITY_OFF);
		} else {
			setProgress((Window.PROGRESS_END - Window.PROGRESS_START) / 100	* progress);
		}
	}
}
