package com.ape.material.weather.main;

import android.support.annotation.NonNull;

import com.ape.material.weather.bean.City;
import com.ape.material.weather.data.WeatherRepository;

import org.reactivestreams.Subscription;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


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
        mRepository.getCities().subscribe(new Observer<List<City>>() {
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
    public void unSubscribe() {
        mCompositeDisposable.dispose();
        mCompositeDisposable.clear();
    }
}
