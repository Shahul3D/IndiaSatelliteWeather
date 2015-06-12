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

import android.content.Context;

import com.google.android.gms.analytics.HitBuilders;
import com.shahul3d.indiasatelliteweather.controllers.WeatherApplication;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GA_TimeElapseUtils {

    private Map<String, TimeTrack> trackTrackMap = new HashMap<>();

    private static GA_TimeElapseUtils instance = null;
    private static Context mContext;
    Boolean DEBUG = true;

    protected GA_TimeElapseUtils(Context context) {
        if (mContext == null) mContext = context;
    }

    public static GA_TimeElapseUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (GA_TimeElapseUtils.class) {
                if (instance == null) {
                    instance = new GA_TimeElapseUtils(context);
                }
            }
        }
        return instance;
    }

    public void trackStart(String key, TimeTrack timeObj) {
        if (trackTrackMap.containsKey(key)) {
            if (DEBUG) System.out.println("Track Time Restart - " + key);
        }

        trackTrackMap.put(key, timeObj);
    }

    public void trackStop(String key) {

        // Get TimeTrack Object with key
        TimeTrack timeObj = trackTrackMap.get(key);
        if (timeObj == null) return;

        // Analytics Tracking
        WeatherApplication.analyticsHandler.sendAnalyticsTiming(
                new HitBuilders.TimingBuilder()
                        .setCategory(timeObj.category)
                        .setVariable(timeObj.name)
                        .setLabel(timeObj.label)
                        .setValue(timeElapse(timeObj)));

        // Remove completed TimeTrack obj from map
        trackTrackMap.remove(key);
    }

    private long timeElapse(TimeTrack timeObj) {
        return getCurrentTime(timeObj.timeUnit) - timeObj.startTime;
    }

    private static long getCurrentTime(TimeUnit timeUnit) {

        switch (timeUnit) {
            case NANOSECONDS:
                return System.nanoTime();

            case MILLISECONDS:
                return System.currentTimeMillis();

            default:
                return System.currentTimeMillis();
        }

    }

    public static class TimeTrack {

        TimeUnit timeUnit;
        long startTime;

        String category;
        String name;
        String label;

        public TimeTrack(TimeUnit timeUnit, String category, String name, String label) {
            this.timeUnit = timeUnit;
            this.category = category;
            this.name = name;
            this.label = label;
            this.startTime = getCurrentTime(timeUnit);
        }

    }

}