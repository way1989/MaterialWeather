package com.ape.material.weather.fragment;

import android.content.Context;

import com.ape.material.weather.BuildConfig;
import com.ape.material.weather.api.ApiService;
import com.ape.material.weather.api.CacheService;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.bean.HeWeather;
import com.ape.material.weather.data.IRepositoryManager;
import com.ape.material.weather.data.LocationUtil;
import com.ape.material.weather.util.FragmentScope;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictProvider;

/**
 * Created by android on 17-9-8.
 */
@FragmentScope
public class WeatherModel extends WeatherContract.Model {
    private static final String LANG = "zh-cn";

    @Inject
    public WeatherModel(Context context, IRepositoryManager manager) {
        mContext = context;
        mRepositoryManager = manager;
    }

    @Override
    public Observable<HeWeather> getWeather(String city, boolean force) {
        return mRepositoryManager.obtainCacheService(CacheService.class)
                .getWeather(mRepositoryManager.obtainRetrofitService(ApiService.class)
                                .getWeather(BuildConfig.HEWEATHER_KEY, city, LANG),
                        new DynamicKey(city), new EvictProvider(force));
    }

    @Override
    public Observable<City> getLocation() {

        return LocationUtil.getCity(mContext, mRepositoryManager);
    }

}
