package com.ape.material.weather.search;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ape.material.weather.bean.City;
import com.ape.material.weather.data.WeatherRepository;

import org.reactivestreams.Subscription;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by android on 16-11-16.
 */

public class SearchPresenter extends SearchContract.Presenter {
    private static final String TAG = "SearchPresenter";
    @NonNull
    private final CompositeDisposable mSubscriptions;
    private WeatherRepository mRepository;

    @Inject
    SearchPresenter(WeatherRepository model, SearchContract.View view) {
        mRepository = model;
        mView = view;
        mSubscriptions = new CompositeDisposable();
    }

    @Override
    public void search(String query) {
        Log.d(TAG, "search... query = " + query);
        mSubscriptions.clear();
        mRepository.searchCity(query)
                .subscribe(new Observer<List<City>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mSubscriptions.add(d);
                    }

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
                });
    }

    @Override
    public void addOrUpdateCity(final City city) {
        mSubscriptions.clear();
        mRepository.addCity(city)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mSubscriptions.add(d);
                    }

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
                });
    }

    @Override
    public void unSubscribe() {
        mSubscriptions.dispose();
        mSubscriptions.clear();
    }
}
