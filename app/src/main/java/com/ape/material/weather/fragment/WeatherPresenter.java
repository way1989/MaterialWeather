package com.ape.material.weather.fragment;

import android.util.Log;

import com.ape.material.weather.bean.entity.Weather;

import rx.Observer;

/**
 * Created by android on 16-11-10.
 */

public class WeatherPresenter extends WeatherContract.Presenter {
    private static final String TAG = "WeatherPresenter";

    @Override
    public void getWeather(String city, String lang, boolean force) {
        Log.i(TAG, "getWeather... city = " + city + ", lang = " + lang);
        mRxManage.add(mModel.getWeather(city, lang, force).subscribe(new Observer<Weather>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.showErrorTip(e.getMessage());
            }

            @Override
            public void onNext(Weather weather) {
                mView.onWeatherChange(weather);
            }
        }));
    }
}
