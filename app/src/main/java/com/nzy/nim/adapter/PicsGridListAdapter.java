package com.nzy.nim.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nzy.nim.R;
import com.nzy.nim.tool.common.ImageUtil;

import java.util.List;

/**
 * @author LIUBO
 * @date 2015-4-6下午7:21:33
 * @TODO 缩略图的表格展示适配器
 */
public class PicsGridListAdapter extends BaseAdapter {
	private Context context;
	private List<String> urls;

	public PicsGridListAdapter(Context context, List<String> urls) {
		this.context = context;
		this.urls = urls;
	}

	@Override
	public int getCount() {
		return urls == null ? 0 : urls.size();
	}

	@Override
	public String getItem(int position) {
		return urls.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		final String url = urls.get(position);
		ViewHolder vh = null;
		if (view == null) {
			vh = new ViewHolder();
			view = LayoutInflater.from(context).inflate(
					R.layout.grid_item, null);
			vh.iv = (ImageView) view.findViewById(R.id.item_grid_image);
			view.setTag(vh);
		} else {
			vh = (ViewHolder) view.getTag();
		}
		ImageUtil.displayImg(url, vh.iv);
		return view;
	}

	static class ViewHolder {
		ImageView iv;
	}
}
