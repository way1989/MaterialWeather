package com.ape.material.weather.dynamicweather;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.AnimationUtils;


public class DynamicWeatherView extends SurfaceView implements SurfaceHolder.Callback {

    static final String TAG = DynamicWeatherView.class.getSimpleName();
    private DrawThread mDrawThread;
    private BaseDrawer preDrawer, curDrawer;
    private float curDrawerAlpha = 0f;
    private BaseDrawer.Type curType = BaseDrawer.Type.DEFAULT;
    private int mWidth, mHeight;
    private SurfaceHolder holder;

    public DynamicWeatherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        curDrawerAlpha = 0f;
        mDrawThread = new DrawThread();
        holder = getHolder();
        holder.addCallback(this);
        holder.setFormat(PixelFormat.RGBA_8888);
    }

    private void setDrawer(BaseDrawer baseDrawer) {
        if (baseDrawer == null) {
            return;
        }
        curDrawerAlpha = 0f;
        if (this.curDrawer != null) {
            this.preDrawer = curDrawer;
        }
        this.curDrawer = baseDrawer;
        // updateDrawerSize(getWidth(), getHeight());
        // invalidate();
    }

    public void setDrawerType(BaseDrawer.Type type) {
        if (type == null) {
            return;
        }
        // UiUtil.toastDebug(getContext(), "setDrawerType->" + type.name());
        if (type != curType) {
            curType = type;
            setDrawer(BaseDrawer.makeDrawerByType(getContext(), curType));
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // updateDrawerSize(w, h);
        mWidth = w;
        mHeight = h;
    }

    private boolean drawSurface(Canvas canvas) {
        final int w = mWidth;
        final int h = mHeight;
        if (w == 0 || h == 0) {
            return true;
        }
        boolean needDrawNextFrame = false;
        // Log.d(TAG, "curDrawerAlpha->" + curDrawerAlpha);
        if (curDrawer != null) {
            curDrawer.setSize(w, h);
            needDrawNextFrame = curDrawer.draw(canvas, curDrawerAlpha);
        }
        if (preDrawer != null && curDrawerAlpha < 1f) {
            needDrawNextFrame = true;
            preDrawer.setSize(w, h);
            preDrawer.draw(canvas, 1f - curDrawerAlpha);
        }
        if (curDrawerAlpha < 1f) {
            curDrawerAlpha += 0.04f;
            if (curDrawerAlpha > 1) {
                curDrawerAlpha = 1f;
                preDrawer = null;
            }
        }
        // if (needDrawNextFrame) {
        // ViewCompat.postInvalidateOnAnimation(this);
        // }
        return needDrawNextFrame;
    }

    public void onResume() {
        Log.i(TAG, "onResume");
        // Let the drawing thread resume running.
        if (mDrawThread != null) {
            mDrawThread.setSuspend(false);
        }
    }

    public void onPause() {
        Log.i(TAG, "onPause");
        // Make sure the drawing thread is not running while we are paused.
        if (mDrawThread != null) {
            mDrawThread.setSuspend(true);
        }
    }

    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        // Make sure the drawing thread goes away.
        mDrawThread.setRunning(false);
        getHolder().removeCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Tell the drawing thread that a surface is available.
        Log.i(TAG, "surfaceCreated");
        mDrawThread = new DrawThread();
        mDrawThread.setRunning(true);
        mDrawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");
        // We need to tell the drawing thread to stop, and block until
        // it has done so.
        mDrawThread.setRunning(false);
    }

    private class DrawThread extends Thread {
        private final Object control = new Object();
        // These are protected by the Thread's lock.
        boolean isRunning;
        private boolean suspended = false;
        public void setRunning(boolean running) {
            isRunning = running;
        }

        public void setSuspend(boolean suspend) {
            if (!suspend) {
                synchronized (control) {
                    control.notifyAll();
                }
            }

            this.suspended = suspend;
        }
        @Override
        public void run() {
            while (isRunning) {
                // Log.i(TAG, "DrawThread run..");
                // Synchronize with activity: block until the activity is ready
                // and we have a surface; report whether we are active or
                // inactive
                // at this point; exit thread when asked to quit.
                synchronized (this) {
                    if (suspended) {
                        try {
                            synchronized (control) {
                                control.wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    final long startTime = AnimationUtils.currentAnimationTimeMillis();
                    // Lock the canvas for drawing.
                    Canvas canvas = holder.lockCanvas();

                    if (canvas != null) {
                        canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
                        // Update graphics.
                        drawSurface(canvas);
                        // All done!
                        holder.unlockCanvasAndPost(canvas);
                        //logger.addSplit("unlockCanvasAndPost");
                    } else {
                        Log.i(TAG, "Failure locking canvas");
                    }
                    final long drawTime = AnimationUtils.currentAnimationTimeMillis() - startTime;
                    final long needSleepTime = 16 - drawTime;
                    //Log.i(TAG, "drawSurface drawTime->" + drawTime + " needSleepTime->" + Math.max(0, needSleepTime));// needSleepTime);
                    if (needSleepTime > 0) {
                        SystemClock.sleep(needSleepTime);
                    }

                }
            }
        }
    }

}
