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

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.shahul3d.indiasatelliteweather.R;

public class WeatherApplication extends Application {
    synchronized Tracker getTracker() {
        GoogleAnalytics.getInstance(this).setDryRun(true);
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        Tracker t = analytics.newTracker(R.xml.global_tracker);
        return t;
    }
}
