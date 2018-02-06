package com.nzy.nim.activity.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.adapter.CommonAdapter;
import com.nzy.nim.adapter.ViewHolder;
import com.nzy.nim.api.URLs;
import com.nzy.nim.db.bean.GroupMembers;
import com.nzy.nim.db.tmpbean.DBConversion;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.HttpUtil;
import com.nzy.nim.tool.common.ImageUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.view.RoundImageView;
import com.nzy.nim.vo.OldPersonVO;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

public class MembersListActivity extends BaseActivity implements
		OnItemClickListener {
	private TextView titleContent;
	private boolean isManagerMembers;
	private ListView listView;
	private CommonAdapter<GroupMembers> memberAdapter;
	private List<GroupMembers> members = new ArrayList<GroupMembers>();
	private String groupId;
	private boolean isDeleteMember = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_listview);
		groupId = getIntent().getStringExtra("groupId");
		isManagerMembers = getIntent().getBooleanExtra("isManagerMembers",
				false);
		initTopBar();
		initView();
		initAdapter();
		getGroupMembers(groupId);
	}
	/**
	 //     * @param groupId
	 //     * @Author LIUBO
	 //     * @TODO TODO 从服务器上获取该组的成员
	 //     * @Date 2015-2-10
	 //     * @Return void
	 //     */
	private void getGroupMembers(final String groupId) {
		RequestParams params = new RequestParams();
		params.add("ringId", groupId);

		HttpUtil.post(URLs.LIST_BY_RINGID, params,
				new TextHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, Header[] arg1, String arg2) {
						if (arg2 != null) {
							try {
								List<OldPersonVO> list = JSON.parseArray(arg2,
										OldPersonVO.class);
								List<GroupMembers> groupMembers = DBConversion
										.getInstance().getMembers(list, groupId);
								members.addAll(groupMembers);
								memberAdapter.notifyDataSetChanged();
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, String arg2,
										  Throwable arg3) {
					}
				});
	}

	private void initAdapter() {
		memberAdapter = new CommonAdapter<GroupMembers>(this, members,
				R.layout.activity_childitem_list) {
			@Override
			public void convert(ViewHolder helper, final GroupMembers item) {
				RoundImageView head = helper.getView(R.id.child_image);
				ImageView deleteIcon = helper.getView(R.id.child_select_flag);
				TextView headerTv = helper.getView(R.id.childitem_header);
				headerTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				headerTv.setBackgroundColor(getResources().getColor(
						R.color.gray));
				headerTv.setMinHeight(50);
				if (helper.getPosition() == 0) {
					headerTv.setText("圈主");
					headerTv.setVisibility(View.VISIBLE);
					deleteIcon.setVisibility(View.GONE);
				} else if (helper.getPosition() == 1) {
					headerTv.setText("圈成员");
					headerTv.setVisibility(View.VISIBLE);
				} else {
					headerTv.setVisibility(View.GONE);
				}
				if (isManagerMembers && helper.getPosition() != 0) {
					deleteIcon.setImageResource(R.drawable.icon_delete_2);
					deleteIcon.setVisibility(View.VISIBLE);
				}
				ImageUtil.displayHeadImg(item.getImgUrl(), head);
				helper.setText(R.id.child_name, item.getName());
				helper.setText(R.id.child_sign, item.getSign());
				deleteIcon.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						showDialog(item);
					}
				});
			}
		};
		listView.setAdapter(memberAdapter);
	}

	private void initView() {
		listView = (ListView) findViewById(R.id.common_listview_lv);
		listView.setOnItemClickListener(this);
		listView.setSelector(R.drawable.listview_selector_bg_1);
		listView.setBackgroundColor(getResources().getColor(R.color.white));
	}

	private void initTopBar() {
		titleContent = (TextView) findViewById(R.id.top_bar_content);
		Button next = (Button) findViewById(R.id.top_bar_next);
		next.setVisibility(View.GONE);
		findViewById(R.id.top_back_bg).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				exitActivity();
			}

		});
		if (isManagerMembers)
			titleContent.setText("圈成员管理");
		else
			titleContent.setText("圈成员列表");
	}

	private void exitActivity() {
		if (!isDeleteMember)
			setResult(RESULT_OK);
		else
			setResult(RingSettingActivity.RESULT_DELETE_MEMBER);
		finish();
	}

	public static void actionIntent(Activity activity, String groupId,
									boolean isManagerMember) {
		Intent intent = new Intent(activity, MembersListActivity.class);
		intent.putExtra("isManagerMembers", isManagerMember);
		intent.putExtra("groupId", groupId);
		activity.startActivityForResult(intent, 0);
	}

	private void showDialog(final GroupMembers member) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(member.getName()).setMessage("确定要踢出该成员吗？")
				.setNegativeButton("否", null)
				.setPositiveButton("是", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						deleteMember(member);
					}
				}).create().show();
	}

	/**
	 * 踢出相应的圈成员
	 *
	 */
	private void deleteMember(final GroupMembers member) {
		RequestParams params = new RequestParams();
		params.add("ringId", groupId);
		params.add("personId", member.getMemberId());
		HttpUtil.post(URLs.RINGER_DELETE_PERSON, params,
				new TextHttpResponseHandler() {

					@Override
					public void onSuccess(int arg0, Header[] arg1, String arg2) {
						if (arg2 != null && DataUtil.isOk(arg2)) {
							members.remove(member);
							DBHelper.getInstance().delete(GroupMembers.class,
									"groupid=? and memberid=?",
									member.getGroupId(), member.getMemberId());
							ToastUtil.show(MembersListActivity.this,
									member.getName() + "已经被踢出该圈了！",
									Gravity.CENTER);
							memberAdapter.notifyDataSetChanged();
							isDeleteMember = true;
						} else {
							ToastUtil.show(MembersListActivity.this, "踢出"
									+ member.getName() + "失败！", Gravity.CENTER);
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, String arg2,
										  Throwable arg3) {
						ToastUtil.show(MembersListActivity.this,
								"当前网络不给力，请稍后再试！", Gravity.CENTER);
					}
				});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
		FriendsInfoActivity.actionIntent(this, members.get(position)
				.getMemberId());
	}

	@Override
	public void onBackPressed() {
		exitActivity();
	}
}
