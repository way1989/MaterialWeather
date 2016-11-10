package com.ape.material.weather.fragment;

import com.ape.material.weather.BuildConfig;
import com.ape.material.weather.api.Api;
import com.ape.material.weather.bean.entity.Weather;
import com.ape.material.weather.util.RxSchedulers;

import rx.Observable;

/**
 * Created by android on 16-11-10.
 */

public class WeatherModel implements WeatherContract.Model {
    @Override
    public Observable<Weather> getWeather(String city, String lang) {
        return Api.getInstance().getWeather(BuildConfig.HEWEATHER_KEY, city, lang)
                .compose(RxSchedulers.<Weather>io_main());
    }
}
