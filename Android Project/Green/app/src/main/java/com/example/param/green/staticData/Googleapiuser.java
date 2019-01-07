package com.example.param.green.staticData;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Param on 24-08-2017.
 */

public final class Googleapiuser {
    private  static GoogleApiClient mGoogleApiClient;

    public  static GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public static void setmGoogleApiClient(GoogleApiClient mGoogleApiClient) {
        mGoogleApiClient = mGoogleApiClient;
    }
}
