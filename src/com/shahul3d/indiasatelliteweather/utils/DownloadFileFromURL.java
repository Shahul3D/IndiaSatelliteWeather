package com.shahul3d.indiasatelliteweather.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.shahul3d.indiasatelliteweather.Fragment_ViewMap;
import com.squareup.okhttp.OkHttpClient;

public class DownloadFileFromURL extends AsyncTask<String, Integer, Integer> {

	private OkHttpClient okHttpClient = new OkHttpClient();
	Fragment_ViewMap mapFragment;
	private String sd_card_path;

	public DownloadFileFromURL(Fragment_ViewMap myfrag, String sd_path) {
		// TODO: direct communication with the fragment should be avoided.
		mapFragment = myfrag;
		sd_card_path = sd_path;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mapFragment.setRefreshActionButtonState(true);
	}

	@Override
	protected Integer doInBackground(String... map_urls) {
		int down_status = -1;
		InputStream inputStream_conn = null;
		FileOutputStream outFileOutStream = null;
		ByteArrayOutputStream outArrrayIPStream = null;

		try {
			URL map_url = new URL(map_urls[0]);
			// TODO: TO handle connection exceptions
			HttpURLConnection connection = okHttpClient.open(map_url);
			// connection.setConnectTimeout(3000);

			// Getting CurrentUpdateTime of the image from the server and
			// compare it with the current image's update time. If both are
			// same, then no need download the same image again.
			long res_update_time = connection.getHeaderFieldDate("Last-Modified", 0);
			Log.d("shahul", "current modified time: " + res_update_time	+ "  VS previous: " + mapFragment.getLastModifiedTime());
			if (res_update_time == mapFragment.getLastModifiedTime()) {
				// We have the latest version. No need to downlaod again.
				connection.disconnect();
				return 2;
			}
			publishProgress(10); // Updating the user about the download.
			// Log.d("shahul", "Res code" + connection.getResponseCode());
			if (connection.getResponseCode() != 200) {
				connection.disconnect();
				return -1;
			}
			inputStream_conn = connection.getInputStream();
			outArrrayIPStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];

			// for progress update
			long total = 0;
			int lenghtOfFile = connection.getContentLength();

			// download the file
			for (int count; (count = inputStream_conn.read(buffer)) != -1;) {
				outArrrayIPStream.write(buffer, 0, count);
				total += count;
				if (lenghtOfFile > 0) {
					// update status, only if total length is known
					publishProgress((int) (total * 100 / lenghtOfFile));
				}
			}
			byte[] response = outArrrayIPStream.toByteArray();

			Bitmap bmp = BitmapFactory.decodeByteArray(response, 0,	response.length);
			// TODO: Costly operation. need to find some workaround.
			bmp = Bitmap.createBitmap(bmp, 110, 230, 800, 800);

			File temp_file = new File(sd_card_path + File.separator	+ "temp.jpg");
			outFileOutStream = new FileOutputStream(temp_file.getPath());
			// Compression Quality set to 100. ie. NO COMPRESSION.
			// Doesn't want to waste costly CPU cycles to save plenty of KBs.
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, outFileOutStream);
			outFileOutStream.flush();
			outFileOutStream.close();

			boolean success = temp_file.renameTo(new File(sd_card_path,	"map.jpg"));
			//Log.d("shahul", "Map  saved to: " + temp_file.getAbsolutePath()	+ ". Overwritten? = " + success);

			// TODO: direct communication with the fragment should be avoided.
			// Updating the current image's modified time in the preference.
			mapFragment.updateLastModifiedTime(res_update_time);
			down_status = 1;

		} catch (Exception e) {
			Log.e("Error in retrieving the Map: ", e.getMessage());
			e.printStackTrace();
		} finally {
			// removes progress visibility.
			publishProgress(100);
			try {
				if (inputStream_conn != null)
					inputStream_conn.close();
				if (outFileOutStream != null)
					outFileOutStream.close();
				if (outArrrayIPStream != null)
					outArrrayIPStream.close();
			} catch (IOException e) {
				Log.e("Error in closing file connections: ", e.getMessage());
				e.printStackTrace();
			}
		}
		return down_status;
	}

	protected void onProgressUpdate(Integer... progress) {
		mapFragment.updateDownloadProgress(progress[0]);
	}

	@Override
	protected void onPostExecute(Integer result) {

		// Error
		if (result < 0) {
			Toast.makeText(mapFragment.getActivity(),"Unable to retrive the MAP!", Toast.LENGTH_SHORT).show();
		}
		// MAP downloaded successfully
		else if (result == 1) {
			mapFragment.updateMap();
			Toast.makeText(mapFragment.getActivity(),"Map Successfully Updated", Toast.LENGTH_SHORT).show();
		}
		// NO update available
		else if (result == 2) {
			Toast.makeText(mapFragment.getActivity(), "No update available!", Toast.LENGTH_SHORT).show();
		}
		mapFragment.setRefreshActionButtonState(false);
		Log.d("shahul", "Download MAP completed with result: " + result);
	}
}
