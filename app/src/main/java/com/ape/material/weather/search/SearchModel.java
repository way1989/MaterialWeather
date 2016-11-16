package com.ape.material.weather.search;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.ape.material.weather.App;
import com.ape.material.weather.BuildConfig;
import com.ape.material.weather.api.Api;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.bean.HeCity;
import com.ape.material.weather.db.CityProvider;
import com.ape.material.weather.util.RxSchedulers;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by android on 16-11-16.
 */

public class SearchModel implements SearchContract.Model {
    private static final String TAG = "SearchModel";

    @Override
    public Observable<List<City>> search(String query) {
        Log.d(TAG, "search... query = " + query);
        return Api.getInstance().searchCity(BuildConfig.HEWEATHER_KEY, query).map(new Func1<HeCity, List<City>>() {
            @Override
            public List<City> call(HeCity heCity) {
                Log.d(TAG, "map... result heCity = " + heCity);
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
                Log.d(TAG, "map... end size = " + cities.size());
                return cities;
            }
        }).compose(RxSchedulers.<List<City>>io_main());
    }

    @Override
    public Observable<Boolean> addOrUpdateCity(final City city) {
        Log.d(TAG, "addOrUpdateCity... city = " + city.getCity());
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                boolean result = updateCity(city);
                Log.d(TAG, "addOrUpdateCity... result = " + result);
                subscriber.onNext(result);
                subscriber.onCompleted();
            }
        }).compose(RxSchedulers.<Boolean>io_main());
    }

    private boolean updateCity(City city) {
        ContentValues values = new ContentValues();
        values.put(CityProvider.CityConstants.CITY, city.getCity());
        values.put(CityProvider.CityConstants.AREA_ID, city.getAreaId());
        values.put(CityProvider.CityConstants.COUNTRY, city.getCountry());
        values.put(CityProvider.CityConstants.LATITUDE, city.getLat());
        values.put(CityProvider.CityConstants.LONGITUDE, city.getLon());
        values.put(CityProvider.CityConstants.PROVINCE, city.getProv());
        ContentResolver contentResolver = App.getContext().getContentResolver();
        int rowsModified = contentResolver.update(CityProvider.CITY_CONTENT_URI,
                values, CityProvider.CityConstants.AREA_ID + "=?", new String[]{city.getAreaId()});
        if (rowsModified == 0) {
            values.put(CityProvider.CityConstants.IS_LOCATION, 0);
            // If no prior row existed, insert a new one
            Uri uri = contentResolver.insert(CityProvider.CITY_CONTENT_URI, values);
            return uri != null;
        }
        return rowsModified > 0;
    }
}
