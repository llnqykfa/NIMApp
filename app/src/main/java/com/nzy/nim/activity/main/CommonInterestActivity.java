package com.nzy.nim.activity.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
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
import com.nzy.nim.db.InterestPeopleList;
import com.nzy.nim.helper.DialogHelper;
import com.nzy.nim.pulltorefresh.PullToRefreshBase;
import com.nzy.nim.pulltorefresh.PullToRefreshListView;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.HttpUtil;
import com.nzy.nim.tool.common.ImageUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.view.RoundImageView;
import com.nzy.nim.vo.QYApplication;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LIUBO
 * @date 2015-4-13下午10:52:13
 * @TODO 共同兴趣的人
 */
public class CommonInterestActivity extends BaseActivity implements
		PullToRefreshBase.OnRefreshListener<ListView> {
	public static final String TAG = CommonInterestActivity.class.getName();
	private String titleContent;
	private String hobbyId;
	private List<InterestPeopleList> sourceDatas = new ArrayList<InterestPeopleList>();
	private ListView listView;
	private PullToRefreshListView pullRefreshListView;
	private TextView emptyTv;
	private CommonAdapter<InterestPeopleList> adapter;
	private ProgressDialog pgDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_layout_refresh_listview);
		titleContent = getIntent().getStringExtra("title");
		hobbyId = getIntent().getStringExtra("hobbyId");
		pgDialog = DialogHelper.getSDialog(CommonInterestActivity.this, "搜索中···", false);
		initTopBar();
		initView();
		initAdapter();
		getCommonInterestPersons(hobbyId);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				FriendsInfoActivity.actionIntent(CommonInterestActivity.this,
						adapter.getItem(position).getPk_person());
			}
		});
	}

	private void initView() {
		emptyTv = (TextView) findViewById(R.id.common_layout_refresh_listview_empty);
		pullRefreshListView = (PullToRefreshListView) findViewById(R.id.refresh_listview);
		listView = pullRefreshListView.getRefreshableView();
		pullRefreshListView.setPullRefreshEnabled(false);
		// QYApplication.initPullRefulsh(pullRefreshListView, this, false,
		// false,
		// TAG);
	}

	private void initTopBar() {
		findViewById(R.id.top_back_bg).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		TextView title = (TextView) findViewById(R.id.top_bar_content);
		Button next = (Button) findViewById(R.id.top_bar_next);
		title.setText(titleContent);
		next.setText("换一批");
		next.setVisibility(View.GONE);
		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getCommonInterestPersons(hobbyId);
			}
		});
	}

	private void initAdapter() {
		adapter = new CommonAdapter<InterestPeopleList>(this, sourceDatas,
				R.layout.activity_childitem_list) {
			@Override
			public void convert(ViewHolder helper, InterestPeopleList item) {
				RoundImageView header = helper.getView(R.id.child_image);
				if (item.getPhotopath() != null)
					ImageUtil.displayHeadImg(item.getPhotopath(), header);
				else
					header.setImageResource(R.drawable.pic_default_head);
				helper.setText(R.id.child_name, item.getUsername());
				helper.setText(R.id.child_sign, item.getDesigninfo());
			}
		};
	}

	/**
	 * @author LIUBO
	 * @date 2015-4-13下午9:24:30
	 * @TODO 获取同兴趣的人
	 */
	private void getCommonInterestPersons(String hobbyId) {
		pgDialog.show();
		RequestParams params = new RequestParams();
		params.add("hobbyId", hobbyId);
		params.add("personId", QYApplication.getPersonId());
		HttpUtil.post(URLs.LIST_RADOM_HOBBY_PERSON, params,
				new TextHttpResponseHandler() {

					@Override
					public void onSuccess(int arg0, Header[] arg1, String arg2) {
						if (arg2 != null) {
							List<InterestPeopleList> people = JSON.parseArray(arg2,
									InterestPeopleList.class);
							sourceDatas.clear();
							if(DataUtil.isEmpty(people)){
								emptyTv.setVisibility(View.VISIBLE);
								return;
							}
							if (people.size() != 0) {
								emptyTv.setVisibility(View.GONE);
								sourceDatas.addAll(people);
								adapter.notifyDataSetChanged();
							} else {
								emptyTv.setVisibility(View.VISIBLE);
							}
							// QYApplication.refulshComplete(pullRefreshListView,
							// TAG);
							pgDialog.dismiss();
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, String arg2,
							Throwable arg3) {
						pgDialog.dismiss();
						ToastUtil.show(CommonInterestActivity.this,
								"网络异常,请检查网络!", Gravity.TOP);
					}
				});
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		getCommonInterestPersons(hobbyId);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

	}

	public static void actionIntent(Context context, String title,
			String hobbyId) {
		Intent intent = new Intent(context, CommonInterestActivity.class);
		intent.putExtra("title", title);
		intent.putExtra("hobbyId", hobbyId);
		context.startActivity(intent);
	}

}
