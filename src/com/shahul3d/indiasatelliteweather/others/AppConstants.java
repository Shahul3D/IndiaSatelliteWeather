package com.shahul3d.indiasatelliteweather.others;

import java.util.HashMap;
import java.util.Map;

public final class AppConstants {
	private AppConstants() {
		// supress object creation
	}

	//Intent Extra Keys
	public static final String INTENT_EXTRA_KEY_IN = "EXTRA_IN";
	public static final String INTENT_EXTRA_KEY_OUT = "EXTRA_OUT";
	public static final String INTENT_EXTRA_KEY_UPDATE = "EXTRA_UPDATE";
	public static final String INTENT_EXTRA_KEY_MAP_TYPE = "EXTRA_MAP_TYPE";
	
	//BroadCast Message Actions
	public static final String ACTION_DOWNLOAD_MAP_RESPONSE = "DownloadMAPIntentService.RESPONSE";
	public static final String ACTION_DOWNLOAD_MAP_PROGRES_UPDATE = "DownloadMAPIntentService.UPDATE";
	
	//Weather Map Types.
	public static final String PREFIX_STICKY_BROADCAST = "com.shahul3d.IndiaSatelliteWeather.";
	public static final String MAP_INDIA_WEATHER_UV = "india_weather_uv";
	public static final String MAP_INDIA_WEATHER_IR = "india_weather_ir";
	public static final String MAP_INDIA_WEATHER_COLOR = "india_color";
	public static final String MAP_INDIA_WIND_FLOW = "india_windflow";

	//Weather MAP type - Link relationships.
	public static final Map<String, String> MAP_URL;
    static
    {
        MAP_URL = new HashMap<String, String>();
        MAP_URL.put(MAP_INDIA_WEATHER_UV, "http://www.imd.gov.in/section/satmet/img/sector-eir.jpg");
        MAP_URL.put(MAP_INDIA_WEATHER_COLOR, "http://tropic.ssec.wisc.edu/real-time/indian/images/xxirm5bbm.jpg");
        MAP_URL.put(MAP_INDIA_WEATHER_IR, "http://tropic.ssec.wisc.edu/real-time/indian/images/irnm5.GIF");
        MAP_URL.put(MAP_INDIA_WIND_FLOW, "http://tropic.ssec.wisc.edu/real-time/indian/winds/wm5midshr.GIF");
    }
}
