package com.ape.material.weather.manage;

import com.ape.material.weather.bean.City;

import java.util.List;

import rx.Observer;

/**
 * Created by way on 2016/11/13.
 */

public class ManageLocationPresenter extends ManageLocationContract.Presenter {
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
