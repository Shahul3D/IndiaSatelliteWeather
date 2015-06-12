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

package com.shahul3d.indiasatelliteweather.controllers;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.shahul3d.indiasatelliteweather.R;
import com.shahul3d.indiasatelliteweather.utils.AnalyticsUtil;

import io.fabric.sdk.android.Fabric;

public class WeatherApplication extends Application {
    private static Context mContext;
    public static AnalyticsUtil analyticsHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        Fabric.with(this, new Crashlytics());
        analyticsHandler = new AnalyticsUtil(this);

        //Initializing default values for preferences at first app launch.
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Initialization for Google Analytics Instance.
        analyticsHandler.initializeGATracker();
    }

    public static Context getContext() {
        return mContext;
    }
}
