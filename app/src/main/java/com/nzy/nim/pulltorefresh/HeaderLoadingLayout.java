package com.nzy.nim.pulltorefresh;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nzy.nim.R;

/**
 * 这个类封装了下拉刷新的布局
 * 
 * @author 张全艺
 * @since 2015-1-23
 */
public class HeaderLoadingLayout extends LoadingLayout {
	// 旋转动画时间
	public static final int ROTATE_ANIM_DURATION = 150;

	// Header的容器
	private RelativeLayout mHeaderContainer;
	// 箭头图片
	private ImageView mArrowImageView;
	// 进度条
	private ProgressBar mProgressBar;
	// 状态提示文本
	private TextView mHintTextView;
	// 最后更新时间的文本信息
	private TextView mHeaderTimeView;
	// 最后更新时间的标题
	private TextView mHeaderTimeViewTitle;
	// 向上的动画，松手时触发
	private Animation mRotateUpAnim;
	// 向下的动画，下拉时触发
	private Animation mRotateDownAnim;

	float pivotValue = 0.5f; // 旋转中心点
	float toDegree = -180f; // 旋转的角度

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            context
	 */
	public HeaderLoadingLayout(Context context) {
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
	public HeaderLoadingLayout(Context context, AttributeSet attrs) {
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
		mHeaderContainer = (RelativeLayout) findViewById(R.id.pull_to_refresh_header_content);
		mArrowImageView = (ImageView) findViewById(R.id.pull_to_refresh_header_arrow);
		mHintTextView = (TextView) findViewById(R.id.pull_to_refresh_header_hint_textview);
		mProgressBar = (ProgressBar) findViewById(R.id.pull_to_refresh_header_progressbar);
		mHeaderTimeView = (TextView) findViewById(R.id.pull_to_refresh_header_time);
		mHeaderTimeViewTitle = (TextView) findViewById(R.id.pull_to_refresh_last_update_time_text);

		/*
		 * 初始化旋转动画
		 * 
		 * 参数：1、2角度-------3、4类型和值--5、6---
		 */
		mRotateUpAnim = new RotateAnimation(0.0f, toDegree,
				Animation.RELATIVE_TO_SELF, pivotValue,
				Animation.RELATIVE_TO_SELF, pivotValue);
		mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateUpAnim.setFillAfter(true);// 设置为true时，动画结束的时候会停留在最后一帧

		mRotateDownAnim = new RotateAnimation(toDegree, 0.0f,
				Animation.RELATIVE_TO_SELF, pivotValue,
				Animation.RELATIVE_TO_SELF, pivotValue);
		mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateDownAnim.setFillAfter(true);
	}

	@Override
	public void setLastUpdatedLabel(CharSequence label) {
		// 如果最后更新的时间的文本是空的话，隐藏前面的标题
		mHeaderTimeViewTitle
				.setVisibility(TextUtils.isEmpty(label) ? View.INVISIBLE
						: View.VISIBLE);
		mHeaderTimeView.setText(label);
	}

	/**
	 * 获取布局的大小，即高度
	 */
	@Override
	public int getContentSize() {
		if (mHeaderContainer != null) {
			return mHeaderContainer.getHeight();
		}

		return (int) (getResources().getDisplayMetrics().density * 60);// 60dp
	}

	/**
	 * 创建刷新的header布局
	 */
	@Override
	protected View createLoadingView(Context context, AttributeSet attrs) {
		View container = LayoutInflater.from(context).inflate(
				R.layout.pull_to_refresh_header, null);
		return container;
	}

	@Override
	protected void onStateChanged(State curState, State oldState) {
		mArrowImageView.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.INVISIBLE);

		super.onStateChanged(curState, oldState);
	}

	@Override
	protected void onReset() {
		mArrowImageView.clearAnimation();// 停止动画
		mHintTextView.setText(R.string.pull_to_refresh_header_hint_normal);// 显示下拉可以刷新的提示
	}

	/**
	 * 若前个状态有动画，则先释放动画，然后再开启当前的动画，下同
	 */
	@Override
	protected void onPullToRefresh() {
		if (State.RELEASE_TO_REFRESH == getPreState()) {
			mArrowImageView.clearAnimation();
			mArrowImageView.startAnimation(mRotateDownAnim);
		}

		mHintTextView.setText(R.string.pull_to_refresh_header_hint_normal);
	}

	@Override
	protected void onReleaseToRefresh() {
		mArrowImageView.clearAnimation();
		mArrowImageView.startAnimation(mRotateUpAnim);
		mHintTextView.setText(R.string.pull_to_refresh_header_hint_ready);
	}

	@Override
	protected void onRefreshing() {
		mArrowImageView.clearAnimation();
		mArrowImageView.setVisibility(View.INVISIBLE);
		mProgressBar.setVisibility(View.VISIBLE);
		mHintTextView.setText(R.string.pull_to_refresh_header_hint_loading);
	}
}
