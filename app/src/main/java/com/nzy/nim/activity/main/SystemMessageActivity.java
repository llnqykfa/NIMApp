package com.nzy.nim.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.db.bean.SessionMsg;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.vo.MsgManager;
import com.nzy.nim.vo.QYApplication;

/**
 * @性质：系统消息处理界面： 包括好友请求、组圈邀请、座位转让
 * 
 */
public class SystemMessageActivity extends BaseActivity {
	private ImageView backBtn;
	private Button nextBtn;
	private TextView title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_message);
		QYApplication.ACTIVITY_NAME="系统消息";
		DBHelper.getInstance().clearUnReadTips(SessionMsg.class, null,
				MsgManager.FRIEND_REQUEST);
		nextBtn = (Button) findViewById(R.id.top_bar_next);
		title = (TextView) findViewById(R.id.top_bar_content);
		title.setText("系统消息");
		nextBtn.setVisibility(View.GONE);
		findViewById(R.id.top_back_bg).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	public static void actionIntent(Context context) {
		context.startActivity(new Intent(context, SystemMessageActivity.class));
	}
}
