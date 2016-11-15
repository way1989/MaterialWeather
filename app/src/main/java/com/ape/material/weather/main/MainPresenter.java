package com.ape.material.weather.main;

import com.ape.material.weather.bean.City;
import com.ape.material.weather.util.AppConstant;

import java.util.List;

import rx.Observer;
import rx.functions.Action1;

/**
 * Created by android on 16-11-10.
 */

public class MainPresenter extends MainContract.Presenter {
    @Override
    public void onStart() {
        super.onStart();
        //监听城市列表变化
        mRxManage.on(AppConstant.CITY_LIST_CHANGED, new Action1<List<City>>() {

            @Override
            public void call(List<City> cities) {
                if (cities != null) {
                    mView.onCityChange(cities);
                } else {
                    mView.reloadCity();
                }
            }
        });

    }

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
