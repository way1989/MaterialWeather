package com.ape.material.weather.fragment;

import com.ape.material.weather.base.BaseModel;
import com.ape.material.weather.base.BasePresenter;
import com.ape.material.weather.base.IView;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.bean.HeWeather;

import io.reactivex.Observable;

/**
 * Created by android on 16-11-10.
 */

public class WeatherContract {
    interface View extends IView {
        //返回获取的天气
        void onWeatherChange(HeWeather weather);

        void showErrorTip(String msg);

        void onCityChange(City city);
    }


    abstract static class Presenter extends BasePresenter<Model, View> {
        //发起获取天气
        public abstract void getWeather(String city, boolean force);

        public abstract void getLocation();

    }

    abstract static class Model extends BaseModel {
        public abstract Observable<HeWeather> getWeather(String city, boolean force);

        public abstract Observable<City> getLocation();
    }
}
