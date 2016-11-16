package com.ape.material.weather.manage;

import com.ape.material.weather.bean.City;
import com.ape.material.weather.util.AppConstant;

import java.util.ArrayList;
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

    @Override
    public void swapCity(final ArrayList<City> data) {
        mRxManage.add(mModel.swapCity(data).subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                mRxManage.post(AppConstant.CITY_LIST_CHANGED, data);
            }
        }));
    }

    @Override
    public void deleteCity(City city) {
        mRxManage.add(mModel.deleteCity(city).subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                //mRxManage.post(AppConstant.CITY_LIST_CHANGED, null);
                mView.onCityModify();
            }
        }));
    }

    @Override
    public void undoCity(City city) {
        mRxManage.add(mModel.undoCity(city).subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                //mRxManage.post(AppConstant.CITY_LIST_CHANGED, null);
                mView.onCityModify();
            }
        }));
    }
}
