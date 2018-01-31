package com.ape.material.weather.api;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.ape.material.weather.bean.City;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by android on 18-1-31.
 */
@Dao
public interface CityDao {
    @Query("SELECT * from city")
    public List<City> getCityAll();

    @Query("SELECT * from city")
    public Flowable<List<City>> getCityFromCache();

    @Query("SELECT * from city where areaId = :areaId")
    public City getCityByAreaId(String areaId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long[] insertAll(List<City> cities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insert(City city);

    @Delete
    public int delete(City city);

    @Update
    public int update(City city);

    @Update
    public int updateAll(List<City> cities);
}
