package com.example.root.shanshine17;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnclickHandler,
        LoaderManager.LoaderCallbacks<String[]> {

        private static final String TAG = MainActivity.class.getSimpleName();
        private TextView mErrorMessage;
        private ProgressBar progressBar;
        private RecyclerView mRecyclerView;
        private ForecastAdapter mForecastAdapter;
        private static final int FORECAST_LOADER_ID = 0;
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
        /**
         * this ID will uniquely identify the loader, we can use it, for example, to get a  handle
         * on our Loader at a later point in time through the support LoadManager
         */
        int loaderId = FORECAST_LOADER_ID;
        /**
         * From MainActivity, we hace implemented the LoaderCallBacks interface with the type of
         * String array. (implements LoaderCallbacks<String[]>) the variable callback is passed
         * to the call to initloader below. this mean that whenever the loadermanager has
         * something to notify us of, it will do so through this callback
         */
        LoaderManager.LoaderCallbacks<String[]> callbacks = MainActivity.this;

        /**
         * The second parameter of the iniloader method below is a bundle. optionally, you can pass
         * a bundle to initloader that you can then acces from within the oncreloader
         * callback. in our case, we don't actually use the bundle, but its here in case we wanted
         * to
         */
        Bundle bundleLoader = null;
        /**
         * Ensure a loader is initialized and active. if the loader doesn't already exist, one is
         * created and (if the activity/fragment is currentl started) starts the loader. otherwise
         * the last created loader is re-used
         */
        getSupportLoaderManager().initLoader(loaderId, bundleLoader, callbacks);
    }


    /**
     * Instantiate and return a new Loader for the given ID
     * @param id the ID whose loader is to be created
     * @param args any arguments supplied by the caller
     * @return Return a new Loader instance that is ready to start loading
     */

    @Override
    public Loader<String[]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String[]>(this) {
            //this String array will hold and help cache our weather data
            String[] mWeatherData = null;

            /** subclasses of AsyncTaskLoader must implement this to take care of loading their data */
            @Override
            protected void onStartLoading() {
                if (mWeatherData != null){
                    deliverResult(mWeatherData);
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            /**
             * This method of the AsyncTaskLoader that will load and parse the josn data
             * from OpenWeatherMap in the background
             * @return Weather data from OpenWeatherMap as an array Strings
             *          null if an error occurs
             */
            @Override
            public String[] loadInBackground() {
            String locationQuery = SunshinePreferences
                    .getPreferredWeatherLocation(MainActivity.this);
                URL weatherRequestUrl = NetworkUtils.buildUrl(locationQuery);

                try {
                    String jsonWeatherResponse = NetworkUtils
                            .getResponseFromHttpUrl(weatherRequestUrl);
                    String[] simpleJsonWeatherData = OpenWeatherJsonUtils
                            .getSimpleWeatherStringFromJson(MainActivity.this, jsonWeatherResponse);
                    return  simpleJsonWeatherData;
                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }
        };

    }

    /**
     * Called when a previously created loader has finished its load
     * @param loader the loader that has finished
     * @param data the data generated by the loader
     */
    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        progressBar.setVisibility(View.INVISIBLE);
        mForecastAdapter.setmWeatherData(data);
        if (null == data){
            shoErrorMessage();
        }else {
            showWeatherDataDisplay();
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable. the application should at this point
     * remove any references it has to the loaders data
     * @param loader the loader that is being reset
     */
    @Override
    public void onLoaderReset(Loader<String[]> loader) {

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
        Class destination = DetailActivity.class;
        Intent intent = new Intent(context,destination);
        intent.putExtra(Intent.EXTRA_TEXT,weatherForDay);
        startActivity(intent);
        //Toast.makeText(context,weatherForDay,Toast.LENGTH_SHORT).show();
    }

    /**
     * this method uses the URI scheme for showing a location found on a map in conjuction with
     * an imlicit intent.
     */
    private void openlocationMap(){
        String addresLocation = "1600 Amphitheatre Parkway, CA";
        Uri geolocation = Uri.parse("geo:0,0?q=" +addresLocation);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geolocation);

        if (intent.resolveActivity(getPackageManager()) !=null){
            startActivity(intent);
        }else {
            Log.d(TAG, "couldn't call "+geolocation.toString() +",no receiving apps installed");
        }
    }

    private void shoErrorMessage(){
            mErrorMessage.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);

        }

        private void showWeatherDataDisplay(){
            mRecyclerView.setVisibility(View.VISIBLE);
            mErrorMessage.setVisibility(View.INVISIBLE);
        }

    /**
     * this method is used we are resetting data, so that at one point in time during a
     * refresh of our data, you can see that there is no data showing
     */
    private void ivalidateData(){
            mForecastAdapter.setmWeatherData(null);
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
            ivalidateData();
            getSupportLoaderManager().restartLoader(FORECAST_LOADER_ID, null, this);
            return true;
        }

        if (id == R.id.action_map){
            openlocationMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

