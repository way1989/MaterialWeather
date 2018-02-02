package com.ape.material.weather.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ScrollView;

import java.io.File;
import java.io.FileOutputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;

/**
 * Created by android on 18-1-26.
 */

public class RxImage {
    public static Observable<File> saveText2ImageObservable(final ScrollView view) {
        return Observable.create(new ObservableOnSubscribe<Bitmap>() {

            @Override
            public void subscribe(ObservableEmitter<Bitmap> e) throws Exception {
                View v = view.getRootView();
                v.setDrawingCacheEnabled(true);
                v.buildDrawingCache();
                Bitmap bitmap = v.getDrawingCache();
                //Bitmap bitmap = saveScrollViewToBitmap(view);
                if (bitmap == null) {
                    throw new Exception("无法生产图片!");
                }
                e.onNext(bitmap);
                e.onComplete();
            }
        }).map(new Function<Bitmap, File>() {
            @Override
            public File apply(Bitmap bitmap) throws Exception {
                File appDir = view.getContext().getExternalCacheDir();
                if (!appDir.exists() || appDir.isFile()) {
                    appDir.mkdirs();
                }
                String fileName = "share.jpg";
                File file = new File(appDir, fileName);
                FileOutputStream outputStream = new FileOutputStream(file);
                assert bitmap != null;
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
//                Uri uri = Uri.fromFile(file);
//                Intent scannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
//                context.sendBroadcast(scannerIntent);
                //Uri contentURI = getImageContentUri(context, file.getAbsolutePath());
                return file;
            }

        });
    }


    public static Uri getImageContentUri(Context context, String absPath) {

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , new String[]{MediaStore.Images.Media._ID}
                , MediaStore.Images.Media.DATA + "=? "
                , new String[]{absPath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(id));

        } else if (!absPath.isEmpty()) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, absPath);
            return context.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } else {
            return null;
        }
    }

    private static Bitmap saveScrollViewToBitmap(ScrollView scrollView) {
        int h = 0;
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
        }
        Bitmap bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        return bitmap;
    }
}
