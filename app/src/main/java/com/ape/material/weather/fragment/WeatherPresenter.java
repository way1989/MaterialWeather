package com.ape.material.weather.fragment;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ape.material.weather.bean.City;
import com.ape.material.weather.bean.HeWeather;
import com.ape.material.weather.data.WeatherRepository;

import org.reactivestreams.Subscription;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by android on 16-11-10.
 */

public class WeatherPresenter extends WeatherContract.Presenter {
    private static final String TAG = "WeatherPresenter";
    WeatherRepository mRepository;
    @NonNull
    private CompositeDisposable mSubscriptions;

    @Inject
    WeatherPresenter(WeatherRepository model, WeatherContract.View view) {
        mRepository = model;
        mView = view;
        mSubscriptions = new CompositeDisposable();
    }

    @Override
    public void getWeather(String city, boolean force) {
        Log.i(TAG, "getWeather... city = " + city);
        mSubscriptions.clear();
        mRepository.getWeather(city, force).subscribe(new Observer<HeWeather>() {
            @Override
            public void onSubscribe(Disposable d) {
                mSubscriptions.add(d);
            }

            @Override
            public void onNext(HeWeather heWeather) {
                mView.onWeatherChange(heWeather);
            }

            @Override
            public void onError(Throwable e) {
                mView.showErrorTip(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void getLocation() {
        Log.d(TAG, "getLocation...");
        mSubscriptions.clear();
        mRepository.getLocation().subscribe(new Observer<City>() {
            @Override
            public void onSubscribe(Disposable d) {
                mSubscriptions.add(d);
            }

            @Override
            public void onNext(City city) {
                mView.onCityChange(city);
            }

            @Override
            public void onError(Throwable e) {
                mView.showErrorTip(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void unSubscribe() {
        mSubscriptions.dispose();
        mSubscriptions.clear();
    }
}
