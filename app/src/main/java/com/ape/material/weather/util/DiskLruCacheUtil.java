package com.ape.material.weather.util;

import android.content.Context;
import android.os.Environment;

import com.ape.material.weather.BuildConfig;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 缓存工具类
 *
 * @author way
 */

public class DiskLruCacheUtil {
    private static final String TAG = "DiskLruCacheUtil";
    private static final String CACHE_OBJECT = "object";// 对象缓存目录
    private static final int APP_VERSION = BuildConfig.VERSION_CODE;
    private static final int VALUE_COUNT = 1;// 同一个key可以对应多少个缓存文件
    private static final int MAX_SIZE = 10 * 1024 * 1024;// 一个缓存文件最大可以缓存10M
    private static final int DISK_CACHE_INDEX = 0;

    private volatile static DiskLruCacheUtil sInstance;
    /**
     * Disk LRU cache
     */
    private DiskLruCache mDiskCache;

    private DiskLruCacheUtil(Context context) {
        // Set up disk cache
        if (mDiskCache == null || mDiskCache.isClosed()) {
            File diskCacheDir = getDiskCacheDir(context, CACHE_OBJECT);
            if (diskCacheDir != null) {
                if (!diskCacheDir.exists()) {
                    diskCacheDir.mkdirs();
                }
                if (getUsableSpace(diskCacheDir) > MAX_SIZE) {
                    try {
                        mDiskCache = DiskLruCache.open(diskCacheDir, APP_VERSION, VALUE_COUNT, MAX_SIZE);
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    /**
     * Used to create a singleton of {@link DiskLruCacheUtil}
     *
     * @param context The {@link Context} to use
     * @return A new instance of this class.
     */
    public static DiskLruCacheUtil getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (DiskLruCacheUtil.class) {
                if (sInstance == null)
                    sInstance = new DiskLruCacheUtil(context.getApplicationContext());
            }
        }
        return sInstance;
    }

    /**
     * 获取相应的缓存目录
     *
     * @param context
     * @param uniqueName
     * @return
     */
    private static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath, uniqueName);
    }

    /**
     * Check how much usable space is available at a given path.
     *
     * @param path The path to check
     * @return The space available in bytes
     */
    public static long getUsableSpace(final File path) {
        return path.getUsableSpace();
    }

    /**
     * 传入缓存的key值，以得到相应的MD5值
     *
     * @param key
     * @return
     */
    public static String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 关闭流
     *
     * @param closeables
     */
    private static void closeIO(Closeable... closeables) {
        if (null == closeables || closeables.length <= 0) {
            return;
        }
        for (Closeable cb : closeables) {
            try {
                if (null == cb) {
                    continue;
                }
                cb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存对象缓存
     *
     * @param key
     * @param ser
     */
    public void saveObject(String key, Serializable ser) {
        // Add to disk cache
        if (mDiskCache != null && !mDiskCache.isClosed()) {
            key = hashKeyForDisk(key);
            ObjectOutputStream oos = null;
            try {

                final DiskLruCache.Editor editor = mDiskCache.edit(key);
                if (editor != null) {
                    oos = new ObjectOutputStream(editor.newOutputStream(DISK_CACHE_INDEX));
                    oos.writeObject(ser);
                    oos.flush();
                    editor.commit();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeIO(oos);
            }
        }
    }

    /**
     * 读取对象缓存
     *
     * @param key
     * @return
     */
    public Serializable readObject(String key) {
        if (mDiskCache != null && !mDiskCache.isClosed()) {
            ObjectInputStream ois = null;
            try {
                final DiskLruCache.Snapshot snapshot = mDiskCache.get(hashKeyForDisk(key));
                if (snapshot != null) {
                    ois = new ObjectInputStream(snapshot.getInputStream(DISK_CACHE_INDEX));
                    return (Serializable) ois.readObject();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                closeIO(ois);
            }
        }
        return null;
    }
}
