package com.ape.material.weather.base;


import com.ape.material.weather.data.IRepositoryManager;

/**
 * Created by jess on 8/5/16 12:55
 * contact with jess.yan.effort@gmail.com
 */
public abstract class BaseModel<T extends IRepositoryManager> implements IModel {
    protected T mRepositoryManager;//用于管理网络请求层,以及数据缓存层

    @Override
    public void onDestroy() {
        mRepositoryManager = null;
    }
}
