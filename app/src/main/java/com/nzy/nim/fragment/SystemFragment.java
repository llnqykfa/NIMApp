package com.nzy.nim.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.nzy.nim.R;
import com.nzy.nim.adapter.SystemMsgAdapter;
import com.nzy.nim.db.bean.Invitations;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.vo.MsgManager;

import java.util.ArrayList;
import java.util.List;

public class SystemFragment extends Fragment {
	private ListView listView;// 添加的好友的ListView
	private SystemMsgAdapter adapter; // 新朋友的适配器
	private List<Invitations> mDatas = new ArrayList<Invitations>();// 适配器的 数据源

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_newfriends, container,
				false);
		listView = (ListView) view.findViewById(R.id.lv_newFriends);
		// 获取新朋友表中的数据
		List<Invitations> temp = DBHelper.getInstance().getInvitesOrderByTime();
		mDatas.clear();
//		mDatas.addAll(temp);
		for (int i = 0; i <temp.size() ; i++) {
			Invitations invitations = temp.get(i);
			if(invitations.getStatus()!= MsgManager.INVITE_OTHER){
				mDatas.add(invitations);
			}
		}
		adapter = new SystemMsgAdapter(getActivity(), mDatas,
				R.layout.activity_newfriends_item);
		listView.setAdapter(adapter);
		return view;
	}
}
