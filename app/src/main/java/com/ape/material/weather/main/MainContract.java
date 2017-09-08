package com.ape.material.weather.main;

import com.ape.material.weather.base.BaseModel;
import com.ape.material.weather.base.BasePresenter;
import com.ape.material.weather.base.IView;
import com.ape.material.weather.bean.City;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by android on 16-11-10.
 */

public class MainContract {

    interface View extends IView {
        //返回获取的城市列表
        void onCityChange(List<City> cities);

        void reloadCity();
    }

    abstract static class Presenter extends BasePresenter<Model, View> {
        //发起获取城市列表
        public abstract void getCities();
    }

    abstract static class Model extends BaseModel {

        /**
         * get all city
         *
         * @return city list
         */
        abstract Observable<List<City>> getCities();
    }
}
