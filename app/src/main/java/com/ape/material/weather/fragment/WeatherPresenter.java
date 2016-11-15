package com.ape.material.weather.fragment;

import android.location.Location;
import android.util.Log;

import com.ape.material.weather.App;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.bean.HeWeather;
import com.ape.material.weather.util.CityLocationManager;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import rx.Observer;

/**
 * Created by android on 16-11-10.
 */

public class WeatherPresenter extends WeatherContract.Presenter implements CityLocationManager.Listener {
    private static final String TAG = "WeatherPresenter";
    private Semaphore mRequestLocationLock = new Semaphore(1);
    private CityLocationManager mLocationManager;

    @Override
    public void getWeather(String city, String lang, boolean force) {
        Log.i(TAG, "getWeather... city = " + city + ", lang = " + lang);
        mRxManage.add(mModel.getWeather(city, lang, force).subscribe(new Observer<HeWeather>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.showErrorTip(e.getMessage());
            }

            @Override
            public void onNext(HeWeather weather) {
                mView.onWeatherChange(weather);
            }
        }));
    }

    @Override
    public void getLocation() {
        Log.d(TAG, "getLocation...");
        if (mLocationManager == null)
            mLocationManager = new CityLocationManager(App.getContext());
        mLocationManager.setListener(this);
        try {
            if (!mRequestLocationLock.tryAcquire(5000L, TimeUnit.MILLISECONDS)) {
                Log.d(TAG, "Time out waiting to get location");
                throw new RuntimeException("Time out waiting to get location");
            }
            mLocationManager.startReceivingLocationUpdates();
        } catch (Exception e) {
            mView.showErrorTip(e.getMessage());
        }
    }

    @Override
    public void getCity(double latitude, double longitude) {
        Log.i(TAG, "getCity... latitude = " + latitude + ", longitude = " + longitude);
        mRxManage.add(mModel.getCity(latitude, longitude).subscribe(new Observer<City>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.showErrorTip(e.getMessage());
            }

            @Override
            public void onNext(City city) {
                mView.onCityChange(city);
            }
        }));
    }

    @Override
    public void onLocationSuccess(Location location) {
        if (mLocationManager != null) mLocationManager.setListener(null);
        mRequestLocationLock.release();
        getCity(location.getLatitude(), location.getLongitude());
    }
}
