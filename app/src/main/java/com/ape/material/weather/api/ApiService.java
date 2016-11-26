package com.ape.material.weather.api;

import com.ape.material.weather.bean.HeCity;
import com.ape.material.weather.bean.HeWeather;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by android on 16-9-30.
 */

public interface ApiService {
    /**
     * @param key  用户认证key
     * @param city 城市名称 city可通过城市中英文名称、ID和IP地址进行，例如city=北京，city=beijing，city=CN101010100，city=
     *             60.194.130.1
     * @param lang 多语言，默认为中文，可选参数
     * @return 天气结果
     */
    @GET("weather?")
    Observable<HeWeather> getWeather(@Query("key") String key, @Query("city") String city,
                                     @Query("lang") String lang);

    /**
     * @param key  用户认证key
     * @param city 城市名称 city可通过城市中英文名称、ID和IP地址进行，例如city=北京，city=beijing，city=CN101010100，city=
     *             60.194.130.1
     * @return 搜索到的城市结果
     */
    @GET("search?")
    Observable<HeCity> searchCity(@Query("key") String key, @Query("city") String city);
}
