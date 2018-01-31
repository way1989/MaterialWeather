package com.ape.material.weather.dagger2;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

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
import com.ape.material.weather.data.LocationUtil;
import com.ape.material.weather.util.ActivityScope;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;
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
                boolean exist = cityDao.getCityByAreaId(city.getAreaId()) != null;
                Log.d(TAG, "addCity... exist = " + exist);
                city.setIsLocation(0);
                long id = cityDao.insert(city);
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
                    cityDao.insert(city);
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
                cityDao.updateAll(cities);

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
        return LocationUtil.getCity(getApplication(), mRepositoryManager);
    }

    public Observable<HeWeather> getWeather(String city, boolean force) {
        return mRepositoryManager.obtainCacheService(CacheService.class)
                .getWeather(mRepositoryManager.obtainRetrofitService(ApiService.class)
                                .getWeather(BuildConfig.HEWEATHER_KEY, city, LANG),
                        new DynamicKey(city), new EvictProvider(force));
    }
}
