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

import org.androidannotations.annotations.EBean;

@EBean
public class CommonUtils {
    public static void trackException(String log, Exception e)
    {
        Log.e("Unable to set http cache");
        Crashlytics.log(log);
        Crashlytics.logException(e);
    }
}
