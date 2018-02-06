package com.nzy.nim.tool.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.nzy.nim.R;
import com.nzy.nim.adapter.FaceAdapter;
import com.nzy.nim.db.bean.ChatEmoji;

import java.util.ArrayList;
import java.util.List;

public class FaceHelper {

	private static FaceHelper faceHelper;

	private FaceHelper() {
	}

	public static FaceHelper getInstance() {
		if (faceHelper == null) {
			synchronized (FaceHelper.class) {
				if (faceHelper == null)
					faceHelper = new FaceHelper();
			}
		}
		return faceHelper;
	}

	/** 获取表情集合 */
	private List<List<ChatEmoji>> getEmojis() {
		return FaceConversionUtil.getInstace().emojiLists;
	}

	/**
	 * 初始化显示表情的viewpager
	 */
	public ArrayList<View> Init_viewPager(Context context,
			ArrayList<View> viewList) {
		ArrayList<View> pageViews = new ArrayList<View>();
		// 左侧添加空页
		View nullView1 = new View(context);
		// 设置透明背景
		nullView1.setBackgroundColor(Color.TRANSPARENT);
		pageViews.add(nullView1);
		// 中间添加表情页
		for (int i = 0; i < viewList.size(); i++) {
			pageViews.add(viewList.get(i));
		}
		// 右侧添加空页面
		View nullView2 = new View(context);
		// 设置透明背景
		nullView2.setBackgroundColor(Color.TRANSPARENT);
		pageViews.add(nullView2);
		return pageViews;
	}

	/**
	 * 
	 * @author LIUBO
	 * @date 2015-3-23上午9:37:45
	 * @TODO 初始化表情适配器
	 * @param context
	 * @return
	 */
	public ArrayList<FaceAdapter> initFaceAdapter(Context context) {
		List<List<ChatEmoji>> emojis = getEmojis();
		// 中间添加表情页
		ArrayList<FaceAdapter> faceAdapters = new ArrayList<FaceAdapter>();
		for (int i = 0; i < emojis.size(); i++) {
			FaceAdapter adapter = new FaceAdapter(context, emojis.get(i));
			faceAdapters.add(adapter);
		}
		return faceAdapters;
	}

	/**
	 * @author LIUBO
	 * @date 2015-3-23上午9:38:11
	 * @TODO 初始化gridview
	 * @param context
	 * @param faceAdapters
	 * @param listener
	 * @return
	 */
	public ArrayList<View> initGridView(Context context,
			ArrayList<FaceAdapter> faceAdapters, OnItemClickListener listener) {
		ArrayList<View> viewList = new ArrayList<View>();
		for (int i = 0; i < faceAdapters.size(); i++) {
			GridView view = new GridView(context);
			view.setAdapter(faceAdapters.get(i));
			view.setOnItemClickListener(listener);
			view.setNumColumns(7);
			view.setBackgroundColor(Color.TRANSPARENT);
			view.setHorizontalSpacing(1);
			view.setVerticalSpacing(1);
			view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
			view.setCacheColorHint(0);
			view.setPadding(5, 0, 5, 0);
			view.setSelector(new ColorDrawable(Color.TRANSPARENT));
			view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
			view.setGravity(Gravity.CENTER);
			viewList.add(view);
		}
		return viewList;
	}

	/**
	 * 初始化游标
	 */
	public ArrayList<ImageView> Init_Point(Context context,
			ArrayList<View> pageViews, LinearLayout layout_point) {
		ArrayList<ImageView> pointViews = new ArrayList<ImageView>();
		ImageView imageView;
		for (int i = 0; i < pageViews.size(); i++) {
			imageView = new ImageView(context);
			imageView.setBackgroundResource(R.drawable.d1);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
			layoutParams.leftMargin = 10;
			layoutParams.rightMargin = 10;
			layoutParams.width = 8;
			layoutParams.height = 8;
			if (layout_point != null) {
				layout_point.addView(imageView, layoutParams);
			}
			if (i == 0 || i == pageViews.size() - 1) {
				imageView.setVisibility(View.GONE);
			}
			if (i == 1) {
				imageView.setBackgroundResource(R.drawable.d2);
			}
			pointViews.add(imageView);

		}
		return pointViews;
	}

	/**
	 * 绘制游标背景
	 */
	public void draw_Point(int index, List<ImageView> pointViews) {
		for (int i = 1; i < pointViews.size(); i++) {
			if (index == i) {
				pointViews.get(i).setBackgroundResource(R.drawable.d2);
			} else {
				pointViews.get(i).setBackgroundResource(R.drawable.d1);
			}
		}
	}
}
