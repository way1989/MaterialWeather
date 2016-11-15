package com.ape.material.weather.fragment;

import com.ape.material.weather.base.BaseModel;
import com.ape.material.weather.base.BasePresenter;
import com.ape.material.weather.base.BaseView;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.bean.HeWeather;

import rx.Observable;

/**
 * Created by android on 16-11-10.
 */

public class WeatherContract {
    interface View extends BaseView {
        //返回获取的天气
        void onWeatherChange(HeWeather weather);

        void showErrorTip(String msg);

        void onCityChange(City city);
    }

    interface Model extends BaseModel {
        //请求获取天气
        Observable<HeWeather> getWeather(String city, String lang, boolean force);

        Observable<City> getCity();
    }

    abstract static class Presenter extends BasePresenter<WeatherContract.View, WeatherContract.Model> {
        //发起获取天气
        public abstract void getWeather(String city, String lang, boolean force);

        public abstract void getLocation();

    }
}
