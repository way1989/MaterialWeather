package com.ape.material.weather.fragment;

import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.ape.material.weather.App;
import com.ape.material.weather.BuildConfig;
import com.ape.material.weather.api.Api;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.bean.HeCity;
import com.ape.material.weather.bean.HeWeather;
import com.ape.material.weather.util.DeviceUtil;
import com.ape.material.weather.util.DiskLruCacheUtil;
import com.ape.material.weather.util.RxSchedulers;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

import static com.ape.material.weather.util.DeviceUtil.hasInternet;


/**
 * Created by way on 16-11-10.
 */

public class WeatherModel implements WeatherContract.Model {
    private static final String TAG = "WeatherModel";
    // wifi缓存时间为5分钟
    private static final long WIFI_CACHE_TIME = 5 * 60 * 1000;
    // 其他网络环境为1小时
    private static final long OTHER_CACHE_TIME = 60 * 60 * 1000;

    @Override
    public Observable<HeWeather> getWeather(String city, String lang, boolean force) {
        Observable<HeWeather> disk = getLocalWeather(city, force);
        Observable<HeWeather> network = getRemoteWeather(city, lang);
        return Observable.concat(disk, network).filter(new Func1<HeWeather, Boolean>() {
            @Override
            public Boolean call(HeWeather weather) {
                return weather != null && weather.isOK();
            }
        }).first().compose(RxSchedulers.<HeWeather>io_main());
    }

    public Observable<City> getCity(final double lat, final double lon) {
        Observable<String> cityName = getCityName(lat, lon);
        return cityName.flatMap(new Func1<String, Observable<City>>() {
            @Override
            public Observable<City> call(String s) {
                return Api.getInstance().searchCity(BuildConfig.HEWEATHER_KEY, s)
                        .filter(new Func1<HeCity, Boolean>() {
                            @Override
                            public Boolean call(HeCity heCity) {
                                Log.i(TAG, "filter... heCity = " + heCity);
                                return heCity != null && heCity.isOK();
                            }
                        }).map(new Func1<HeCity, City>() {
                            @Override
                            public City call(HeCity heCity) {
                                Log.i(TAG, "map... heCity = " + heCity);
                                HeCity.HeWeather5Bean.BasicBean basicBean = heCity.getHeWeather5().get(0).getBasic();
                                City city = new City(basicBean.getCity(), basicBean.getCnty(),
                                        basicBean.getId(), basicBean.getLat(), basicBean.getLon(), basicBean.getProv());
                                city.setLocation(true);
                                return city;
                            }
                        });
            }
        }).compose(RxSchedulers.<City>io_main());
    }

    @NonNull
    private Observable<String> getCityName(final double lat, final double lon) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Geocoder gc = new Geocoder(App.getContext());
                try {
                    List<Address> addresses = gc.getFromLocation(lat, lon, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        subscriber.onNext(addresses.get(0).getLocality());
                        subscriber.onCompleted();
                    } else {
                        throw new Exception("addressed is null");
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).filter(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                Log.i(TAG, "filter city name = " + s);
                return !TextUtils.isEmpty(s);
            }
        }).map(new Func1<String, String>() {
            @Override
            public String call(String s) {
                return s.replace("市", "");
            }
        });
    }

    private Observable<HeWeather> getLocalWeather(final String city, final boolean force) {
        return Observable.create(new Observable.OnSubscribe<HeWeather>() {

            @Override
            public void call(Subscriber<? super HeWeather> subscriber) {
                HeWeather weather = (HeWeather) DiskLruCacheUtil.getInstance(App.getContext())
                        .readObject(city);
                if (weather != null && weather.isOK())
                    subscriber.onNext(weather);
                subscriber.onCompleted();
            }
        }).filter(new Func1<HeWeather, Boolean>() {
            @Override
            public Boolean call(HeWeather weather) {
                if (!hasInternet()) return true;//如果没有网，直接使用缓存
                if (force) return false;//如果强制刷新数据
                //判断是否缓存超时
                return !isCacheFailure(weather);
            }
        });
    }

    private boolean isCacheFailure(HeWeather weather) {
        boolean isWifi = DeviceUtil.isWifiOpen();
        long cacheTime = weather.getUpdateTime();

        long existTime = System.currentTimeMillis() - cacheTime;
        boolean failure;
        if (isWifi) {
            failure = existTime > WIFI_CACHE_TIME;
        } else {
            failure = existTime > OTHER_CACHE_TIME;
        }
        Log.i(TAG, "existTime = " + existTime / 1000 + "s, isOverTime = " + failure);
        return failure;
    }

    private Observable<HeWeather> getRemoteWeather(final String city, String lang) {
        return Api.getInstance().getWeather(BuildConfig.HEWEATHER_KEY, city, lang)
                .filter(new Func1<HeWeather, Boolean>() {
                    @Override
                    public Boolean call(HeWeather weather) {
                        return weather != null && weather.isOK();
                    }
                }).map(new Func1<HeWeather, HeWeather>() {
                    @Override
                    public HeWeather call(HeWeather weather) {
                        weather.setUpdateTime(System.currentTimeMillis());
                        DiskLruCacheUtil.getInstance(App.getContext()).saveObject(city, weather);
                        return weather;
                    }
                });
    }

}
