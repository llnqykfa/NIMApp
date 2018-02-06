package com.nzy.nim.activity.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.adapter.ImageGridAdapter;
import com.nzy.nim.tool.common.ImageUtil;

import java.util.ArrayList;

public class PhotoActivity extends BaseActivity {
	public static final String MODIFY_IMG = "modify_img_flag";
	private ArrayList<View> listViews = null;
	private ViewPager pager;
	private MyPageAdapter adapter;
	private int count;
	private int index;
	RelativeLayout photo_relativeLayout;
	private TextView titleContent;
	private Button deleteBtn;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		index = getIntent().getIntExtra("selectIndex", 0);
		initTopBar();
		initView();

	}

	private void initTopBar() {
		titleContent = (TextView) findViewById(R.id.top_bar_content);
		Button next = (Button) findViewById(R.id.top_bar_next);
		next.setVisibility(View.GONE);
		titleContent.setText("(" + index + "/"
				+ ImageGridAdapter.selectPicPaths.size() + ")");
		findViewById(R.id.top_back_bg).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				resultDatas();
			}
		});
	}

	private void initView() {
		photo_relativeLayout = (RelativeLayout) findViewById(R.id.photo_relativeLayout);
		photo_relativeLayout.setBackgroundColor(0x70000000);
		pager = (ViewPager) findViewById(R.id.photo_viewpager);
		pager.setOnPageChangeListener(pageChangeListener);
		deleteBtn = (Button) findViewById(R.id.photo_bt_del);
		for (int i = 0; i < ImageGridAdapter.selectPicPaths.size(); i++) {
			initListViews(ImageGridAdapter.selectPicPaths.get(i));
		}
		adapter = new MyPageAdapter(listViews);// 构造adapter
		pager.setAdapter(adapter);// 设置适配器
		pager.setCurrentItem(index);
		deleteBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (listViews.size() == 1) {
					ImageGridAdapter.selectPicPaths.clear();
					finish();
				} else {
					ImageGridAdapter.selectPicPaths.remove(count);
					pager.removeAllViews();
					listViews.remove(count);
					if (count + 1 < ImageGridAdapter.selectPicPaths.size())
						count = count + 1;
					titleContent.setText("(" + count + "/"
							+ ImageGridAdapter.selectPicPaths.size() + ")");
					adapter.setListViews(listViews);
					adapter.notifyDataSetChanged();
				}
			}
		});
	}

	private void initListViews(String path) {
		if (listViews == null)
			listViews = new ArrayList<View>();
		ImageView img = new ImageView(this);// 构造textView对象
		img.setBackgroundColor(0xff000000);
		img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		ImageUtil.displayLocalImg(null, path, img);
		listViews.add(img);// 添加view
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		resultDatas();
	}

	/**
	 * 页面更变的监听
	 */
	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		public void onPageSelected(int arg0) {// 页面选择响应函数
			count = arg0;
			titleContent.setText("(" + (arg0 + 1) + "/"
					+ ImageGridAdapter.selectPicPaths.size() + ")");
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {// 滑动中。。。

		}

		public void onPageScrollStateChanged(int arg0) {// 滑动状态改变

		}
	};

	class MyPageAdapter extends PagerAdapter {

		private ArrayList<View> listViews;// content

		private int size;// 页数

		public MyPageAdapter(ArrayList<View> listViews) {// 构造函数
															// 初始化viewpager的时候给的一个页面
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public void setListViews(ArrayList<View> listViews) {// 自己写的一个方法用来添加数据
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public int getCount() {// 返回数量
			return size;
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		public void destroyItem(View arg0, int arg1, Object arg2) {// 销毁view对象
			((ViewPager) arg0).removeView(listViews.get(arg1 % size));
		}

		public void finishUpdate(View arg0) {
		}

		public Object instantiateItem(View arg0, int arg1) {// 返回view对象
			try {
				((ViewPager) arg0).addView(listViews.get(arg1 % size), 0);

			} catch (Exception e) {
			}
			return listViews.get(arg1 % size);
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

	/**
	 * 返回数据
	 */
	public void resultDatas() {
		setResult(RESULT_OK);
		finish();
	}

	public static void actionIntent(Activity activity, int selectIndex,
			int requestCode) {
		Intent intent = new Intent(activity, PhotoActivity.class);
		intent.putExtra("selectIndex", selectIndex);
		activity.startActivityForResult(intent, requestCode);
	}
}
