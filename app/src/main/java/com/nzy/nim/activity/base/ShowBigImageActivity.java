package com.nzy.nim.activity.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.nzy.nim.R;
import com.nzy.nim.tool.common.ImageUtil;
import com.nzy.nim.view.photoview.PhotoView;

public class ShowBigImageActivity extends BaseActivity {
	private PhotoView image;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_big_image);
		image = (PhotoView) findViewById(R.id.show_big_image_image);
		String path = getIntent().getExtras().getString("imgPath");
		if(path==null){
			path="";
		}
		ImageUtil.displayHeadImg(path, image);
	}

	/**
	 * 
	 * @todo 从其他页面跳转到当前页面
	 * @param context
	 *            图片的uri路径
	 */
	public static void actionIntent(Context context, String imgPath) {
		Intent intent = new Intent(context, ShowBigImageActivity.class);
		intent.putExtra("imgPath", imgPath);
		context.startActivity(intent);
	}
}
