package com.popularmovies.abhis.popularmovies.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.popularmovies.abhis.popularmovies.R;

public class Intro extends AppIntro {

    private Boolean firstTime = null;
    private boolean isFirstTime() {
        if (firstTime == null) {
            SharedPreferences mPreferences = getSharedPreferences("first_time", Context.MODE_PRIVATE);
            firstTime = mPreferences.getBoolean("firstTime", true);
            if (firstTime) {
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("firstTime", false);
                editor.apply();

            }

        }
        return firstTime;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(isFirstTime()){
            addSlide(IntroSlide.newInstance(R.layout.fragment_first));
            addSlide(IntroSlide.newInstance(R.layout.fragment_second));
        }
        else{
            Intent first_time=new Intent(Intro.this, MainActivity.class);
            startActivity(first_time);
        }
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent skip=new Intent(Intro.this, MainActivity.class);
        startActivity(skip);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent skip=new Intent(Intro.this, MainActivity.class);
        startActivity(skip);
        finish();
    }
}