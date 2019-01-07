package com.example.param.green.staticData;

import android.app.Application;

/**
 * Created by Param on 05-10-2017.
 */

public class MyApplication extends Application {

    private static MyApplication mInstance2;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance2 = this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance2;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}