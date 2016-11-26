package com.ape.material.weather.search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ape.material.weather.bean.City;

import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by android on 16-11-16.
 */

public class SearchPresenter extends SearchContract.Presenter {
    private static final String TAG = "SearchPresenter";
    @NonNull
    private final CompositeSubscription mSubscriptions;

    @Inject
    SearchPresenter(Context context, SearchContract.Model model, SearchContract.View view) {
        mContext = context;
        mModel = model;
        mView = view;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void search(String query) {
        Log.d(TAG, "search... query = " + query);
        mSubscriptions.clear();
        Subscription subscription = mModel.search(query).subscribe(new Observer<List<City>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.onSearchError(e);
            }

            @Override
            public void onNext(List<City> cities) {
                mView.onSearchResult(cities);
            }
        });
        mSubscriptions.add(subscription);
    }

    @Override
    public void addOrUpdateCity(final City city) {
        mSubscriptions.clear();
        Subscription subscription = mModel.addOrUpdateCity(city).subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Boolean result) {
                mView.onSaveCitySucceed(result ? city : null);
            }
        });
        mSubscriptions.add(subscription);
    }
}
