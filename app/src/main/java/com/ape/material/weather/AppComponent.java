package com.ape.material.weather;

import android.content.Context;

import com.ape.material.weather.data.IRepositoryManager;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by android on 16-11-25.
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    //用于管理网络请求层,以及数据缓存层,对外开放的接口
    IRepositoryManager repositoryManager();

    Context getContext();
}
