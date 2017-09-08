package com.ape.material.weather.search;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.ape.material.weather.BuildConfig;
import com.ape.material.weather.api.ApiService;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.bean.HeCity;
import com.ape.material.weather.data.DBUtil;
import com.ape.material.weather.data.IRepositoryManager;
import com.ape.material.weather.util.ActivityScope;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;

/**
 * Created by android on 17-9-8.
 */
@ActivityScope
public class SearchModel extends SearchContract.Model {
    private static final String TAG = "SearchModel";

    @Inject
    public SearchModel(Context context, IRepositoryManager manager) {
        mContext = context;
        mRepositoryManager = manager;
    }

    @Override
    Observable<List<City>> search(String query) {
        return mRepositoryManager.obtainRetrofitService(ApiService.class)
                .searchCity(BuildConfig.HEWEATHER_KEY, query)
                .map(new Function<HeCity, List<City>>() {
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

                });
    }

    @Override
    Observable<Boolean> addOrUpdateCity(final City city) {
        Log.d(TAG, "addCity... city = " + city.getCity());
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                boolean exist = DBUtil.isExist(mContext, city);
                Log.d(TAG, "addCity... exist = " + exist);
                e.onNext(!exist && DBUtil.addCity(mContext, city, false));
                e.onComplete();
            }
        });
    }
}
