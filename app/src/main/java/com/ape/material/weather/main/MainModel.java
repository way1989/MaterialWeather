package com.ape.material.weather.main;

import android.content.ContentValues;

import com.ape.material.weather.App;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.db.CityProvider;
import com.ape.material.weather.db.DBUtil;
import com.ape.material.weather.util.RxSchedulers;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by android on 16-11-10.
 */

public class MainModel implements MainContract.Model {
    private static final String TAG = "MainModel";

    @Override
    public Observable<List<City>> getCities() {
        return Observable.create(new Observable.OnSubscribe<List<City>>() {
            @Override
            public void call(Subscriber<? super List<City>> subscriber) {
                ArrayList<City> cities = DBUtil.getCityFromCache();
                if (cities.isEmpty()) {
                    City city = new City();
                    city.setCity("自动定位");
                    city.setLocation(true);
                    cities.add(city);

                    insertAutoLocation();
                }
                subscriber.onNext(cities);
                subscriber.onCompleted();
            }
        }).compose(RxSchedulers.<List<City>>io_main());
    }

    private void insertAutoLocation() {
        ContentValues values = new ContentValues();
        values.put(CityProvider.CityConstants.CITY, "Auto Location");
        values.put(CityProvider.CityConstants.IS_LOCATION, 1);
        App.getContext().getContentResolver().insert(CityProvider.CITY_CONTENT_URI, values);
    }


}
