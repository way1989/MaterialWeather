package com.ape.material.weather.share;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ape.material.weather.R;
import com.ape.material.weather.base.BaseActivity;
import com.ape.material.weather.util.RxSchedulers;
import com.ape.material.weather.util.ShareUtils;

import java.io.File;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;


public class ShareActivity extends BaseActivity {
    private static final String TAG = "ShareActivity";
    private static final String EXTRA_SHARE_MESSAGE = "share_message";
    @BindView(R.id.share_container)
    ScrollView scrollView;
    @BindView(R.id.tv_share_content)
    TextView tv;

    DisposableObserver<File> observer = new DisposableObserver<File>() {
        @Override
        public void onNext(File uri) {
            Log.d(TAG, "getShareContent onNext: uri = " + uri);
            ShareUtils.shareImage(ShareActivity.this, uri, "分享到");
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "onError: ", e);
            Toast.makeText(getApplicationContext(), "分享失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onComplete() {

        }
    };

    @NonNull
    private CompositeDisposable mCompositeDisposable;

    public static void start(Context context, String shareMessage) {
        Intent intent = new Intent(context, ShareActivity.class);
        intent.putExtra(EXTRA_SHARE_MESSAGE, shareMessage);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_share;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCompositeDisposable = new CompositeDisposable();
        loadData();
    }

    protected void loadData() {
        String shareMessage = getIntent().getStringExtra(EXTRA_SHARE_MESSAGE);
        tv.setText(shareMessage);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                RxImage.saveText2ImageObservable(ShareActivity.this, scrollView)
                        .compose(RxSchedulers.<File>io_main())
                        .subscribe(observer);
                mCompositeDisposable.add(observer);
                ShareActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }
}
