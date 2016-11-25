package com.ape.material.weather.manage;

import com.ape.material.weather.base.BaseModel;
import com.ape.material.weather.base.BasePresenter;
import com.ape.material.weather.base.BaseView;
import com.ape.material.weather.bean.City;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by way on 2016/11/13.
 */

public class ManageContract {
    interface Model extends BaseModel {
        Observable<List<City>> getCities();

        Observable<Boolean> swapCity(ArrayList<City> data);

        Observable<Boolean> deleteCity(City city);

        Observable<Boolean> undoCity(City city);
    }

    interface View extends BaseView {
        void onCityChange(List<City> cities);

        void onCityModify();
    }

    abstract static class Presenter extends BasePresenter<ManageContract.View, ManageContract.Model> {
        public abstract void getCities();

        public abstract void swapCity(ArrayList<City> data);

        public abstract void deleteCity(City city);

        public abstract void undoCity(City city);
    }
}
