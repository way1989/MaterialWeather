package com.ape.material.weather.manage;

import android.support.annotation.NonNull;

import com.ape.material.weather.bean.City;
import com.ape.material.weather.data.WeatherRepository;
import com.ape.material.weather.util.RxBus;
import com.ape.material.weather.util.RxEvent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


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
        mRepository.getCities().doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                mView.showLoading();
            }
        }).subscribe(new Observer<List<City>>() {
            @Override
            public void onSubscribe(Disposable d) {
                mCompositeDisposable.add(d);
            }

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
        });
    }

    @Override
    public void swapCity(final ArrayList<City> data) {
        mCompositeDisposable.clear();
        mRepository.swapCity(data).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
                mCompositeDisposable.add(d);
            }

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
        });
    }

    @Override
    public void deleteCity(City city) {
        mCompositeDisposable.clear();
        mRepository.deleteCity(city).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
                mCompositeDisposable.add(d);
            }

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
        });
    }

    @Override
    public void undoCity(City city) {
        mCompositeDisposable.clear();
        mRepository.undoCity(city).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
                mCompositeDisposable.add(d);
            }

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
        });
    }

    @Override
    public void getLocation() {
        mCompositeDisposable.clear();
        mRepository.getLocation().subscribe(new Observer<City>() {
            @Override
            public void onSubscribe(Disposable d) {
                mCompositeDisposable.add(d);
            }

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
        });
    }

    @Override
    public void unSubscribe() {
        mCompositeDisposable.dispose();
        mCompositeDisposable.clear();
    }
}
