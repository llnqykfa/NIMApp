package com.nzy.nim.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseFragment;
import com.nzy.nim.activity.main.AddNewFriendsActivity;
import com.nzy.nim.activity.main.FriendsInfoActivity;
import com.nzy.nim.adapter.ContactsAdapter;
import com.nzy.nim.db.bean.Contacts;
import com.nzy.nim.db.tmpbean.DBConversion;
import com.nzy.nim.helper.DialogHelper;
import com.nzy.nim.helper.SideBar;
import com.nzy.nim.http.HttpHelper;
import com.nzy.nim.http.listener.HttpDatasListener;
import com.nzy.nim.pulltorefresh.PullToRefreshBase;
import com.nzy.nim.pulltorefresh.PullToRefreshListView;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.NetUtil;
import com.nzy.nim.tool.common.StringUtil;
import com.nzy.nim.vo.PersonVO;
import com.nzy.nim.vo.QYApplication;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 
 * @author(作者) LIUBO
 * @Date(日期) 2015-2-4 下午11:35:13
 * @classify(类别)
 * @TODO(功能) 联系人列表
 * @Param(参数)
 * @Remark(备注)
 * 
 */
public class ContactsFragment extends BaseFragment implements
		OnItemClickListener, HttpDatasListener, ContactsAdapter.SearchContactsListener,
		PullToRefreshBase.OnRefreshListener<ListView> {
	public static final String TAG = ContactsFragment.class.getName();// 类的全路径

	private List<Contacts> sourceDatas = new ArrayList<Contacts>();// 好友列表数据源
	private ContactsAdapter adapter;
	private ListView contactLv;// 联系人列表
	private PullToRefreshListView pullToRefresh;
	private HttpHelper httpHelper;
	private SideBar sideBar;
	private TextView dialog;
	private PinyinComparator pinyinComparator;// 首字母比较器
	private ProgressDialog pgDialog;
	private View view;
	private MyReceiver myReceiver;
	private RelativeLayout rel_addfriend;
	private RelativeLayout rel_friend;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
//		if (view==null){
			view = inflater.inflate(R.layout.activity_booksfriend_main_list,
					container, false);
			// 初始化
			initView(view);
			initData();
			registerBroadcast();
//		}

		return view;
	}

	private void registerBroadcast() {
		myReceiver = new MyReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction("update_contacts_List");
		getActivity().registerReceiver(myReceiver, filter);
	}

	@Override
	public void onStart() {
		super.onStart();
//		refresh();


	}
	private class MyReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
//			ToastUtil.showShort(getActivity(),"成功接收");
			pullToRefresh.doPullRefreshing(true, 1000);
			refresh();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getActivity().unregisterReceiver(myReceiver);
	}



	/**
	 * @Author liubo
	 * @date 2015-3-12上午11:36:47
	 * @TODO(功能) 刷新好友列表
	 * @mark(备注)
	 */
	public void refresh() {
		// 初始化列表中的数据
		List<Contacts> temp = DataSupport.where("userid = ?",
				QYApplication.getPersonId()).find(Contacts.class);
		sourceDatas.clear();
		// 从本地获取我的好友列表
		Collections.sort(temp, pinyinComparator);
		sourceDatas.addAll(temp);
		adapter.notifyDataSetChanged();
		if(sourceDatas.size()==0){
			rel_addfriend.setVisibility(View.VISIBLE);
			rel_friend.setVisibility(View.GONE);
		}else{
			rel_addfriend.setVisibility(View.GONE);
			rel_friend.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 设置适配器
	 */

	private void setAdapter() {
		adapter = new ContactsAdapter(getActivity(), sourceDatas,
				R.layout.contacts_item);
		adapter.setSearchContactsListener(this);
		contactLv.setAdapter(adapter);
		contactLv.setOnItemClickListener(this);
	}

	/**
	 * @Author LIUBO
	 * @TODO 初始化界面
	 * @Date 2015-1-27
	 * @Return void
	 */
	private void initView(View view) {
		rel_addfriend = (RelativeLayout) view.findViewById(R.id.rel_addfriend);
		rel_friend = (RelativeLayout) view.findViewById(R.id.rel_friend);
		sideBar = (SideBar) view
				.findViewById(R.id.bookfriend_main_list_sidrbar);
		dialog = (TextView) view.findViewById(R.id.bookfriend_main_list_dialog);
		sideBar.setTextView(dialog);
		pullToRefresh = (PullToRefreshListView) view
				.findViewById(R.id.booksfriends_main_list_refulsh_view);

		httpHelper = new HttpHelper();
		pinyinComparator = new PinyinComparator();
		pgDialog = DialogHelper.getSDialog(getActivity(), "加载中···", false);
		httpHelper.setMyHttpDatasListener(this);
		contactLv = pullToRefresh.getRefreshableView();// 得到实际的ListVIew
		contactLv.setSelector(R.drawable.listview_selector_bg_1);
		contactLv.setBackgroundColor(getResources().getColor(R.color.white));
		QYApplication.initPullRefulsh(pullToRefresh, this, false, false, TAG);
		setAdapter();
		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					contactLv.setSelection(position);
				}
			}
		});
		rel_addfriend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AddNewFriendsActivity.actionIntent(getActivity());
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position != 0) {
			// 好友列表
			FriendsInfoActivity.actionIntent(getActivity(),
					adapter.getItem(position).getContactId());
		}
	}

	@Override
	public void pullAllFriends(List<PersonVO> list) {
		// 删除本地
		DataSupport.deleteAll(Contacts.class, "userid=?",
				QYApplication.getPersonId());
		adapter.notifyDataSetChanged();
		QYApplication.refulshComplete(pullToRefresh, TAG);
		// 保存服务器上的最新联系人
		List<Contacts> tmpList = saveContacts(list);
		// 清空内存中的旧的数据
		sourceDatas.clear();
		// 从本地获取我的好友列表
		Collections.sort(tmpList, pinyinComparator);
		sourceDatas.addAll(tmpList);
		pgDialog.dismiss();
		if(sourceDatas.size()==0){
			rel_addfriend.setVisibility(View.VISIBLE);
			rel_friend.setVisibility(View.GONE);
		}else{
			rel_addfriend.setVisibility(View.GONE);
			rel_friend.setVisibility(View.VISIBLE);
		}

	}

	private List<Contacts> saveContacts(List<PersonVO> list) {
		List<Contacts> tmpList = new ArrayList<Contacts>();
		if (!DataUtil.isEmpty(list)) {
			tmpList = DBConversion.getInstance().getContacts(list);
			DataSupport.saveAll(tmpList);
		}
		return tmpList;
	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	@SuppressLint("DefaultLocale")
	private void filterData(String filterStr) {
		List<Contacts> filterDateList = new ArrayList<Contacts>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = sourceDatas;
		} else {
			filterDateList.clear();
			String name;
			for (Contacts contact : sourceDatas) {
				if(!DataUtil.isEmpty(contact.getRemark())){
					name=contact.getRemark();
				}else{
					name= contact.getUserName();
				}
				String str = StringUtil.converterToSpell(name);
				if (name.indexOf(filterStr.toString()) != -1
						|| str.startsWith(filterStr.toString().toLowerCase())) {
					filterDateList.add(contact);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.update(filterDateList);
	}

	/**
	 * @author LIUBO
	 * @date 2015-3-30下午8:01:49
	 * @TODO 拼音比较大小
	 */
	class PinyinComparator implements Comparator<Contacts> {
		@Override
		public int compare(Contacts lhs, Contacts rhs) {
			if (lhs.getFirstLetter().length()==0||rhs.getFirstLetter().length()==0) {
				return 1;
			}
			if (lhs.getFirstLetter().charAt(0) == ('@')
					|| rhs.getFirstLetter().charAt(0) == ('#')) {
				return 1;
			} else if (lhs.getFirstLetter().charAt(0) == ('#')
					|| rhs.getFirstLetter().charAt(0) == ('@')) {
				return -1;
			} else {
				return String.valueOf(lhs.getFirstLetter().charAt(0))
						.compareTo(
								String.valueOf(rhs.getFirstLetter().charAt(0)));
			}
		}
	}

	@Override
	public void search(String s) {
		filterData(s);
	}

	@Override
	protected void lazyLoad() {
	}

	// 初始化列表中的数据
	private void initData() {
		List<Contacts> temp = DataSupport.where("userid = ?",
				QYApplication.getPersonId()).find(Contacts.class);
		if (temp == null || temp.size() == 0) {
			if(NetUtil.checkNetwork(getActivity())){
				pgDialog.show();
			}else{
				pgDialog.dismiss();
			}
			httpHelper.getAllMyFriends(getActivity(),
					QYApplication.getPersonId());
		} else {
			sourceDatas.clear();
			// 从本地获取我的好友列表
			Collections.sort(temp, pinyinComparator);
			sourceDatas.addAll(temp);
		}
		if(sourceDatas.size()==0){
			rel_addfriend.setVisibility(View.VISIBLE);
			rel_friend.setVisibility(View.GONE);
		}else{
			rel_addfriend.setVisibility(View.GONE);
			rel_friend.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		httpHelper.getAllMyFriends(getActivity(), QYApplication.getPersonId());
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

	}
}