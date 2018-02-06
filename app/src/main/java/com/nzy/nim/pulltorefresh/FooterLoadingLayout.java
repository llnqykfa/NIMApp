package com.nzy.nim.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.nzy.nim.R;

/**
 * 这个类封装了下拉刷新的布局
 * 
 * @author 张全艺
 * @since 2015-1-23
 */
public class FooterLoadingLayout extends LoadingLayout {

	private ProgressBar mProgressBar;// 进度条

	private TextView mHintView;// 显示的文本

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            context
	 */
	public FooterLoadingLayout(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            context
	 * @param attrs
	 *            attrs
	 */
	public FooterLoadingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 *            context
	 */
	private void init(Context context) {
		mProgressBar = (ProgressBar) findViewById(R.id.pull_to_load_footer_progressbar);
		mHintView = (TextView) findViewById(R.id.pull_to_load_footer_hint_textview);
		setState(State.RESET);// 设置默认状态
	}

	@Override
	protected View createLoadingView(Context context, AttributeSet attrs) {
		View container = LayoutInflater.from(context).inflate(
				R.layout.pull_to_load_footer, null);
		return container;
	}

	@Override
	public void setLastUpdatedLabel(CharSequence label) {
	}

	@Override
	public int getContentSize() {
		View view = findViewById(R.id.pull_to_load_footer_content);
		if (view != null) {
			return view.getHeight();
		}
		return (int) (getResources().getDisplayMetrics().density * 40);// 40dp
	}

	/**
	 * 如果状态改变了的话调用
	 */
	@Override
	protected void onStateChanged(State curState, State oldState) {
		mProgressBar.setVisibility(View.GONE);
		mHintView.setVisibility(View.INVISIBLE);
		super.onStateChanged(curState, oldState);
	}

	/**
	 * 以下是各个状态变化是的一些基本设置，ke参照LoadingLayout里的注释
	 */
	@Override
	protected void onReset() {
		mHintView.setText(R.string.pull_to_refresh_header_hint_loading);
	}

	@Override
	protected void onPullToRefresh() {
		mHintView.setVisibility(View.VISIBLE);
		mHintView.setText(R.string.pull_to_refresh_header_hint_normal2);
	}

	@Override
	protected void onReleaseToRefresh() {
		mHintView.setVisibility(View.VISIBLE);
		mHintView.setText(R.string.pull_to_refresh_header_hint_ready);
	}

	@Override
	protected void onRefreshing() {
		mProgressBar.setVisibility(View.VISIBLE);
		mHintView.setVisibility(View.VISIBLE);
		mHintView.setText(R.string.pull_to_refresh_header_hint_loading);
	}

	@Override
	protected void onNoMoreData() {
		mHintView.setVisibility(View.VISIBLE);
//		mHintView.setText(R.string.pushmsg_center_no_more_msg);
	}
}
