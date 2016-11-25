package com.ape.material.weather.util;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by way on 2016/10/27.
 */

public class RxBus {
    private static volatile RxBus sInstance;
    private final Subject subject;

    private RxBus() {
        subject = new SerializedSubject<>(PublishSubject.create());
    }

    public static RxBus getInstance() {
        if (sInstance == null) {
            synchronized (RxBus.class) {
                if (sInstance == null) {
                    sInstance = new RxBus();
                }
            }
        }
        return sInstance;
    }

    public void post(Object event) {
        subject.onNext(event);
    }

    public <T> Observable<T> toObservable(Class<T> eventType) {
        return subject.ofType(eventType);
    }
}
