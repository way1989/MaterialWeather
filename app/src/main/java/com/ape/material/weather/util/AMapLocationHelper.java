package com.ape.material.weather.util;

import android.content.Context;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * Created by android on 17-8-31.
 */

public class AMapLocationHelper {
    private static final String TAG = "AMapLocationHelper";
    private static volatile AMapLocationHelper sInstance;
    private AMapLocationClient mLocationClient = null;

    private AMapLocationHelper(Context context) {
        mLocationClient = new AMapLocationClient(context.getApplicationContext());
    }

    public static AMapLocationHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AMapLocationHelper.class) {
                if (sInstance == null) {
                    sInstance = new AMapLocationHelper(context);
                }
            }
        }
        return sInstance;
    }

    public void startLocation(AMapLocationListener listener) {
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        option.setOnceLocationLatest(true);
        option.setHttpTimeOut(20000L);
        mLocationClient.setLocationOption(option);
        mLocationClient.setLocationListener(listener);
        mLocationClient.startLocation();
    }

    public void stopLocation() {
        mLocationClient.stopLocation();
        //mLocationClient.onDestroy();
    }

}
