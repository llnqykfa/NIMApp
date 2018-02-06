package com.nzy.nim.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.tool.common.ToastUtil;


public class MakeRingFirstActivity extends BaseActivity {
	private EditText editText;
	public static MakeRingFirstActivity instance;
	private ScrollView scroll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_make_ring_first);
		instance = this;
		initTopBar();
		editText = (EditText) findViewById(R.id.make_ring_first_et);
		scroll = (ScrollView) findViewById(R.id.make_ring_first_scroll);
		editText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						scroll.fullScroll(ScrollView.FOCUS_DOWN);
					}
				}, 200);
			}
		});
	}

	private void initTopBar() {
		TextView title = (TextView) findViewById(R.id.top_bar_content);
		Button next = (Button) findViewById(R.id.top_bar_next);
		title.setText(R.string.input_ring_name);

		next.setText("下一步");
		findViewById(R.id.top_back_bg).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(editText.getText()))
					ToastUtil.showShort(MakeRingFirstActivity.this, "圈名不能为空！");
				else if (editText.length() > 15) {
					ToastUtil.showShort(MakeRingFirstActivity.this,
							"圈名不能超过15个字！");
				} else {
					MakeRingSecondActivity.actionIntent(
							MakeRingFirstActivity.this, editText.getText()
									.toString());
				}

			}
		});
	}

	public static void actionIntent(Context context) {
		context.startActivity(new Intent(context, MakeRingFirstActivity.class));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		instance = null;
	}
}
