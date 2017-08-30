package com.ape.material.weather.util;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by way on 2016/10/27.
 */

public class RxBus {
    private static volatile RxBus sInstance;
    private final Subject<Object> subject;

    private RxBus() {
        subject = PublishSubject.create().toSerialized();
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
