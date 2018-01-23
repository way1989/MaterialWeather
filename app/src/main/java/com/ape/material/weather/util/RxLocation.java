package com.ape.material.weather.util;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.amap.api.location.AMapLocation;

import io.reactivex.Observable;

import static com.ape.material.weather.util.Preconditions.checkNotNull;


/**
 * Created by android on 18-1-23.
 */

public class RxLocation {

    @CheckResult
    @NonNull
    public static Observable<AMapLocation> requestLocation(@NonNull AMapLocationHelper locationHelper) {
        checkNotNull(locationHelper, "locationHelper == null");
        return new LocationObservable(locationHelper);
    }
}
