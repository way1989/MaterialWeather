package com.ape.material.weather.base;


import io.reactivex.disposables.Disposable;

public abstract class BasePresenter<M extends IModel, V extends IView> implements IPresenter {
    protected M mModel;
    protected V mView;

    public abstract void subscribe(Disposable disposable);

    public abstract void unSubscribe();
}
