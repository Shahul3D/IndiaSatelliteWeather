package com.shahul3d.indiasatelliteweather.data;

import org.androidannotations.annotations.EBean;

@EBean(scope = EBean.Scope.Singleton)
public class AppConstants {

    //Weather Map Types.
    public static final String MAP_UV = "map_uv";
    public static final String MAP_IR = "map_ir";
    public static final String MAP_COLOR = "map_color";
    public static final String MAP_WIND_FLOW = "map_windflow";

}
