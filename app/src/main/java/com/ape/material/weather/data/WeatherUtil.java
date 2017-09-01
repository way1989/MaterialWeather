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
                final boolean hasInternet = hasInternet();
                Log.d(TAG, "getLocalWeather: force = " + force + ", hasInternet = " + hasInternet);
                if (!force || !hasInternet) {//无网或者非强制刷新,先读取缓存
                    HeWeather weather = (HeWeather) DiskLruCacheUtil.getInstance(App.getContext())
                            .readObject(city);
                    if (!hasInternet || !isCacheFailure(weather)) {//无网或者缓存未过期，直接使用缓存
                        Log.d(TAG, "getLocalWeather: local weather = " + weather.isOK());
                        e.onNext(weather);
                    }
                }
                e.onComplete();
            }
        });
    }

    public static Observable<HeWeather> getRemoteWeather(final String city) {
        return Api.getInstance().getWeather(BuildConfig.HEWEATHER_KEY, city, LANG)
                .map(new Function<HeWeather, HeWeather>() {
                    @Override
                    public HeWeather apply(HeWeather heWeather) throws Exception {
                        Log.d(TAG, "getRemoteWeather: map heWeather = " + heWeather.getErrorStatus());
                        heWeather.setUpdateTime(System.currentTimeMillis());
                        DiskLruCacheUtil.getInstance(App.getContext()).saveObject(city, heWeather);
                        return heWeather;
                    }
                });
    }

    public static boolean isCacheFailure(HeWeather weather) {
        if (weather == null || !weather.isOK()) {
            return true;
        }
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
