package com.example.root.shanshine17;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.root.shanshine17.data.SunshinePreferences;
import com.example.root.shanshine17.utilities.NetworkUtils;
import com.example.root.shanshine17.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity {
        private TextView mTextview;
        private TextView mErrorMessage;
        private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       mTextview = (TextView) findViewById(R.id.tv_weather_data);
        mErrorMessage = (TextView)findViewById(R.id.tv_error_message);
        progressBar = (ProgressBar)findViewById(R.id.loadProgress);
        loadWeatherData();
        }
        //this method preferred location and execute new Asynctask and call it 
        private void loadWeatherData(){
            showWeatherDataDisplay();
            String location = SunshinePreferences.getPreferredWeatherLocation(this);
            new FetchWeatherTask().execute(location);
        }
        private void shoErrorMessage(){
            mErrorMessage.setVisibility(View.VISIBLE);
            mTextview.setVisibility(View.INVISIBLE);

        }

        private void showWeatherDataDisplay(){
            mTextview.setVisibility(View.VISIBLE);
            mErrorMessage.setVisibility(View.INVISIBLE);
        }

        public class FetchWeatherTask extends AsyncTask<String, Void,String[]>{

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

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
                progressBar.setVisibility(View.INVISIBLE);
                if (weatherData != null){
                    showWeatherDataDisplay();
                    for (String weatherString : weatherData){
                        mTextview.append(weatherString + "\n\n\n");
                    }
                }else {
                    shoErrorMessage();
                }
            }
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();

        if (id == R.id.action_refresh){
            mTextview.setText("");
            loadWeatherData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

