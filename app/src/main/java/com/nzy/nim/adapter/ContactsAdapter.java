package com.nzy.nim.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nzy.nim.R;
import com.nzy.nim.db.bean.Contacts;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.ImageUtil;
import com.nzy.nim.view.ClearEditText;
import com.nzy.nim.view.RoundImageView;

import java.util.List;

@SuppressLint("DefaultLocale")
public class ContactsAdapter extends BaseAdapter implements SectionIndexer {
	private List<Contacts> mDatas;
	private Context context;
	private int itemLayoutId;
	private SearchContactsListener listener;
	private ClearEditText edit;

	public interface SearchContactsListener {
		void search(String s);
	}

	public void setSearchContactsListener(SearchContactsListener listener) {
		this.listener = listener;
	}

	public ContactsAdapter(Context context, List<Contacts> mDatas,
						   int itemLayoutId) {
		this.mDatas = mDatas;
		this.context = context;
		this.itemLayoutId = itemLayoutId;
	}

	public void update(List<Contacts> datas) {
		this.mDatas = datas;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mDatas == null ? 1 : mDatas.size() + 1;
	}

	@Override
	public Contacts getItem(int position) {
		if (position > 0)
			return mDatas.get(position - 1);
		else
			return null;
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
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position == 0) {//好友页面第一行为搜索栏
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.search_common_et, null);
			}
			edit = (ClearEditText) convertView.findViewById(R.id.search_edit);
			edit.setFocusable(true);
			// 该功能暂时不用隐藏掉
//			edit.setVisibility(View.GONE);
			edit.setFocusableInTouchMode(true);
			edit.requestFocus();
			edit.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					if (listener != null) {
						listener.search(s.toString());
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
				}
			});
		} else {

			ViewHolder vh = null;
			final Contacts contact = getItem(position);
			if (convertView == null) {
				vh = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						itemLayoutId, null);
				vh.rel_childitem_header= (RelativeLayout) convertView.findViewById(R.id.rel_childitem_header);
				vh.tvHead = (TextView) convertView
						.findViewById(R.id.childitem_header);//首字提醒栏
				vh.headImg = (RoundImageView) convertView
						.findViewById(R.id.child_image);
				vh.tvName = (TextView) convertView
						.findViewById(R.id.tv_contacts_name);
				vh.tv_school = (TextView) convertView
						.findViewById(R.id.tv_contacts_school);
				vh.tv_class = (TextView) convertView
						.findViewById(R.id.tv_contacts_class);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			// 根据position获取分类的首字母的Char ascii值
			int section = getSectionForPosition(position);
			// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
			if (position == getPositionForSection(section)) {
				vh.rel_childitem_header.setVisibility(View.VISIBLE);
				vh.tvHead.setVisibility(View.VISIBLE);
				vh.tvHead.setText(String.valueOf(contact.getFirstLetter()
						.toUpperCase().charAt(0)));
				vh.tvHead.setTextColor(Color.GRAY);
			} else {
				vh.rel_childitem_header.setVisibility(View.GONE);
				vh.tvHead.setVisibility(View.GONE);
			}
			ImageUtil.displayHeadImg(contact.getPhotoPath(), vh.headImg);//加载头像图片

			if (!DataUtil.isEmpty(contact.getRemark())) {
				vh.tvName.setText(contact.getRemark());
			} else if (!DataUtil.isEmpty(contact.getUserName()))
				vh.tvName.setText(contact.getUserName());
			else
				vh.tvName.setText("");

//			if(!DataUtil.isEmpty(contact.getSchoolName())){
//				vh.tv_school.setText(contact.getSchoolName());
//			}else{
//				vh.tv_school.setText("");
//			}
//			if(!DataUtil.isEmpty(contact.getaClassId())){
//				vh.tv_class.setText(contact.getaClassId());
//			}else{
//				vh.tv_class.setText("");
//			}
		}
		return convertView;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		if(DataUtil.isEmpty(getItem(position))){
			return -1;
		}
//		if(!getItem(position).getFirstLetter().toUpperCase().isEmpty()){
			return getItem(position).getFirstLetter().toUpperCase().charAt(0);
//		}else{
//			return 0;
//		}
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		if(DataUtil.isEmpty(mDatas)){
			return -1;
		}
		for (int i = 0; i < mDatas.size(); i++) {
			char firstChar = mDatas.get(i).getFirstLetter().toUpperCase()
					.charAt(0);
			if (firstChar == section) {
				return i + 1;
			}
		}

		return -1;
	}

	public void reflush() {
		notifyDataSetChanged();
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	final static class ViewHolder {
		TextView tvHead;
		TextView tvName;
		RoundImageView headImg;
		TextView tv_school;
		TextView tv_class;
		RelativeLayout rel_childitem_header;
	}
}
