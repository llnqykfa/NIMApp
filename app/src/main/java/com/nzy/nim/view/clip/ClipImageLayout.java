package com.nzy.nim.view.clip;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;

public class ClipImageLayout extends RelativeLayout {

	private ClipZoomImageView mZoomImageView;
	private ClipImageBorderView mClipImageView;
	private int mHorizontalPadding = 60;

	public ClipImageLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mZoomImageView = new ClipZoomImageView(context);
		mClipImageView = new ClipImageBorderView(context);
		android.view.ViewGroup.LayoutParams lp = new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		this.addView(mZoomImageView, lp);
		this.addView(mClipImageView, lp);
		// 计算padding的px
		mHorizontalPadding = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, mHorizontalPadding, getResources()
						.getDisplayMetrics());
		mZoomImageView.setHorizontalPadding(mHorizontalPadding);
		mClipImageView.setHorizontalPadding(mHorizontalPadding);
		mClipImageView.setIsCircle(false);
	}

	/**
	 * 对外公布设置边距的方法,单位为dp
	 * 
	 * @param mHorizontalPadding
	 */
	public void setHorizontalPadding(int horizontalPadding) {
		mHorizontalPadding = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, horizontalPadding, getResources()
						.getDisplayMetrics());
		mZoomImageView.setHorizontalPadding(horizontalPadding);
		mClipImageView.setHorizontalPadding(horizontalPadding);
	}

	public void setIsCircle(boolean value) {
		mClipImageView.setIsCircle(value);
	}

	/**
	 * 裁切图片
	 * 
	 * @return
	 */
	public Bitmap clip() {
		return mZoomImageView.clip();
	}

	public void setBitmap(Bitmap bitmap) {
		mZoomImageView.setImageBitmap(bitmap);
	}

}
