package com.zdt.zyellowpage.customView;

import java.util.Hashtable;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 横向布局时，可自动换行的LinearLayout
 * @author Kevin
 *
 */
public class PredicateLayout extends LinearLayout {
	int mLeft, mRight, mTop, mBottom;
	Hashtable<View, Position> map = new Hashtable<View, Position>();

	/**
	 * 每个view上下的间距
	 */
	private final int dividerLine = 5;
	/**
	 * 每个view左右的间距
	 */
	private final int dividerCol = 8;

	public PredicateLayout(Context context) {
		super(context);
	}

	public PredicateLayout(Context context, int horizontalSpacing,
			int verticalSpacing) {
		super(context);
	}

	public PredicateLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int mWidth = MeasureSpec.getSize(widthMeasureSpec);
		int mCount = getChildCount();
		// int mX = 0;
		// int mY = 0;
		mLeft = 0;
		mRight = 0;
		mTop = 20;
		mBottom = 0;

		int j = 0;

		for (int i = 0; i < mCount; i++) {
			final View child = getChildAt(i);

			child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
			// 此处增加onlayout中的换行判断，用于计算所需的高度
			int childw = child.getMeasuredWidth();
			int childh = child.getMeasuredHeight();
			mRight += childw; // 将每次子控件宽度进行统计叠加，如果大于设定的宽度则需要换行，高度即Top坐标也需重新设置

			Position position = new Position();
			mLeft = getPosition(i - j, i);
			mRight = mLeft + child.getMeasuredWidth();
			if (mRight >= mWidth) {
				// mX = childw;
				// mY += childh;
				j = i;
				mLeft = getPaddingLeft();
				mRight = mLeft + child.getMeasuredWidth();
				mTop += childh + dividerLine;
				// PS：如果发现高度还是有问题就得自己再细调了
			}
			mBottom = mTop + child.getMeasuredHeight();
			// mY = mTop; //每次的高度必须记录 否则控件会叠加到一起
			// mX = mRight;
			position.left = mLeft;
			position.top = mTop;
			position.right = mRight;
			position.bottom = mBottom;
			map.put(child, position);
		}
		setMeasuredDimension(mWidth, mBottom + getPaddingBottom());
	}

	@Override
	protected LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(1, 1); // default of 1px spacing
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			Position pos = map.get(child);
			if (pos != null) {
				child.layout(pos.left, pos.top, pos.right, pos.bottom);
			} else {
				Log.i("MyLayout", "error");
			}
		}
	}

	private class Position {
		int left, top, right, bottom;
	}

	public int getPosition(int IndexInRow, int childIndex) {
		if (IndexInRow > 0) {
			return getPosition(IndexInRow - 1, childIndex - 1)
					+ getChildAt(childIndex - 1).getMeasuredWidth()
					+ dividerCol;
		}
		return getPaddingLeft();
	}
}