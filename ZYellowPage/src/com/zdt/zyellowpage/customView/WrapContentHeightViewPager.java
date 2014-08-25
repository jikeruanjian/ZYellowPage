package com.zdt.zyellowpage.customView;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * ViewPager wrapContent解决方案
 */
public class WrapContentHeightViewPager extends ViewPager {

	/** The m gesture detector. */
	private GestureDetector mGestureDetector;

	// private float xDistance, yDistance, xLast, yLast;

	public WrapContentHeightViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		mGestureDetector = new GestureDetector(context, new XScrollDetector());
		setFadingEdgeLength(0);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// switch (ev.getAction()) {
		// case MotionEvent.ACTION_DOWN:
		// xDistance = yDistance = 0f;
		// xLast = ev.getX();
		// yLast = ev.getY();
		// break;
		// case MotionEvent.ACTION_MOVE:
		// final float curX = ev.getX();
		// final float curY = ev.getY();
		//
		// xDistance += Math.abs(curX - xLast);
		// yDistance += Math.abs(curY - yLast);
		// xLast = curX;
		// yLast = curY;
		//
		// if (xDistance > yDistance) {
		// return true;
		// }
		// }
		// return false;
		// boolean result = false;
		// if (mGestureDetector.onTouchEvent(ev)) {
		// result = super.onInterceptTouchEvent(ev);
		// }
		// Log.e("test", "ViewPager" + result);
		// return result;
		return mGestureDetector.onTouchEvent(ev)
				&& super.onInterceptTouchEvent(ev);
	}

	// @Override
	// public boolean onTouchEvent(MotionEvent ev) {
	// boolean result = false;
	// if (mGestureDetector.onTouchEvent(ev)) {
	// Log.e("test", "ViewPageronTouch左右");
	// result = super.onTouchEvent(ev);
	// }
	// Log.e("test", "ViewPageronTouch" + result);
	// return result;
	// }

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int height = 50;
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			child.measure(widthMeasureSpec,
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			int h = child.getMeasuredHeight();
			if (h < 50) {
				child.getLayoutParams().height = 50;
				h = 50;
			}
			if (h > height)
				height = h;
		}

		if (height < 50)
			height = 50;
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
				MeasureSpec.EXACTLY);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
