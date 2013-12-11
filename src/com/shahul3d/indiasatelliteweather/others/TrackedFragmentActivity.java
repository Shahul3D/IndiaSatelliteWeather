package com.shahul3d.indiasatelliteweather.others;

import android.support.v4.app.FragmentActivity;

import com.google.analytics.tracking.android.EasyTracker;

//Just extend this base class whenever Google Analytics tracking is required on a FragmentActivity.
public abstract class TrackedFragmentActivity extends FragmentActivity {
	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}
}