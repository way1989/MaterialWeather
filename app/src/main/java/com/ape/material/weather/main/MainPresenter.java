package com.ape.material.weather.main;

import android.support.annotation.NonNull;

import com.ape.material.weather.bean.City;
import com.ape.material.weather.data.WeatherRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;


/**
 * Created by android on 16-11-10.
 */

public class MainPresenter extends MainContract.Presenter {
    private WeatherRepository mRepository;
    @NonNull
    private CompositeDisposable mCompositeDisposable;

    @Inject
    MainPresenter(WeatherRepository model, MainContract.View view) {
        mRepository = model;
        mView = view;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void getCities() {
        mCompositeDisposable.clear();
        DisposableObserver<List<City>> observer = new DisposableObserver<List<City>>() {

            @Override
            public void onNext(List<City> cities) {
                mView.onCityChange(cities);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        mRepository.getCities().subscribe(observer);
        register(observer);
    }

    @Override
    public void unSubscribe() {
        mCompositeDisposable.clear();
    }

    public void register(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }

}
