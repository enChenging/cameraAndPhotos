package com.release.cameralibrary.photo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * @author Mr.release
 * @create 2020-01-03
 * @Describe 不可滚动的GridView
 */

public class GridViewNoScroll extends GridView {
	public GridViewNoScroll(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GridViewNoScroll(Context context) {
		super(context);
	}

	public GridViewNoScroll(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}