package com.nzy.nim.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * 封装了ScrollView的下拉刷新
 * 
 * 注：其实ScrollView 可以不用下拉上拉之类的操作
 * 
 * @author 张全艺
 * @since2015-1-23
 */
public class PullToRefreshScrollView extends PullToRefreshBase<ScrollView> {
	/**
	 * 构造方法
	 * 
	 * @param context
	 *            context
	 */
	public PullToRefreshScrollView(Context context) {
		this(context, null);
	}

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            context
	 * @param attrs
	 *            attrs
	 */
	public PullToRefreshScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            context
	 * @param attrs
	 *            attrs
	 * @param defStyle
	 *            defStyle
	 */
	public PullToRefreshScrollView(Context context, AttributeSet attrs,
								   int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected ScrollView createRefreshableView(Context context,
											   AttributeSet attrs) {
		ScrollView scrollView = new ScrollView(context);
		return scrollView;
	}

	@Override
	protected boolean isReadyForPullDown() {
		return mRefreshableView.getScrollY() == 0;
	}

	@Override
	protected boolean isReadyForPullUp() {
		View scrollViewChild = mRefreshableView.getChildAt(0);
		if (scrollViewChild != null) {
			return mRefreshableView.getScrollY() >= (scrollViewChild
					.getHeight() - getHeight());
		}

		return false;
	}

}
