package com.example.root.shanshine17;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.shanshine17.data.SunshinePreferences;
import com.example.root.shanshine17.utilities.NetworkUtils;
import com.example.root.shanshine17.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnclickHandler {

        private TextView mErrorMessage;
        private ProgressBar progressBar;
        private RecyclerView mRecyclerView;
        private ForecastAdapter mForecastAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview_forecast);
        mErrorMessage = (TextView)findViewById(R.id.tv_error_message);
        /*
         * LinearLayoutManager can support HORIZONTAL or VERTICAL orientations. The reverse layout
         * parameter is useful mostly for HORIZONTAL layouts that should reverse for right to left
         * languages.
         */
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
         /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);
         /*
         * The ForecastAdapter is responsible for linking our weather data with the Views that
         * will end up displaying our weather data.
         */
        mForecastAdapter = new ForecastAdapter(this);
         /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mForecastAdapter);
        progressBar = (ProgressBar)findViewById(R.id.loadProgress);
        loadWeatherData();
        }
        //this method preferred location and execute new Asynctask and call it 
        private void loadWeatherData(){
            showWeatherDataDisplay();
            String location = SunshinePreferences.getPreferredWeatherLocation(this);
            new FetchWeatherTask().execute(location);
        }
    /**
     * This method is overridden by our MainActivity class in order to handle RecyclerView item
     * clicks.
     *
     * @param weatherForDay The weather for the day that was clicked
     */
    @Override
    public void onClick(String weatherForDay) {
        Context context = this;
        Toast.makeText(context,weatherForDay,Toast.LENGTH_SHORT).show();
    }

    private void shoErrorMessage(){
            mErrorMessage.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);

        }

        private void showWeatherDataDisplay(){
            mRecyclerView.setVisibility(View.VISIBLE);
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
                   mForecastAdapter.setmWeatherData(weatherData);
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
            mForecastAdapter.setmWeatherData(null);
            loadWeatherData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

