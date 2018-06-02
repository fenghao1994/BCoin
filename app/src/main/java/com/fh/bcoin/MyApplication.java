package com.fh.bcoin;

import android.app.Application;

import com.zhy.http.okhttp.OkHttpUtils;

import okhttp3.OkHttpClient;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        OkHttpUtils.initClient(okHttpClient);
    }
}
