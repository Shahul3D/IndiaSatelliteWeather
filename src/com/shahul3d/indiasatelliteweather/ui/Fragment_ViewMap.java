package com.shahul3d.indiasatelliteweather.ui;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.DisplayType;

import java.io.File;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.shahul3d.indiasatelliteweather.R;
import com.shahul3d.indiasatelliteweather.bg.MapDownloaderService;
import com.shahul3d.indiasatelliteweather.others.AppConstants;
import com.shahul3d.indiasatelliteweather.utils.CommonUtils;
import com.shahul3d.indiasatelliteweather.utils.DecodeUtils;

public class Fragment_ViewMap extends android.support.v4.app.Fragment {
	private ImageViewTouch mImage;
	private TextView mapDateTime;
	private Menu optionsMenu;
	private SharedPreferences preference_General = null;
	private ActivityListenerInterface mListener;
	//Setting the default MAP type
	private static String currentMAP=AppConstants.MAP_INDIA_WEATHER_UV;

	
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
		downloadMap(currentMAP);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_view_map, container, false);
		mImage = (ImageViewTouch) view.findViewById(R.id.imageViewTouch);
		mapDateTime = (TextView) view.findViewById(R.id.map_datetime);
		// Default image configurations
		// mImage.setDisplayType( DisplayType.FIT_IF_BIGGER );
		mImage.setDisplayType(DisplayType.NONE);
		updateMap(currentMAP);
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		this.optionsMenu = menu;
		inflater.inflate(R.menu.activity__view_image, menu);

		// persisting the loading progress on screen rotation.
		updateRefreshSpinner();
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.refresh_item:
			downloadMap(currentMAP);
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

	public void updateMap(final String mapType) {
		currentMAP = mapType;
		String imagePath = getActivity().getExternalFilesDir(Context.STORAGE_SERVICE).getAbsolutePath() + File.separator + mapType + ".jpg";

		// Checking the file availability without trying to open it.
		int x=1;
		x=2;
		if (CommonUtils.isFileExists(imagePath)) {
			x=4;

			Uri imageUri = Uri.parse(imagePath);
			final int size = -1; // use the original image size
			Bitmap bitmap = DecodeUtils.decode(getActivity().getApplicationContext(), imageUri, size, size);
			if (null != bitmap) {
				mImage.setImageBitmap(bitmap, null,ImageViewTouchBase.ZOOM_INVALID,	ImageViewTouchBase.ZOOM_INVALID);
				if (mapDateTime != null) {
					mapDateTime.setText(CommonUtils.getFormattedLastModifiedTime(preference_General, getActivity(), mapType));
				}
			}
		} else {
			// Cached Map is not available. I know user is going to refresh now!
			// wait. I'm here to help you. :)
//			downloadMap(mapType);
			if (mImage != null && mapDateTime != null) {
				mImage.clear();
				mapDateTime.setText(getActivity().getString(R.string.loading));
			}
		}
	}
	
	public void updateDownloadProgress(final int progress)
	{
		if(mListener != null)
		{
			mListener.updateProgress(progress);
		}
	}

	public void setRefreshActionButtonState(final boolean refreshState) {
		if (optionsMenu != null) {
			final MenuItem refreshItem = optionsMenu.findItem(R.id.refresh_item);
			if (refreshItem != null) {
				if (refreshState) {
					refreshItem.setActionView(R.layout.action_progressbar);
				} else {
					refreshItem.setActionView(null);
				}
			}
		}
	}
	
	
	public void updateRefreshSpinner()
	{
	    IntentFilter filter = new IntentFilter(AppConstants.PREFIX_STICKY_BROADCAST+ currentMAP);
	    Intent stickyIntent = getActivity().registerReceiver(null, filter);
	    
	    if(stickyIntent != null)
	    {
	    	//If any sticky broadcast exists, then start the spinner. current map download is going on.. 
	    	boolean isSticky = stickyIntent.getBooleanExtra("running", false);
	    	if(isSticky)
	    		setRefreshActionButtonState(true);
	    }
	}
	

	public void downloadMap(final String mapType) {
		
		if (isNetworkAvailable()) {
		 //Start MyIntentService
		  Intent intentDownloadMAPIntentService = new Intent(getActivity(), MapDownloaderService.class);
		  intentDownloadMAPIntentService.putExtra(AppConstants.INTENT_EXTRA_KEY_IN, mapType);
		  getActivity().startService(intentDownloadMAPIntentService);
		  setRefreshActionButtonState(true); //Turn on the spinner
		}else {
			Toast.makeText(getActivity(), getActivity().getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
		}
		
	}

	private boolean isNetworkAvailable() {
		//http://stackoverflow.com/questions/4238921/android-detect-whether-there-is-an-internet-connection-available
	    ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}
	
	@Override
	public void onResume() {
	  super.onResume();
	  
	  // Register mMessageReceiver to receive messages.
	  LocalBroadcastManager.getInstance(getActivity()).registerReceiver(MAPDownloadBroadcastResultReceiver, 
			  new IntentFilter(AppConstants.ACTION_DOWNLOAD_MAP_RESPONSE));
	  LocalBroadcastManager.getInstance(getActivity()).registerReceiver(MAPDownloadBroadcastUpdateReceiver, 
			  new IntentFilter(AppConstants.ACTION_DOWNLOAD_MAP_PROGRES_UPDATE));
	  CommonUtils.printLog("Receiver Registered");
	}
	
	
	@Override
	public void onPause() {
	  // Unregister since the activity is not visible
	  LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(MAPDownloadBroadcastResultReceiver);
	  LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(MAPDownloadBroadcastUpdateReceiver);
	  super.onPause();
	  CommonUtils.printLog("Receiver UnRegistered");
	}
	
	private BroadcastReceiver MAPDownloadBroadcastResultReceiver = new BroadcastReceiver() {
	  @Override
	  public void onReceive(Context context, Intent intent) {
		  //TODO: Need to  get MAP type from the intent once multiple maps are supported
	    int result = intent.getIntExtra(AppConstants.INTENT_EXTRA_KEY_OUT,0);
	    String reqMapType = intent.getStringExtra(AppConstants.INTENT_EXTRA_KEY_MAP_TYPE);
	    setRefreshActionButtonState(false);
	    updateMap(reqMapType);
	    CommonUtils.printLog( "Got result: " + result+"  maptype:"+reqMapType);
	  }
	};

	private BroadcastReceiver MAPDownloadBroadcastUpdateReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {
			  //TODO: Need to  get MAP type from the intent once multiple maps are supported
		    int progress = intent.getIntExtra(AppConstants.INTENT_EXTRA_KEY_UPDATE, 0);
		    updateDownloadProgress(progress);
		    CommonUtils.printLog( "Got Update: " + progress);
		  }
		};
}
