package com.ape.material.weather.api;

import com.ape.material.weather.bean.HeWeather;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictProvider;
import io.rx_cache2.LifeCache;

/**
 * Created by android on 17-9-6.
 */

public interface CacheService {
    @LifeCache(duration = 30, timeUnit = TimeUnit.MINUTES)
    Observable<HeWeather> getWeather(Observable<HeWeather> users, DynamicKey key, EvictProvider evictProvider);
}
