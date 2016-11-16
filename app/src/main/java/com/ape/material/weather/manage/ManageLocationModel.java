package com.ape.material.weather.manage;

import com.ape.material.weather.bean.City;
import com.ape.material.weather.db.DBUtil;
import com.ape.material.weather.util.RxSchedulers;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by way on 2016/11/13.
 */

public class ManageLocationModel implements ManageLocationContract.Model {
    private static final String TAG = "ManageLocationModel";

    @Override
    public Observable<List<City>> getCities() {
        return Observable.create(new Observable.OnSubscribe<List<City>>() {
            @Override
            public void call(Subscriber<? super List<City>> subscriber) {
                ArrayList<City> cities = DBUtil.getCityFromCache();
                subscriber.onNext(cities);
                subscriber.onCompleted();
            }
        }).compose(RxSchedulers.<List<City>>io_main());
    }

    @Override
    public Observable<Boolean> swapCity(final ArrayList<City> data) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                for (int i = 0; i < data.size(); i++) {
                    City city = data.get(i);
                    boolean result = DBUtil.updateIndex(city, i);
                }
                subscriber.onNext(true);
                subscriber.onCompleted();
            }
        }).compose(RxSchedulers.<Boolean>io_main());
    }

    @Override
    public Observable<Boolean> deleteCity(final City city) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {

                boolean result = DBUtil.deleteCity(city);
                subscriber.onNext(result);
                subscriber.onCompleted();
            }
        }).compose(RxSchedulers.<Boolean>io_main());
    }

    @Override
    public Observable<Boolean> undoCity(final City city) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                boolean result = DBUtil.undoCity(city);
                subscriber.onNext(result);
                subscriber.onCompleted();
            }
        }).compose(RxSchedulers.<Boolean>io_main());
    }


}
