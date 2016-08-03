package com.shihc.demo.andrewdemo.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.DecelerateInterpolator;

/**
 * TODO: document your custom view class.
 */
public class WaveformAnimView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "WaveformAnimView";
    private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    private Paint mPaint;
    private float mWidth;
    private float mHeight;

    private final Object mSurfaceLock = new Object();
    private DrawThread mThread;
    private long SLEEP_TIME = 100;
    private ValueAnimator mAnimator;
    private float mAnimatedValue;

    private float attenuationCoefficient;
    private float finiteScale;//x轴缩放比例

    private float rect1X;


    private float startX, startY, endX, endY;
    private PorterDuffXfermode xfermode;


    public WaveformAnimView(Context context) {
        super(context);
        init();
    }

    public WaveformAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveformAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        getHolder().addCallback(this);
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);

        xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatedValue = (float) animation.getAnimatedValue();
            }
        });

        mAnimator.setInterpolator(new DecelerateInterpolator());
        mAnimator.setDuration(500);
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mThread = new DrawThread(holder);
        mThread.setRun(true);
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        //这里可以获取SurfaceView的宽高等信息
        mWidth = width;
        mHeight = height;
        startX = -mWidth / 2;
        startY = 0;
        endX = mWidth / 2;
        endY = 0;
        finiteScale = 1 / (mWidth / 2) * 2;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        synchronized (mSurfaceLock) {  //这里需要加锁，否则doDraw中有可能会crash
            mThread.setRun(false);
            holder.removeCallback(this);
        }
    }

    private class DrawThread extends Thread {
        private SurfaceHolder mHolder;
        private boolean mIsRun = false;

        public DrawThread(SurfaceHolder holder) {
            super(TAG);
            mHolder = holder;
        }

        @Override
        public void run() {
            while (true) {
                synchronized (mSurfaceLock) {
                    if (!mIsRun) {
                        return;
                    }
                    Canvas canvas = mHolder.lockCanvas();
                    if (canvas != null) {
                        doDraw(canvas);  //这里做真正绘制的事情
                        mHolder.unlockCanvasAndPost(canvas);
                    }
                }
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void setRun(boolean isRun) {
            this.mIsRun = isRun;
        }
    }

    private void doDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        canvas.save();
        canvas.translate(mWidth / 2, mHeight / 2);
        drawFirstLayer(canvas);
        drawSecondLayer(canvas);
        canvas.restore();
    }

    private void drawSecondLayer(Canvas canvas) {
        //将绘制操作保存到新的图层（更官方的说法应该是离屏缓存）
        int layer = canvas.saveLayer(-mWidth / 2, -mHeight / 2, mWidth / 2, mHeight / 2, null, Canvas.ALL_SAVE_FLAG);

        mPaint.setStyle(Paint.Style.FILL);

        mPaint.setColor(Color.RED);
        canvas.drawPath(computeLine3Path(), mPaint);

        mPaint.setColor(Color.GREEN);
        canvas.drawPath(computeLine4Path(), mPaint);

        LinearGradient gradient2 = new LinearGradient(-mWidth / 2, -mHeight / 2, -mWidth / 2, mHeight / 2,
                Color.RED, Color.GREEN, Shader.TileMode.REPEAT);
        mPaint.setShader(gradient2);
        mPaint.setXfermode(xfermode);
        canvas.drawRect(-mWidth / 2, -mHeight / 2, mWidth / 2, mHeight / 2, mPaint);

        // 还原画布
        canvas.restoreToCount(layer);

        //波浪线描边
        mPaint.setXfermode(null);
        mPaint.setShader(null);
        mPaint.setStyle(Paint.Style.STROKE);

        mPaint.setColor(Color.RED);
        canvas.drawPath(computeLine3Path(), mPaint);

        mPaint.setColor(Color.GREEN);
        canvas.drawPath(computeLine4Path(), mPaint);
    }

    private void drawFirstLayer(Canvas canvas) {
        //将绘制操作保存到新的图层（更官方的说法应该是离屏缓存）
        int sc = canvas.saveLayer(-mWidth / 2, -mHeight / 2, mWidth / 2, mHeight / 2, null, Canvas.ALL_SAVE_FLAG);

        mPaint.setStyle(Paint.Style.FILL);

        mPaint.setColor(Color.BLUE);
        canvas.drawPath(computeLine1Path(), mPaint);

        mPaint.setColor(Color.MAGENTA);
        canvas.drawPath(computeLine2Path(), mPaint);

        LinearGradient gradient = new LinearGradient(-mWidth / 2, -mHeight / 2, -mWidth / 2, mHeight / 2,
                Color.RED, Color.GREEN, Shader.TileMode.REPEAT);
        mPaint.setShader(gradient);
        mPaint.setXfermode(xfermode);
        canvas.drawRect(-mWidth / 2, -mHeight / 2, mWidth / 2, mHeight / 2, mPaint);

        // 还原画布
        canvas.restoreToCount(sc);

        mPaint.setXfermode(null);
        mPaint.setShader(null);
        mPaint.setStyle(Paint.Style.STROKE);
        //波浪线描边
        mPaint.setColor(Color.BLUE);
        canvas.drawPath(computeLine1Path(), mPaint);

        mPaint.setColor(Color.MAGENTA);
        canvas.drawPath(computeLine2Path(), mPaint);
    }

    private Path computeLine1Path() {
        Path path = new Path();
        path.moveTo(startX, 0);
        for (float i = startX; i < endX; i += 10) {
            float x = i * finiteScale;
            float y = (float) (0.5 * Math.pow((4 / (4 + Math.pow(x, 4))), 2.5) * Math.sin(0.75 * Math.PI * x - 0.5 * Math.PI));
            path.lineTo(i, y * mHeight);
        }
        return path;
    }

    private Path computeLine2Path() {
        Path path = new Path();
        path.moveTo(startX, 0);
        for (float i = startX; i < endX; i++) {
            float x = i * finiteScale;
            float y = (float) (0.5 * Math.pow((4 / (4 + Math.pow(x, 4))), 2.5) * Math.sin(0.75 * Math.PI * x + 0.5 * Math.PI));
            path.lineTo(i, y * mHeight);
        }
        return path;
    }

    private Path computeLine3Path() {
        Path path = new Path();
        path.moveTo(startX, 0);
        for (float i = startX; i < endX; i++) {
            float x = i * finiteScale;
            float y = (float) (0.5 * Math.pow((4 / (4 + Math.pow(x, 4))), 2.5) * Math.sin(0.75 * Math.PI * x - 0.2 * Math.PI));
            path.lineTo(i, y * mHeight);
        }
        return path;
    }

    private Path computeLine4Path() {
        Path path = new Path();
        path.moveTo(startX, 0);
        for (float i = startX; i < endX; i++) {
            float x = i * finiteScale;
            float y = (float) (0.5 * Math.pow((4 / (4 + Math.pow(x, 4))), 2.5) * Math.sin(0.75 * Math.PI * x + 0.8 * Math.PI));
            path.lineTo(i, y * mHeight);
        }
        return path;
    }
}
