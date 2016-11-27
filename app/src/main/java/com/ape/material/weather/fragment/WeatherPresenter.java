package com.ape.material.weather.fragment;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ape.material.weather.bean.City;
import com.ape.material.weather.bean.HeWeather;
import com.ape.material.weather.data.WeatherRepository;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by android on 16-11-10.
 */

public class WeatherPresenter extends WeatherContract.Presenter {
    private static final String TAG = "WeatherPresenter";
    WeatherRepository mRepository;
    @NonNull
    private CompositeSubscription mSubscriptions;

    @Inject
    WeatherPresenter(WeatherRepository model, WeatherContract.View view) {
        mRepository = model;
        mView = view;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void getWeather(String city, boolean force) {
        Log.i(TAG, "getWeather... city = " + city);
        mSubscriptions.clear();
        Subscription subscription = mRepository.getWeather(city, force).subscribe(new Observer<HeWeather>() {
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
        });
        mSubscriptions.add(subscription);
    }

    @Override
    public void getLocation() {
        Log.d(TAG, "getLocation...");
        mSubscriptions.clear();
        Subscription subscription = mRepository.getLocation().subscribe(new Observer<City>() {
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
        });
        mSubscriptions.add(subscription);
    }

    @Override
    public void unSubscribe() {
        mSubscriptions.clear();
    }
}
