package com.ape.material.weather.data;

import android.util.Log;

import com.ape.material.weather.App;
import com.ape.material.weather.BuildConfig;
import com.ape.material.weather.api.Api;
import com.ape.material.weather.bean.HeWeather;
import com.ape.material.weather.util.DeviceUtil;
import com.ape.material.weather.util.DiskLruCacheUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static com.ape.material.weather.util.DeviceUtil.hasInternet;

/**
 * Created by way on 2016/11/26.
 */

public class WeatherUtil {
    private static final String TAG = "WeatherUtil";
    // wifi缓存时间为5分钟
    private static final long WIFI_CACHE_TIME = 5 * 60 * 1000;
    // 其他网络环境为1小时
    private static final long OTHER_CACHE_TIME = 60 * 60 * 1000;
    private static final String LANG = "zh-cn";

    public static Observable<HeWeather> getLocalWeather(final String city, final boolean force) {
        return Observable.create(new ObservableOnSubscribe<HeWeather>() {
            @Override
            public void subscribe(ObservableEmitter<HeWeather> e) throws Exception {
                HeWeather weather = (HeWeather) DiskLruCacheUtil.getInstance(App.getContext())
                        .readObject(city);
                if (weather != null && weather.isOK()) {
                    e.onNext(weather);
                }
                e.onComplete();
            }
        }).filter(new Predicate<HeWeather>() {
            @Override
            public boolean test(HeWeather heWeather) throws Exception {
                if (!hasInternet()) return true;//如果没有网，直接使用缓存
                if (force) return false;//如果强制刷新数据
                //判断是否缓存超时
                return !isCacheFailure(heWeather);
            }
        });
    }

    public static Observable<HeWeather> getRemoteWeather(final String city) {
        return Api.getInstance().getWeather(BuildConfig.HEWEATHER_KEY, city, LANG)
                .filter(new Predicate<HeWeather>() {
                    @Override
                    public boolean test(HeWeather heWeather) throws Exception {
                        return heWeather != null && heWeather.isOK();
                    }
                }).map(new Function<HeWeather, HeWeather>() {
                    @Override
                    public HeWeather apply(HeWeather heWeather) throws Exception {
                        heWeather.setUpdateTime(System.currentTimeMillis());
                        DiskLruCacheUtil.getInstance(App.getContext()).saveObject(city, heWeather);
                        return heWeather;
                    }
                });
    }

    public static boolean isCacheFailure(HeWeather weather) {
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
}
