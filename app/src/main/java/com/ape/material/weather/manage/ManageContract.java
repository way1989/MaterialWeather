package com.ape.material.weather.manage;

import com.ape.material.weather.base.BasePresenter;
import com.ape.material.weather.base.BaseView;
import com.ape.material.weather.bean.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by way on 2016/11/13.
 */

public class ManageContract {

    interface View extends BaseView {
        void onCityChange(List<City> cities);

        void onCityModify();

        void onLocationChanged(City city);
    }

    abstract static class Presenter extends BasePresenter<ManageContract.View> {
        public abstract void getCities();

        public abstract void swapCity(ArrayList<City> data);

        public abstract void deleteCity(City city);

        public abstract void undoCity(City city);

        public abstract void getLocation();
    }
}
