package com.ape.material.weather.util;

import android.app.Application;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by liweiping on 18-1-23.
 */

public class RxLocation extends Observable<AMapLocation> {
    private static final String TAG = "RxLocation";
    private AMapLocationClient mLocationClient;

    public RxLocation(Application application) {
        mLocationClient = new AMapLocationClient(application);
        mLocationClient.setLocationOption(getAMapLocationClientOption());
    }

    @Override
    protected void subscribeActual(Observer<? super AMapLocation> observer) {
        Listener listener = new Listener(mLocationClient, observer);
        observer.onSubscribe(listener);
        mLocationClient.setLocationListener(listener);
        mLocationClient.startLocation();
        Log.d(TAG, "subscribeActual: startLocation...");
    }

    private AMapLocationClientOption getAMapLocationClientOption() {
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        option.setOnceLocationLatest(true);
        option.setHttpTimeOut(20000L);
        return option;
    }

    private static final class Listener implements Disposable, AMapLocationListener {
        private final AtomicBoolean unsubscribed = new AtomicBoolean();
        private final AMapLocationClient mMapLocationClient;
        private final Observer<? super AMapLocation> observer;

        Listener(AMapLocationClient locationHelper, Observer<? super AMapLocation> observer) {
            this.mMapLocationClient = locationHelper;
            this.observer = observer;
        }

        @Override
        public void onLocationChanged(AMapLocation location) {
            Log.d(TAG, "onLocationChanged: isDisposed = " + isDisposed() + ", location = " + location);
            if (!isDisposed()) {
                observer.onNext(location);
                observer.onComplete();
                dispose();
            }
        }

        @Override
        public void dispose() {
            Log.d(TAG, "dispose: stopLocation...");
            if (unsubscribed.compareAndSet(false, true)) {
                mMapLocationClient.unRegisterLocationListener(this);
                mMapLocationClient.stopLocation();
                mMapLocationClient.onDestroy();
            }
        }

        @Override
        public boolean isDisposed() {
            return unsubscribed.get();
        }
    }
}
