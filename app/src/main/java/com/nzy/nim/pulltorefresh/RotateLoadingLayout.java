package com.nzy.nim.pulltorefresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nzy.nim.R;


/**
 * 这个类封装了下拉刷新的布局
 * 
 * @author 张全艺
 * @since 2015-1-23
 */
public class RotateLoadingLayout extends LoadingLayout {

	// 动画插值
	static final Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();
	// Header的容器
	private RelativeLayout mHeaderContainer;
	// 箭头图片
	private ImageView mArrowImageView;
	// 状态提示TextView
	private TextView mHintTextView;
	// 最后更新时间的TextView
	private TextView mHeaderTimeView;
	// 最后更新时间的标题
	private TextView mHeaderTimeViewTitle;
	// 旋转的动画
	private Animation mRotateAnimation;

	float pivotValue = 0.5f; // 原点
	float toDegree = 720.0f; // 旋转的角度

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            context
	 */
	public RotateLoadingLayout(Context context) {
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
	public RotateLoadingLayout(Context context, AttributeSet attrs) {
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
		mHeaderTimeView = (TextView) findViewById(R.id.pull_to_refresh_header_time);
		mHeaderTimeViewTitle = (TextView) findViewById(R.id.pull_to_refresh_last_update_time_text);
		// 设置箭头拉伸
		mArrowImageView.setScaleType(ScaleType.CENTER);
		mArrowImageView.setImageResource(R.drawable.default_ptr_rotate);
		// 初始旋转动画
		mRotateAnimation = new RotateAnimation(0.0f, toDegree,
				Animation.RELATIVE_TO_SELF, pivotValue,
				Animation.RELATIVE_TO_SELF, pivotValue);
		// 保留最后的状态
		mRotateAnimation.setFillAfter(true);
		// 动画方式
		mRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
		// 动画时间
		mRotateAnimation.setDuration(1200);
		// 可无限重复
		mRotateAnimation.setRepeatCount(Animation.INFINITE);
		// 重复的方法是重新开始
		mRotateAnimation.setRepeatMode(Animation.RESTART);
	}

	/**
	 * 创建新的可加载View
	 */
	@Override
	protected View createLoadingView(Context context, AttributeSet attrs) {
		View container = LayoutInflater.from(context).inflate(
				R.layout.pull_to_refresh_header2, null);
		return container;
	}

	/**
	 * 设置最后更新的文本提示信息
	 */
	@Override
	public void setLastUpdatedLabel(CharSequence label) {
		// 如果最后更新的时间的文本是空的话，隐藏前面的标题
		mHeaderTimeViewTitle
				.setVisibility(TextUtils.isEmpty(label) ? View.INVISIBLE
						: View.VISIBLE);
		mHeaderTimeView.setText(label);
	}

	/**
	 * 得到容易的高度
	 */
	@Override
	public int getContentSize() {
		if (null != mHeaderContainer) {
			return mHeaderContainer.getHeight();
		}

		return (int) (getResources().getDisplayMetrics().density * 60);
	}

	/**
	 * 状态改变
	 */
	@Override
	protected void onStateChanged(State curState, State oldState) {
		super.onStateChanged(curState, oldState);
	}

	/**
	 * 重置时显示的文本信息
	 */
	@Override
	protected void onReset() {
		resetRotation();
		mHintTextView.setText(R.string.pull_to_refresh_header_hint_normal);
	}

	/**
	 * 释放刷新时的信息
	 */
	@Override
	protected void onReleaseToRefresh() {
		mHintTextView.setText(R.string.pull_to_refresh_header_hint_ready);
	}

	/**
	 * 正在下拉时的信息
	 */
	@Override
	protected void onPullToRefresh() {
		mHintTextView.setText(R.string.pull_to_refresh_header_hint_normal);
	}

	/**
	 * 刷新时的信息
	 */
	@Override
	protected void onRefreshing() {
		resetRotation();
		mArrowImageView.startAnimation(mRotateAnimation);
		mHintTextView.setText(R.string.pull_to_refresh_header_hint_loading);
	}

	@SuppressLint("NewApi")
	@Override
	public void onPull(float scale) {
		float angle = scale * 180f; // SUPPRESS CHECKSTYLE
		mArrowImageView.setRotation(angle);
	}

	/**
	 * 重置动画
	 */
	@SuppressLint("NewApi")
	private void resetRotation() {
		// 清除原来的状态
		mArrowImageView.clearAnimation();
		// 旋转角度置0
		mArrowImageView.setRotation(0);
	}
}
