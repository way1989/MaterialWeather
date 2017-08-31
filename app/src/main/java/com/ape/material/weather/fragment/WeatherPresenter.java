package com.ape.material.weather.fragment;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ape.material.weather.bean.City;
import com.ape.material.weather.bean.HeWeather;
import com.ape.material.weather.data.WeatherRepository;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by android on 16-11-10.
 */

public class WeatherPresenter extends WeatherContract.Presenter {
    private static final String TAG = "WeatherPresenter";
    private WeatherRepository mRepository;
    @NonNull
    private CompositeDisposable mCompositeDisposable;

    @Inject
    WeatherPresenter(WeatherRepository model, WeatherContract.View view) {
        mRepository = model;
        mView = view;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void getWeather(String city, boolean force) {
        Log.i(TAG, "getWeather... city = " + city);
        mCompositeDisposable.clear();
        DisposableObserver<HeWeather> observer = new DisposableObserver<HeWeather>() {
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
        };
        mRepository.getWeather(city, force).subscribe(observer);
        register(observer);
    }

    @Override
    public void getLocation() {
        Log.d(TAG, "getLocation...");
        mCompositeDisposable.clear();
        DisposableObserver<City> observer = new DisposableObserver<City>() {
            @Override
            public void onNext(City city) {
                Log.d(TAG, "getLocation onNext: city = " + city);
                mView.onCityChange(city);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ", e);
                mView.showErrorTip(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        };
        mRepository.getLocation().subscribe(observer);
        register(observer);
    }

    @Override
    public void register(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void unSubscribe() {
        mCompositeDisposable.clear();
    }
}
