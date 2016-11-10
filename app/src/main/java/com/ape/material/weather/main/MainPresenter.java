package com.ape.material.weather.main;

import com.ape.material.weather.bean.City;

import java.util.List;

import rx.Observer;

/**
 * Created by android on 16-11-10.
 */

public class MainPresenter extends MainContract.Presenter {
    @Override
    public void getCities() {
        mRxManage.add(mModel.getCities().subscribe(new Observer<List<City>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<City> cities) {
                mView.onCityChange(cities);
            }
        }));
    }
}
