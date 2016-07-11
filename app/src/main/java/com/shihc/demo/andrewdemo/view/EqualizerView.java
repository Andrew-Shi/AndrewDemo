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
import android.util.SparseArray;
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
    private Bitmap mExampleDrawable;

    private float animPercent;
    private float mWidth;
    private float mHeight;
    private ValueAnimator percentAnimator;

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
        mExampleDrawable = BitmapFactory.decodeResource(getResources(), a.getResourceId(R.styleable.EqualizerView_effectThumb, android.support.design.R.drawable.abc_seekbar_thumb_material));
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
                Log.d("onAnimationUpdate", "animPercent : " + animPercent);
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
                for (EffectPoint effectPoint : effectPoints) {
                    effectPoint.onAnimationEnd(animPercent);
                }
                animPercent = 0f;
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

        float offset = mWidth / (effectCount * 2);
        for (int i = 0; i < effectCount; i++) {
            effectPoints[i] = new EffectPoint();
            effectPoints[i].x = (i * 2 + 1) * offset;
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
            canvas.drawText(effectDes[i], effectPoint.x, effectPoint.textY, mTextPaint);
            //画底层线条
            mPaint.setColor(mBottomLayerLineColor);
            canvas.drawLines(new float[]{effectPoint.x, effectPoint.topY, effectPoint.x, effectPoint.bottomY}, mPaint);
            //画上层层线条
            mPaint.setColor(mUpperLayerLineColor);
            float animProgressY = effectPoint.getAnimProgressY(animPercent);
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
            canvas.drawBitmap(mExampleDrawable, effectPoint.x - mExampleDrawable.getWidth() / 2, animProgressY - mExampleDrawable.getHeight() / 2, mPaint);
        }
    }

    /**
     * 设置单个频率的大小
     *
     * @param index    第几个频率，范围为[0, effectCount)
     * @param progress 频率的高低，范围[0, 100]
     */
    public void setProgress(int index, int progress) {
        if (index < 0 || index > effectCount - 1 || progress < 0 || progress > 100) {
            Toast.makeText(getContext(), "传递的参数错误", Toast.LENGTH_SHORT).show();
            return;
        }
        effectPoints[index].setCurrentProgress(progress);
        startAnim();
    }

    /**
     * 设置多个频率的大小
     *
     * @param mulProgress key表示第几个频率，范围为[0, effectCount)
     *                    value表示频率的高低，范围[0, 100]
     */
    public void setMulProgress(SparseArray<Integer> mulProgress) {
        if (mulProgress == null || mulProgress.size() == 0) {
            Toast.makeText(getContext(), "传递的数据不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = 0; i < mulProgress.size(); i++) {
            int index = mulProgress.keyAt(i);
            int progress = mulProgress.get(index);
            if (index < 0 || index > effectCount - 1 || progress < 0 || progress > 100) {
                Toast.makeText(getContext(), "传递的参数错误", Toast.LENGTH_SHORT).show();
                return;
            }
            effectPoints[index].setCurrentProgress(progress);
        }
        startAnim();
    }

    private void startAnim() {
        if (percentAnimator.isRunning()) {
            Log.d("EqualizerView", "重新开始动画");
            percentAnimator.cancel();
        }
        percentAnimator.start();
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
            Log.d("EqualizerView", "x : " + x + ",animPercent : " + animPercent);
            return preProgressY + (currentProgressY - preProgressY) * animPercent;
        }

        public void setCurrentProgress(float currentProgress) {
            float temp = bottomY - (currentProgress / 100f) * (bottomY - topY);
            if (currentProgressY < 0){
                currentProgressY = temp;
                preProgressY = temp;
            } else {
                preProgressY = currentProgressY;
                currentProgressY = temp;
            }
            changed = true;
        }

        public void onAnimationEnd(float animPercent) {
            changed = false;
            preProgressY = getAnimProgressY(animPercent);
            Log.d("EqualizerView", "动画结束，" + animPercent);
        }

        public boolean isChanged() {
            return changed;
        }
    }
}
