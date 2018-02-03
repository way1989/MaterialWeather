package com.ape.material.weather.dagger2;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

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
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictProvider;

/**
 * Created by android on 18-1-29.
 */
@ActivityScope
public class WeatherViewModel extends AndroidViewModel {
    private static final String TAG = "WeatherViewModel";
    private static final long TIMEOUT_DURATION = 20L;//20s timeout
    private static final String LANG = "zh-cn";
    private IRepositoryManager mRepositoryManager;//用于管理网络请求层,以及数据缓存层

    private MutableLiveData<MenuItem> mMenuItemMutableLiveData = new MutableLiveData<>();

    @Inject
    public WeatherViewModel(@NonNull Application application, IRepositoryManager manager) {
        super(application);
        mRepositoryManager = manager;
    }

    public void setMenuItem(MenuItem menuItem) {
        mMenuItemMutableLiveData.setValue(menuItem);
    }

    public MutableLiveData<MenuItem> getMenuItemMutableLiveData() {
        return mMenuItemMutableLiveData;
    }

    public Observable<List<City>> search(String query) {
        return search(mRepositoryManager, query)
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
                CityDao cityDao = getDB(mRepositoryManager);
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
                CityDao cityDao = getDB(mRepositoryManager);
                List<City> cities = cityDao.getCityAll();
                if (cities.isEmpty()) {
                    City city = new City();
                    city.setIsLocation(1);
                    city.setCity(MainActivity.UNKNOWN_CITY);
                    cities.add(city);
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
                CityDao cityDao = getDB(mRepositoryManager);
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
                CityDao cityDao = getDB(mRepositoryManager);
                boolean result = cityDao.delete(city) > 0;
                e.onNext(result);
                e.onComplete();
            }
        });
    }

    public Observable<City> getLocation() {
        return new RxLocation(getApplication())
//                .timeout(TIMEOUT_DURATION, TimeUnit.SECONDS)
                .observeOn(Schedulers.io())
                .map(new Function<AMapLocation, String>() {
                    @Override
                    public String apply(AMapLocation location) throws Exception {
                        if (location.getErrorCode() != 0) {
                            //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                            Log.e(TAG, "getLocation Error!!! ErrCode:" + location.getErrorCode()
                                    + ", errInfo:" + location.getErrorInfo());
                            throw new Exception(location.getAdCode() + ": " + location.getErrorInfo());
                        }
                        final double longitude = location.getLongitude();
                        final double latitude = location.getLatitude();
                        String coordinate = latitude + "," + longitude;
//                        String coordinate = location.getCity().replace("市", "");
                        Log.d(TAG, "getLocation: location = " + coordinate);
                        return coordinate;
                    }
                })
                .flatMap(new Function<String, ObservableSource<City>>() {
                    @Override
                    public ObservableSource<City> apply(String s) throws Exception {
                        Log.d(TAG, "getLocation flatMap: location = " + s);
                        return search(mRepositoryManager, s)
                                .map(new Function<HeCity, City>() {
                                    @Override
                                    public City apply(HeCity heCity) throws Exception {
                                        Log.i(TAG, "getLocation map: heCity = " + heCity);
                                        HeCity.HeWeather5Bean.BasicBean basicBean = heCity.getHeWeather5().get(0).getBasic();
                                        City city = new City(basicBean.getCity(), basicBean.getCnty(),
                                                basicBean.getId(), basicBean.getLat(), basicBean.getLon(), basicBean.getProv());
                                        city.setIsLocation(1);
                                        CityDao cityDao = getDB(mRepositoryManager);
                                        final City exist = cityDao.getCityByLocation();
                                        Log.d(TAG, "getLocation exist = " + exist);
                                        if (exist != null) {
                                            cityDao.delete(exist);
                                        }
                                        cityDao.insert(city);
                                        return city;
                                    }
                                });
                    }
                });
    }

    private CityDao getDB(IRepositoryManager repositoryManager) {
        return repositoryManager.obtainRoomDatabase(CityDatabase.class,
                CityDatabase.DATABASE_NAME).cityDao();
    }

    private Observable<HeCity> search(IRepositoryManager repositoryManager, String query) {
        return repositoryManager.obtainRetrofitService(ApiService.class)
                .searchCity(BuildConfig.HEWEATHER_KEY, query);
    }

    public Observable<HeWeather> getWeather(final City city, boolean force) {
        final CacheService cacheService = mRepositoryManager.obtainCacheService(CacheService.class);
        final ApiService apiService = mRepositoryManager.obtainRetrofitService(ApiService.class);
        return cacheService.getWeather(apiService.getWeather(BuildConfig.HEWEATHER_KEY, city.getAreaId(), LANG),
                new DynamicKey(city), new EvictProvider(force))
                .timeout(TIMEOUT_DURATION, TimeUnit.SECONDS)
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<HeWeather>() {
                    @Override
                    public void accept(HeWeather heWeather) throws Exception {
                        Log.d(TAG, "getWeather: map weather isOK = " + heWeather.isOK());
                        if (heWeather.isOK()) {
                            String code = heWeather.getWeather().getNow().getCond().getCode();
                            String codeTxt = heWeather.getWeather().getNow().getCond().getTxt();
                            String tmp = heWeather.getWeather().getNow().getTmp();
                            Log.d(TAG, "getWeather: codeTxt = " + codeTxt + ", code = " + code + ", tmp = " + tmp);
                            city.setCode(code);
                            city.setCodeTxt(codeTxt);
                            city.setTmp(tmp);
                            getDB(mRepositoryManager).update(city);
                        }
                    }
                });
    }
}
