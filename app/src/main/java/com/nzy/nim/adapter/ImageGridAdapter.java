package com.nzy.nim.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nzy.nim.R;
import com.nzy.nim.db.bean.ImageItem;
import com.nzy.nim.tool.common.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class ImageGridAdapter extends BaseAdapter {
	public static final int MAX_SELECT = 9;
	private TextCallBack textCallBack = null;
	// 选择后的图片的路径集合
	public static ArrayList<String> selectPicPaths = new ArrayList<String>();
	private List<ImageItem> dataList;// 数据源
	private Context context;
	private Handler handler;

	public interface TextCallBack {
		public void onListen(int count);
	}

	public void setTextCallBack(TextCallBack textCallBack) {
		this.textCallBack = textCallBack;
	}

	public ImageGridAdapter(Context context, List<ImageItem> dataList,
							Handler handler) {
		this.dataList = dataList;
		this.context = context;
		this.handler = handler;
		initDataList();
	}

	public void refresh() {
		initDataList();
		notifyDataSetChanged();
	}

	private void initDataList() {
		int size = selectPicPaths.size();
		for (ImageItem img : dataList) {
			for (int i = 0; i < size; i++) {
				if ((img.thumbnailPath != null && img.thumbnailPath
						.equals(selectPicPaths.get(i)))
						|| (img.imagePath != null && img.imagePath
								.equals(selectPicPaths.get(i)))) {
					img.isSelected = true;
					break;
				}
			}
		}
	}

	@Override
	public int getCount() {
		return dataList == null ? 0 : dataList.size();
	}

	@Override
	public ImageItem getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;
		final ImageItem item = dataList.get(position);
		if (convertView == null) {
			holder = new Holder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.gridview_select_pic_item, null);
			holder.iv = (ImageView) convertView
					.findViewById(R.id.select_pic_item_iv_img);
			holder.selected = (ImageView) convertView
					.findViewById(R.id.select_pic_item_iv_check);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		if (item.isSelected) {
			holder.selected.setImageResource(R.drawable.ic_select_yes);
			holder.iv.setColorFilter(Color.parseColor("#77000000"));
		} else {
			holder.selected.setImageResource(R.drawable.ic_select_no);
			holder.iv.setColorFilter(null);
		}
		// 有缩略图则显示缩略图，没有显示原图

		ImageUtil
				.displayLocalImg(item.thumbnailPath, item.imagePath, holder.iv);

		holder.iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (item.isSelected) {
					holder.selected.setImageResource(R.drawable.ic_select_no);
					selectPicPaths.remove(item.imagePath);
					holder.iv.setColorFilter(null);
				} else {
					if (selectPicPaths.size() >= 9) {
						handler.sendEmptyMessage(0);
					} else {
						holder.selected
								.setImageResource(R.drawable.ic_select_yes);
						holder.iv.setColorFilter(Color.parseColor("#77000000"));
						selectPicPaths.add(item.imagePath);
					}
				}
				item.isSelected = !item.isSelected;
				if (textCallBack != null)
					textCallBack.onListen(selectPicPaths.size());
			}
		});
		return convertView;
	}

	class Holder {
		private ImageView iv;
		private ImageView selected;
	}
}
