package com.ape.material.weather.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ape.material.weather.App;
import com.ape.material.weather.AppComponent;
import com.ape.material.weather.bean.HeWeather;
import com.ape.material.weather.dynamicweather.BaseDrawer;
import com.trello.rxlifecycle2.components.support.RxFragment;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * Created by android on 16-11-10.
 */

public abstract class BaseFragment<T extends BasePresenter> extends RxFragment {
    protected View rootView;
    protected boolean isViewInitiated;
    protected boolean isDataInitiated;
    @Inject
    protected T mPresenter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isViewInitiated = true;
        prepareFetchData();
    }

    @Override

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
            prepareFetchData();
    }

    public abstract void loadDataFirstTime();

    private boolean prepareFetchData() {
        if (getUserVisibleHint() && isViewInitiated && !isDataInitiated) {
            loadDataFirstTime();
            isDataInitiated = true;
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null)
            rootView = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, rootView);

        initPresenter(((App) getActivity().getApplication()).getAppComponent());
        initView();
        return rootView;
    }

    protected abstract void initView();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    //public abstract String getTitle();

    public abstract BaseDrawer.Type getDrawerType();

    /**
     * should override this method when use MVP
     */
    protected void initPresenter(AppComponent appComponent) {
    }

    /**
     * must override this method
     *
     * @return resource layout id
     */
    protected abstract
    @LayoutRes
    int getLayoutId();

    public abstract HeWeather getWeather();
}
