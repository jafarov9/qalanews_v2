package com.hajma.qalanews_android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;

public class CheckNetwork {

    private Context context;

    // You need to pass the context when creating the class
    public CheckNetwork(Context context) {
        this.context = context;
    }

    // Network Check
    public void registerNetworkCallback() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkRequest.Builder builder = new NetworkRequest.Builder();


            if(Build.VERSION.SDK_INT > 23) {
                connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(Network network) {
                        Variables.isNetworkConnected = true; // Global Static Variable
                    }

                    @Override
                    public void onLost(Network network) {
                        Variables.isNetworkConnected = false; // Global Static Variable
                    }
                });
            }else {
                Variables.isNetworkConnected = true;
            }



            Variables.isNetworkConnected = false;
        }catch (Exception e){
            Variables.isNetworkConnected = false;
        }
    }

}
