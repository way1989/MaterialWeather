package com.ape.material.weather.manage;

import android.support.annotation.NonNull;

import com.ape.material.weather.bean.City;
import com.ape.material.weather.data.WeatherRepository;
import com.ape.material.weather.util.RxBus;
import com.ape.material.weather.util.RxEvent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.functions.Action0;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by way on 2016/11/13.
 */

public class ManagePresenter extends ManageContract.Presenter {
    WeatherRepository mRepository;
    @NonNull
    private CompositeSubscription mSubscriptions;

    @Inject
    ManagePresenter(WeatherRepository model, ManageContract.View view) {
        mRepository = model;
        mView = view;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void getCities() {
        mSubscriptions.clear();
        Subscription subscription = mRepository.getCities()
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showLoading();
                    }
                })
                .subscribe(new Observer<List<City>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<City> cities) {
                mView.onCityChange(cities);
            }
        });
        mSubscriptions.add(subscription);
    }

    @Override
    public void swapCity(final ArrayList<City> data) {
        mSubscriptions.clear();
        Subscription subscription = mRepository.swapCity(data).subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                RxEvent.MainEvent event = new RxEvent.MainEvent(data, Integer.MIN_VALUE);
                RxBus.getInstance().post(event);
            }
        });
        mSubscriptions.add(subscription);
    }

    @Override
    public void deleteCity(City city) {
        mSubscriptions.clear();
        Subscription subscription = mRepository.deleteCity(city).subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                mView.onCityModify();
            }
        });
        mSubscriptions.add(subscription);
    }

    @Override
    public void undoCity(City city) {
        mSubscriptions.clear();
        Subscription subscription = mRepository.undoCity(city).subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                mView.onCityModify();
            }
        });
        mSubscriptions.add(subscription);
    }

    @Override
    public void getLocation() {
        mSubscriptions.clear();
        Subscription subscription = mRepository.getLocation().subscribe(new Observer<City>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                mView.onLocationChanged(null);

            }

            @Override
            public void onNext(City city) {
                mView.onLocationChanged(city);
            }
        });
        mSubscriptions.add(subscription);
    }

    @Override
    public void unSubscribe() {
        mSubscriptions.clear();
    }
}
