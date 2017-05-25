package com.example.root.shanshine17;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {
    private String mForecast;
    private TextView mTextview;
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
}
