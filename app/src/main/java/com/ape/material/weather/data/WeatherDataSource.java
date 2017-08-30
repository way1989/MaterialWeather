package com.ape.material.weather.data;

import com.ape.material.weather.bean.City;
import com.ape.material.weather.bean.HeWeather;

import java.util.List;

import io.reactivex.Observable;


/**
 * Created by way on 2016/11/26.
 */

public interface WeatherDataSource {
    /**
     * get all city
     *
     * @return city list
     */
    Observable<List<City>> getCities();

    /**
     * get the city by GPS
     *
     * @return {@link City}
     */
    Observable<City> getLocation();

    /**
     * get weather by city name or area id
     *
     * @param city  city name or area id
     * @param force if force refresh
     * @return {@link HeWeather}
     */
    Observable<HeWeather> getWeather(String city, boolean force);


    /**
     * swap the city index in database
     *
     * @return if succeed
     */
    Observable<Boolean> swapCity(List<City> cities);

    /**
     * delete city from database
     *
     * @param city which need to delete
     * @return if succeed
     */
    Observable<Boolean> deleteCity(City city);

    /**
     * undo city to database
     *
     * @param city which need to undo
     * @return if succeed
     */
    Observable<Boolean> undoCity(City city);

    /**
     * search city by heWeather api from internet
     *
     * @param query the key to search
     * @return result city list
     */
    Observable<List<City>> searchCity(String query);

    /**
     * add new city to database
     *
     * @param city which need to add
     * @return if succeed
     */
    Observable<Boolean> addCity(City city);
}
