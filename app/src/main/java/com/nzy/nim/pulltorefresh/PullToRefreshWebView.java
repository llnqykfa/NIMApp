package com.nzy.nim.pulltorefresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.webkit.WebView;

/**
 * 封装了WebView的下拉刷新
 * 
 * 注：这个也可以不用，webView自己封装该功能
 * 
 * @author 张全艺
 * @since 2015-1-23
 */
public class PullToRefreshWebView extends PullToRefreshBase<WebView> {
	/**
	 * 构造方法
	 * 
	 * @param context
	 *            context
	 */
	public PullToRefreshWebView(Context context) {
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
	public PullToRefreshWebView(Context context, AttributeSet attrs) {
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
	public PullToRefreshWebView(Context context, AttributeSet attrs,
								int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 创建webView实例
	 */
	@Override
	protected WebView createRefreshableView(Context context, AttributeSet attrs) {
		WebView webView = new WebView(context);
		return webView;
	}

	/**
	 * 是否可下拉
	 */
	@Override
	protected boolean isReadyForPullDown() {
		return mRefreshableView.getScrollY() == 0;
	}

	/**
	 * 是否可以开始下拉
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("FloatMath")
	@Override
	protected boolean isReadyForPullUp() {
		float exactContentHeight = FloatMath.floor(mRefreshableView
				.getContentHeight() * mRefreshableView.getScale());
		return mRefreshableView.getScrollY() >= (exactContentHeight - mRefreshableView
				.getHeight());
	}
}
