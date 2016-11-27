package com.ape.material.weather.search;

import com.ape.material.weather.base.BasePresenter;
import com.ape.material.weather.base.BaseView;
import com.ape.material.weather.bean.City;

import java.util.List;

/**
 * Created by android on 16-11-16.
 */

public class SearchContract {

    interface View extends BaseView {
        void onSearchResult(List<City> cities);

        void onSearchError(Throwable e);

        void onSaveCitySucceed(City city);
    }

    abstract static class Presenter extends BasePresenter<SearchContract.View> {
        public abstract void search(String query);

        public abstract void addOrUpdateCity(City city);
    }
}
