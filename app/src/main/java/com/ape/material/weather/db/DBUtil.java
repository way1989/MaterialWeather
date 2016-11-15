package com.ape.material.weather.db;

import android.content.ContentResolver;
import android.database.Cursor;
import android.util.Log;

import com.ape.material.weather.App;
import com.ape.material.weather.bean.City;

import java.util.ArrayList;

/**
 * Created by android on 16-11-15.
 */

public class DBUtil {
    private static final String TAG = "DBUtil";

    public static ArrayList<City> getCityFromCache() {
        ArrayList<City> cities = new ArrayList<>();
        ContentResolver contentResolver = App.getContext().getContentResolver();
        Cursor cursor = contentResolver.query(CityProvider.CITY_CONTENT_URI, null, null, null,
                CityProvider.CityConstants.DEFAULT_SORT_ORDER);
        Log.d(TAG, "getCityFromCache cursor = " + cursor);
        if (cursor == null) return cities;
        Log.d(TAG, "getCityFromCache cursor.size = " + cursor.getCount());
        if (cursor.getCount() < 1) {
            cursor.close();
            return cities;
        }
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            String city = cursor.getString(cursor.getColumnIndex(CityProvider.CityConstants.CITY));
            String country = cursor.getString(cursor.getColumnIndex(CityProvider.CityConstants.COUNTRY));
            String areaId = cursor.getString(cursor.getColumnIndex(CityProvider.CityConstants.AREA_ID));
            String lat = cursor.getString(cursor.getColumnIndex(CityProvider.CityConstants.LATITUDE));
            String lon = cursor.getString(cursor.getColumnIndex(CityProvider.CityConstants.LONGITUDE));
            String prov = cursor.getString(cursor.getColumnIndex(CityProvider.CityConstants.PROVINCE));
            int isLocation = cursor.getInt(cursor.getColumnIndex(CityProvider.CityConstants.IS_LOCATION));
            City c = new City(city, country, areaId, lat, lon, prov);
            c.setLocation(isLocation == 1);
            cities.add(c);
        }
        if (!cursor.isClosed()) cursor.close();
        Log.d(TAG, "getCityFromCache cities.size = " + cities.size());
        return cities;
    }
}
