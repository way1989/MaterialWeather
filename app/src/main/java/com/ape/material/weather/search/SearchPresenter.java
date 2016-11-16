package com.ape.material.weather.search;

import android.util.Log;

import com.ape.material.weather.bean.City;

import java.util.List;

import rx.Observer;

/**
 * Created by android on 16-11-16.
 */

public class SearchPresenter extends SearchContract.Presenter {
    private static final String TAG = "SearchPresenter";

    @Override
    public void search(String query) {
        Log.d(TAG, "search... query = " + query);
        //mRxManage.clear();
        mRxManage.add(mModel.search(query).subscribe(new Observer<List<City>>() {
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
        }));
    }

    @Override
    public void addOrUpdateCity(final City city) {
        mRxManage.add(mModel.addOrUpdateCity(city).subscribe(new Observer<Boolean>() {
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
        }));
    }
}
