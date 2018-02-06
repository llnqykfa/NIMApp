package com.nzy.nim.activity.main;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.db.bean.SessionMsg;
import com.nzy.nim.fragment.InterestFragment;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.vo.MsgManager;
import com.nzy.nim.vo.QYApplication;

import java.util.Date;

/**
 * @author LIUBO
 * @date 2015-4-14下午3:37:55
 * @TODO 兴趣中心界面
 */
public class InterestationsCenterActivity extends BaseActivity {
	private FragmentManager frManager;
	private InterestFragment fragment;
	private boolean isFromMsgCenter = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_head_fragment);
		QYApplication.ACTIVITY_NAME="兴趣";
		isFromMsgCenter = getIntent().getBooleanExtra("isFromMsgCenter", false);
		if (isFromMsgCenter) {// 点击兴趣中心，更新时间
			ContentValues values = new ContentValues();
			values.put("creattime", new Date().getTime());
			DBHelper.getInstance().update(SessionMsg.class, values,
					"userid=? and maintype=?", QYApplication.getPersonId(),
					MsgManager.INTEREST_TYPE + "");
		}
		frManager = getSupportFragmentManager();
		FragmentTransaction transaction = frManager.beginTransaction();
		if (fragment == null) {
			fragment = new InterestFragment();
			Bundle bundle = new Bundle();
			bundle.putBoolean("isFromMsgCenter_flag", isFromMsgCenter);
			fragment.setArguments(bundle);
			transaction.add(R.id.activity_head_fragment_content, fragment);
		} else {
			transaction.show(fragment);
		}
		transaction.commit();
	}

	public static void actionIntent(Context context, boolean isFromMsgCenter) {
		Intent intent = new Intent(context, InterestationsCenterActivity.class);
		intent.putExtra("isFromMsgCenter", isFromMsgCenter);
		context.startActivity(intent);
	}
}
