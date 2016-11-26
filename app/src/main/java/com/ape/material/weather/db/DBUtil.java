package com.ape.material.weather.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
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

    public static boolean isExist(City city) {
        ContentResolver contentResolver = App.getContext().getContentResolver();
        Cursor cursor = contentResolver.query(CityProvider.CITY_CONTENT_URI,
                new String[]{CityProvider.CityConstants.CITY},
                CityProvider.CityConstants.AREA_ID + "=?", new String[]{city.getAreaId()}, null);
        Log.d(TAG, "getCityFromCache cursor = " + cursor);
        if (cursor == null) return false;
        int size = cursor.getCount();
        Log.d(TAG, "getCityFromCache cursor.size = " + size);
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return size > 0;
    }

    public static boolean addCity(City city, boolean autoLocation) {
        ContentValues values = new ContentValues();
        values.put(CityProvider.CityConstants.CITY, city.getCity());
        values.put(CityProvider.CityConstants.AREA_ID, city.getAreaId());
        values.put(CityProvider.CityConstants.COUNTRY, city.getCountry());
        values.put(CityProvider.CityConstants.LATITUDE, city.getLat());
        values.put(CityProvider.CityConstants.LONGITUDE, city.getLon());
        values.put(CityProvider.CityConstants.PROVINCE, city.getProv());
        values.put(CityProvider.CityConstants.IS_LOCATION, autoLocation ? 1 : 0);
        values.put(CityProvider.CityConstants.ORDER_INDEX, getCacheCitySize());

        ContentResolver contentResolver = App.getContext().getContentResolver();
        Uri uri = contentResolver.insert(CityProvider.CITY_CONTENT_URI, values);
        return uri != null;
    }

    public static boolean updateCity(City city, boolean autoLocation) {
        ContentValues values = new ContentValues();
        values.put(CityProvider.CityConstants.CITY, city.getCity());
        values.put(CityProvider.CityConstants.AREA_ID, city.getAreaId());
        values.put(CityProvider.CityConstants.COUNTRY, city.getCountry());
        values.put(CityProvider.CityConstants.LATITUDE, city.getLat());
        values.put(CityProvider.CityConstants.LONGITUDE, city.getLon());
        values.put(CityProvider.CityConstants.PROVINCE, city.getProv());
        ContentResolver contentResolver = App.getContext().getContentResolver();
        String where = autoLocation ? CityProvider.CityConstants.IS_LOCATION + "=?"
                : CityProvider.CityConstants.AREA_ID + "=?";
        String[] selectionArgs = autoLocation ? new String[]{"1"} : new String[]{city.getAreaId()};
        int rowsModified = contentResolver.update(CityProvider.CITY_CONTENT_URI,
                values, where, selectionArgs);
        if (rowsModified == 0) {
            values.put(CityProvider.CityConstants.IS_LOCATION, autoLocation ? 1 : 0);
            values.put(CityProvider.CityConstants.ORDER_INDEX, getCacheCitySize());
            // If no prior row existed, insert a new one
            Uri uri = contentResolver.insert(CityProvider.CITY_CONTENT_URI, values);
            return uri != null;
        }
        return rowsModified > 0;
    }

    public static int getCacheCitySize() {
        ContentResolver contentResolver = App.getContext().getContentResolver();
        Cursor cursor = contentResolver.query(CityProvider.CITY_CONTENT_URI,
                new String[]{CityProvider.CityConstants.CITY}, null, null, null);
        Log.d(TAG, "getCityFromCache cursor = " + cursor);
        if (cursor == null) return 0;
        int size = cursor.getCount();
        Log.d(TAG, "getCityFromCache cursor.size = " + size);
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return size;
    }

    public static boolean updateIndex(City city, int i) {
        ContentResolver contentResolver = App.getContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CityProvider.CityConstants.ORDER_INDEX, i);
        int rowsModified = contentResolver.update(CityProvider.CITY_CONTENT_URI,
                values, CityProvider.CityConstants.AREA_ID + "=?",
                new String[]{city.getAreaId()});
        return rowsModified > 0;
    }

    public static boolean deleteCity(City city) {
        ContentResolver contentResolver = App.getContext().getContentResolver();
        int rowsModified = contentResolver.delete(CityProvider.CITY_CONTENT_URI,
                CityProvider.CityConstants.AREA_ID + "=?", new String[]{city.getAreaId()});
        return rowsModified > 0;
    }

    public static boolean undoCity(City city) {
        ContentValues values = new ContentValues();
        values.put(CityProvider.CityConstants.CITY, city.getCity());
        values.put(CityProvider.CityConstants.AREA_ID, city.getAreaId());
        values.put(CityProvider.CityConstants.COUNTRY, city.getCountry());
        values.put(CityProvider.CityConstants.LATITUDE, city.getLat());
        values.put(CityProvider.CityConstants.LONGITUDE, city.getLon());
        values.put(CityProvider.CityConstants.PROVINCE, city.getProv());
        values.put(CityProvider.CityConstants.IS_LOCATION, city.isLocation());
        values.put(CityProvider.CityConstants.ORDER_INDEX, city.getIndex());

        ContentResolver contentResolver = App.getContext().getContentResolver();
        Uri uri = contentResolver.insert(CityProvider.CITY_CONTENT_URI, values);
        return uri != null;
    }
}
