package com.nzy.nim.activity.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.nzy.nim.R;
import com.nzy.nim.tool.common.ImageUtil;
import com.nzy.nim.view.RoundImageView;

public class AlertDialogActivity extends BaseActivity {
	public static final int FORWARD_FLAG = 0;// 转发对话框

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int flag = getIntent().getIntExtra("dialog_flag", -1);
		if (flag == FORWARD_FLAG) {// 转发对话框
			setContentView(R.layout.activity_forward_dialog);
			initForwardDialogView();
		}
	}

	/**
	 * 初始化转发对话框
	 */
	private void initForwardDialogView() {
		String imgUrl = getIntent().getStringExtra("headImg");
		String nick = getIntent().getStringExtra("nick");
		boolean isGroup = getIntent().getBooleanExtra("isGroup", false);
		RoundImageView head = (RoundImageView) findViewById(R.id.forward_dialog_headImg);
		TextView nickTv = (TextView) findViewById(R.id.forward_dialog_nick);

		ImageUtil.displayHeadImg(imgUrl, head);
		nickTv.setText(nick);
		Button cancle = (Button) findViewById(R.id.forward_dialog_cancle);
		Button ring = (Button) findViewById(R.id.forward_dialog_Ring);
		Button send = (Button) findViewById(R.id.forward_dialog_send);
		cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_OK, new Intent().putExtra("send_flag", false));
				finish();
			}
		});
		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mIntent = new Intent("com.app.quanyou.ForwardMessageActivity");
				//发送广播
				sendBroadcast(mIntent);
				finish();
			}
		});
		if (isGroup){
			ring.setVisibility(View.VISIBLE);
		}
		ring.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mIntent = new Intent("com.app.quanyou.ForwardMessageActivity");
				//发送广播
				mIntent.putExtra("isRingDynamic",true);
				sendBroadcast(mIntent);
				finish();
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		setResult(RESULT_OK);
		finish();
		return true;
	}

	/**
	 * 从转发页面跳转过来
	 * 
	 * @param activity
	 * @param dialogFlag
	 * @param imgUrl
	 * @param nick
	 * @param requestCode
	 */
	public static void actionForwardIntent(Activity activity, int dialogFlag,
			String imgUrl, String nick, int requestCode) {
		Intent intent = new Intent(activity, AlertDialogActivity.class);
		intent.putExtra("headImg", imgUrl);
		intent.putExtra("nick", nick);
		intent.putExtra("dialog_flag", dialogFlag);
		activity.startActivityForResult(intent, requestCode);
	}
	/**
	 * 从转发页面跳转过来
	 *
	 * @param activity
	 * @param dialogFlag
	 * @param imgUrl
	 * @param nick
	 * @param requestCode
	 */
	public static void actionForwardIntent(Activity activity, int dialogFlag,
			String imgUrl, String nick, int requestCode,boolean isGroup) {
		Intent intent = new Intent(activity, AlertDialogActivity.class);
		intent.putExtra("headImg", imgUrl);
		intent.putExtra("nick", nick);
		intent.putExtra("dialog_flag", dialogFlag);
		intent.putExtra("isGroup", isGroup);
		activity.startActivityForResult(intent, requestCode);
	}
}
