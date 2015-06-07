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

import android.content.SharedPreferences;

import org.androidannotations.annotations.EBean;

@EBean
public class PreferenceUtil {
    public static final String KEY_POST_FIX = "_update_time";

    public static void updateLastModifiedTime(SharedPreferences preference_General, String mapType, final String lastModifiedDateTime) {
        SharedPreferences.Editor editor = preference_General.edit();
        editor.putLong(mapType + KEY_POST_FIX, FormatUtils.timeStringToMillis(lastModifiedDateTime));
        editor.apply();
    }

    public static long getLastModifiedTime(SharedPreferences preference_General, String mapType) {
        long lastModifiedDateTime = 0l;
        if (preference_General != null) {
            lastModifiedDateTime = preference_General.getLong(mapType + KEY_POST_FIX, 0l);
        }
        return lastModifiedDateTime;
    }
}
