package com.shihc.demo.andrewdemo;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by shihc on 16/7/12.
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
}
