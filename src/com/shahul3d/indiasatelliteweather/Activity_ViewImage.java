package com.shahul3d.indiasatelliteweather;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

public class Activity_ViewImage extends FragmentActivity {

	private Fragment_ViewMap mapFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_map);

		FragmentManager fm = getSupportFragmentManager();
		mapFragment = (Fragment_ViewMap) fm.findFragmentByTag("mapFragment");

		// If the Fragment is non-null, then it is retained across a configuration change.
		//since it is configured as RetainedInstance we no need handle if the instance is available.
		Log.d("shahul", "fragment state during onCreate()= " + mapFragment);
		if (mapFragment == null) {
			mapFragment = new Fragment_ViewMap();
			fm.beginTransaction().add(R.id.frame_mapfragment, mapFragment, "mapFragment").commit();
		}
	}
}
