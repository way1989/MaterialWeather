package com.ape.material.weather.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

import static com.ape.material.weather.db.CityProvider.CityDatabaseHelper.CITY_TABLE_NAME;


public class CityProvider extends ContentProvider {
    public static final String AUTHORITY = "com.ape.material.weather.City";// 授权
    public static final Uri CITY_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + CITY_TABLE_NAME);// 城市uri
    private static final String TAG = "CityProvider";
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);// 匹配

    private static final int CITYS = 1;// 多个城市查询
    private static final int CITY_ID = 2;// 单个城市查询

    static {
        URI_MATCHER.addURI(AUTHORITY, "city", CITYS);
        URI_MATCHER.addURI(AUTHORITY, "city/#", CITY_ID);
    }

    private SQLiteOpenHelper mOpenHelper;

    private static void infoLog(String data) {
        Log.i(TAG, data);
    }

    @Override
    public boolean onCreate() {
        infoLog("onCreate...");
        mOpenHelper = new CityDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case CITYS:
                qBuilder.setTables(CITY_TABLE_NAME);
                break;
            case CITY_ID:
                qBuilder.setTables(CITY_TABLE_NAME);
                qBuilder.appendWhere(CityConstants._ID + "=");
                qBuilder.appendWhere(uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
        }

        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = CityConstants.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        Cursor ret = qBuilder.query(mOpenHelper.getReadableDatabase(), projection, selection,
                selectionArgs, null, null, orderBy);

        if (ret == null) {
            infoLog("CityProvider.query: failed");
        } else {
            ret.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return ret;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case CITYS:
                return CityConstants.CONTENT_TYPE;
            case CITY_ID:
                return CityConstants.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URL");
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues initialValues) {

        ContentValues values = (initialValues != null) ? new ContentValues(
                initialValues) : new ContentValues();

/*        for (String colName : CityConstants.getRequiredColumns()) {
            if (!values.containsKey(colName)) {
                throw new IllegalArgumentException("Missing column: " + colName);
            }
        }*/

        long rowId = mOpenHelper.getWritableDatabase().insert(CITY_TABLE_NAME,
                CityConstants.CITY, values);

        if (rowId < 0) {
            throw new SQLException("Failed to insert row into " + uri);
        }

        Uri noteUri = ContentUris.withAppendedId(CITY_CONTENT_URI, rowId);
        getContext().getContentResolver().notifyChange(noteUri, null);// 发出通知
        return noteUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (URI_MATCHER.match(uri)) {
            case CITYS:
                count = db.delete(CITY_TABLE_NAME, selection, selectionArgs);
                break;
            case CITY_ID:
                String segment = uri.getPathSegments().get(1);

                if (TextUtils.isEmpty(selection)) {
                    selection = CityConstants._ID + "=" + segment;
                } else {
                    selection = CityConstants._ID + "=" + segment + " AND (" + selection + ")";
                }
                count = db.delete(CITY_TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot delete from URL: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count;
        long rowId = 0;
        int match = URI_MATCHER.match(uri);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (match) {
            case CITYS:
                count = db.update(CITY_TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case CITY_ID:
                String tmpSegment = uri.getPathSegments().get(1);
                rowId = Long.parseLong(tmpSegment);
                count = db.update(CITY_TABLE_NAME, values, CityConstants._ID + "=" + rowId, null);
                break;
            default:
                throw new UnsupportedOperationException("Cannot update URL: " + uri);
        }
        infoLog("*** notifyChange() rowId: " + rowId + " url " + uri);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static class CityDatabaseHelper extends SQLiteOpenHelper {
        static final String CITY_TABLE_NAME = "city";// 城市表名
        private static final String DATABASE_NAME = "city.db";
        private static final int DATABASE_VERSION = 1;

        CityDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            infoLog("creating new city table");
            db.execSQL("CREATE table IF NOT EXISTS "
                    + CITY_TABLE_NAME + " ("
                    + CityConstants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + CityConstants.CITY + " TEXT, "
                    + CityConstants.COUNTRY + " TEXT,"
                    + CityConstants.AREA_ID + " TEXT, "
                    + CityConstants.LATITUDE + " TEXT, "
                    + CityConstants.LONGITUDE + " TEXT, "
                    + CityConstants.PROVINCE + " TEXT, "
                    + CityConstants.IS_LOCATION + " TEXT, "
                    + CityConstants.ORDER_INDEX + " INTEGER AUTOINCREMENT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            infoLog("onUpgrade: from " + oldVersion + " to " + newVersion);
            switch (oldVersion) {
                default:
                    db.execSQL("DROP TABLE IF EXISTS " + CITY_TABLE_NAME);
                    onCreate(db);
            }
        }
    }

    public static final class CityConstants implements BaseColumns {
        public static final String CITY = "city";
        public static final String COUNTRY = "country";
        public static final String AREA_ID = "areaId";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String PROVINCE = "province";
        public static final String IS_LOCATION = "isLocation";
        public static final String ORDER_INDEX = "orderIndex";
        public static final String DEFAULT_SORT_ORDER = ORDER_INDEX + " ASC"; // 默认按照orderIndex排序
        private static final String CONTENT_TYPE = "vnd.android.cursor.dir/city";
        private static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/city";

        private CityConstants() {
        }

        static ArrayList<String> getRequiredColumns() {
            ArrayList<String> tmpList = new ArrayList<>();
            tmpList.add(CITY);
            tmpList.add(AREA_ID);
            return tmpList;
        }

        public static String[] getCityDefaultProjection() {
            return new String[]{CITY, COUNTRY, AREA_ID, LATITUDE, LONGITUDE, PROVINCE,
                    IS_LOCATION, ORDER_INDEX};
        }
    }
}
