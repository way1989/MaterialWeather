package com.ape.material.weather.main;

import com.ape.material.weather.base.BaseModel;
import com.ape.material.weather.base.BasePresenter;
import com.ape.material.weather.base.BaseView;
import com.ape.material.weather.bean.City;

import java.util.List;

import rx.Observable;

/**
 * Created by android on 16-11-10.
 */

public class MainContract {

    interface View extends BaseView {
        //返回获取的新闻
        void onCityChange(List<City> cities);
    }

    interface Model extends BaseModel {
        //请求获取新闻
        Observable<List<City>> getCities();
    }

    abstract static class Presenter extends BasePresenter<View, Model> {
        //发起获取新闻请求
        public abstract void getCities();
    }

}
