package com.nzy.nim.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nzy.nim.R;
import com.nzy.nim.tool.common.ImageUtil;

import java.util.ArrayList;

/**
 * 展示选择的图片（带有添加图标）
 *
 * @author liu
 *
 */
public class ListSelectedPicAdapter extends BaseAdapter {
	private LayoutInflater inflater; // 视图容器
	private ArrayList<String> imgPaths;
	private Context context;
	private int maxPicSize = 4;
	private boolean isFirst=true;
	public ListSelectedPicAdapter(Context context, ArrayList<String> imgPaths,
								  int maxNum) {
		inflater = LayoutInflater.from(context);
		this.imgPaths = imgPaths;
		this.context = context;
		this.maxPicSize = maxNum;
	}

	public int getCount() {
		return imgPaths == null ? 1 : imgPaths.size() + 1;
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {

		return 0;
	}

	/**
	 * 展示的图片有限，数量不是很大这里就不用viewHolder 进行优化，防止图片异步加载错乱
	 */
	@SuppressLint({ "InflateParams", "DefaultLocale" })
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_published_grida, null);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView
					.findViewById(R.id.item_grida_image);
			convertView.setTag(holder);
		}else{
			holder= (ViewHolder) convertView.getTag();
		}
		Log.e("==============",position+"");
		if (position == imgPaths.size()) {
			holder.image.setVisibility(View.VISIBLE);
			holder.image.setImageBitmap(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.icon_addpic_focused));
			if (position == maxPicSize) {
				holder.image.setVisibility(View.GONE);
			}
		} else {
			if (parent.getChildCount()==position){
				if (imgPaths.get(position).toLowerCase().startsWith("http")) {
					ImageUtil.displayNetImg(imgPaths.get(position), holder.image);
				} else
					ImageUtil.displayLocalImg(imgPaths.get(position), null, holder.image);
			}
		}

		return convertView;
	}

	class ViewHolder {
		ImageView image;
	}
}
