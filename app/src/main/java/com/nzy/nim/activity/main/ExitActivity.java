package com.nzy.nim.activity.main;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.manager.AppManager;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.vo.PersonSession;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.vo.UserInfo;

import org.litepal.crud.DataSupport;

/**
 * 
 * 名称：退出界面 性质： 参数：
 * 
 * @author 刘波 日期：2014-12-20 TODO 退出当前系统 备注：
 * 
 */
public class ExitActivity extends BaseActivity {
	private LinearLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exit);
		QYApplication.ACTIVITY_NAME="退出";
		layout = (LinearLayout) findViewById(R.id.exit_layout);
		layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	public void exitbutton1(View v) {
		this.finish();
	}

	public void exitbutton0(View v) {
		this.finish();
		//删除当前用户Token
		PersonSession oldPersonSession = DBHelper.getInstance().
				find(PersonSession.class, "personId=?", QYApplication.getPersonId());
		if (oldPersonSession!=null) {
			oldPersonSession.delete();
		}
		//删除当前用户信息表
		DataSupport.deleteAll(UserInfo.class);
//		MainActivity.customClient.setStop(true);
//		SPHelper.saveExitFlag(true);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				AppManager.getInstance().exit();
			}
		}, 1000l);
//		SPUtils.clear(this);
	}
}
