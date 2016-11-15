package com.ape.material.weather.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

/**
 * A class that handles everything about location.
 */
public class CityLocationManager {
    private static final String TAG = "CityLocationManager";
    private CityListener[] mLocationListeners = new CityListener[]{
            new CityListener(LocationManager.NETWORK_PROVIDER),
            new CityListener(LocationManager.GPS_PROVIDER)};
    private Context mContext;
    private Listener mListener;
    private LocationManager mLocationManager;


    public CityLocationManager(Context context) {
        mContext = context.getApplicationContext();
    }

    public void setListener(Listener listener) {
        Log.e(TAG, "----recordLocation listener = " + listener);
        mListener = listener;
    }

    public void startReceivingLocationUpdates() {
        Log.d(TAG, "startReceivingLocationUpdates");
        if (mLocationManager == null)
            mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        try {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    1000, 0F, mLocationListeners[1]);
        } catch (SecurityException e) {
            Log.i(TAG, "fail to request location update, ignore", e);
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "provider does not exist " + e.getMessage());
        }

        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000, 0F, mLocationListeners[0]);
        } catch (SecurityException e) {
            Log.i(TAG, "fail to request location update, ignore", e);
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "provider does not exist " + e.getMessage());
        }
    }

    public void stopReceivingLocationUpdates() {
        Log.d(TAG, "--------stopReceivingLocationUpdates start");
        if (mLocationManager != null) {
            for (CityListener listener : mLocationListeners) {
                try {
                    mLocationManager.removeUpdates(listener);
                } catch (Exception e) {
                    Log.i(TAG, "fail to remove location listeners, ignore", e);
                }
            }
            mLocationManager = null;
            Log.d(TAG, "stopReceivingLocationUpdates end");
        }
    }

    public void onDestory() {
        stopReceivingLocationUpdates();
    }

    public interface Listener {

        void onLocationSuccess(Location location);
    }

    private class CityListener implements LocationListener {
        private Location mLastLocation;
        private String mProvider;

        CityListener(String provider) {
            mProvider = provider;
        }

        @Override
        public void onLocationChanged(Location newLocation) {
            Log.v(TAG, "onLocationChanged");
            if (mLastLocation != null)
                return;
            if (newLocation.getLatitude() == 0.0
                    && newLocation.getLongitude() == 0.0) {
                // Hack to filter out 0.0,0.0 locations
                return;
            }

            Log.d(TAG, "Got first location   lat = " + newLocation.getLatitude()
                    + ", lon = " + newLocation.getLongitude()
                    + ", provider = " + newLocation.getProvider());
            mLastLocation = newLocation;

            if (mListener != null) {
                mListener.onLocationSuccess(newLocation);
            }
            stopReceivingLocationUpdates();
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.v(TAG, "onProviderDisabled");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.v(TAG, "onStatusChanged  provider=" + provider + " status=" + status);
            switch (status) {
                case LocationProvider.OUT_OF_SERVICE:
                case LocationProvider.TEMPORARILY_UNAVAILABLE: {
                    break;
                }
            }
        }

    }
}
