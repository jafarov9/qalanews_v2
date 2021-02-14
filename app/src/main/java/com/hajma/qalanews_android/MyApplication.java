package com.hajma.qalanews_android;

import android.app.Application;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LocaleHelper.setLocale(this, "az");
        // Register Callback - Call this in your app start!
        CheckNetwork network = new CheckNetwork(getApplicationContext());
        network.registerNetworkCallback();
    }
}
