package com.ape.material.weather.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ape.material.weather.App;
import com.ape.material.weather.AppComponent;
import com.ape.material.weather.dynamicweather.BaseDrawer;
import com.trello.rxlifecycle.components.support.RxFragment;

import butterknife.ButterKnife;

/**
 * Created by android on 16-11-10.
 */

public abstract class BaseFragment<T extends BasePresenter> extends RxFragment {
    protected View rootView;
    /**
     * 视图是否已经初初始化
     */
    protected boolean isInit = false;
    protected boolean isLoad = false;

/*    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isInit = true;
        isCanLoadData();
    }*/

    @Override

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isCanLoadData();
    }

    public abstract void lazyLoad();

    public abstract void stopLoad();

    /**
     * 是否可以加载数据 可以加载数据的条件： 1.视图已经初始化 2.视图对用户可见
     */
    private void isCanLoadData() {
        if (!isInit) {
            return;
        }

        if (getUserVisibleHint()) {
            lazyLoad();
            isLoad = true;
        } else {
            if (isLoad) {
                stopLoad();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null)
            rootView = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, rootView);

        initPresenter(((App) getActivity().getApplication()).getAppComponent());
        initView();
        isInit = true;
        /**初始化的时候去加载数据**/
        isCanLoadData();
        return rootView;
    }

    protected abstract void initView();

    /**
     * 视图销毁的时候讲Fragment是否初始化的状态变为false
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isInit = false;
        isLoad = false;
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
}
