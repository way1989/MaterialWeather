package com.ape.material.weather.widget.dynamic;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.animation.AnimationUtils;

import java.lang.ref.WeakReference;


/**
 * Created by liyu on 2017/8/16.
 */

public class DynamicWeatherView extends TextureView implements TextureView.SurfaceTextureListener {

    private int mFromColor;
    private DrawThread mDrawThread;
    private BaseWeatherType mWeatherType;
    private int mViewWidth;
    private int mViewHeight;

    public DynamicWeatherView(Context context) {
        this(context, null);
    }

    public DynamicWeatherView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DynamicWeatherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mWeatherType = new DefaultType(context.getResources());
        setSurfaceTextureListener(this);
//        SurfaceHolder holder = getHolder();
//        holder.addCallback(this);
//        holder.setFormat(PixelFormat.RGBA_8888);
    }

    public int getColor() {
        return mWeatherType.getColor();
    }

    public void setType(final BaseWeatherType type) {
        if (mWeatherType != null) {
            mWeatherType.endAnimation(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mFromColor = mWeatherType.getColor();
                    mWeatherType = type;
                    mDrawThread.setWeatherType(type);
                    if (mWeatherType != null) {
                        mWeatherType.onSizeChanged(mViewWidth, mViewHeight);
                    }
                    if (mWeatherType != null)
                        mWeatherType.startAnimation(mFromColor);
                }
            });
        } else {
            mFromColor = type.getColor();
            mWeatherType = type;
            mWeatherType.onSizeChanged(mViewWidth, mViewHeight);
            mWeatherType.startAnimation(mFromColor);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        if (mWeatherType != null) {
            mWeatherType.onSizeChanged(w, h);
        }
    }

    public void onResume() {
        if (mDrawThread != null) {
            mDrawThread.setSuspend(false);
        }
    }

    public void onPause() {
        if (mDrawThread != null) {
            mDrawThread.setSuspend(true);
        }
    }

    public void onDestroy() {
        mDrawThread.setRunning(false);
        setSurfaceTextureListener(null);
        if (mWeatherType != null) {
            mWeatherType.endAnimation(null);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mDrawThread = new DrawThread(this);
        mDrawThread.setWeatherType(mWeatherType);
        mDrawThread.setRunning(true);
        mDrawThread.start();
        mWeatherType.startAnimation(mWeatherType.getColor());

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mDrawThread.setRunning(false);
        setSurfaceTextureListener(null);
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private static class DrawThread extends Thread {

        private final Object mObject = new Object();
        WeakReference<TextureView> mSurfaceHolderWeakReference;
        WeakReference<BaseWeatherType> mWeatherTypeWeakReference;
        private boolean mIsRunning = false;
        private boolean mSuspended = false;

        DrawThread(TextureView holder) {
            mSurfaceHolderWeakReference = new WeakReference<>(holder);
        }

        void setWeatherType(BaseWeatherType weatherType) {
            if (mWeatherTypeWeakReference != null) {
                mWeatherTypeWeakReference.clear();
            }
            mWeatherTypeWeakReference = new WeakReference<>(weatherType);
        }

        void setRunning(boolean running) {
            mIsRunning = running;
            if (!running) {
                synchronized (mObject) {
                    mObject.notifyAll();
                }
            }
        }

        void setSuspend(boolean suspend) {
            this.mSuspended = suspend;
            if (!suspend) {
                synchronized (mObject) {
                    mObject.notifyAll();
                }
            }
        }

        @Override
        public void run() {
            while (mIsRunning) {
                if (mSuspended) {
                    try {
                        synchronized (mObject) {
                            mObject.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (!mIsRunning) {
                    return;
                }
                TextureView holder = mSurfaceHolderWeakReference.get();
                BaseWeatherType weatherType = mWeatherTypeWeakReference.get();
                if (holder == null || weatherType == null) {
                    continue;
                }
                final long startTime = AnimationUtils.currentAnimationTimeMillis();
                Canvas canvas = holder.lockCanvas();
                if (canvas != null) {
                    weatherType.onDrawElements(canvas);
                    holder.unlockCanvasAndPost(canvas);
                    final long drawTime = AnimationUtils.currentAnimationTimeMillis() - startTime;
                    final long needSleepTime = 16 - drawTime;
                    if (needSleepTime > 0) {
                        SystemClock.sleep(needSleepTime);
                    }
                }

            }
        }

    }
}
