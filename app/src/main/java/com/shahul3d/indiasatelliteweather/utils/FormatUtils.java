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

import com.crashlytics.android.Crashlytics;
import com.noveogroup.android.log.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtils {
    public static String getTimeAgo(long time) {
        final int SECOND_MILLIS = 1000;
        final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        final int DAY_MILLIS = 24 * HOUR_MILLIS;

        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static long timeStringToMillis(String timeString) {
        long timeInMilliseconds = 0l;
//    String givenDateString = "Mon, 02 Mar 2015 19:27:55 GMT";
        final String pattern = "EEE, dd MMM yyyy HH:mm:ss z";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
        try {
            Date mDate = sdf.parse(timeString);
            timeInMilliseconds = mDate.getTime();
        } catch (ParseException e) {
            Log.e("Unable to parse last downloaded date");
            Crashlytics.logException(e);
        }

        return timeInMilliseconds;
    }
}
