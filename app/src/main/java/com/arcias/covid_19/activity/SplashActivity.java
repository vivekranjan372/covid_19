package com.arcias.covid_19.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.arcias.covid_19.R;

public class SplashActivity extends AppCompatActivity {
    private static long SPLASH_TIME_OUT=2000;
    private SharedPreferences preferences;
    private boolean isFirstTime;
     private ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        actionBar=getSupportActionBar();
        actionBar.hide();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                preferences=getSharedPreferences("onBoarding",MODE_PRIVATE);
                isFirstTime=preferences.getBoolean("firstTime",true);
                if(isFirstTime)
                {
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putBoolean("firstTime",false);
                    editor.apply();
                    startActivity(new Intent(SplashActivity.this
                            ,OnBoardingActivity.class));
                    finish();

                }
                else
                {
                    startActivity(new Intent(SplashActivity.this,
                            MainActivity.class));
                    finish();
                }

            }
        },SPLASH_TIME_OUT);
    }
    }
