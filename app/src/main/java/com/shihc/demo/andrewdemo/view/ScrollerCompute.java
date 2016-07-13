package com.shihc.demo.andrewdemo.view;

import android.content.Context;
import android.os.SystemClock;

/**
 * 计算视图偏移的工具类
 * 
 * @author Administrator
 * 
 */
public class ScrollerCompute {

	/** 开始时的X坐标 */
	private int startX;
	/** 开始时的Y坐标 */
	private int startY;
	/** X方向上要移动的距离 */
	private int distanceX;
	/** Y方向上要移动的距离 */
	private int distanceY;
	/** 开始的时间 */
	private long startTime;
	/** 移动是否结束 */
	private boolean isFinish;
	/** 当前X轴的坐标 */
	private long currX;
	/** 当前Y轴的坐标 */
	private long currY;
	/** 默认的时间间隔 */
	private int duration = 100;

	public ScrollerCompute(Context ctx) {

	}

	/**
	 * 开始移动
	 * 
	 * @param startX
	 *            开始时的X坐标
	 * @param startY
	 *            开始时的Y坐标
	 * @param distanceX
	 *            X方向上要移动的距离
	 * @param distanceY
	 *            Y方向上要移动的距离
	 */
	public void startScroll(int startX, int startY, int distanceX, int distanceY) {
		this.startX = startX;
		this.startY = startY;
		this.distanceX = distanceX;
		this.distanceY = distanceY;
		this.startTime = SystemClock.uptimeMillis();
		this.isFinish = false;
	}

	/**
	 * 判断当前运行状态
	 * 
	 * @return
	 */
	public boolean computeOffset() {

		if (isFinish) {
			return false;
		}
		// 获得所用的时间
		long passTime = SystemClock.uptimeMillis() - startTime;
		System.out.println("passTime::" + passTime);
		// 如果时间还在允许的范围内
		if (passTime < duration) {
			currX = startX + distanceX * passTime / duration;
			currY = startY + distanceY * passTime / duration;
		} else {
			currX = startX + distanceX;
			currY = startY + distanceY;
			isFinish = true;
		}

		return true;
	}

	/**
	 * 获取当前X的值
	 * 
	 * @return
	 */
	public long getCurrX() {
		return currX;
	}

	public void setCurrX(long currX) {
		this.currX = currX;
	}

	/**
	 * 获取当前Y的值
	 * 
	 * @return
	 */
	public long getCurrY() {
		return currY;
	}

	public void setCurrY(long currY) {
		this.currY = currY;
	}

}