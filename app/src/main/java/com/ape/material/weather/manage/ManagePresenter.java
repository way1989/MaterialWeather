package com.ape.material.weather.manage;

import android.support.annotation.NonNull;

import com.ape.material.weather.bean.City;
import com.ape.material.weather.util.ActivityScope;
import com.ape.material.weather.util.RxBus;
import com.ape.material.weather.util.RxEvent;
import com.ape.material.weather.util.RxSchedulers;

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
@ActivityScope
public class ManagePresenter extends ManageContract.Presenter {
    @NonNull
    private CompositeDisposable mCompositeDisposable;

    @Inject
    ManagePresenter(ManageContract.Model model, ManageContract.View view) {
        mModel = model;
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
        mModel.getCities().compose(RxSchedulers.<List<City>>io_main())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showLoading();
                    }
                }).subscribe(observer);
        subscribe(observer);
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
        mModel.swapCity(data).compose(RxSchedulers.<Boolean>io_main()).subscribe(observer);
        subscribe(observer);
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
        mModel.deleteCity(city).compose(RxSchedulers.<Boolean>io_main()).subscribe(observer);
        subscribe(observer);
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
        mModel.undoCity(city).compose(RxSchedulers.<Boolean>io_main()).subscribe(observer);
        subscribe(observer);
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
