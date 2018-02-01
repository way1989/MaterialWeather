package com.ape.material.weather.dagger2;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.ape.material.weather.BuildConfig;
import com.ape.material.weather.activity.MainActivity;
import com.ape.material.weather.api.ApiService;
import com.ape.material.weather.api.CacheService;
import com.ape.material.weather.api.CityDao;
import com.ape.material.weather.api.CityDatabase;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.bean.HeCity;
import com.ape.material.weather.bean.HeWeather;
import com.ape.material.weather.data.IRepositoryManager;
import com.ape.material.weather.util.AMapLocationHelper;
import com.ape.material.weather.util.ActivityScope;
import com.ape.material.weather.util.RxLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictProvider;

/**
 * Created by android on 18-1-29.
 */
@ActivityScope
public class WeatherViewModel extends AndroidViewModel {
    private static final String TAG = "WeatherViewModel";
    private static final String LANG = "zh-cn";
    private IRepositoryManager mRepositoryManager;//用于管理网络请求层,以及数据缓存层

    @Inject
    public WeatherViewModel(@NonNull Application application, IRepositoryManager manager) {
        super(application);
        mRepositoryManager = manager;
    }

    public Observable<List<City>> search(String query) {
        return mRepositoryManager.obtainRetrofitService(ApiService.class)
                .searchCity(BuildConfig.HEWEATHER_KEY, query)
                .map(new Function<HeCity, List<City>>() {
                    @Override
                    public List<City> apply(HeCity heCity) throws Exception {
                        Log.d(TAG, "searchCity... result heCity = " + heCity);
                        ArrayList<City> cities = new ArrayList<>();
                        if (heCity == null || !heCity.isOK())
                            return cities;
                        List<HeCity.HeWeather5Bean> beanList = heCity.getHeWeather5();
                        for (HeCity.HeWeather5Bean bean : beanList) {
                            if (TextUtils.equals(bean.getStatus(), "ok")) {
                                HeCity.HeWeather5Bean.BasicBean basicBean = bean.getBasic();
                                City city = new City(basicBean.getCity(), basicBean.getCnty(),
                                        basicBean.getId(), basicBean.getLat(), basicBean.getLon(),
                                        basicBean.getProv());
                                cities.add(city);
                            }
                        }
                        Log.d(TAG, "searchCity... end size = " + cities.size());
                        return cities;
                    }

                });
    }

    public Observable<Boolean> addOrUpdateCity(final City city) {
        Log.d(TAG, "addCity... city = " + city.getCity());
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                CityDao cityDao = mRepositoryManager.obtainRoomDatabase(CityDatabase.class, CityDatabase.DATABASE_NAME).cityDao();
                final boolean exist = cityDao.getCityByAreaId(city.getAreaId()) != null;
                Log.d(TAG, "addCity... exist = " + exist);
                long id = -1;
                if (!exist) {
                    city.setIsLocation(0);
                    city.setIndex(cityDao.getCityCount());
                    id = cityDao.insert(city);
                }
                e.onNext(!exist && id > 0);
                e.onComplete();
            }
        });
    }

    public Observable<List<City>> getCities() {
        return Observable.create(new ObservableOnSubscribe<List<City>>() {
            @Override
            public void subscribe(ObservableEmitter<List<City>> e) throws Exception {
                CityDao cityDao = mRepositoryManager.obtainRoomDatabase(CityDatabase.class, CityDatabase.DATABASE_NAME).cityDao();
                List<City> cities = cityDao.getCityAll();
                //DBUtil.getCityFromCache(getApplication());
                if (cities.isEmpty()) {
                    City city = new City();
                    city.setIsLocation(1);
                    city.setCity(MainActivity.UNKNOWN_CITY);
                    cities.add(city);
                    //cityDao.insert(city);
                    //DBUtil.insertAutoLocation(getApplication());
                }
                e.onNext(cities);
                e.onComplete();
            }
        });
    }

    public Observable<Boolean> swapCity(final List<City> cities) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                CityDao cityDao = mRepositoryManager.obtainRoomDatabase(CityDatabase.class, CityDatabase.DATABASE_NAME).cityDao();
                for (int i = 0; i < cities.size(); i++) {
                    City city = cities.get(i);
                    city.setIndex(i);
                    cityDao.update(city);
                }
                e.onNext(true);
                e.onComplete();
            }
        });
    }

    public Observable<Boolean> deleteCity(final City city) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                CityDao cityDao = mRepositoryManager.obtainRoomDatabase(CityDatabase.class, CityDatabase.DATABASE_NAME).cityDao();
                boolean result = cityDao.delete(city) > 0;
                e.onNext(result);
                e.onComplete();
            }
        });
    }

    public Observable<City> getLocation() {
        return RxLocation.requestLocation(AMapLocationHelper.getInstance(this.getApplication()))
                .timeout(20, TimeUnit.SECONDS)
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
                        return mRepositoryManager.obtainRetrofitService(ApiService.class).searchCity(BuildConfig.HEWEATHER_KEY, s)
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
                                        CityDao cityDao = mRepositoryManager.obtainRoomDatabase(CityDatabase.class, CityDatabase.DATABASE_NAME).cityDao();
                                        cityDao.insert(city);
                                        //DBUtil.updateCity(context, city, true);
                                        return city;
                                    }
                                });
                    }
                });
    }

    public Observable<HeWeather> getWeather(String city, boolean force) {
        return mRepositoryManager.obtainCacheService(CacheService.class)
                .getWeather(mRepositoryManager.obtainRetrofitService(ApiService.class)
                                .getWeather(BuildConfig.HEWEATHER_KEY, city, LANG),
                        new DynamicKey(city), new EvictProvider(force));
    }
}
