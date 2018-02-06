package com.nzy.nim.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nzy.nim.R;
import com.nzy.nim.db.bean.MyGroups;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.ImageCache;
import com.nzy.nim.tool.common.ImageUtil;
import com.nzy.nim.view.RoundImageView;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.vo.UserInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyGroupsAdapter extends BaseAdapter {
	private List<MyGroups> list;
	private Context context;
	private String userHead;

	public MyGroupsAdapter(Context context, List<MyGroups> list) {
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		// 有背景图
		return list == null ? 2 : list.size() + 2;
	}

	@Override
	public int getItemViewType(int position) {

		return position == 0 ? 0 : 1;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public MyGroups getItem(int position) {
		if (position > 1)
			return list.get(position - 2);
		else
			return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position == 0) {
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.front_cover_bg, null);
			}
			RoundImageView head = (RoundImageView) convertView
					.findViewById(R.id.my_groups_user_head);
			UserInfo user = DBHelper.getInstance().getUserById(
					QYApplication.getPersonId());
			if (user != null)
				userHead = user.getPhotoPath();
			if(userHead!=null){
				ImageUtil.displayHeadImg(userHead, head);
			}else{
				ImageUtil.displayHeadImg("", head);
			}
		} else {
			final MyGroups group =getItem(position);
//			Log.e("","group="+group.toString());
//			final MyGroups group =list.get(position-2);
			ViewHolder vh = null;
			if (convertView == null) {
				vh = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.listview_my_groups_item, null);
				vh.day = (TextView) convertView
						.findViewById(R.id.my_groups_item_day);
				vh.month = (TextView) convertView
						.findViewById(R.id.my_groups_item_month);
				vh.groupImg = (ImageView) convertView
						.findViewById(R.id.my_groups_item_ivShow);
				vh.title = (TextView) convertView
						.findViewById(R.id.my_groups_item_introduce);
				vh.container1 = (LinearLayout) convertView
						.findViewById(R.id.my_groups_item_container_1);
				vh.container2 = (LinearLayout) convertView
						.findViewById(R.id.my_groups_item_container_2);
				vh.tvContainer2 = (TextView) convertView
						.findViewById(R.id.my_groups_item_day_1);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
//			if(list.size()>2){
//				if(position<list.size()-1){
//					handleDatas(group, vh, position);
//				}
//			}else {
				handleDatas(group, vh, position);
//			}


		}
		return convertView;
	}
	private void handleDatas(final MyGroups group, ViewHolder vh, int position) {
		vh.container2.setVisibility(View.GONE);
		vh.container1.setVisibility(View.GONE);
//		显示第一个item布局：发表拍照
		if (position == 1) {
			vh.day.setText("今天");
//			vh.month.setVisibility(View.INVISIBLE);
			vh.title.setVisibility(View.INVISIBLE);
			vh.container2.setVisibility(View.VISIBLE);
			vh.container1.setVisibility(View.GONE);
			vh.groupImg.setImageResource(R.drawable.ic_tack_photo);
		} else {
			vh.container1.setVisibility(View.VISIBLE);
			vh.container2.setVisibility(View.GONE);

			Calendar calendar = Calendar.getInstance();

			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
//			SimpleDateFormat formatter2 = new SimpleDateFormat("MM");
			SimpleDateFormat formatter3 = new SimpleDateFormat("dd");
			Date    curDate    =   new Date(System.currentTimeMillis());//获取当前时间
			String currentTime = formatter.format(curDate);
			String preTime = formatter.format(group.getJoinTime());
//
			Integer intPreTime = Integer.valueOf(preTime);   //发送时间
			Integer intCurrentTime = Integer.valueOf(currentTime);//当前时间
			int intLastTime;
			if (position==2){
				intLastTime=intCurrentTime;
			}else{
				Date date = list.get(position - 3).getJoinTime();
				intLastTime= Integer.valueOf(formatter.format(date));//上一条时间
			}
				if (intCurrentTime==intPreTime){
					vh.container2.setVisibility(View.GONE);
					vh.container1.setVisibility(View.GONE);
				}else{
//					如果两个item时间相同，隐藏时间
					if (intPreTime==intLastTime){
						vh.container2.setVisibility(View.GONE);
						vh.container1.setVisibility(View.GONE);
					}else{
						if (intCurrentTime-intPreTime==1){
							vh.container2.setVisibility(View.VISIBLE);
							vh.container1.setVisibility(View.GONE);
							vh.tvContainer2.setText("昨天");
						}else{
							vh.container1.setVisibility(View.VISIBLE);
							vh.container2.setVisibility(View.GONE);
						}
					}
				}

			if (group.getJoinTime() != null) {
					calendar.setTime(group.getJoinTime());
			}
			vh.day.setText(formatter3.format(group.getJoinTime()));
			vh.month.setText(calendar.get(Calendar.MONTH) + 1 + "月");
			if(group.getGroupImg()!=null){
				if (ImageCache.getInstance().get(group.getGroupImg()) != null) {
					vh.groupImg.setImageBitmap(ImageCache.getInstance().get(
							group.getGroupImg()));
					Toast.makeText(context,group.getGroupImg(),Toast.LENGTH_SHORT).show();
				} else{
					ImageUtil.displayNetImg(group.getGroupImg(), vh.groupImg);
				}
			}else{
				ImageUtil.displayNetImg("", vh.groupImg);
			}

			vh.title.setVisibility(View.VISIBLE);
			if (!DataUtil.isEmpty(group.getGroupName()))
				vh.title.setText(group.getGroupName());
			else
				vh.title.setText("无题");
		}
	}

	final class ViewHolder {
		TextView day;
		TextView month;
		ImageView groupImg;
		TextView title;
		LinearLayout container1;
		LinearLayout container2;
		TextView tvContainer2;

	}
}
