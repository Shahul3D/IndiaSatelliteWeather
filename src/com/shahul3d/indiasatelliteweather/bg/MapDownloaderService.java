package com.shahul3d.indiasatelliteweather.bg;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.shahul3d.indiasatelliteweather.others.AppConstants;
import com.shahul3d.indiasatelliteweather.utils.CommonUtils;
import com.squareup.okhttp.OkHttpClient;

public class MapDownloaderService extends Service {
	final int STATUS_UPDATE_THRESHOLD = 10;
	LocalBroadcastManager localBroadCastInstance;
	private OkHttpClient okHttpClient;


	@Override
	public IBinder onBind(Intent arg0) {
		CommonUtils.printLog("MapDownloaderService Binded");
		return null;
	}

	@Override
	public void onCreate() {
		CommonUtils.printLog("MapDownloaderService Created");
		localBroadCastInstance = LocalBroadcastManager.getInstance(this);
		okHttpClient = new OkHttpClient();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		CommonUtils.printLog("MapDownloaderService Started");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			// TODO: Remove these debug messages
			String requestedMapType = intent.getStringExtra(AppConstants.INTENT_EXTRA_KEY_IN);
			CommonUtils.printLog("MapDownloaderService onCommandStart() : "	+ requestedMapType);

			// Starting a new thread for download
			MAPDownloadThread myThread = new MAPDownloadThread(this, intent);
			myThread.start();
		}
		// NOT_STICKY: No need to restart the service if it get killed by user or by system.
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		CommonUtils.printLog("MapDownloaderService Destroyed");
	}

	
	class MAPDownloadThread extends Thread {
		private static final String INNER_TAG = "Download_";
		Context context;
		Intent intent;
		
		Intent stickyIntent;
		public MAPDownloadThread(Context serviceContext, Intent intent)
		{
			this.context = serviceContext;
			this.intent = intent;
		}

		public void run() {
			//Getting the requested MAP type
			final String requestedMapType = intent.getStringExtra(AppConstants.INTENT_EXTRA_KEY_IN);
			
			this.setName(INNER_TAG+requestedMapType);
			CommonUtils.printLog("new MAPDownloadThread Started for: "+requestedMapType);
			
			InputStream inputStream_conn = null;
			FileOutputStream outFileOutStream = null;
			ByteArrayOutputStream outArrrayIPStream = null;

			try {
				
				//Publishing a Sticky Broadcast to indicate the current operation.
				stickyIntent = new Intent(AppConstants.PREFIX_STICKY_BROADCAST+requestedMapType);
				setStickyIntent(stickyIntent);
				
				URL map_url = new URL(AppConstants.MAP_URL.get(requestedMapType));
				// TODO: handle unknown map type and null cases.
				// TODO: TO handle connection exceptions
				HttpURLConnection connection = okHttpClient.open(map_url);
				// connection.setConnectTimeout(3000);

				// Getting CurrentUpdateTime of the image from the server and
				// compare it with the current image's update time. If both are
				// same, then no need download the same image again.
				long res_update_time = connection.getHeaderFieldDate("Last-Modified", 0);
//				Log.d("shahul", "current modified time: " + res_update_time	+ "  VS previous: " + mapFragment.get().getLastModifiedTime());

				if ((res_update_time == CommonUtils.getLastModifiedTime(null, getApplicationContext(), requestedMapType))) {
					// We have the latest version. No need to downlaod again.
					connection.disconnect();
					returnResult(2,requestedMapType);
					return;
				}
				
				updateProgress(3, requestedMapType); // Updating the user about the download.
				// Log.d("shahul", "Res code" + connection.getResponseCode());
				if (connection.getResponseCode() != 200 || !CommonUtils.storageReady()) {
					connection.disconnect();
					returnResult(-1, requestedMapType);
					return; //TODO: test this scenario
				}
				inputStream_conn = connection.getInputStream();
				outArrrayIPStream = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];

				// for progress update
				long total = 0;
				final long lenghtOfFile = connection.getContentLength();
				int updateTrigger=0;
				final long MAX_DOWNLOAD_PROGRESS=90;
				
				// download the file
				for (int count; (count = inputStream_conn.read(buffer)) != -1;) {
					outArrrayIPStream.write(buffer, 0, count);
					total += count;
					updateTrigger ++;
					if (updateTrigger > STATUS_UPDATE_THRESHOLD) {
						Thread.sleep(3000);
						if (lenghtOfFile > 0) {
							updateTrigger = 0;
							// update status, only if total length is known
							updateProgress(total * MAX_DOWNLOAD_PROGRESS / lenghtOfFile, requestedMapType);
							// TODO: for download, total progress should go upto 80,
							// remaining 20 can be shown for processing.
						}
					}
				}
				byte[] response = outArrrayIPStream.toByteArray();

				Bitmap bmp = BitmapFactory.decodeByteArray(response, 0,	response.length);
				// TODO: Costly operation. need to find some workaround.
				bmp = Bitmap.createBitmap(bmp, 110, 230, 800, 800);
				String sd_card_path = getApplicationContext().getExternalFilesDir(Context.STORAGE_SERVICE).getAbsolutePath();
				File temp_file = new File( sd_card_path + File.separator	+ "temp.jpg");
				outFileOutStream = new FileOutputStream(temp_file.getPath());
				updateProgress(93, requestedMapType);
				
				// Compression Quality set to 100. ie. NO COMPRESSION.
				// Doesn't want to waste costly CPU cycles to save plenty of KBs.
				bmp.compress(Bitmap.CompressFormat.JPEG, 100, outFileOutStream);
				outFileOutStream.flush();
				outFileOutStream.close();
				updateProgress(97, requestedMapType);

//				boolean success = 
				temp_file.renameTo(new File(sd_card_path,	requestedMapType+".jpg"));
				//Log.d("shahul", "Map  saved to: " + temp_file.getAbsolutePath()	+ ". Overwritten? = " + success);

				// TODO: direct communication with the fragment should be avoided.
				// Updating the current image's modified time in the preference.
				CommonUtils.updateLastModifiedTime(null, getApplicationContext(), requestedMapType, res_update_time);
				returnResult(1, requestedMapType);
			} catch (Exception e) {
				returnResult(-2,requestedMapType);
				CommonUtils.printLog("Error in retrieving the Map: "+ e.getMessage());
				CommonUtils.trackException("DownloadMapError", e);
				e.printStackTrace();
			} finally {
				try {
					updateProgress(100, requestedMapType);
					removeStickyIntent(stickyIntent);
					
					if (inputStream_conn != null)
						inputStream_conn.close();
					if (outFileOutStream != null)
						outFileOutStream.close();
					if (outArrrayIPStream != null)
						outArrrayIPStream.close();
				} catch (IOException e) {
					
					CommonUtils.printLog("Error in closing file connections: "+ e.getMessage());
					CommonUtils.trackException("Error in closing file connections", e);
					e.printStackTrace();
				}
			}
			   
			   returnResult(1,requestedMapType);
			   removeStickyIntent(stickyIntent);
			   stickyIntent = null;

			   updateProgress(100,requestedMapType);
			
			
			CommonUtils.printLog("new MAPDownloadThread Ended");
		}
	}
	
	
	private void setStickyIntent(Intent intent) {
		intent.putExtra("running", true);
		this.sendStickyBroadcast(intent);
	}
	
	private void removeStickyIntent(Intent intent)
	{
		this.removeStickyBroadcast(intent);
	}
	
	private void updateProgress(long progress, String requestedMapType) {
		//TODO: Try to re use the Intent instaed of craeting a  new one on each call.
		Intent progressUpdateIntent = new Intent(AppConstants.ACTION_DOWNLOAD_MAP_PROGRES_UPDATE);
		progressUpdateIntent.putExtra(AppConstants.INTENT_EXTRA_KEY_UPDATE, (int) progress);
		progressUpdateIntent.putExtra(AppConstants.INTENT_EXTRA_KEY_MAP_TYPE, requestedMapType);
		localBroadCastInstance.sendBroadcast(progressUpdateIntent);
		CommonUtils.printLog("Sent update broadcast: " + progress);
	}

	private void returnResult(int result, String requestedMapType) {
		//TODO: Try to re use the Intent instaed of craeting a  new one on each call.
		Intent resultIntent = new Intent(AppConstants.ACTION_DOWNLOAD_MAP_RESPONSE);
		resultIntent.putExtra(AppConstants.INTENT_EXTRA_KEY_OUT, result);
		resultIntent.putExtra(AppConstants.INTENT_EXTRA_KEY_MAP_TYPE, requestedMapType);
		localBroadCastInstance.sendBroadcast(resultIntent);
		CommonUtils.printLog("Sent result broadcast");
	}
}