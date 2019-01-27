package com.popularmovies.abhis.popularmovies.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class FirstTime_Intro {
    private Boolean firstTime = null;
    Context _context;

    private boolean isFirstTime() {
        if (firstTime == null) {
            SharedPreferences mPreferences = _context.getSharedPreferences("first_time", Context.MODE_PRIVATE);
            firstTime = mPreferences.getBoolean("firstTime", true);
            if (firstTime) {
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("firstTime", false);
                editor.apply();
                Intent first_time = new Intent(_context, Intro.class);
                _context.startActivity(first_time);
            } else {
                Intent first_time = new Intent(_context, MainActivity.class);
                _context.startActivity(first_time);
            }
        }
        return firstTime;
    }
}