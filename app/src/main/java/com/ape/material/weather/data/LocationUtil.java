package com.ape.material.weather.data;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import com.ape.material.weather.App;
import com.ape.material.weather.BuildConfig;
import com.ape.material.weather.api.Api;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.bean.HeCity;
import com.ape.material.weather.util.CityLocationManager;
import com.ape.material.weather.util.RxSchedulers;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Emitter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Cancellable;
import rx.functions.Func1;

/**
 * Created by way on 2016/11/26.
 */

public class LocationUtil {
    private static final String TAG = "LocationUtil";
    private static final long LOCATION_OUT_TIME = 10;//10s out time to get location

    /**
     * get Location: Latitude and Longitude
     */
    public static Observable<Location> getLocation() {
        return Observable.fromEmitter(new Action1<Emitter<Location>>() {
            @Override
            public void call(final Emitter<Location> emitter) {
                final CityLocationManager manager = new CityLocationManager(App.getContext());
                CityLocationManager.Listener listener = new CityLocationManager.Listener() {
                    @Override
                    public void onLocationSuccess(Location location) {
                        emitter.onNext(location);
                        emitter.onCompleted();
                    }
                };
                emitter.setCancellation(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        manager.setListener(null);
                    }
                });
                manager.setListener(listener);

                manager.startReceivingLocationUpdates();
            }
        }, Emitter.BackpressureMode.LATEST)
                .timeout(LOCATION_OUT_TIME, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<City> getCity(double latitude, double longitude) {
        Observable<String> cityName = getCityName(latitude, longitude);
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
                                DBUtil.updateCity(city, true);
                                return city;
                            }
                        });
            }
        }).compose(RxSchedulers.<City>io_main());
    }

    /**
     * get city name by Geocoder
     *
     * @param lat Latitude
     * @param lon Longitude
     */
    private static Observable<String> getCityName(final double lat, final double lon) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Geocoder geocoder = new Geocoder(App.getContext());
                try {
                    List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
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
}
