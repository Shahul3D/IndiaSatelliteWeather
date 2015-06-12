/*
 *
 *  * ******************************************************************************
 *  *  * Copyright (c) 2015.  Shahul Hameed.
 *  *  *
 *  *  * Licensed under GNU GENERAL PUBLIC LICENSE;
 *  *  * you may not use this file except in compliance with the License.
 *  *  *
 *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  * See the License for the specific language governing permissions and
 *  *  * limitations under the License.
 *  *  ******************************************************************************
 *
 */

package com.shahul3d.indiasatelliteweather.utils;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

public class AnalyticsUtil {
    Context context;
    // Prevent hits from being sent to reports, i.e. during testing.
    private static final boolean GA_IS_DRY_RUN = false;
    private static final String GLOBAL_PROPERTY_ID = "UA-46030637-1";
    Tracker gaTracker = null;


    public AnalyticsUtil(Context context) {
        this.context = context;
    }

/*
    synchronized Tracker getTracker() {
        GoogleAnalytics.getInstance(this).setDryRun(true);
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        Tracker t = analytics.newTracker(R.xml.global_tracker);
        return t;
    }
*/


    /*
     * Method to handle basic Google Analytics initialization.
     * All Google Analytics work occurs off the main thread.
     */
    public void initializeGATracker() {

        // Set dryRun flag.
        GoogleAnalytics.getInstance(context).setDryRun(GA_IS_DRY_RUN);

        // Set the log level to verbose if dryRun.
        // DEFAULT is set to DRY RUN (only logging will happen)
        GoogleAnalytics.getInstance(context).getLogger()
                .setLogLevel(GA_IS_DRY_RUN ? Logger.LogLevel.VERBOSE : Logger.LogLevel.WARNING);

        // Set the opt out flag when user updates a tracking preference.
        /*
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
                        if (key.equals(TRACKING_PREF_KEY)) {
                            GoogleAnalytics.getInstance(getApplicationContext()).setAppOptOut(
                                    pref.getBoolean(key, false));
                        }
                    }
                });
                */
    }

    public synchronized Tracker getTracker() {
        if (gaTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
            gaTracker = analytics.newTracker(GLOBAL_PROPERTY_ID);
        }
        return gaTracker;
    }

    public void trackScreen(String screenName) {

        Tracker tracker = getTracker();

        if (tracker == null) return;

        // Set the screen name.
        tracker.setScreenName(screenName);

        // Send AppView hit.
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void sendAnalyticsTiming(HitBuilders.TimingBuilder event) {
        // Get tracker
        Tracker tracker = getTracker();

        if (tracker == null) return;

        // Send TimingBuilder Map
        tracker.send(event.build());
    }
}
