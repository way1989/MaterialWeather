package com.ape.material.weather.data;

import android.arch.persistence.room.RoomDatabase;

/**
 * ================================================
 * Created by JessYan on 17/03/2017 11:15
 * Contact with jess.yan.effort@gmail.com
 * Follow me on https://github.com/JessYanCoding
 * ================================================
 */

public interface IRepositoryManager {

    /**
     * 根据传入的 Class 获取对应的 Retrofit service
     *
     * @param service
     * @param <T>
     * @return
     */
    <T> T obtainRetrofitService(Class<T> service);

    /**
     * 根据传入的 Class 获取对应的 RxCache service
     *
     * @param cache
     * @param <T>
     * @return
     */
    <T> T obtainCacheService(Class<T> cache);

    /**
     * 清理所有缓存
     */
    void clearAllCache();

    /**
     * 根据传入的 Class 获取对应的 RxCache service
     *
     * @param database RoomDatabase Class
     * @param <DB>     RoomDatabase
     * @param dbName   RoomDatabase name
     * @return RoomDatabase
     */
    <DB extends RoomDatabase> DB obtainRoomDatabase(Class<DB> database, String dbName);
}
