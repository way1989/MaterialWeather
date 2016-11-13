package com.ape.material.weather.manage;

import com.ape.material.weather.base.BaseModel;
import com.ape.material.weather.base.BasePresenter;
import com.ape.material.weather.base.BaseView;
import com.ape.material.weather.bean.City;

import java.util.List;

import rx.Observable;

/**
 * Created by way on 2016/11/13.
 */

public class ManageLocationContract {
    interface Model extends BaseModel {
        Observable<List<City>> getCities();
    }

    interface View extends BaseView {
        void onCityChange(List<City> cities);
    }

    abstract static class Presenter extends BasePresenter<ManageLocationContract.View, ManageLocationContract.Model> {
        public abstract void getCities();
    }
}
