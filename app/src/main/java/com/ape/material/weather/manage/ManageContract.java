package com.ape.material.weather.manage;

import com.ape.material.weather.base.BaseModel;
import com.ape.material.weather.base.BasePresenter;
import com.ape.material.weather.base.IView;
import com.ape.material.weather.bean.City;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by way on 2016/11/13.
 */

public class ManageContract {

    interface View extends IView {
        void onCityChange(List<City> cities);

        void onCityModify();

        void onLocationChanged(City city);

        void showLoading();
    }

    abstract static class Presenter extends BasePresenter<Model, View> {
        public abstract void getCities();

        public abstract void swapCity(ArrayList<City> data);

        public abstract void deleteCity(City city);

        public abstract void undoCity(City city);

        public abstract void getLocation();
    }

    abstract static class Model extends BaseModel {
        abstract Observable<List<City>> getCities();

        abstract Observable<Boolean> swapCity(List<City> cities);

        abstract Observable<Boolean> deleteCity(final City city);

        abstract Observable<Boolean> undoCity(City city);

        abstract Observable<City> getLocation();
    }
}
