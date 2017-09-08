package com.ape.material.weather.main;

import android.content.Context;

import com.ape.material.weather.bean.City;
import com.ape.material.weather.data.DBUtil;
import com.ape.material.weather.util.ActivityScope;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by android on 17-9-7.
 */
@ActivityScope
public class MainModel extends MainContract.Model {
    @Inject
    public MainModel(Context context) {
        mContext = context;
    }

    @Override
    Observable<List<City>> getCities() {
        return Observable.create(new ObservableOnSubscribe<List<City>>() {
            @Override
            public void subscribe(ObservableEmitter<List<City>> e) throws Exception {
                ArrayList<City> cities = DBUtil.getCityFromCache(mContext);
                if (cities.isEmpty()) {
                    City city = new City();
                    city.setLocation(true);
                    cities.add(city);

                    DBUtil.insertAutoLocation(mContext);
                }
                e.onNext(cities);
                e.onComplete();
            }
        });
    }
}
