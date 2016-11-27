package com.ape.material.weather.main;

import android.support.annotation.NonNull;

import com.ape.material.weather.bean.City;
import com.ape.material.weather.data.WeatherRepository;

import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by android on 16-11-10.
 */

public class MainPresenter extends MainContract.Presenter {
    WeatherRepository mRepository;
    @NonNull
    private CompositeSubscription mSubscriptions;

    @Inject
    MainPresenter(WeatherRepository model, MainContract.View view) {
        mRepository = model;
        mView = view;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void getCities() {
        mSubscriptions.clear();
        Subscription subscription = mRepository.getCities().subscribe(new Observer<List<City>>() {
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
}
