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

import org.reactivestreams.Subscriber;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Emitter;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;


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
        return Observable.create(new ObservableOnSubscribe<Location>() {
            @Override
            public void subscribe(final ObservableEmitter<Location> e) throws Exception {
                final CityLocationManager manager = new CityLocationManager(App.getContext());
                CityLocationManager.Listener listener = new CityLocationManager.Listener() {
                    @Override
                    public void onLocationSuccess(Location location) {
                        e.onNext(location);
                        e.onComplete();
                    }
                };
                e.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        manager.setListener(null);
                    }
                });
                manager.setListener(listener);

                manager.startReceivingLocationUpdates();
            }
        }).timeout(LOCATION_OUT_TIME, TimeUnit.SECONDS).compose(RxSchedulers.<Location>io_main());
    }

    public static Observable<City> getCity(double latitude, double longitude) {
        Observable<String> cityName = getCityName(latitude, longitude);
        return cityName.flatMap(new Function<String, ObservableSource<City>>() {
            @Override
            public ObservableSource<City> apply(String s) throws Exception {
                return Api.getInstance().searchCity(BuildConfig.HEWEATHER_KEY, s)
                        .filter(new Predicate<HeCity>() {
                            @Override
                            public boolean test(HeCity heCity) throws Exception {
                                Log.i(TAG, "filter... heCity = " + heCity);
                                return heCity != null && heCity.isOK();
                            }
                        }).map(new Function<HeCity, City>() {
                            @Override
                            public City apply(HeCity heCity) throws Exception {
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
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                Geocoder geocoder = new Geocoder(App.getContext());
                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    e.onNext(addresses.get(0).getLocality());
                    e.onComplete();
                } else {
                    throw new Exception("addressed is null");
                }
            }
        }).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                return !TextUtils.isEmpty(s);
            }
        }).map(new Function<String, String>() {
            @Override
            public String apply(String s) throws Exception {
                return s.replace("市", "");
            }
        });
    }
}
