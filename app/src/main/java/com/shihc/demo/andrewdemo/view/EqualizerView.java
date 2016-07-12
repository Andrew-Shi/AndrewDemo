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
import android.util.Log;
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
    private int mBottomLayerLineColor = Color.parseColor("#2f2e32");//底层线条颜色
    private int mUpperLayerLineColor = Color.parseColor("#ffcd2d");//上层线条颜色

    private EffectPoint[] effectPoints;

    private Paint mPaint;//底层线条画笔
    private Paint mHorizontalLinePaint;//连接频率中间的线条画笔
    private TextPaint mTextPaint;//底部文本画笔
    private float mTextHeight;
    private Bitmap mThumbDrawable;

    private float animPercent;//动画过程中间值，用于动画执行过程中坐标的计算
    private float mWidth;
    private float mHeight;
    private ValueAnimator percentAnimator;
    private float mOffset;
    private EffectPoint clickEffectPoint;

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

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.EqualizerView, defStyle, 0);
        effectCount = a.getInt(R.styleable.EqualizerView_effectCount, 1);
        effectDes = getResources().getStringArray(a.getResourceId(R.styleable.EqualizerView_effectDes, R.array.effectDes));
        mThumbDrawable = BitmapFactory.decodeResource(getResources(), a.getResourceId(R.styleable.EqualizerView_effectThumb, android.support.design.R.drawable.abc_seekbar_thumb_material));
        a.recycle();

        float density = getResources().getDisplayMetrics().density;        // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2 * density);

        mHorizontalLinePaint = new Paint();
        mHorizontalLinePaint.setAntiAlias(true);
        mHorizontalLinePaint.setStrokeCap(Paint.Cap.BUTT);
        mHorizontalLinePaint.setStrokeWidth(5 * density);
        mHorizontalLinePaint.setColor(Color.parseColor("#4c4b50"));

        mTextPaint = new TextPaint();
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.parseColor("#80ffffff"));

        effectPoints = new EffectPoint[effectCount];
        createAnim();
    }

    private void createAnim() {
        percentAnimator = ValueAnimator.ofFloat(0, 1);
        percentAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animPercent = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        percentAnimator.setInterpolator(new DecelerateInterpolator());
        percentAnimator.setDuration(500);
        percentAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                for (EffectPoint effectPoint : effectPoints) {
                    effectPoint.onAnimationEnd(animPercent);
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

        mTextPaint.setTextSize(20f / 450f * mWidth);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom - fontMetrics.top;

        mOffset = mWidth / (effectCount * 2);
        for (int i = 0; i < effectCount; i++) {
            effectPoints[i] = new EffectPoint();
            effectPoints[i].x = (i * 2 + 1) * mOffset;
            effectPoints[i].topY = paddingTop;
            effectPoints[i].bottomY = h - paddingBottom - mTextHeight;
            effectPoints[i].textY = h - paddingBottom;
            effectPoints[i].setCurrentProgress(50);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < effectCount; i++) {
            EffectPoint effectPoint = effectPoints[i];
            //写文字
            canvas.drawText(effectDes[i], effectPoint.x, effectPoint.textY, mTextPaint);
            //获取滑块的位置
            float animProgressY = effectPoint.getAnimProgressY(animPercent);

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
                canvas.drawLines(new float[]{effectPoint.x, animProgressY, nextEffectPoint.x, nextEffectPoint.getAnimProgressY(animPercent)}, mHorizontalLinePaint);
            }

            //画滑动块
            canvas.drawBitmap(mThumbDrawable, effectPoint.x - mThumbDrawable.getWidth() / 2, animProgressY - mThumbDrawable.getHeight() / 2, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        animPercent = 1.0f;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < effectCount; i++) {
                    clickEffectPoint = effectPoints[i];
                    if (clickEffectPoint.isClicked(event.getX(), event.getY())){
                        Log.d("EqualizerView", "第" + i + "个音效被点击");
                        clickEffectPoint.setCurrentProgressY(event.getY());
                        invalidate();
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (clickEffectPoint != null){
                    clickEffectPoint.setCurrentProgressY(event.getY());
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                clickEffectPoint = null;
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 设置频率的大小
     *
     * @param progress 频率的高低，个数必须为 effectCount，范围[0, 100]
     */
    public void setProgress(int[] progress) {
        if (progress == null || progress.length != effectCount) {
            Toast.makeText(getContext(), "传递的数据错误", Toast.LENGTH_SHORT).show();
            return;
        }
        stopAnim();
        for (int i = 0; i < effectCount; i++) {
            int item = progress[i];
            if (item < 0 || item > 100) {
                Toast.makeText(getContext(), "传递的参数错误", Toast.LENGTH_SHORT).show();
                return;
            }
            effectPoints[i].setCurrentProgress(item);
        }
        startAnim();
    }

    private void startAnim() {
        percentAnimator.start();
    }

    private void stopAnim() {
        if (percentAnimator.isRunning()) {
            percentAnimator.cancel();
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

        public EffectPoint() {
        }

        public float getAnimProgressY(float animPercent) {
            return preProgressY + (currentProgressY - preProgressY) * animPercent;
        }

        public void setCurrentProgress(float currentProgress) {
            float temp = bottomY - (currentProgress / 100f) * (bottomY - topY);
            if (currentProgressY < 0) {
                currentProgressY = temp;
                preProgressY = temp;
            } else {
                preProgressY = currentProgressY;
                currentProgressY = temp;
            }
            changed = true;
        }

        public void setCurrentProgressY(float y) {
            currentProgressY = y;
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
    }

    //回调函数
    public interface OnChangeListener {
        //滑动前
        public void onProgressBefore();

        //滑动时
        public void onProgressChanged(EqualizerView seekBar, int progressLow, int progressHigh);

        //滑动后
        public void onProgressAfter();
    }
}
