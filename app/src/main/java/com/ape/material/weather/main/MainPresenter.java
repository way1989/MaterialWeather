package com.ape.material.weather.main;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ape.material.weather.bean.City;

import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by android on 16-11-10.
 */

public class MainPresenter extends MainContract.Presenter {
    @NonNull
    private CompositeSubscription mSubscriptions;

    @Inject
    MainPresenter(Context context, MainContract.Model model, MainContract.View view) {
        mContext = context;
        mModel = model;
        mView = view;
        mSubscriptions = new CompositeSubscription();
    }
//    @Override
//    public void onStart() {
//        super.onStart();
//        //监听城市列表变化
//        mRxManage.on(AppConstant.CITY_LIST_CHANGED, new Action1<List<City>>() {
//
//            @Override
//            public void call(List<City> cities) {
//                if (cities != null) {
//                    mView.onCityChange(cities);
//                } else {
//                    mView.reloadCity();
//                }
//            }
//        });
//
//    }

    @Override
    public void getCities() {
        mSubscriptions.clear();
        Subscription subscription = mModel.getCities().subscribe(new Observer<List<City>>() {
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
        });
        mSubscriptions.add(subscription);
    }
}
