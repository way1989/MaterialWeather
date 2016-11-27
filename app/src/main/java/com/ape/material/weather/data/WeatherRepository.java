package com.ape.material.weather.data;

import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import com.ape.material.weather.BuildConfig;
import com.ape.material.weather.api.Api;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.bean.HeCity;
import com.ape.material.weather.bean.HeWeather;
import com.ape.material.weather.util.RxSchedulers;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by way on 2016/11/26.
 */

public class WeatherRepository implements WeatherDataSource {
    private static final String TAG = "WeatherRepository";

    @Inject
    WeatherRepository() {
    }

    @Override
    public Observable<List<City>> getCities() {
        return Observable.create(new Observable.OnSubscribe<List<City>>() {
            @Override
            public void call(Subscriber<? super List<City>> subscriber) {
                ArrayList<City> cities = DBUtil.getCityFromCache();
                if (cities.isEmpty()) {
                    City city = new City();
                    city.setLocation(true);
                    cities.add(city);

                    DBUtil.insertAutoLocation();
                }
                subscriber.onNext(cities);
                subscriber.onCompleted();
            }
        }).compose(RxSchedulers.<List<City>>io_main());
    }

    @Override
    public Observable<City> getLocation() {
        return LocationUtil.getLocation().flatMap(new Func1<Location, Observable<City>>() {
            @Override
            public Observable<City> call(Location location) {
                Log.i(TAG, "flatMap... lat = " + location.getLatitude() + ", lon = " + location.getLongitude());
                return LocationUtil.getCity(location.getLatitude(), location.getLongitude());
            }
        });
    }

    @Override
    public Observable<HeWeather> getWeather(String city, boolean force) {
        Observable<HeWeather> disk = WeatherUtil.getLocalWeather(city, force);
        Observable<HeWeather> network = WeatherUtil.getRemoteWeather(city);
        return Observable.concat(disk, network).filter(new Func1<HeWeather, Boolean>() {
            @Override
            public Boolean call(HeWeather weather) {
                return weather != null && weather.isOK();
            }
        }).first().compose(RxSchedulers.<HeWeather>io_main());
    }

    @Override
    public Observable<Boolean> swapCity(final List<City> cities) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                for (int i = 0; i < cities.size(); i++) {
                    City city = cities.get(i);
                    DBUtil.updateIndex(city, i);
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

    @Override
    public Observable<List<City>> searchCity(String query) {
        Log.d(TAG, "searchCity... query = " + query);
        return Api.getInstance().searchCity(BuildConfig.HEWEATHER_KEY, query).map(new Func1<HeCity, List<City>>() {
            @Override
            public List<City> call(HeCity heCity) {
                Log.d(TAG, "searchCity... result heCity = " + heCity);
                ArrayList<City> cities = new ArrayList<>();
                if (heCity == null || !heCity.isOK())
                    return cities;
                List<HeCity.HeWeather5Bean> beanList = heCity.getHeWeather5();
                for (HeCity.HeWeather5Bean bean : beanList) {
                    if (TextUtils.equals(bean.getStatus(), "ok")) {
                        HeCity.HeWeather5Bean.BasicBean basicBean = bean.getBasic();
                        City city = new City(basicBean.getCity(), basicBean.getCnty(),
                                basicBean.getId(), basicBean.getLat(), basicBean.getLon(),
                                basicBean.getProv());
                        cities.add(city);
                    }
                }
                Log.d(TAG, "searchCity... end size = " + cities.size());
                return cities;
            }
        }).compose(RxSchedulers.<List<City>>io_main());
    }

    @Override
    public Observable<Boolean> addCity(final City city) {
        Log.d(TAG, "addCity... city = " + city.getCity());
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                boolean exist = DBUtil.isExist(city);
                Log.d(TAG, "addCity... exist = " + exist);
                subscriber.onNext(!exist && DBUtil.addCity(city, false));
                subscriber.onCompleted();
            }
        }).compose(RxSchedulers.<Boolean>io_main());
    }
}
