package com.example.root.shanshine17;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.root.shanshine17.data.SunshinePreferences;
import com.example.root.shanshine17.utilities.NetworkUtils;
import com.example.root.shanshine17.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity {
        private TextView mTextview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       mTextview = (TextView) findViewById(R.id.tv_weather_data);
        loadWeatherData();
        }
        //this method preferred location and execute new Asynctask and call it 
        private void loadWeatherData(){
            String location = SunshinePreferences.getPreferredWeatherLocation(this);
            new FetchWeatherTask().execute(location);
        }

        public class FetchWeatherTask extends AsyncTask<String, Void,String[]>{

            @Override
            protected String[] doInBackground(String... params) {
                if (params.length == 0){
                return null;
                }
                String location = params [0];
                URL weatherRequestUrl = NetworkUtils.buildUrl(location);
                try {
                    String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);
                    String[] simpleJsonWeatherData = OpenWeatherJsonUtils.getSimpleWeatherStringFromJson(MainActivity.this,jsonWeatherResponse);
                    return simpleJsonWeatherData;
                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String[] weatherData) {
                if (weatherData != null){
                    for (String weatherString : weatherData){
                        mTextview.append(weatherString + "\n\n\n");
                    }
                }
            }
        }
    }

