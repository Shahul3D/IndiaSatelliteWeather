package com.shahul3d.indiasatelliteweather.ui;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.DisplayType;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.shahul3d.indiasatelliteweather.R;
import com.shahul3d.indiasatelliteweather.bg.DownloadFileFromURL;
import com.shahul3d.indiasatelliteweather.utils.DecodeUtils;

public class Fragment_ViewMap extends android.support.v4.app.Fragment {
	private ImageViewTouch mImage;
	private TextView mapDateTime;
	private Menu optionsMenu;
	//Weak reference to AsyncTask to avoid leaking memory.
	private WeakReference<DownloadFileFromURL> downloadMapTask;
	private SharedPreferences preference_General = null;
	private ActivityListenerInterface mListener;
	private final String MAP_URL="http://www.imd.gov.in/section/satmet/img/sector-eir.jpg";

	public static interface ActivityListenerInterface {
		public abstract void updateProgress(int progress);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			this.mListener = (ActivityListenerInterface) activity;
		} catch (final ClassCastException e) {
			throw new ClassCastException(activity.toString() +" must implement ActivityListenerInterface");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		preference_General = getActivity().getSharedPreferences("BackgroundPreference", Activity.MODE_PRIVATE);
		setHasOptionsMenu(true);
		setRetainInstance(true);
		downloadMap(MAP_URL);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_view_map, container, false);
		mImage = (ImageViewTouch) view.findViewById(R.id.imageViewTouch);
		mapDateTime = (TextView) view.findViewById(R.id.map_datetime);
		// Default image configurations
		// mImage.setDisplayType( DisplayType.FIT_IF_BIGGER );
		mImage.setDisplayType(DisplayType.NONE);
		updateMap();
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		this.optionsMenu = menu;
		inflater.inflate(R.menu.activity__view_image, menu);

		// persisting the loading progress on screen rotation.
		if (downloadMapTask != null && downloadMapTask.get() != null && downloadMapTask.get().getStatus() == AsyncTask.Status.RUNNING) {
			final MenuItem refreshItem = menu.findItem(R.id.refresh_item);
			if (refreshItem != null) {
				refreshItem.setActionView(R.layout.action_progressbar);
			}
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.refresh_item:
			downloadMap(MAP_URL);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		//Releasing the weak reference of the activity.
		mListener = null;
	}

	public void updateMap() {
		String imagePath = getActivity().getExternalFilesDir(Context.STORAGE_SERVICE).getAbsolutePath() + File.separator + "map.jpg";
		Uri imageUri = Uri.parse(imagePath);
		final int size = -1; // use the original image size
		//TODO: check the existence of file before decode.
		Bitmap bitmap = DecodeUtils.decode(getActivity().getApplicationContext(), imageUri, size, size);
		if (null != bitmap) {
			mImage.setImageBitmap(bitmap, null, ImageViewTouchBase.ZOOM_INVALID, ImageViewTouchBase.ZOOM_INVALID);
		
			if(mapDateTime != null)
			{
				mapDateTime.setText(getFormattedLastModifiedTime());
			}
		}
	}
	
	public void updateDownloadProgress(int progress)
	{
		if(mListener != null)
		{
			mListener.updateProgress(progress);
		}
	}

	public void setRefreshActionButtonState(final boolean refreshing) {
		if (optionsMenu != null) {
			final MenuItem refreshItem = optionsMenu.findItem(R.id.refresh_item);
			if (refreshItem != null) {
				if (refreshing) {
					refreshItem.setActionView(R.layout.action_progressbar);
				} else {
					refreshItem.setActionView(null);
				}
			}
		}
	}

	public void downloadMap(final String url) {
		if (isNetworkAvailable()) {
			if (downloadMapTask != null && downloadMapTask.get() != null && downloadMapTask.get().getStatus() == AsyncTask.Status.RUNNING) {
				setRefreshActionButtonState(true);
			} else {
				DownloadFileFromURL downloadTast = new DownloadFileFromURL(this, getActivity().getExternalFilesDir(Context.STORAGE_SERVICE).getAbsolutePath());
				downloadMapTask = new WeakReference<DownloadFileFromURL>(downloadTast);
				downloadMapTask.get().execute(url);
			}
		} else {
			Toast.makeText(getActivity(), getActivity().getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
		}
	}

	public void updateLastModifiedTime(final long timeInms) {
		SharedPreferences.Editor editor = preference_General.edit();
		editor.putLong("kal_infra_enh_update_time", timeInms);
		editor.commit();
	}

	public long getLastModifiedTime() {
		long lastModTime = 0;
		if (preference_General != null) {
			lastModTime = preference_General.getLong("kal_infra_enh_update_time", 0l);
		}
		return lastModTime;
	}

	public String getFormattedLastModifiedTime() {
		String formattedTime = "";
		long lastModTime = getLastModifiedTime();
	
		if (lastModTime > 0) {
			// Calendar indianTime = new
			// GregorianCalendar(TimeZone.getTimeZone("GMT+5:30"));
			Calendar indianTime = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
			indianTime.setTimeInMillis(getLastModifiedTime());
			formattedTime = getActivity().getString(R.string.updated_as_on)+ String.format(Locale.US, "%tb %te, %tl:%tM %tp ",	indianTime, indianTime, indianTime, indianTime,indianTime);
			//For Debug. date with seconds
			// formattedTime = "As on "+ String.format(Locale.US,"%tb %te, %tl:%tM:%tS %tp  ",indianTime, indianTime, indianTime,indianTime,indianTime,indianTime);
			indianTime = null;
		}
		return formattedTime;
	}
		
	private boolean isNetworkAvailable() {
		//http://stackoverflow.com/questions/4238921/android-detect-whether-there-is-an-internet-connection-available
	    ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}
	
	
	
	
	// FOR TESTING & DEBUGGING

	/*
	 * @Override public void onResume() { Log.e("shahul", "onResume called");
	 * super.onResume(); }
	 * 
	 * @Override public void onStart() { super.onStart(); Log.e("shahul",
	 * "onStart called"); }
	 */
	
	// public Fragment_ViewMap() {
	// Log.d("shahul", "fragment initiated");
	// }


}
