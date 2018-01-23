package com.ape.material.weather.util;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

import static com.ape.material.weather.util.Preconditions.checkMainThread;

/**
 * Created by android on 18-1-23.
 */

public class LocationObservable extends Observable<AMapLocation> {
    private AMapLocationHelper mLocationHelper;

    public LocationObservable(AMapLocationHelper locationHelper) {
        mLocationHelper = locationHelper;
    }

    @Override
    protected void subscribeActual(Observer<? super AMapLocation> observer) {
        //get location can run in work thread
//        if (!checkMainThread(observer)) {
//            return;
//        }
        Listener listener = new Listener(mLocationHelper, observer);
        observer.onSubscribe(listener);
        mLocationHelper.startLocation(listener);
    }

    static final class Listener extends MainThreadDisposable implements AMapLocationListener {
        private final AMapLocationHelper mLocationHelper;
        private final Observer<? super AMapLocation> observer;

        Listener(AMapLocationHelper locationHelper, Observer<? super AMapLocation> observer) {
            this.mLocationHelper = locationHelper;
            this.observer = observer;
        }


        @Override
        protected void onDispose() {
            mLocationHelper.stopLocation();
        }

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (!isDisposed()) {
                observer.onNext(aMapLocation);
            }
        }
    }
}
