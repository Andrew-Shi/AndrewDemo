package com.shihc.demo.andrewdemo.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.shihc.demo.andrewdemo.R;


/**
 * 音效添加自定义控件
 */
public class EqualizerView extends View {

    private int effectCount;

    private String[] effectDes;

    private int mTextColor = Color.parseColor("#80ffffff");//文本颜色

    private int mHorizontalLineColor = Color.parseColor("#4c4b50");//水平连接线条颜色

    private int mBottomLayerLineColor = Color.parseColor("#2f2e32");//底层线条颜色

    private int mUpperLayerLineColor = Color.parseColor("#ffcd2d");//上层线条颜色

    private int mDivideLineColor = Color.parseColor("#33ffffff");//上层线条颜色

    private Bitmap mThumbDrawable;

    private int min, max; //进度范围大小

    private float mTextSize = 20;

    private EffectPoint[] effectPoints;

    private EffectPoint mClickEffectPoint;

    private Paint mPaint;//底层线条画笔

    private Paint mHorizontalLinePaint;//连接频率中间的线条画笔

    private TextPaint mTextPaint;//底部文本画笔

    private float mAnimPercent;//动画过程中间值，用于动画执行过程中坐标的计算

    private float mWidth;

    private float mHeight;

    private float mDensity;//屏幕密度

    private float mOffset;

    private ValueAnimator mAnimator;

    private int mDrawCount;

    private OnChangeListener mOnChangeListener;
    private int halfDrawableWidth;
    private int halfDrawableHeight;


    public EqualizerView(Context context) {
        super(context);
        init(null, 0);
    }

    public EqualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public EqualizerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.mOnChangeListener = onChangeListener;
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.EqualizerView, defStyle, 0);
        effectCount = a.getInt(R.styleable.EqualizerView_effectCount, 1);
        effectDes = getResources().getStringArray(a.getResourceId(R.styleable.EqualizerView_effectDes, R.array.effectDes));
        mThumbDrawable = BitmapFactory.decodeResource(getResources(), a.getResourceId(R.styleable.EqualizerView_effectThumb, android.support.design.R.drawable.abc_seekbar_thumb_material));
        min = a.getInt(R.styleable.EqualizerView_min, 0);
        max = a.getInt(R.styleable.EqualizerView_max, 100);
        mTextSize = a.getDimensionPixelSize(R.styleable.EqualizerView_textSize, 20);
        a.recycle();

        halfDrawableWidth = mThumbDrawable.getWidth() / 2;
        halfDrawableHeight = mThumbDrawable.getHeight() / 2;

        // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
        mDensity = getResources().getDisplayMetrics().density;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2 * mDensity);

        mHorizontalLinePaint = new Paint();
        mHorizontalLinePaint.setAntiAlias(true);
        mHorizontalLinePaint.setStrokeCap(Paint.Cap.BUTT);
        mHorizontalLinePaint.setStrokeWidth(5 * mDensity);
        mHorizontalLinePaint.setColor(mHorizontalLineColor);

        mTextPaint = new TextPaint();
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);

        effectPoints = new EffectPoint[effectCount];
        createAnim();
    }

    private void createAnim() {
        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimPercent = (float) animation.getAnimatedValue();
                invalidate();
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
                for (EffectPoint effectPoint : effectPoints) {
                    effectPoint.onAnimationEnd(mAnimPercent);
                }
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        mWidth = w;
        mHeight = h;

        mOffset = mWidth / (effectCount * 2);
        for (int i = 0; i < effectCount; i++) {
            effectPoints[i] = new EffectPoint();
            effectPoints[i].x = (i * 2 + 1) * mOffset;
            effectPoints[i].topY = paddingTop;
            effectPoints[i].bottomY = h - paddingBottom * 2 - getTextHeight();
            effectPoints[i].textY = h - paddingBottom;
            effectPoints[i].setProgress((max + min) / 2);
        }
    }

    private float getTextHeight() {
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        return fontMetrics.descent - fontMetrics.ascent;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDivider(canvas);//画横向的短分割线
        drawEffectView(canvas);//画音效相关关的view
    }

    private void drawEffectView(Canvas canvas) {
        for (int i = 0; i < effectCount; i++) {
            EffectPoint effectPoint = effectPoints[i];
            //写文字
            canvas.drawText(effectDes[i], effectPoint.x, effectPoint.textY, mTextPaint);
            //获取滑块的位置
            float animProgressY = effectPoint.getAnimProgressY(mAnimPercent);

            //画底层线条
            mPaint.setColor(mBottomLayerLineColor);
            canvas.drawLines(new float[]{effectPoint.x, effectPoint.topY, effectPoint.x, animProgressY}, mPaint);

            //画上层层线条
            mPaint.setColor(mUpperLayerLineColor);
            canvas.drawLines(new float[]{effectPoint.x, animProgressY, effectPoint.x, effectPoint.bottomY}, mPaint);

            //画左右连接线条
            if (i == effectCount - 1) {
                canvas.drawLines(new float[]{effectPoint.x, animProgressY, mWidth, animProgressY}, mHorizontalLinePaint);
            } else {
                if (i == 0) {
                    canvas.drawLines(new float[]{0, animProgressY, effectPoints[0].x, animProgressY}, mHorizontalLinePaint);
                }
                EffectPoint nextEffectPoint = effectPoints[i + 1];
                canvas.drawLines(new float[]{effectPoint.x, animProgressY, nextEffectPoint.x, nextEffectPoint.getAnimProgressY(mAnimPercent)}, mHorizontalLinePaint);
            }

            //画滑动块
            canvas.drawBitmap(mThumbDrawable, effectPoint.x - halfDrawableWidth, animProgressY - halfDrawableHeight, mPaint);
        }
    }

    private void drawDivider(Canvas canvas) {
        float divideLength = 10 * mDensity;

        float top = effectPoints[0].topY;
        float bottom = effectPoints[0].bottomY;
        float verticalOffset = (bottom - top) / 4;
        float heightY = top + verticalOffset;
        float middleY = top + 2 * verticalOffset;
        float lowY = top + 3 * verticalOffset;

        mPaint.setColor(mDivideLineColor);
        for (int i = 1; i < effectCount; i++) {
            float x = 2 * i * mOffset - divideLength / 2;
            canvas.drawLines(new float[]{x, heightY, x + divideLength, heightY}, mPaint);
            canvas.drawLines(new float[]{x, middleY, x + divideLength, middleY}, mPaint);
            canvas.drawLines(new float[]{x, lowY, x + divideLength, lowY}, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mAnimPercent = 1.0f;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < effectCount; i++) {
                    mClickEffectPoint = effectPoints[i];
                    if (mClickEffectPoint.isClicked(event.getX(), event.getY())) {
                        if (mOnChangeListener != null) {
                            mOnChangeListener.onProgressBefore();
                        }
                        mClickEffectPoint.setProgressY(event.getY(), false);
                        invalidate();
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mClickEffectPoint != null) {
                    mClickEffectPoint.setProgressY(event.getY(), false);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                mClickEffectPoint.setProgressY(event.getY(), true);
                mClickEffectPoint = null;
                if (mOnChangeListener != null) {
                    mOnChangeListener.onProgressChanged(this, getProgress());
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public int[] getProgress() {
        int[] array = new int[effectCount];
        int i = 0;
        for (EffectPoint effectPoint : effectPoints) {
            array[i++] = effectPoint.getProgress();
        }
        return array;
    }

    /**
     * 设置频率的大小
     *
     * @param progress 频率的高低，个数必须为 effectCount，范围[min, max]
     */
    public void setProgress(int[] progress) {
        if (progress == null || progress.length != effectCount) {
            Toast.makeText(getContext(), "传递的数据错误", Toast.LENGTH_SHORT).show();
            return;
        }
        stopAnim();
        for (int i = 0; i < effectCount; i++) {
            int item = progress[i];
            if (item < min || item > max) {
                Toast.makeText(getContext(), "传递的参数错误", Toast.LENGTH_SHORT).show();
                return;
            }
            effectPoints[i].setProgress(item);
        }
        startAnim();
    }

    private void startAnim() {
        mAnimator.start();
    }

    private void stopAnim() {
        if (mAnimator.isRunning()) {
            mAnimator.cancel();
        }
    }

    class EffectPoint {
        float x;
        float topY;
        float bottomY;
        float textY;
        private float currentProgressY = -1;//当前设置的进度的大小的y坐标值
        private float preProgressY = -1;//上一次进度的大小，用于动画的显示
        private boolean changed = true;//currentProgressY是否被改变过，没改变过就不要重绘了
        private int progress;

        public EffectPoint() {
        }

        public float getAnimProgressY(float animPercent) {
            return preProgressY + (currentProgressY - preProgressY) * animPercent;
        }

        public void setProgress(int progress) {
            this.progress = progress;
            float temp = bottomY - ((progress - min) * 1f / (max - min)) * (bottomY - topY);
            if (currentProgressY < 0) {
                currentProgressY = temp;
                preProgressY = temp;
            } else {
                preProgressY = currentProgressY;
                currentProgressY = temp;
            }
            changed = true;
        }

        /**
         * 手指拖动时设置y坐标
         *
         * @param y   当前y坐标值
         * @param end 是否结束滑动
         */
        public void setProgressY(float y, boolean end) {
            if (y < topY) y = topY;
            if (y > bottomY) y = bottomY;
            currentProgressY = y;
            if (end) {
                progress = (int) ((bottomY - y) / (bottomY - topY) * (max - min) + min);
            }
        }

        public void onAnimationEnd(float animPercent) {
            changed = false;
            currentProgressY = preProgressY = getAnimProgressY(animPercent);
        }

        public boolean isChanged() {
            return changed;
        }

        public boolean isClicked(float x, float y) {
            if (y > topY && y < bottomY && x > this.x - mOffset && x < this.x + mOffset) {
                return true;
            }
            return false;
        }

        public int getProgress() {
            return progress;
        }
    }

    //回调函数
    public interface OnChangeListener {

        //滑动前
        public void onProgressBefore();

        //滑动时
        public void onProgressChanged(EqualizerView seekBar, int[] progress);

    }
}
