package com.nzy.nim.activity.login;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.activity.main.MainActivity;
import com.nzy.nim.api.URLs;
import com.nzy.nim.db.bean.Contacts;
import com.nzy.nim.db.bean.Users;
import com.nzy.nim.db.tmpbean.DBConversion;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.vo.PersonVO;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.vo.UserInfo;
import com.nzy.nim.volley.HTTPUtils;
import com.nzy.nim.volley.VolleyListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.List;

/**
 * 
 * @classify(类别) 第一次登陆和数据更新的过渡界面
 * @TODO(功能) TODO 初始化本地数据库
 * @Param(参数)
 * @Remark(备注)
 * 
 */
public class TransitionActivity extends BaseActivity {
	/**
	 * 将服务器上的数据转换到本地
	 */
	private String phone;
	private String personId;
	private boolean mUserInfoFlag=false;
	private UserInfo user;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transition);
		QYApplication.ACTIVITY_NAME="加载数据页";
		 DBConversion.getInstance();
		Intent intent = getIntent();
		phone = intent.getStringExtra("phone");
		personId = intent.getStringExtra("personId");
		initDatas();
	}

	private void initDatas() {
		// 初始化社版中心的数据
		getUserInfoNew(TransitionActivity.this, phone,personId);
		// 监控数据是否加载完成
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				new MyAsyncTask().execute();
			}
		},1000);
	}
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					break;
			}
			super.handleMessage(msg);
		}
	};

	//获取用户信息
	private void getUserInfoNew(final Context context, final String phone, final String personId) {
		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("targetPersonId", personId);
		HTTPUtils.postWithToken(context, URLs.GET_PERSON_INFO, map, new VolleyListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				LoginActivity.pgDialog.dismiss();
				ToastUtil.showShort(context, "服务器繁忙，请重试！");
			}

			@Override
			public void onResponse(String s) {
				try {
					UserInfo info = DBHelper.getInstance().find(UserInfo.class, "personId=?", personId);
					if (info != null) {
						info.delete();
					}
					UserInfo userInfo = new Gson().fromJson(new JSONObject(s).getString("person"), UserInfo.class);
					userInfo.setPhone(phone);
					userInfo.saveThrows();
					getAllMyFriends(context);//保存联系人
					mUserInfoFlag=true;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * 获取联系人信息
	 * @param context
     */
	public void getAllMyFriends(final Context context) {
		HTTPUtils.postWithToken(context, URLs.GET_FRIENDS_INFO, null, new VolleyListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {

			}

			@Override
			public void onResponse(String s) {
				if (!DataUtil.isEmpty(s)) {
					try {
						List<PersonVO> datas = JSON.parseArray(new JSONObject(s).getString("friends"), PersonVO.class);
						// 删除本地
						DataSupport.deleteAll(Contacts.class, "userid=?",
								QYApplication.getPersonId());
						// 保存服务器上的最新联系人
						List<Contacts> tmpList;
						if (!DataUtil.isEmpty(datas)) {
							tmpList = DBConversion.getInstance().getContacts(datas);
							DataSupport.saveAll(tmpList);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}


	/**
	 * 
	 * @classify(类别) 内部异步类
	 * @TODO(功能) TODO 处理一些耗时的数据加载操作
	 * @Param(参数)
	 * @Remark(备注)
	 * 
	 */
	private class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			while (true) {
				// 判断是否加载完成
				if (mUserInfoFlag) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
						errorInit();
					}
					break;
				}else{
					errorInit();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// 进行数据加载完成后的UI操作
			MainActivity.actionIntent(TransitionActivity.this);
			TransitionActivity.this.finish();
		}
	}

	/**
	 * @TODO TODO 从其他界面跳转到过渡界面
	 * @param context
	 *            上下文环境
	 *            登录名
	 * @Return void
	 */
	public static void actionIntent(Context context, String phone, String personId) {
		Intent intent = new Intent(context, TransitionActivity.class);
		intent.putExtra("phone", phone);
		intent.putExtra("personId", personId);
		context.startActivity(intent);
	}

	/**
	 * 
	 * @TODO TODO 初始化数据失败处理
	 * @Return void
	 */
	private void errorInit() {
		// 初始化数据失败恢复用户表数据
		DBHelper.getInstance().delete(Users.class, "userid=?",
				QYApplication.getPersonId());
//		ToastUtil.showShort(QYApplication.getContext(), "初始化数据失败，请重新登陆！！！");
		Intent intent = new Intent(TransitionActivity.this, LoginActivity.class);
		this.startActivity(intent);
		this.finish();
	}

}
