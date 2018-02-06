package com.nzy.nim.activity.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nzy.nim.R;
import com.nzy.nim.adapter.ImageGridAdapter;
import com.nzy.nim.db.ImageBucket;
import com.nzy.nim.db.bean.ImageItem;
import com.nzy.nim.db.tmpbean.AlbumHelper;
import com.nzy.nim.tool.common.ScreenUtils;
import com.nzy.nim.view.PhotoBucketWindow;

import java.util.ArrayList;
import java.util.List;

public class ImageGridActivity extends Activity implements OnClickListener,
		PhotoBucketWindow.OnBucketSelectedListener {
	public final String TAG = getClass().getSimpleName();
	public final static String IMAGE_PATH = "image_path";
	public static Bitmap def;
	private List<ImageItem> dataList = new ArrayList<ImageItem>();
	private GridView gridView;
	private ImageGridAdapter adapter;
	private AlbumHelper helper;
	private PhotoBucketWindow bucketWindow;
	private int mScreenWidth;
	private int mScreenHeight;
	private ImageView backBtn;
	private Button finishBtn;
	private TextView titleContent;
	private Button selectBucketBtn;
	private LinearLayout bottomContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_select_pics);
		def = BitmapFactory.decodeResource(getResources(),
				R.drawable.default_image);
		initView();
		setUpView();
	}

	private void initView() {
		findViewById(R.id.top_back_bg).setOnClickListener(this);
		titleContent = (TextView) findViewById(R.id.top_bar_content);
		finishBtn = (Button) findViewById(R.id.top_bar_next);
		gridView = (GridView) findViewById(R.id.select_pics_grid);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		selectBucketBtn = (Button) findViewById(R.id.select_pics_bucket);
		bottomContainer = (LinearLayout) findViewById(R.id.select_pics_bottom_container);
		finishBtn.setOnClickListener(this);
		selectBucketBtn.setOnClickListener(this);

	}

	private void setUpView() {
		helper = AlbumHelper.getHelper();
		helper.init(this);
		mScreenWidth = ScreenUtils.getScreenWidth(this);
		mScreenHeight = ScreenUtils.getScreenHeight(this);
		List<ImageBucket> list = helper.getImageBucketList(false);
		bucketWindow = new PhotoBucketWindow(this, bottomContainer, list,
				mHandler, mScreenWidth, (int) (mScreenHeight * 0.618));
		bucketWindow.setSelectedListener(this);
		titleContent.setText("选择图片");
		finishBtn.setText("完成(" + ImageGridAdapter.selectPicPaths.size() + "/"
				+ ImageGridAdapter.MAX_SELECT + ")");
		dataList.clear();
		dataList.addAll(helper.getAllImageList(true));
		adapter = new ImageGridAdapter(this, dataList, mHandler);
		gridView.setAdapter(adapter);
		adapter.setTextCallBack(new ImageGridAdapter.TextCallBack() {
			@Override
			public void onListen(int count) {
				finishBtn.setText("完成(" + count + "/"
						+ ImageGridAdapter.MAX_SELECT + ")");
			}
		});
	}

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(ImageGridActivity.this,
						"最多选择" + ImageGridAdapter.MAX_SELECT + "张图片",
						Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		}
	};

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.top_back_bg || id == R.id.top_bar_next) {
			setResult(Activity.RESULT_OK);
			finish();
		} else if (id == R.id.select_pics_bucket) {
			bucketWindow.show();
		}

	}

	public static void actionIntent(Activity activity, int requestCode) {
		activity.startActivityForResult(new Intent(activity,
				ImageGridActivity.class), requestCode);
	}

	@Override
	public void onBucketSelected(List<ImageItem> imageList) {
		dataList.clear();
		dataList.addAll(imageList);
		adapter.refresh();
	}
}
