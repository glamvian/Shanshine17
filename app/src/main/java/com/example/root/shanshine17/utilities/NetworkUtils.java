package com.example.root.shanshine17.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by glamvian on 27/04/17.
 * these utilities will be used to communicate with the weather servers
 */

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String DYNAMIC_WEATHER_URL = "https://andfun-weather.udacity.com/weather";
    private static final String STATIC_WEATHER_URL = "https://andfun-weather.udacity.com/staticweather";

    private static final String FORECAST_BASE_URL = STATIC_WEATHER_URL;

    /**
     * NOTE: These values only effect response form OpenWeatherMap, NOT from the fake weater
     * server. they are simply here to allow us to teach you how to build a URL if you were to use
     * a real API.if you want to connect your app to OpenWeatherMap's, feel free to! However,
     * we are not going to show you how to do so in this course
     */

    //ther format we want our API to return
    private static final String format = "json";
    //the unis we want our API to return
    private static final String units = "metric";
    //the number of days we want our API to return
    private static final int numDays = 14;


    final static String QUERY_PARAM = "q";
    final static String LAT_PARAM = "lat";
    final static String LON_PARAM = "lon";
    final static String FORMAT_PARAM = "mode";
    final static String UNIST_PARAM = "units";
    final static String DAYS_PARAM = "cnt";

    /**
     * Builds the URL used to talk to the weather server using a location. this location is based
     * on the query capabilities of the weather priveder thath we are using
     *@param locationQuery the location that will be queried for
     * @return The URL to use to query the weather server
     */
    public static URL buildUrl(String locationQuery){
        Uri buildUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, locationQuery)
                .appendQueryParameter(FORMAT_PARAM, format)
                .appendQueryParameter(UNIST_PARAM, units)
                .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                .build();

        URL url = null;
        try {
            url = new URL(buildUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        Log.v(TAG, "built URI" + url);
        return url;
    }
    /**
     * Builds the URL used to talk to the weather server using latitude and longitude of a location
     *
     * @param lat the latitude of the location
     * @param lon the longitude of the location
     * @return the url wo use to query the weather server
     */
    public static URL buildUrl(Double lat, Double lon){
        return null;
    }

    /**
     * This method returns the entire result from the HTTP response
     *
     * @param url the url to fetch the HTTP response from
     * @return the contents of the HTTP response
     * @throws IOException related to network and stream reading
     */
    public static String getResponseFromHttpUrl (URL url) throws IOException{
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput){
                return scanner.next();
            }else {
                return null;
            }
        }finally {
            urlConnection.disconnect();
        }
    }
}
