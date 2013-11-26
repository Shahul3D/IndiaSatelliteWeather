package com.shahul3d.indiasatelliteweather;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Window;

import com.shahul3d.indiasatelliteweather.Fragment_ViewMap.ActivityListenerInterface;
import com.shahul3d.indiasatelliteweather.utils.TrackedFragmentActivity;

public class Activity_ViewImage extends TrackedFragmentActivity implements ActivityListenerInterface{

	private Fragment_ViewMap mapFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
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
