package com.ape.material.weather.manage;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ape.material.weather.bean.City;
import com.ape.material.weather.util.RxBus;
import com.ape.material.weather.util.RxBusEvent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by way on 2016/11/13.
 */

public class ManagePresenter extends ManageContract.Presenter {
    @NonNull
    private CompositeSubscription mSubscriptions;

    @Inject
    ManagePresenter(Context context, ManageContract.Model model, ManageContract.View view) {
        mContext = context;
        mModel = model;
        mView = view;
        mSubscriptions = new CompositeSubscription();
    }

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

    @Override
    public void swapCity(final ArrayList<City> data) {
        mSubscriptions.clear();
        Subscription subscription = mModel.swapCity(data).subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                RxBusEvent.MainEvent event = new RxBusEvent.MainEvent(data);
                RxBus.getInstance().post(event);
            }
        });
        mSubscriptions.add(subscription);
    }

    @Override
    public void deleteCity(City city) {
        mSubscriptions.clear();
        Subscription subscription = mModel.deleteCity(city).subscribe(new Observer<Boolean>() {
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
        });
        mSubscriptions.add(subscription);
    }

    @Override
    public void undoCity(City city) {
        mSubscriptions.clear();
        Subscription subscription = mModel.undoCity(city).subscribe(new Observer<Boolean>() {
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
        });
        mSubscriptions.add(subscription);
    }
}
