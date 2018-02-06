package com.nzy.nim.activity.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.adapter.CommonAdapter;
import com.nzy.nim.adapter.ViewHolder;
import com.nzy.nim.api.URLs;
import com.nzy.nim.helper.DialogHelper;
import com.nzy.nim.tool.common.CommonUtil;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.HttpUtil;
import com.nzy.nim.tool.common.ImageUtil;
import com.nzy.nim.tool.common.ScreenUtils;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.view.RoundImageView;
import com.nzy.nim.vo.OldPersonVO;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.vo.UserInfo;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @classify(类别) 添加好友界面
 * @TODO(功能) TODO
 * @Param(参数)
 * @Remark(备注)
 * 
 */




public class AddNewFriendsActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {
	// 标题的
	private TextView titleContent;
	private Button titleNext;
	/**
	 * 搜索输入框
	 */
	private EditText searchEdt;
	private LinearLayout doSearch;// 点击搜索
	private TextView searchContent;// 搜索内容
	private CommonAdapter<OldPersonVO> adapter;
	/**
	 * 要显示的结果
	 */
	private LinearLayout searchFriends;
	private ListView resultLV;// 搜索结果显示
	private List<OldPersonVO> result;// 搜索结果集合
	private TextView noResult;// 没有找到联系人
	private ProgressDialog pgUtil;// 等待进度条

	/**
	 * 登陆用户信息
	 */
	private UserInfo user;
	DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new_friends);
		user = DBHelper.getInstance().getUserById(QYApplication.getPersonId());
		result = new ArrayList<OldPersonVO>();
		pgUtil = DialogHelper.getSDialog(AddNewFriendsActivity.this, "搜索中···", false);
		initView();
	}

	@Override
	protected void onStart() {
		super.onStart();
		searchEdt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				if (s.length() > 0) {
					doSearch.setVisibility(View.VISIBLE);
					searchContent.setText(s);
				} else {
					doSearch.setVisibility(View.GONE);
				}
				searchFriends.setVisibility(View.GONE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private void initView() {

		int width = ScreenUtils.getScreenWidth(this);
		int w = ScreenUtils.dip2px(this, 20);
		RelativeLayout rl_search_left = (RelativeLayout)findViewById(R.id.rl_search_left);
		ViewGroup.LayoutParams params = rl_search_left.getLayoutParams();
		params.width=(width-w)*65/680;
		rl_search_left.setLayoutParams(params);

		findViewById(R.id.top_back_bg).setOnClickListener(this);
		titleContent = (TextView) findViewById(R.id.top_bar_content);
		titleNext = (Button) findViewById(R.id.top_bar_next);
		titleNext.setVisibility(View.GONE);
		titleContent.setText("添加");
		searchEdt = (EditText) findViewById(R.id.add_new_friends_edt);
		doSearch = (LinearLayout) findViewById(R.id.add_new_friends_search_people);
		doSearch.setVisibility(View.GONE);
		searchContent = (TextView) findViewById(R.id.add_new_friends_search_content);
		searchFriends = (LinearLayout) findViewById(R.id.add_new_friends_result);
		searchFriends.setVisibility(View.GONE);
		noResult = (TextView) findViewById(R.id.add_new_friends_noresult);
		resultLV = (ListView) findViewById(R.id.add_new_friends_list);
		adapter = new CommonAdapter<OldPersonVO>(this, result,
				R.layout.activity_childitem_list) {

			@Override
			public void convert(ViewHolder helper, OldPersonVO item) {
				RoundImageView headImg = helper.getView(R.id.child_image);
				ImageUtil.displayHeadImg(item.getPhotopath(), headImg);
				if (!DataUtil.isEmpty(item.getUsername()))
					helper.setText(R.id.child_name, item.getUsername());
				else
					helper.setText(R.id.child_name, "");
				if (!DataUtil.isEmpty(item.getDesigninfo()))
					helper.setText(R.id.child_sign, item.getDesigninfo());
				else
					helper.setText(R.id.child_sign, "");
			}
		};
		resultLV.setAdapter(adapter);
		doSearch.setOnClickListener(this);
		searchEdt.setOnClickListener(this);
		resultLV.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_back_bg:// 返回按钮
			this.finish();
			break;
		case R.id.add_new_friends_search_people:
			// 隐藏软键盘
			CommonUtil.hideKeyboard(AddNewFriendsActivity.this, v);
//			if (isMe(searchContent.getText().toString())) {
//				ToastUtil.showShort(this, "不能添加自己为好友！！！");
//			} else {
				doSearch(searchContent.getText().toString());
//			}
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * @TODO TODO 判断搜索的好友是否是自己
	 * @param condation
	 * @return
	 * @Return boolean
	 */
	private boolean isMe(String condation) {
		if (condation.equals(user.getUserName())
				|| condation.equals(user.getPersonId())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @TODO TODO 调用搜索方法
	 * @param content
	 * @Return void
	 */
	private void doSearch(String content) {
		if (TextUtils.isEmpty(content)) {
			ToastUtil.show(this, "搜索条件不能为空！", Gravity.CENTER);
			return;
		}
		String condication = "code= '" + content + "' or username= '" + content
				+ "' or phone= '" + content + "'";
		pgUtil.show();
		RequestParams params = new RequestParams();
		params.add("cause", condication);
		HttpUtil.post(URLs.GET_PERSON_LIST, params,
				new TextHttpResponseHandler() {

					@Override
					public void onSuccess(int arg0, Header[] arg1, String arg2) {
						if (!DataUtil.isEmpty(arg2)) {
							List<OldPersonVO> list = JSON.parseArray(arg2,
									OldPersonVO.class);
							result.clear();
							result.addAll(list);
							searchFriends.setVisibility(View.VISIBLE);
							resultLV.setVisibility(View.VISIBLE);
							noResult.setVisibility(View.GONE);
							adapter.notifyDataSetChanged();
						} else {
							searchFriends.setVisibility(View.VISIBLE);
							noResult.setVisibility(View.VISIBLE);
							resultLV.setVisibility(View.GONE);
						}
						pgUtil.dismiss();
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, String arg2,
										  Throwable arg3) {
						pgUtil.dismiss();
						DialogHelper.showMsgDialog(AddNewFriendsActivity.this,
								R.string.notify_net_msg);
					}
				});
	}

	public static void actionIntent(Context context) {
		context.startActivity(new Intent(context, AddNewFriendsActivity.class));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
		PersonVerificationInfo.actionIntent(AddNewFriendsActivity.this, result
				.get(position).getPk_person());
	}
}
