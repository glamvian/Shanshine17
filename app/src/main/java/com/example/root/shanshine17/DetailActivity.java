package com.example.root.shanshine17;

import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {
    private String mForecast;
    private TextView mTextview;
    private static final String FORECAST_SHARE_TAG = "#Sunshineapp";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mTextview = (TextView)findViewById(R.id.tv_display_weather);
        Intent intentThatStartActivity = getIntent();
        if (intentThatStartActivity !=null){
            if (intentThatStartActivity.hasExtra(Intent.EXTRA_TEXT)){
                mForecast = intentThatStartActivity.getStringExtra(Intent.EXTRA_TEXT);
                mTextview.setText(mForecast);
            }
        }
    }
    private Intent createShareIntent(){
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mForecast+FORECAST_SHARE_TAG)
                .getIntent();
        return shareIntent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail,menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createShareIntent());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if ( id == R.id.action_setting){
            Intent intent = new Intent(this,SettingActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
