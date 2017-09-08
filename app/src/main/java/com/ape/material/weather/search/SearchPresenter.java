package com.ape.material.weather.search;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ape.material.weather.bean.City;
import com.ape.material.weather.util.ActivityScope;
import com.ape.material.weather.util.RxSchedulers;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by android on 16-11-16.
 */
@ActivityScope
public class SearchPresenter extends SearchContract.Presenter {
    private static final String TAG = "SearchPresenter";
    @NonNull
    private final CompositeDisposable mCompositeDisposable;

    @Inject
    SearchPresenter(SearchContract.Model model, SearchContract.View view) {
        mModel = model;
        mView = view;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void search(String query) {
        Log.d(TAG, "search... query = " + query);
        mCompositeDisposable.clear();
        DisposableObserver<List<City>> observer = new DisposableObserver<List<City>>() {
            @Override
            public void onNext(List<City> cities) {
                mView.onSearchResult(cities);
            }

            @Override
            public void onError(Throwable e) {
                mView.onSearchError(e);
            }

            @Override
            public void onComplete() {

            }
        };
        mModel.search(query).compose(RxSchedulers.<List<City>>io_main()).subscribe(observer);
        subscribe(observer);
    }

    @Override
    public void addOrUpdateCity(final City city) {
        mCompositeDisposable.clear();
        DisposableObserver<Boolean> observer = new DisposableObserver<Boolean>() {
            @Override
            public void onNext(Boolean result) {
                mView.onSaveCitySucceed(result ? city : null);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        mModel.addOrUpdateCity(city).compose(RxSchedulers.<Boolean>io_main()).subscribe(observer);
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
