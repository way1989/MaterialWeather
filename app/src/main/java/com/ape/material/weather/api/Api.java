package com.ape.material.weather.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.ape.material.weather.BuildConfig;

import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by android on 16-11-10.
 */

public class Api {
    private static final String BASE_RUL = BuildConfig.HEWEATHER_URL;
    private static final int READ_TIMEOUT = 60;//读取超时时间,单位  秒
    private static final int CONN_TIMEOUT = 12;//连接超时时间,单位  秒
    private volatile static ApiService sInstance;

    private Api() {
    }

    private static Retrofit newInstance() {
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(READ_TIMEOUT, TimeUnit.MINUTES)
                .connectTimeout(CONN_TIMEOUT, TimeUnit.SECONDS).build();//初始化一个client,不然retrofit会自己默认添加一个
        Gson gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.PROTECTED)//忽略protected字段
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        return new Retrofit.Builder()
                .client(client)//添加一个client,不然retrofit会自己默认添加一个
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_RUL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static ApiService getInstance() {
        if (sInstance == null) {
            synchronized (Api.class) {
                if (sInstance == null) {
                    sInstance = newInstance().create(ApiService.class);
                }
            }
        }
        return sInstance;
    }
}