package com.nzy.nim.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nzy.nim.R;
import com.nzy.nim.db.ImageBucket;
import com.nzy.nim.db.bean.ImageItem;
import com.nzy.nim.tool.common.ImageUtil;

import java.util.List;

public class PhotoBucketWindow extends PopupWindow implements
		OnItemClickListener {
	private Context context;
	private View view;
	private View anchor;
	private ListView listView;
	private List<ImageBucket> bucketList;
	private OnBucketSelectedListener selectedListener;
	private ImageBucket selectedBucket;

	public void setSelectedListener(OnBucketSelectedListener selectedListener) {
		this.selectedListener = selectedListener;
	}

	Handler mHandler;

	@SuppressLint("InflateParams")
	public PhotoBucketWindow(Context context, View anchor,
							 List<ImageBucket> bucketList, Handler mHandler, int width,
							 int height) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.common_listview, null);
		LinearLayout top = (LinearLayout) view
				.findViewById(R.id.common_listview_topbar);
		top.setVisibility(View.GONE);
		listView = (ListView) view.findViewById(R.id.common_listview_lv);
		setContentView(view);
		setWidth(width);
		setHeight(height);
		setFocusable(true);
		setOutsideTouchable(true);
		setAnimationStyle(R.style.Animations_PopDownMenu);
		this.anchor = anchor;
		this.bucketList = bucketList;
		this.context = context;
		this.mHandler = mHandler;
		ImageBucketAdapter adapter = new ImageBucketAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		selectedBucket = bucketList.get(0);
	}

	public void show() {
		showAsDropDown(anchor);

	}

	class ImageBucketAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (bucketList != null)
				return bucketList.size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageBucket bucket = bucketList.get(position);
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.img_bucket_item, null);
			}

			ImageView imageView = (ImageView) convertView
					.findViewById(R.id.img_bucket_item_img);
			ImageView imageView2 = (ImageView) convertView
					.findViewById(R.id.img_bucket_item_choose_flag);
			if (bucket.isSelected)
				imageView2.setImageResource(R.drawable.ic_selected);
			else
				imageView2.setImageBitmap(null);

			TextView tv1 = (TextView) convertView
					.findViewById(R.id.img_bucket_item_name);
			TextView tv2 = (TextView) convertView
					.findViewById(R.id.img_bucket_item_num);
			tv1.setText(bucket.bucketName);
			tv2.setText(bucket.count + "张");
			ImageUtil.displayLocalImg(bucket.imageList.get(0).thumbnailPath,
					bucket.imageList.get(0).imagePath, imageView);
			return convertView;
		}

	}

	public interface OnBucketSelectedListener {
		public void onBucketSelected(List<ImageItem> imageList);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ImageBucket curBucket = bucketList.get(position);
		ImageView imageView2 = (ImageView) view
				.findViewById(R.id.img_bucket_item_choose_flag);
		List<ImageItem> imageList = curBucket.imageList;
		if (selectedListener != null) {
			selectedListener.onBucketSelected(imageList);
		}
		imageView2.setImageResource(R.drawable.ic_selected);
		curBucket.isSelected = true;
		if (curBucket != selectedBucket)
			selectedBucket.isSelected = false;
		selectedBucket = curBucket;
		dismiss();
	}
}
