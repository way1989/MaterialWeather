package com.ape.material.weather.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ape.material.weather.App;

/**
 * Created by android on 16-11-11.
 */

public class DeviceUtil {
    public static boolean isWifiOpen() {
        ConnectivityManager cm = (ConnectivityManager) App.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && !(!info.isAvailable() || !info.isConnected())
                && info.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static boolean hasInternet() {
        ConnectivityManager cm = (ConnectivityManager) App.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isAvailable() && info.isConnected();
    }
}
