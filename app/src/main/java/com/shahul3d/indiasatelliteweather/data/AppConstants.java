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

package com.shahul3d.indiasatelliteweather.data;

import org.androidannotations.annotations.EBean;

import java.util.HashMap;
import java.util.Map;

@EBean(scope = EBean.Scope.Singleton)
public class AppConstants {

    //Weather Map Types.
    public static final String MAP_UV = "map_uv";
    public static final String MAP_IR = "map_ir";
    public static final String MAP_COLOR = "map_color";
    public static final String MAP_HEAT = "map_heat";
    public static final String MAP_WIND_FLOW = "map_windflow";

    //Intent name for downloader service.
    public static final String DOWNLOAD_INTENT_NAME = "DOWNLOAD_MAP_TYPE";

    //URL of supported maps.
    public static final Map<String, String> MAP_URL;
    static
    {
        MAP_URL = new HashMap<String, String>();
        MAP_URL.put(MAP_UV, "http://www.imd.gov.in/section/satmet/img/sector-eir.jpg");
        MAP_URL.put(MAP_COLOR, "http://tropic.ssec.wisc.edu/real-time/indian/images/xxirm5bbm.jpg");
        MAP_URL.put(MAP_IR, "http://tropic.ssec.wisc.edu/real-time/indian/images/irnm5.GIF");
        MAP_URL.put(MAP_HEAT, "http://www.imd.gov.in/section/satmet/img/3Dasiasec_ir1_temp.jpg");
        MAP_URL.put(MAP_WIND_FLOW, "http://tropic.ssec.wisc.edu/real-time/indian/winds/wm5midshr.GIF");
    }

    //Download configurations
    public final int STATUS_UPDATE_THRESHOLD = 10;
    public final long MAX_DOWNLOAD_PROGRESS = 90;

    public String getMapType(int type) {
        String mapType = MAP_UV;
        switch (type) {
            case 1:
                mapType = MAP_COLOR;
                break;
            case 2:
                mapType = MAP_IR;
                break;
            case 3:
                mapType = MAP_HEAT;
                break;
            case 4:
                mapType = MAP_WIND_FLOW;
                break;
        }
        return mapType;
    }
}
