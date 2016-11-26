package com.ape.material.weather.search;

import android.text.TextUtils;
import android.util.Log;

import com.ape.material.weather.BuildConfig;
import com.ape.material.weather.api.Api;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.bean.HeCity;
import com.ape.material.weather.data.DBUtil;
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
                boolean exist = DBUtil.isExist(city);
                Log.d(TAG, "addOrUpdateCity... exist = " + exist);
                subscriber.onNext(!exist && DBUtil.addCity(city, false));
                subscriber.onCompleted();
            }
        }).compose(RxSchedulers.<Boolean>io_main());
    }


}
