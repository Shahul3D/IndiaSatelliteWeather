/*
 * ******************************************************************************
 *  * Copyright (c) 2015.  Shahul Hameed.
 *  *
 *  * Licensed under GNU GENERAL PUBLIC LICENSE;
 *  * you may not use this file except in compliance with the License.
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  ******************************************************************************
 */

package com.shahul3d.indiasatelliteweather.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.shahul3d.indiasatelliteweather.controllers.WeatherApplication;

import org.androidannotations.annotations.EBean;

@EBean
public class PreferenceUtil {
    public static final String KEY_POST_FIX = "_update_time";

    public static void updateLastModifiedTime(String mapType, final String lastModifiedDateTime) {
        final SharedPreferences preference_General = getBackGroundPreference();
        SharedPreferences.Editor editor = preference_General.edit();
        editor.putLong(mapType + KEY_POST_FIX, FormatUtils.timeStringToMillis(lastModifiedDateTime));
        editor.apply();
    }

    public static long getLastModifiedTime(String mapType) {
        final SharedPreferences preference_General = getBackGroundPreference();
        long lastModifiedDateTime = 0l;
        if (preference_General != null) {
            lastModifiedDateTime = preference_General.getLong(mapType + KEY_POST_FIX, 0l);
        }
        return lastModifiedDateTime;
    }

    public static int getAutoRefreshInterval() {
        final SharedPreferences defaultPreference = getDefaultSharedPreferences();
        int interval = -1;
        if (defaultPreference != null) {
            final boolean isAutoUpdateEnabled = defaultPreference.getBoolean("autoUpdateMaps", false);
            if (isAutoUpdateEnabled) {
                interval = Integer.valueOf(defaultPreference.getString("autoUpdateInterval", "-1"));
            }
        }
        return interval;
    }

    public static int getDefaultMapType() {
        final SharedPreferences defaultPreference = getDefaultSharedPreferences();
        int type = 0;
        if (defaultPreference != null) {
            type = Integer.valueOf(defaultPreference.getString("defaultMAP", "0"));
        }
        return type;
    }

    public static SharedPreferences getBackGroundPreference() {
        return WeatherApplication.getContext().getSharedPreferences("BackgroundPreference", Activity.MODE_PRIVATE);
    }

    public static SharedPreferences getDefaultSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(WeatherApplication.getContext());
    }
}
