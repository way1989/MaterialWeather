package com.ape.material.weather.fragment;

import com.ape.material.weather.base.BaseModel;
import com.ape.material.weather.base.BasePresenter;
import com.ape.material.weather.base.BaseView;
import com.ape.material.weather.bean.entity.Weather;

import rx.Observable;

/**
 * Created by android on 16-11-10.
 */

public class WeatherContract {
    interface View extends BaseView {
        //返回获取的天气
        void onWeatherChange(Weather weather);

        void showErrorTip(String msg);
    }

    interface Model extends BaseModel {
        //请求获取天气
        Observable<Weather> getWeather(String city, String lang, boolean force);
    }

    abstract static class Presenter extends BasePresenter<WeatherContract.View, WeatherContract.Model> {
        //发起获取天气
        public abstract void getWeather(String city, String lang, boolean force);
    }
}
