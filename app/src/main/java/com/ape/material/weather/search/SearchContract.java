package com.ape.material.weather.search;

import com.ape.material.weather.base.BaseModel;
import com.ape.material.weather.base.BasePresenter;
import com.ape.material.weather.base.IView;
import com.ape.material.weather.bean.City;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by android on 16-11-16.
 */

public class SearchContract {

    interface View extends IView {
        void onSearchResult(List<City> cities);

        void onSearchError(Throwable e);

        void onSaveCitySucceed(City city);
    }

    abstract static class Presenter extends BasePresenter<Model, View> {
        public abstract void search(String query);

        public abstract void addOrUpdateCity(City city);
    }

    abstract static class Model extends BaseModel {
        abstract Observable<List<City>> search(String query);

        abstract Observable<Boolean> addOrUpdateCity(City city);
    }
}
