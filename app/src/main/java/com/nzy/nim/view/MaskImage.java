package com.nzy.nim.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.nzy.nim.tool.common.ScreenUtils;

public class MaskImage extends ImageView {
	private Paint paint;
	private PorterDuffXfermode porterDuffXfermode;
	// 最大宽高
	private int maxWidth = 0;
	private int maxHeight = 0;
	// 原图的缩放比例
	private float scaleW = 0.0f;
	private float scaleH = 0.0f;
	// 实际画布的宽高
	private int canvasWidth = 0;
	private int canvasHeight = 0;

	public MaskImage(Context context) {
		super(context);

	}

	public MaskImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 实例化混合模式
		porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
		maxWidth = maxHeight = Math.min(
				ScreenUtils.getScreenWidth(context) / 2,
				ScreenUtils.getScreenHeight(context) / 4);
		initPaint();
	}

	private void initPaint() {
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}

	public void show(Bitmap bp, int minSize, int maskSource) {
		setImageBitmap(getPorterDuffImg(bp, minSize, maskSource));
		setScaleType(ScaleType.CENTER);
	}

	public void showDefaultImg(int srcSource, int minSize, int maskSource) {
		Bitmap orignal = BitmapFactory
				.decodeResource(getResources(), srcSource);
		setImageBitmap(getPorterDuffImg(orignal, minSize, maskSource));
		setScaleType(ScaleType.CENTER);
	}

	/**
	 * 显示混合后的图片
	 * 
	 * @param bp
	 * @param maskSource
	 */
	private Bitmap getPorterDuffImg(Bitmap bp, int minSize, int maskSource) {
		initWHSize(bp, minSize, minSize);
		// 获取遮罩层图片
		Bitmap mask = BitmapFactory.decodeResource(getResources(), maskSource);
		Bitmap result = Bitmap.createBitmap(canvasWidth, canvasHeight,
				Config.ARGB_8888);
		Canvas mCanvas = new Canvas(result);
		NinePatch np = new NinePatch(mask, mask.getNinePatchChunk(), null);
		Rect rect = new Rect(0, 0, canvasWidth, canvasHeight);
		np.draw(mCanvas, rect, null);
		paint.setXfermode(porterDuffXfermode);
		mCanvas.drawBitmap(getScaleBitmap(bp, scaleW, scaleH), 0, 0, paint);
		paint.setXfermode(null);
		return result;
	}

	/**
	 * 
	 */
	private void initWHSize(Bitmap bp, int minWidth, int minHeight) {
		int bpWidth = bp.getWidth();
		int bpHeight = bp.getHeight();
		if (bpWidth < minWidth) {// 宽小于最小值时
			canvasWidth = minWidth;
			scaleW = minWidth / (bpWidth * 1.0f);
			setHByWLessMax(minHeight, bpHeight);
		} else if (bpWidth < maxWidth) {
			canvasWidth = bpWidth;
			scaleW = 1.0f;
			setHByWLessMax(minHeight, bpHeight);
		} else {
			// 初始值（假设宽比高大）
			scaleW = maxWidth / (bpWidth * 1.0f);
			canvasWidth = maxWidth;
			setHByWMoreMax(minHeight, bpWidth, bpHeight);
		}

	}

	private void setHByWMoreMax(int minHeight, int bpWidth, int bpHeight) {
		if (bpHeight < minHeight) {
			scaleH = minHeight / (bpHeight * 1.0f);
			canvasHeight = minHeight;
		} else if (bpHeight < maxHeight) {
			scaleH = 1.0f;
			canvasHeight = bpHeight;
		} else {

			if (bpWidth < bpHeight) {// 原图宽比高小
				scaleW = scaleH = maxHeight / (bpHeight * 1.0f);
				canvasHeight = maxHeight;
				canvasWidth = (int) (bpWidth * scaleW);
			} else {
				scaleH = maxWidth / (bpWidth * 1.0f);
				canvasHeight = (int) (bpHeight * scaleH);
			}
		}
	}

	private void setHByWLessMax(int minHeight, int bpHeight) {
		if (bpHeight < minHeight) {
			scaleH = minHeight / (bpHeight * 1.0f);
			canvasHeight = minHeight;
		} else if (bpHeight < maxHeight) {
			scaleH = 1.0f;
			canvasHeight = bpHeight;
		} else {
			scaleH = maxHeight / (bpHeight * 1.0f);
			canvasHeight = maxHeight;
		}
	}

	/**
	 * 获取缩放后的图片
	 * 
	 * @param bitmap
	 * @param scaleWidth
	 * @param scaleHeight
	 * @return
	 */
	private Bitmap getScaleBitmap(Bitmap bitmap, float scaleWidth,
								  float scaleHeight) {
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}
}