package com.shahul3d.indiasatelliteweather;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.DisplayType;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.shahul3d.indiasatelliteweather.utils.DownloadFileFromURL;

public class Fragment_ViewMap extends android.support.v4.app.Fragment {



	ImageViewTouch mImage;
	private Menu optionsMenu;
	DownloadFileFromURL downloadMapTask;
	SharedPreferences preference_General = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		preference_General = getActivity().getSharedPreferences("BackgroundPreference", Activity.MODE_PRIVATE);
		setHasOptionsMenu(true);
		setRetainInstance(true);

		if (storageReady()) {
			if (downloadMapTask != null && downloadMapTask.getStatus() == AsyncTask.Status.RUNNING) {
				setRefreshActionButtonState(true);
			} else {
				downloadMapTask = new DownloadFileFromURL(this, getActivity().getExternalFilesDir(Context.STORAGE_SERVICE).getAbsolutePath());
				downloadMapTask.execute("http://www.imd.gov.in/section/satmet/img/sector-eir.jpg");
				// TODO: To create application constants to save the URLS.
			}
		} else {
			Toast.makeText(getActivity(), "Unable to download the Map. Please check SD card is accessible", Toast.LENGTH_LONG).show();
			// TODO: To creat a generic method for Logger with logging options.
			// TODO: Internationalize the strings. Then load it from the XML resource.
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_view_map, container, false);
		mImage = (ImageViewTouch) view.findViewById(R.id.imageViewTouch);
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
		if (downloadMapTask != null && downloadMapTask.getStatus() == AsyncTask.Status.RUNNING) {
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
			downloadMap("http://www.imd.gov.in/section/satmet/img/sector-eir.jpg");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void updateMap() {
		String imagePath = getActivity().getExternalFilesDir(Context.STORAGE_SERVICE).getAbsolutePath() + File.separator + "map.jpg";
		Uri imageUri = Uri.parse(imagePath);
		final int size = -1; // use the original image size
		Bitmap bitmap = DecodeUtils.decode(getActivity().getApplicationContext(), imageUri, size, size);
		if (null != bitmap) {
			mImage.setImageBitmap(bitmap, null, ImageViewTouchBase.ZOOM_INVALID, ImageViewTouchBase.ZOOM_INVALID);
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
		if (storageReady()) {
			if (downloadMapTask != null && downloadMapTask.getStatus() == AsyncTask.Status.RUNNING) {
				setRefreshActionButtonState(true);
			} else {
				downloadMapTask = new DownloadFileFromURL(this, getActivity().getExternalFilesDir(Context.STORAGE_SERVICE).getAbsolutePath());
				downloadMapTask.execute(url);
			}
		} else {
			Toast.makeText(getActivity(), "Unable to download the Map. Problem on accessing SD card.", Toast.LENGTH_LONG).show();
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

	public static boolean storageReady() {

		String cardstatus = Environment.getExternalStorageState();
		if (cardstatus.equals(Environment.MEDIA_REMOVED) || cardstatus.equals(Environment.MEDIA_UNMOUNTABLE) || cardstatus.equals(Environment.MEDIA_UNMOUNTED)
				|| cardstatus.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
			return false;
		} else {
			return true;
		}
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

	// private OnCompleteListener mListener;

	// public static interface OnCompleteListener {
	// public abstract void onComplete();
	// }

	// @Override
	// public void onAttach(Activity activity) {
	// super.onAttach(activity);
	// try {
	// this.mListener = (OnCompleteListener)activity;
	// }
	// catch (final ClassCastException e) {
	// throw new ClassCastException(activity.toString() +
	// " must implement OnCompleteListener");
	// }
	// }

}
