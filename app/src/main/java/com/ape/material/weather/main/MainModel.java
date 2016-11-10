package com.ape.material.weather.main;

import com.ape.material.weather.bean.City;
import com.ape.material.weather.util.RxSchedulers;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by android on 16-11-10.
 */

public class MainModel implements MainContract.Model {
    @Override
    public Observable<List<City>> getCities() {
        return Observable.create(new Observable.OnSubscribe<List<City>>() {
            @Override
            public void call(Subscriber<? super List<City>> subscriber) {
                ArrayList<City> cities = new ArrayList<>();
                cities.add(new City("长沙", "中国", "CN101250101", "28.197", "112.967", "湖南"));
                cities.add(new City("深圳", "中国", "CN101280601", "22.544", "114.109", "广东"));
                cities.add(new City("攸县", "中国", "CN101250302", "27.000", "113.210", "湖南"));
                subscriber.onNext(cities);
                subscriber.onCompleted();
            }
        }).compose(RxSchedulers.<List<City>>io_main());
    }
}
