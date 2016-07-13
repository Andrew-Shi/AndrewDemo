package com.shihc.demo.andrewdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class HorizontalChooseView extends ViewGroup {

    private List<View> mAllChildView = new ArrayList<>();
    //手势识别器
    private GestureDetector detector;

    private int childWidth = 0;

    private int childHeight = 0;

    private int firstDownX;

    private int currId = 0;

    private ScrollerCompute scrollerCompute;

    private OnChangeListener onChangeListener;

    public HorizontalChooseView(Context context) {
        super(context);
        init(null, 0);
    }

    public HorizontalChooseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public HorizontalChooseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        scrollerCompute = new ScrollerCompute(getContext());
        detector = new GestureDetector(getContext(),
                new GestureDetector.OnGestureListener() {

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return false;
                    }

                    @Override
                    public void onShowPress(MotionEvent e) {
                    }

                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                            float distanceX, float distanceY) {
                        // 手指滑动
                        scrollBy((int) distanceX / 5, 0);
                        return false;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2,
                                           float velocityX, float velocityY) {
                        return false;
                    }

                    @Override
                    public boolean onDown(MotionEvent e) {
                        return false;
                    }
                });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
         */
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        childWidth = sizeWidth / 5;
        childHeight = sizeHeight;
        int childWidthSpec = MeasureSpec.makeMeasureSpec(childWidth, widthMode);
        int childHeightSpec = MeasureSpec.makeMeasureSpec(childHeight, heightMode);
        measureChildren(childWidthSpec, childHeightSpec);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int firstChildX = (getWidth() - childWidth) / 2;
        int count = getChildCount();
        mAllChildView.clear();
        // 遍历所有的孩子
        for (int i = 0; i < count; i++) {
            TextView child = (TextView) getChildAt(i);
            mAllChildView.add(child);
            child.layout(firstChildX, 0, firstChildX + childWidth,
                    childHeight);
            firstChildX += childWidth;
            child.setGravity(Gravity.CENTER);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event); // 指定手势识别器去处理滑动事件
        // 还是得自己处理一些逻辑
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 按下
                firstDownX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE: // 移动
                break;
            case MotionEvent.ACTION_UP: // 抬起
                int nextId = 0; // 记录下一个View的id
                if (event.getX() - firstDownX > childWidth / 2) {
                    // 手指离开点的X轴坐标-firstDownX > 子view宽度的一半，左移
                    nextId = (currId - 1) <= 0 ? 0 : currId - 1;
                } else if (firstDownX - event.getX() > childWidth / 2) {
                    // 手指离开点的X轴坐标 - firstDownX < 子view宽度的一半，右移
                    nextId = currId + 1;
                } else if (Math.abs(firstDownX - event.getX()) < 1) {
                    //点击事件
                    nextId = touchViewIndex(event);
                } else {
                    nextId = currId;
                }
                moveToDest(nextId);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 根据点击位置判断点击的是哪个view
     *
     * @return
     */
    private int touchViewIndex(MotionEvent event) {
        for (int i = 0; i < mAllChildView.size(); i++) {
            View view = mAllChildView.get(i);
            if (inRangeOfView(view, event)) {
                return i;
            }
        }
        return currId;
    }

    private boolean inRangeOfView(View view, MotionEvent ev) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (ev.getRawX() < x
                || ev.getRawX() > (x + view.getWidth())
                || ev.getRawY() < y
                || ev.getRawY() > (y + view.getHeight())) {
            return false;
        }
        return true;
    }


    /**
     * 控制视图的移动
     *
     * @param nextId
     */
    private void moveToDest(int nextId) {
        if (nextId != currId && onChangeListener != null) {
            onChangeListener.onChanged(currId);
        }
        // nextId的合理范围是，nextId >=0 && nextId <= getChildCount()-1
        currId = (nextId >= 0) ? nextId : 0;
        currId = (nextId <= getChildCount() - 1)
                ? nextId
                : (getChildCount() - 1);

        // 视图移动,太直接了，没有动态过程
//         scrollTo(currId * childWidth, 0);
        // 要移动的距离 = 最终的位置 - 现在的位置
        int distanceX = currId * childWidth - getScrollX();
        // 设置运行的时间
        scrollerCompute.startScroll(getScrollX(), 0, distanceX, 0);
        // 刷新视图
        invalidate();
        setCurrItemSelect(currId);
    }

    /**
     * invalidate();会导致这个方法的执行
     */
    @Override
    public void computeScroll() {
        if (scrollerCompute.computeOffset()) {
            int newX = (int) scrollerCompute.getCurrX();
            Log.d("HorizontalChooseView", "newX::" + newX);
            scrollTo(newX, 0);
            invalidate();
        }
    }

    private void setCurrItemSelect(int currId) {
        for (int i = 0; i < mAllChildView.size(); i++) {
            mAllChildView.get(i).setSelected(currId == i);
        }
    }

    public void setCurrId(int currId) {
        moveToDest(currId);
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    interface OnChangeListener {
        public void onChanged(int index);
    }
}
