package com.nzy.nim.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.api.URLs;
import com.nzy.nim.db.bean.Contacts;
import com.nzy.nim.helper.DialogHelper;
import com.nzy.nim.tool.common.CommonUtil;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.HttpUtil;
import com.nzy.nim.tool.common.ImageUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.view.RoundImageView;
import com.nzy.nim.vo.OldPersonVO;
import com.nzy.nim.vo.QYApplication;

import org.apache.http.Header;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class PersonVerificationInfo extends BaseActivity implements
		OnClickListener {

	private RoundImageView m_image;
	private TextView m_nickName;
	private TextView m_sex;
	private EditText m_write;
	private ImageView m_previous;
	private Button m_next;
	private TextView mTitle;
	private OldPersonVO m_toPerson;
	private List<Contacts> sourceDatas = new ArrayList<Contacts>();// 好友列表数据源
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_verification);
		findViewById();
		getPerson(getIntent().getStringExtra("personId"));
	}

	protected void findViewById() {
		// 关联组件
		m_image = (RoundImageView) findViewById(R.id.person_verification_image);
		m_nickName = (TextView) findViewById(R.id.person_verificationInfo_nickName);
		m_sex = (TextView) findViewById(R.id.person_verification_sex);
		m_write = (EditText) findViewById(R.id.person_verification_writeInfo);
		m_previous = (ImageView) findViewById(R.id.top_bar_back);
		m_next = (Button) findViewById(R.id.top_bar_next);
		mTitle = (TextView) findViewById(R.id.top_bar_content);

	}

	/**
	 * @author LIUBO
	 * @date 2015-4-5下午8:04:10
	 * @TODO 获取用户信息
	 * @param personId
	 */
	private void getPerson(final String personId) {
		RequestParams param = new RequestParams();
		param.add("personId", personId);
		HttpUtil.post(URLs.GET_PERSON, param, new TextHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] arg1, String arg2) {
				if (arg2 != null) {
					m_toPerson = JSON.parseObject(arg2, OldPersonVO.class);
					if (personId.equals(QYApplication.getPersonId())) {
						m_next.setVisibility(View.GONE);
					}
					initDatas();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, String arg2,
								  Throwable arg3) {
				ToastUtil.show(PersonVerificationInfo.this, "刷新失败！",
						Gravity.TOP);
			}
		});
	}

	protected void initDatas() {
		mTitle.setText("好友验证");
		m_next.setText("发送");
		m_nickName.setText(m_toPerson.getUsername());
		if (m_toPerson.getSex()==null || m_toPerson.getSex())
			m_sex.setText("男");
		else
			m_sex.setText("女");
		ImageUtil.displayHeadImg(m_toPerson.getPhotopath(),m_image);
		m_previous.setOnClickListener(this);
		m_next.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 上一步
		case R.id.top_bar_back:
			this.finish();
			break;
		// 发送
		case R.id.top_bar_next:
			// 点击发送隐藏软键盘
			CommonUtil.hideKeyboard(PersonVerificationInfo.this, v);
			if (DataUtil.isEmpty(m_write.getText()))
				m_write.setText("null");
			// 向服务器发送添加好友请求
			// 初始化列表中的数据
			List<Contacts> temp = DataSupport.where("userid = ?",
					QYApplication.getPersonId()).find(Contacts.class);
			sourceDatas.clear();
			sourceDatas.addAll(temp);
			boolean isFriend=false;
			for (int i = 0; i < sourceDatas.size(); i++) {
				if (m_toPerson.getPk_person().equals(sourceDatas.get(i).getContactId())){
					isFriend=true;
				}
			}
			if (isFriend) {
				DialogHelper.showMsgDialog(
						PersonVerificationInfo.this,
						DataUtil.dealMessage("该用户已经是您的好友！"));
			}else{
				addFriend(QYApplication.getPersonId(), m_toPerson.getPk_person(),
						m_write.getText().toString());
			}
			break;
		}
	}

	/**
	 * @author 刘波 TODO 把该座位的同学加为好友
	 * @param personId
	 * @param toPersonId
	 *            Return:
	 */
	private void addFriend(String personId, String toPersonId, String content) {
		RequestParams param = new RequestParams();
		param.add("personId", personId);
		param.add("toPersonId", toPersonId);
		param.add("content", content);
		HttpUtil.post(URLs.REQUEST_FRIEND, param,
				new TextHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, Header[] arg1, String arg2) {
						// 加为好友请求发送成功
						if (!DataUtil.isEmpty(arg2) && DataUtil.isOk(arg2)) {
							DialogHelper.showMsgDialog(
									PersonVerificationInfo.this, "好友请求发送成功！！");
						} else {
							DialogHelper.showMsgDialog(
									PersonVerificationInfo.this,
									DataUtil.dealMessage(arg2));
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, String arg2,
										  Throwable arg3) {
						// 加为好友请求发送失败
						DialogHelper.showMsgDialog(PersonVerificationInfo.this,
								R.string.notify_net_msg);
					}
				});
	}

	public static void actionIntent(Context context, String personId) {
		Intent intent = new Intent(context, PersonVerificationInfo.class);
		intent.putExtra("personId", personId);
		context.startActivity(intent);
	}
}
