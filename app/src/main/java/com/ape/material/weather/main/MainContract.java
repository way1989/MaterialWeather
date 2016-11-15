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

    interface Model extends BaseModel {
        //请求获取城市列表
        Observable<List<City>> getCities();
    }

    interface View extends BaseView {
        //返回获取的城市列表
        void onCityChange(List<City> cities);

        void reloadCity();
    }

    abstract static class Presenter extends BasePresenter<View, Model> {
        //发起获取城市列表
        public abstract void getCities();
    }

}
