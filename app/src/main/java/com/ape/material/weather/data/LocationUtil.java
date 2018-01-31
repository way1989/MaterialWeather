package com.ape.material.weather.data;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.ape.material.weather.BuildConfig;
import com.ape.material.weather.api.ApiService;
import com.ape.material.weather.api.CityDao;
import com.ape.material.weather.api.CityDatabase;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.bean.HeCity;
import com.ape.material.weather.util.AMapLocationHelper;
import com.ape.material.weather.util.RxLocation;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by way on 2016/11/26.
 */

public class LocationUtil {
    private static final String TAG = "LocationUtil";
    private static final long LOCATION_OUT_TIME = 20;//20s out time to get location

    public static Observable<City> getCity(final Context context, final IRepositoryManager repositoryManager) {
        return RxLocation.requestLocation(AMapLocationHelper.getInstance(context))
                .observeOn(Schedulers.io())
                .timeout(LOCATION_OUT_TIME, TimeUnit.SECONDS)
                .map(new Function<AMapLocation, String>() {
                    @Override
                    public String apply(AMapLocation aMapLocation) throws Exception {
                        if (aMapLocation.getErrorCode() != 0) {
                            //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                            Log.e(TAG, "location Error, ErrCode:" + aMapLocation.getErrorCode()
                                    + ", errInfo:" + aMapLocation.getErrorInfo());
                            throw new Exception(aMapLocation.getAdCode() + ": " + aMapLocation.getErrorInfo());
                        }
                        String city = "";
                        if (aMapLocation.getErrorCode() == 0) {
//                            city = aMapLocation.getDistrict();
//                            if (TextUtils.isEmpty(city)) {
                            city = aMapLocation.getCity();
//                            }
                            Log.d(TAG, "onLocationChanged: city = " + city + ", district = " + aMapLocation.getDistrict());
                            if (!TextUtils.isEmpty(city)) {
                                return city;
                            }
                        } else {
                            //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                            Log.e(TAG, "location Error, ErrCode:" + aMapLocation.getErrorCode()
                                    + ", errInfo:" + aMapLocation.getErrorInfo());
                            throw new Exception(aMapLocation.getAdCode() + ": " + aMapLocation.getErrorInfo());
                        }
                        return city;
                    }
                })
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        return s.replace("市", "");
                    }
                }).flatMap(new Function<String, ObservableSource<City>>() {
                    @Override
                    public ObservableSource<City> apply(String s) throws Exception {
                        Log.d(TAG, "getCity start flatMap: city = " + s);
                        return repositoryManager.obtainRetrofitService(ApiService.class).searchCity(BuildConfig.HEWEATHER_KEY, s)
                                .filter(new Predicate<HeCity>() {
                                    @Override
                                    public boolean test(HeCity heCity) throws Exception {
                                        Log.i(TAG, "filter... heCity = " + heCity);
                                        return heCity != null && heCity.isOK();
                                    }
                                }).map(new Function<HeCity, City>() {
                                    @Override
                                    public City apply(HeCity heCity) throws Exception {
                                        Log.i(TAG, "map... heCity = " + heCity);
                                        HeCity.HeWeather5Bean.BasicBean basicBean = heCity.getHeWeather5().get(0).getBasic();
                                        City city = new City(basicBean.getCity(), basicBean.getCnty(),
                                                basicBean.getId(), basicBean.getLat(), basicBean.getLon(), basicBean.getProv());
                                        city.setIsLocation(1);
                                        CityDao cityDao = repositoryManager.obtainRoomDatabase(CityDatabase.class, CityDatabase.DATABASE_NAME).cityDao();
                                        cityDao.update(city);
                                        //DBUtil.updateCity(context, city, true);
                                        return city;
                                    }
                                });
                    }
                });
    }

}
