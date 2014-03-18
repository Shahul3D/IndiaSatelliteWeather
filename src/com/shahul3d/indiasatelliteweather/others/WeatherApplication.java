package com.shahul3d.indiasatelliteweather.others;

import java.util.Vector;

import android.app.Application;


public class WeatherApplication extends Application{

//	http://www.intertech.com/Blog/androids-application-class/

//This value can be altered from multiple threads,  should be thread safe.
public Vector<String> runningServiceList = new Vector<String>(); 
}
