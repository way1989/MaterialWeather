package com.ape.material.weather.api;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;

import com.ape.material.weather.bean.City;

/**
 * Created by android on 18-1-31.
 */
@Database(entities = {City.class}, version = 2, exportSchema = false)
public abstract class CityDatabase extends RoomDatabase {
    public static final String TABLE_NAME = "city";// 城市表名
    public static final String DATABASE_NAME = "city.db";
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
            database.execSQL("DROP TABLE IF EXISTS city");
        }
    };

    public abstract CityDao cityDao();
}
