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

import java.util.HashMap;
import java.util.Map;

public class AppConstants {
    //Weather Map Types.
    public static final String MAP_UV = "map_uv";
    public static final String MAP_IR = "map_ir";
    public static final String MAP_COLOR = "map_color";
    public static final String MAP_HEAT = "map_heat";
    public static final String MAP_WIND_FLOW = "map_windflow";

    //Forcast Map Types
    public static final String FORCAST_24 = "forcast_24";
    public static final String FORCAST_48 = "forcast_48";
    public static final String FORCAST_72 = "forcast_72";
    public static final String FORCAST_96 = "forcast_96";
    public static final String FORCAST_120 = "forcast_120";
    public static final String FORCAST_144 = "forcast_144";
    public static final String FORCAST_168 = "forcast_168";

    public enum MapType {
        LIVE(0), FORECAST(1);
        public int value;

        MapType(int value) {
            this.value = value;
        }
    }

    //Intent name for downloader service.
    public static final String DOWNLOAD_INTENT_NAME = "DOWNLOAD_MAP_ID";
    public static final String DOWNLOAD_MAP_TYPE = "DOWNLOAD_MAP_TYPE";

    //URL of supported maps.
    public static final Map<String, String> MAP_URL;

    static {
        MAP_URL = new HashMap<String, String>();
        MAP_URL.put(MAP_UV, "http://www.imd.gov.in/section/satmet/img/sector-eir.jpg");
        MAP_URL.put(MAP_COLOR, "http://tropic.ssec.wisc.edu/real-time/indian/images/xxirm5bbm.jpg");
        MAP_URL.put(MAP_IR, "http://tropic.ssec.wisc.edu/real-time/indian/images/irnm5.GIF");
        MAP_URL.put(MAP_HEAT, "http://www.imd.gov.in/section/satmet/img/3Dasiasec_ir1_temp.jpg");
        MAP_URL.put(MAP_WIND_FLOW, "http://tropic.ssec.wisc.edu/real-time/indian/winds/wm5midshr.GIF");

        MAP_URL.put(FORCAST_24, "http://www.imd.gov.in/section/nhac/img/24hGFS574rain.gif");
        MAP_URL.put(FORCAST_48, "http://www.imd.gov.in/section/nhac/img/48hGFS574rain.gif");
        MAP_URL.put(FORCAST_72, "http://www.imd.gov.in/section/nhac/img/72hGFS574rain.gif");
        MAP_URL.put(FORCAST_96, "http://www.imd.gov.in/section/nhac/img/96hGFS574rain.gif");
        MAP_URL.put(FORCAST_120, "http://www.imd.gov.in/section/nhac/img/120hGFS574rain.gif");
        MAP_URL.put(FORCAST_144, "http://www.imd.gov.in/section/nhac/img/144hGFS574rain.gif");
        MAP_URL.put(FORCAST_168, "http://www.imd.gov.in/section/nhac/img/168hGFS574rain.gif");
    }

    //Download configurations
    public static final int STATUS_UPDATE_THRESHOLD = 10;
    public static final long MAX_DOWNLOAD_PROGRESS = 90;

    public static String getMapType(int mapID, int mapType) {
        String mapFileName = "";
        if (mapType == MapType.LIVE.value) {
            switch (mapID) {
                case 0:
                    mapFileName = MAP_UV;
                    break;
                case 1:
                    mapFileName = MAP_COLOR;
                    break;
                case 2:
                    mapFileName = MAP_IR;
                    break;
                case 3:
                    mapFileName = MAP_HEAT;
                    break;
                case 4:
                    mapFileName = MAP_WIND_FLOW;
                    break;
            }
        } else if (mapType == MapType.FORECAST.value) {
            switch (mapID) {
                case 0:
                    mapFileName = FORCAST_24;
                    break;
                case 1:
                    mapFileName = FORCAST_48;
                    break;
                case 2:
                    mapFileName = FORCAST_72;
                    break;
                case 3:
                    mapFileName = FORCAST_96;
                    break;
                case 4:
                    mapFileName = FORCAST_120;
                    break;
                case 5:
                    mapFileName = FORCAST_144;
                    break;
                case 6:
                    mapFileName = FORCAST_168;
                    break;
            }
        }
        return mapFileName;
    }
}
