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

package com.shahul3d.indiasatelliteweather.data;

import java.util.HashMap;
import java.util.Map;

public class AppConstants {
    //Tab Labels
    public static final String LIVE_MAP_TAB_LABELS[] = new String[]{"Infra Red","Satellite", "Color Composite" , "Heat Map", "Wind Direction"};
    public static final String FORECAST_TAB_LABELS[] = new String[]{"Tomorrow", "Day 2", "Day 3", "Day 4", "Day 5", "Day 6", "Day 7", "Day 8", "Day 9", "Day 10"};
    public static final String TEMP_FORECAST_TAB_LABELS[] = new String[]{"ALL", "South", "North", "East", "West"};
    //Weather Map Types.
    public static final String MAP_UV = "map_uv";
    public static final String MAP_IR = "map_ir";
    public static final String MAP_COLOR = "map_color";
    public static final String MAP_HEAT = "map_heat";
    public static final String MAP_WIND_FLOW = "map_windflow";

    //Forecast Map Types
    public static final String FORCAST_24 = "forcast_24";
    public static final String FORCAST_48 = "forcast_48";
    public static final String FORCAST_72 = "forcast_72";
    public static final String FORCAST_96 = "forcast_96";
    public static final String FORCAST_120 = "forcast_120";
    public static final String FORCAST_144 = "forcast_144";
    public static final String FORCAST_168 = "forcast_168";
    public static final String FORCAST_192 = "forcast_192";
    public static final String FORCAST_216 = "forcast_216";
    public static final String FORCAST_240 = "forcast_240";

    //Temperature Forecast Map Types
    public static final String TEMP_FORECAST_ALL = "temp_forecast_all";
    public static final String TEMP_FORECAST_SOUTH = "temp_forecast_south";
    public static final String TEMP_FORECAST_NORTH = "temp_forecast_north";
    public static final String TEMP_FORECAST_EAST = "temp_forecast_east";
    public static final String TEMP_FORECAST_WEST = "temp_forecast_west";

    public enum MapType {
        LIVE(0), FORECAST(1),TEMP_FORECAST(2);
        public int value;

        MapType(int value) {
            this.value = value;
        }
    }

    //Intent name for downloader service.
    public static final String DOWNLOAD_INTENT_NAME = "DOWNLOAD_MAP_ID";
    public static final String DOWNLOAD_MAP_TYPE = "DOWNLOAD_MAP_TYPE";

    public static final long HTTP_DEFAULT_READ_TIMEOUT_MILLIS = 180000; //3 mins
    public static final long HTTP_DEFAULT_WRITE_TIMEOUT_MILLIS = 120000; //2 mins
    public static final long HTTP_DEFAULT_CONNECT_TIMEOUT_MILLIS = 60000;//1 min


    //URL of supported maps.
    public static final Map<String, String> MAP_URL;

    static {
        MAP_URL = new HashMap<String, String>();
        MAP_URL.put(MAP_UV, "http://satellite.imd.gov.in/img/3Dasiasec_ir1.jpg");
        MAP_URL.put(MAP_COLOR, "http://tropic.ssec.wisc.edu/real-time/indian/images/xxirm5bbm.jpg");
        MAP_URL.put(MAP_IR, "http://tropic.ssec.wisc.edu/real-time/indian/images/irnm5.GIF");
        MAP_URL.put(MAP_HEAT, "http://satellite.imd.gov.in/img/3Dasiasec_bt1.jpg");
        MAP_URL.put(MAP_WIND_FLOW, "http://tropic.ssec.wisc.edu/real-time/indian/winds/wm5midshr.GIF");

        MAP_URL.put(FORCAST_24, "http://nwp.imd.gov.in/gfs/24hGFS1534rain.gif");
        MAP_URL.put(FORCAST_48, "http://nwp.imd.gov.in/gfs/48hGFS1534rain.gif");
        MAP_URL.put(FORCAST_72, "http://nwp.imd.gov.in/gfs/72hGFS1534rain.gif");
        MAP_URL.put(FORCAST_96, "http://nwp.imd.gov.in/gfs/96hGFS1534rain.gif");
        MAP_URL.put(FORCAST_120, "http://nwp.imd.gov.in/gfs/120hGFS1534rain.gif");
        MAP_URL.put(FORCAST_144, "http://nwp.imd.gov.in/gfs/144hGFS1534rain.gif");
        MAP_URL.put(FORCAST_168, "http://nwp.imd.gov.in/gfs/168hGFS1534rain.gif");
        MAP_URL.put(FORCAST_192, "http://nwp.imd.gov.in/gfs/192hGFS1534rain.gif");
        MAP_URL.put(FORCAST_216, "http://nwp.imd.gov.in/gfs/216hGFS1534rain.gif");
        MAP_URL.put(FORCAST_240, "http://nwp.imd.gov.in/gfs/240hGFS1534rain.gif");

        MAP_URL.put(TEMP_FORECAST_ALL, "http://www.monsoondata.org/wx2/temp12.png");
        MAP_URL.put(TEMP_FORECAST_SOUTH, "http://www.monsoondata.org/wx2/ezindia1_day1.png");
        MAP_URL.put(TEMP_FORECAST_NORTH, "http://www.monsoondata.org/wx2/ezindia3_day1.png");
        MAP_URL.put(TEMP_FORECAST_EAST, "http://www.monsoondata.org/wx2/ezindia4_day1.png");
        MAP_URL.put(TEMP_FORECAST_WEST, "http://www.monsoondata.org/wx2/ezindia2_day1.png");
    }

    //Download configurations
    public static final int STATUS_UPDATE_THRESHOLD = 10;
    //Remaining 10% is for Trimming the image & storing it on disk.
    public static final long MAX_DOWNLOAD_PROGRESS = 90;

    public static String getMapType(int tabID, int mapType) {
        String mapFileName = "";
        if (mapType == MapType.LIVE.value) {
            switch (tabID) {
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
            switch (tabID) {
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
                case 7:
                    mapFileName = FORCAST_192;
                    break;
                case 8:
                    mapFileName = FORCAST_216;
                    break;
                case 9:
                    mapFileName = FORCAST_240;
                    break;
            }
        }
        else if (mapType == MapType.TEMP_FORECAST.value) {
            switch (tabID) {
                case 0:
                    mapFileName = TEMP_FORECAST_ALL;
                    break;
                case 1:
                    mapFileName = TEMP_FORECAST_SOUTH;
                    break;
                case 2:
                    mapFileName = TEMP_FORECAST_NORTH;
                    break;
                case 3:
                    mapFileName = TEMP_FORECAST_EAST;
                    break;
                case 4:
                    mapFileName = TEMP_FORECAST_WEST;
                    break;
            }
        }

        return mapFileName;
    }

    public static String getMapURL(String mapFileName) {
        return MAP_URL.get(mapFileName);
    }
}
