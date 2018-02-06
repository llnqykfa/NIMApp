package com.nzy.nim.activity.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.nzy.nim.R;
import com.nzy.nim.activity.main.MyPersonalInfoActivity;
import com.nzy.nim.api.FileUtils;
import com.nzy.nim.constant.MyConstants;
import com.nzy.nim.tool.common.ImageUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.view.clip.ClipImageLayout;

public class ClipPicActivity extends BaseActivity {
	private ClipImageLayout mCutPicView;
	private Button sureBtn;
	private CompressFormat format;
	private String extensions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clip_pic);
		initTopBar();
		initView();
	}

	private void initTopBar() {
		Button next = (Button) findViewById(R.id.top_bar_next);
		TextView title = (TextView) findViewById(R.id.top_bar_content);
		next.setVisibility(View.INVISIBLE);
		title.setText("移动和缩放");
		findViewById(R.id.top_back_bg).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_OK);
				ClipPicActivity.this.finish();
			}
		});
	}

	@SuppressLint("NewApi")
	private void initView() {
		mCutPicView = (ClipImageLayout) findViewById(R.id.clip_pic_cliplayout);
		sureBtn = (Button) findViewById(R.id.clip_pic_sure);
		String path = getIntent().getStringExtra("pic_path");
		int size = getIntent().getIntExtra("padding_size", 30);
		boolean flag = getIntent().getBooleanExtra("isCircle", false);
		extensions = FileUtils.getExtensions(path);
		format = ImageUtil.getImgExtensions(extensions);
		if (format == null) {
			ToastUtil.show(this, "不支持该图片格式！", Gravity.CENTER);
			this.finish();
		}
		Bitmap bp = ImageUtil.compressBySize(path, 480, 600);
		mCutPicView.setBitmap(bp);
		mCutPicView.setHorizontalPadding(size);
		mCutPicView.setIsCircle(flag);

		sureBtn.setOnClickListener(new OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				Bitmap bitmap = ImageUtil.compressImage(mCutPicView.clip(),
						format, 50);
				String path = ImageUtil.saveBitmap(bitmap,
						MyConstants.BASE_DIR, System.currentTimeMillis()
								+ "." + extensions);
				Intent intent = new Intent(ClipPicActivity.this,
						MyPersonalInfoActivity.class);
				intent.putExtra("cut_pic", path);
				setResult(RESULT_OK, intent);
				ClipPicActivity.this.finish();
			}
		});

	}

	/**
	 * @TODO 跳转到图片裁剪界面
	 * @param context
	 * @param path
	 * @param requestCode
	 * @param isCircle
	 *            是否是圆形,默认为正方形
	 */
	public static void actionIntent(Context context, String path,
			int requestCode, boolean isCircle, int padding) {
		Intent intent = new Intent(context, ClipPicActivity.class);
		intent.putExtra("pic_path", path);
		intent.putExtra("isCircle", isCircle);
		intent.putExtra("padding_size", padding);
		((Activity) context).startActivityForResult(intent, requestCode);
	}
}
