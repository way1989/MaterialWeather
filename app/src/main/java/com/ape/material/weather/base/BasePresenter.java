package com.ape.material.weather.base;


import io.reactivex.disposables.Disposable;

public abstract class BasePresenter<T> {
    protected T mView;
    public abstract void register(Disposable disposable);
    public abstract void unSubscribe();
}
