package com.nzy.nim.view.clip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class ClipImageBorderView extends View {

	/**
	 * 水平方向与View的边距
	 */
	private int mHorizontalPadding;
	/**
	 * 垂直方向与View的边距
	 */
	private int mVerticalPadding;
	/**
	 * 绘制的矩形的宽度
	 */
	private int mWidth;
	/**
	 * 边框的宽度 单位dp
	 */
	private int mBorderWidth = 1;

	private Paint mPaint;
	private Canvas mCanvas;
	private Bitmap mBgBitmap;
	private RectF mRect;
	private boolean isCircle = false;

	public ClipImageBorderView(Context context) {
		this(context, null);
	}

	public ClipImageBorderView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ClipImageBorderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mBorderWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, mBorderWidth, getResources()
						.getDisplayMetrics());
		mPaint = new Paint();
	}

	/**
	 * 绘制阴影的画笔
	 */
	private void initShadowPaint() {

		// 绘制阴影
		mPaint.setARGB(185, 0, 0, 0);
		mPaint.setStyle(Style.FILL);

	}

	/**
	 * 绘制边框的画笔
	 */
	private void initFramePaint() {
		mPaint.reset();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Style.STROKE);
		mPaint.setColor(Color.WHITE);
		mPaint.setStrokeWidth(mBorderWidth);
	}

	/**
	 * 绘制实心图的画笔
	 * 
	 */
	private void initFullPaint() {
		mPaint.reset();
		mPaint.setARGB(255, 0, 0, 0);
		mPaint.setXfermode(new PorterDuffXfermode(Mode.XOR));// XOR模式：重叠部分被掏空
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 计算矩形区域的宽度
		mWidth = getWidth() - 2 * mHorizontalPadding;
		// 计算距离屏幕垂直边界 的边距
		mVerticalPadding = (getHeight() - mWidth) / 2;

		if (mBgBitmap == null) {
			mBgBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
					Config.ARGB_8888);
			mCanvas = new Canvas(mBgBitmap);
			mRect = new RectF(0, 0, getWidth(), getHeight());
		}
		// 绘制阴影层
		initShadowPaint();
		mCanvas.drawRect(mRect, mPaint);

		if (isCircle) {
			initFullPaint();
			// 绘制实心圆 ，绘制完后，在mCanvas画布中，mPaintRect和mPaintCirle相交部分即被掏空
			mCanvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2
					- mHorizontalPadding, mPaint);
			// 将阴影层画进本View的画布中
			canvas.drawBitmap(mBgBitmap, null, mRect, new Paint());
			initFramePaint();
			// 绘制圆环
			canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2
					- mHorizontalPadding, mPaint);
		}else if(false){

			initFullPaint();


			// 绘制实心矩形
			mCanvas.drawRect(mHorizontalPadding, 2*mVerticalPadding,
					mHorizontalPadding + mWidth, mVerticalPadding + mWidth*50/72,
					mPaint);
			// 将阴影层画进本View的画布中
			canvas.drawBitmap(mBgBitmap, null, mRect, new Paint());
			initFramePaint();
			// 绘制边框
			canvas.drawRect(mHorizontalPadding, 2*mVerticalPadding,
					mHorizontalPadding + mWidth, mVerticalPadding + mWidth*50/72,
					mPaint);
		} else {
			initFullPaint();
			// 绘制实心矩形
			mCanvas.drawRect(mHorizontalPadding, mVerticalPadding,
					mHorizontalPadding + mWidth, mVerticalPadding + mWidth,
					mPaint);
			// 将阴影层画进本View的画布中
			canvas.drawBitmap(mBgBitmap, null, mRect, new Paint());
			initFramePaint();
			// 绘制边框
			canvas.drawRect(mHorizontalPadding, mVerticalPadding,
					mHorizontalPadding + mWidth, mVerticalPadding + mWidth,
					mPaint);

		}

		mPaint.setXfermode(null);
	}

	public void setHorizontalPadding(int mHorizontalPadding) {
		this.mHorizontalPadding = mHorizontalPadding;

	}

	public void setIsCircle(boolean value) {
		this.isCircle = value;
	}

}
