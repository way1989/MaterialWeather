package com.ape.material.weather.main;

import android.support.annotation.NonNull;

import com.ape.material.weather.bean.City;
import com.ape.material.weather.util.ActivityScope;
import com.ape.material.weather.util.RxSchedulers;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;


/**
 * Created by android on 16-11-10.
 */
@ActivityScope
public class MainPresenter extends MainContract.Presenter {
    @NonNull
    private CompositeDisposable mCompositeDisposable;

    @Inject
    MainPresenter(MainContract.Model model, MainContract.View view) {
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
        mModel.getCities().compose(RxSchedulers.<List<City>>io_main()).subscribe(observer);
        subscribe(observer);
    }

    @Override
    public void unSubscribe() {
        mCompositeDisposable.clear();
    }

    public void subscribe(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }

}
