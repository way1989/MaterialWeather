package com.ape.material.weather.data;

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
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;


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
        return Observable.create(new ObservableOnSubscribe<List<City>>() {
            @Override
            public void subscribe(ObservableEmitter<List<City>> e) throws Exception {
                ArrayList<City> cities = DBUtil.getCityFromCache();
                if (cities.isEmpty()) {
                    City city = new City();
                    city.setLocation(true);
                    cities.add(city);

                    DBUtil.insertAutoLocation();
                }
                e.onNext(cities);
                e.onComplete();
            }
        }).compose(RxSchedulers.<List<City>>io_main());
    }

    @Override
    public Observable<City> getLocation() {
//        return LocationUtil.getLocation().flatMap(new Function<Location, ObservableSource<City>>() {
//            @Override
//            public ObservableSource<City> apply(Location location) throws Exception {
//                Log.i(TAG, "flatMap... lat = " + location.getLatitude() + ", lon = " + location.getLongitude());
//                return LocationUtil.getCity(location.getLatitude(), location.getLongitude());
//            }
//        });
        return LocationUtil.getCity();
    }

    @Override
    public Observable<HeWeather> getWeather(String city, boolean force) {
        Observable<HeWeather> disk = WeatherUtil.getLocalWeather(city, force);
        Observable<HeWeather> network = WeatherUtil.getRemoteWeather(city);

        return disk.switchIfEmpty(network).doOnNext(new Consumer<HeWeather>() {
            @Override
            public void accept(HeWeather heWeather) throws Exception {
                if (heWeather == null || !heWeather.isOK()) {
                    throw new Exception("get Weather failed");
                }
            }
        }).timeout(20, TimeUnit.SECONDS).compose(RxSchedulers.<HeWeather>io_main());
    }

    @Override
    public Observable<Boolean> swapCity(final List<City> cities) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                for (int i = 0; i < cities.size(); i++) {
                    City city = cities.get(i);
                    DBUtil.updateIndex(city, i);
                }
                e.onNext(true);
                e.onComplete();
            }
        }).compose(RxSchedulers.<Boolean>io_main());
    }

    @Override
    public Observable<Boolean> deleteCity(final City city) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                boolean result = DBUtil.deleteCity(city);
                e.onNext(result);
                e.onComplete();
            }
        }).compose(RxSchedulers.<Boolean>io_main());
    }

    @Override
    public Observable<Boolean> undoCity(final City city) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                boolean result = DBUtil.undoCity(city);
                e.onNext(result);
                e.onComplete();
            }
        }).compose(RxSchedulers.<Boolean>io_main());
    }

    @Override
    public Observable<List<City>> searchCity(String query) {
        Log.d(TAG, "searchCity... query = " + query);
        return Api.getInstance().searchCity(BuildConfig.HEWEATHER_KEY, query).map(new Function<HeCity, List<City>>() {
            @Override
            public List<City> apply(HeCity heCity) throws Exception {
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
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                boolean exist = DBUtil.isExist(city);
                Log.d(TAG, "addCity... exist = " + exist);
                e.onNext(!exist && DBUtil.addCity(city, false));
                e.onComplete();
            }
        }).compose(RxSchedulers.<Boolean>io_main());
    }
}
