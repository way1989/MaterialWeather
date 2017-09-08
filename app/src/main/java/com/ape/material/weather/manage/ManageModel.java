package com.ape.material.weather.manage;

import android.content.Context;

import com.ape.material.weather.bean.City;
import com.ape.material.weather.data.DBUtil;
import com.ape.material.weather.data.IRepositoryManager;
import com.ape.material.weather.data.LocationUtil;
import com.ape.material.weather.util.ActivityScope;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by android on 17-9-8.
 */
@ActivityScope
public class ManageModel extends ManageContract.Model {

    @Inject
    public ManageModel(Context context, IRepositoryManager manager) {
        mContext = context;
        mRepositoryManager = manager;
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

    @Override
    Observable<Boolean> swapCity(final List<City> cities) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                for (int i = 0; i < cities.size(); i++) {
                    City city = cities.get(i);
                    DBUtil.updateIndex(mContext, city, i);
                }
                e.onNext(true);
                e.onComplete();
            }
        });
    }

    @Override
    Observable<Boolean> deleteCity(final City city) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                boolean result = DBUtil.deleteCity(mContext, city);
                e.onNext(result);
                e.onComplete();
            }
        });
    }

    @Override
    Observable<Boolean> undoCity(final City city) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                boolean result = DBUtil.undoCity(mContext, city);
                e.onNext(result);
                e.onComplete();
            }
        });
    }

    @Override
    Observable<City> getLocation() {
        return LocationUtil.getCity(mContext, mRepositoryManager);
    }
}
