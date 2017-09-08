package com.ape.material.weather.fragment;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ape.material.weather.bean.City;
import com.ape.material.weather.bean.HeWeather;
import com.ape.material.weather.util.FragmentScope;
import com.ape.material.weather.util.RxSchedulers;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by android on 16-11-10.
 */
@FragmentScope
public class WeatherPresenter extends WeatherContract.Presenter {
    private static final String TAG = "WeatherPresenter";
    @NonNull
    private CompositeDisposable mCompositeDisposable;

    @Inject
    WeatherPresenter(WeatherContract.Model model, WeatherContract.View view) {
        mModel = model;
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
                Log.d(TAG, "onError: e = " + e);
                mView.showErrorTip(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        };
        mModel.getWeather(city, force).compose(RxSchedulers.<HeWeather>io_main()).subscribe(observer);
        subscribe(observer);
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
        mModel.getLocation().compose(RxSchedulers.<City>io_main()).subscribe(observer);
        subscribe(observer);
    }

    @Override
    public void subscribe(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void unSubscribe() {
        mCompositeDisposable.clear();
    }
}
