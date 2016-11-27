package com.ape.material.weather.fragment;

import com.ape.material.weather.base.BasePresenter;
import com.ape.material.weather.base.BaseView;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.bean.HeWeather;

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


    abstract static class Presenter extends BasePresenter<WeatherContract.View> {
        //发起获取天气
        public abstract void getWeather(String city, boolean force);

        public abstract void getLocation();

    }
}
