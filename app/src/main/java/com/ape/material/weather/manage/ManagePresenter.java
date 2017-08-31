package com.ape.material.weather.manage;

import android.support.annotation.NonNull;

import com.ape.material.weather.bean.City;
import com.ape.material.weather.data.WeatherRepository;
import com.ape.material.weather.util.RxBus;
import com.ape.material.weather.util.RxEvent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;


/**
 * Created by way on 2016/11/13.
 */

public class ManagePresenter extends ManageContract.Presenter {
    private WeatherRepository mRepository;
    @NonNull
    private CompositeDisposable mCompositeDisposable;

    @Inject
    ManagePresenter(WeatherRepository model, ManageContract.View view) {
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
        mRepository.getCities().doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                mView.showLoading();
            }
        }).subscribe(observer);
        register(observer);
    }

    @Override
    public void swapCity(final ArrayList<City> data) {
        mCompositeDisposable.clear();
        DisposableObserver<Boolean> observer = new DisposableObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                RxEvent.MainEvent event = new RxEvent.MainEvent(data, Integer.MIN_VALUE);
                RxBus.getInstance().post(event);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        mRepository.swapCity(data).subscribe(observer);
        register(observer);
    }

    @Override
    public void deleteCity(City city) {
        mCompositeDisposable.clear();
        DisposableObserver<Boolean> observer = new DisposableObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                mView.onCityModify();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        mRepository.deleteCity(city).subscribe(observer);
        register(observer);
    }

    @Override
    public void undoCity(City city) {
        mCompositeDisposable.clear();
        DisposableObserver<Boolean> observer = new DisposableObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                mView.onCityModify();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        mRepository.undoCity(city).subscribe(observer);
        register(observer);
    }

    @Override
    public void getLocation() {
        mCompositeDisposable.clear();
        DisposableObserver<City> observer = new DisposableObserver<City>() {
            @Override
            public void onNext(City city) {
                mView.onLocationChanged(null);
            }

            @Override
            public void onError(Throwable e) {

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
