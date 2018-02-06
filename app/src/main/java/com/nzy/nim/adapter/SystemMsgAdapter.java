package com.nzy.nim.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nzy.nim.R;
import com.nzy.nim.api.URLs;
import com.nzy.nim.db.bean.Contacts;
import com.nzy.nim.db.bean.Invitations;
import com.nzy.nim.db.bean.MyGroups;
import com.nzy.nim.db.bean.SessionMsg;
import com.nzy.nim.db.tmpbean.DBConversion;
import com.nzy.nim.http.HttpHelper;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.HttpUtil;
import com.nzy.nim.tool.common.ImageUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.view.ClickableTextview;
import com.nzy.nim.vo.MsgManager;
import com.nzy.nim.vo.OldPersonVO;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.vo.RingThemeVO;
import com.nzy.nim.vo.RingThemesTmp;

import org.apache.http.Header;
import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * x新朋友的适配器
 * 
 * @author(作者) LIUBO
 * @Date(日期) 2015-1-30 下午7:47:31
 * @classify(类别)
 * @TODO(功能) TODO
 * @Param(参数) @param <T>
 * @Remark(备注)
 * 
 */
public class SystemMsgAdapter extends CommonAdapter<Invitations> {
	private Context context;
	private List<Invitations> mDatas;

	public SystemMsgAdapter(Context context, List<Invitations> mDatas,
							int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
		this.context = context;
		this.mDatas = mDatas;
	}

	@Override
	public void convert(ViewHolder helper, final Invitations item) {
		ImageView image = helper.getView(R.id.newfriends_item_iv);
		TextView textName = helper.getView(R.id.newfriend_item_name);
		TextView tv_time = helper.getView(R.id.tv_time);
		ClickableTextview howToFind = helper.getView(R.id.newfriend_item_howToFind);
		final Button agreeBtn = helper.getView(R.id.newfriends_item_btn_agree);
		if (item instanceof Invitations) {
			final Invitations newFriends = (Invitations) item;
			ImageUtil.displayHeadImg(newFriends.getImgUrl(), image);
			textName.setText(newFriends.getSendUserName());
			howToFind.setText(newFriends.getLeaveComment());
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(newFriends.getReceMsgTime()!=null){
				String data = format.format(newFriends.getReceMsgTime());
				tv_time.setText(data);
			}else{
				tv_time.setVisibility(View.GONE);
			}
			final List<Contacts> contactses = DataSupport.where("userid=? and contactid=?", QYApplication.getPersonId(), newFriends.getSendUserId()).find(Contacts.class);
			if (newFriends.getStatus() == MsgManager.INVITE_AGREED) {
				agreeBtn.setVisibility(View.VISIBLE);
				agreeBtn.setText("已同意");
				agreeBtn.setBackgroundResource(R.drawable.shape_bg_true);
				agreeBtn.setTextColor(context.getResources().getColor(R.color.btn_b));
				agreeBtn.setEnabled(false);
			} else if (newFriends.getStatus() < 0) {
				agreeBtn.setVisibility(View.INVISIBLE);
			}else if(newFriends.getStatus()==MsgManager.INVITE_OTHER){
				agreeBtn.setVisibility(View.GONE);
				image.setImageResource(R.drawable.ic_applogo);
			} else {
				agreeBtn.setVisibility(View.VISIBLE);
				agreeBtn.setText("同意");
				agreeBtn.setEnabled(true);
				agreeBtn.setBackgroundResource(R.drawable.shape_bg);
				agreeBtn.setTextColor(context.getResources().getColor(R.color.white));
			}
			if(newFriends.getMsgType()==MsgManager.FRIEND_REQUEST){
				if(contactses.size()!=0){
					agreeBtn.setVisibility(View.VISIBLE);
					agreeBtn.setText("已同意");
					agreeBtn.setBackgroundResource(R.drawable.shape_bg_true);
					agreeBtn.setTextColor(context.getResources().getColor(R.color.btn_b));
					agreeBtn.setEnabled(false);
					ContentValues values = new ContentValues();
					values.put("status","1");
					DataSupport.updateAll(Invitations.class,values,"status=?",""+newFriends.getStatus());
				}
			}
			// 同意
			agreeBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					List<Contacts> contactses = DataSupport.where("userid=? and contactid=?",QYApplication.getPersonId(), newFriends.getSendUserId()).find(Contacts.class);
					if(newFriends.getMsgType()==MsgManager.FRIEND_REQUEST){
						if(contactses.size()==0){
							doAgree(item);
						}else {
							ContentValues values = new ContentValues();
							values.put("status","1");
							DataSupport.updateAll(Invitations.class,values,"status=?",""+newFriends.getStatus());
						}
					}else{
						doAgree(item);
					}
					agreeBtn.setText("已同意");
					agreeBtn.setEnabled(false);
					agreeBtn.setBackgroundResource(R.drawable.shape_bg_true);
					agreeBtn.setTextColor(context.getResources().getColor(R.color.btn_b));
				}
			});
		}

	}

	/**
	 * 
	 * @Author LIUBO
	 * @TODO TODO 同意好友请求的处理
	 * @param item
	 * @Date 2015-1-31
	 * @Return void
	 */
	private void doAgree(final Invitations item) {
		int type = item.getMsgType();
		// 判断好友请求是否是圈请求
		if (type == MsgManager.RING_ADD_REQUEST) {
			acceptRequestRing(item);
		} else if (type == MsgManager.FRIEND_REQUEST) {
			// 向对方发送同意请求
			agreeBecomeFriend(item);
		} else if (type == MsgManager.RING_BE_INVITED) {
			agreeJoinRing("true", item);

		}
	}



	/**
	 * 同意好友请求后刷新消息中心和新朋友状态
	 * 
	 */
	private void updateMessage(Invitations item) {
		if (!DBHelper.getInstance().isExist(
				SessionMsg.class,
				DBHelper.getInstance().createUSM(item.getSendUserId(),
						item.getMsgType()))) {
			if (item.getMsgType() == MsgManager.FRIEND_BE_AGREED)// 好友请求消息被同意
				updateSessionContact(item);
		}
		if (item.getMsgType() == MsgManager.RING_BE_INVITED)
			updateSessionGroup(item);
	}

	// 将聊天对象加到消息中心列表中(圈)
	private void updateSessionGroup(Invitations item) {
		SessionMsg msg = new SessionMsg();
		msg.setCreatTime(new Date());
		msg.setNewContent("你已成为该圈的成员，快去看看吧!");
		msg.setTitles(item.getGroupName());
		msg.setImgUrl("");
		msg.setGroup(true);
		msg.setSendUserId(item.getGroupId());
		msg.setSubType(MsgManager.CHAT_WITH_FRIEND);
		msg.setMainType(MsgManager.CHAT_TYPE);
		msg.setUserId(QYApplication.getPersonId());
		msg.saveThrows();
	}

	// 将聊天对象加到消息中心列表中(好友)
	private void updateSessionContact(Invitations item) {
		SessionMsg msg = new SessionMsg();
		msg.setCreatTime(new Date());
		msg.setNewContent("你们已经是好友了，快快聊天吧！！！");
		msg.setTitles(item.getSendUserName());
		msg.setImgUrl(item.getImgUrl());
		msg.setMainType(MsgManager.CHAT_TYPE);
		msg.setSubType(MsgManager.CHAT_WITH_FRIEND);
		msg.setSendUserId(item.getSendUserId());
		msg.setUserId(QYApplication.getPersonId());
		msg.setGroup(false);
		// 保存到数据库中
		msg.saveThrows();
	}

	/**
	 * 更新新朋友表中数据的状态
	 * 
	 * @param msg
	 */
	private void updateSystemMsg(final Invitations msg) {
		// 更新添加好友请求的状态
		ContentValues values = new ContentValues();
		values.put("status", 1);
		DataSupport.updateAll(Invitations.class, values, "msgid=?",
				msg.getMsgId());
		mDatas.clear();
		List<Invitations> invitesOrderByTime = DBHelper.getInstance().getInvitesOrderByTime();
		for (int i = 0; i <invitesOrderByTime.size() ; i++) {
			Invitations invitations = invitesOrderByTime.get(i);
			if(invitations.getStatus()!= MsgManager.INVITE_OTHER){
				mDatas.add(invitations);
			}
		}
		notifyDataSetChanged();
	}

	/**
	 * @author LIUBO
	 * @date 2015-4-10上午11:05:33
	 * @TODO 保存新加的联系人
	 */
	private void saveContact(final String personId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpHelper.getPerson(personId, new HttpUtil.OnPostListener() {
					@Override
					public void onSuccess(String jsonData) {
						if (!DataUtil.isEmpty(jsonData))// 保存联系人
							DBConversion
									.getInstance()
									.getContact(
											JSON.parseObject(jsonData,
													OldPersonVO.class))
									.saveThrows();
						//发送广播更新好友列表
						Intent intent = new Intent();
						intent.setAction("update_contacts_List");
						context.sendBroadcast(intent);
					}

					@Override
					public void onFailure() {

					}
				});
			}

		}).start();
	}

	/**
	 * @author LIUBO
	 * @date 2015-4-10上午11:23:10
	 * @TODO 保存我的组圈信息
	 * @param groupId
	 */
	private void saveMyGroup(final String groupId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (!DBHelper.getInstance().isExist(MyGroups.class,
						"groupid=?", groupId)) {
					RingThemesTmp groupTmp = DBHelper.getInstance().find(
							RingThemesTmp.class, "groupid=?", groupId);
					if (groupTmp == null) {
						HttpHelper.getRingTheme(groupId, new HttpUtil.OnPostListener() {
							@Override
							public void onSuccess(String jsonData) {
								if (!DataUtil.isEmpty(jsonData)) {
									DBConversion
											.getInstance()
											.getGroup(
													JSON.parseObject(jsonData,
															RingThemeVO.class))
											.saveThrows();
								}
							}

							@Override
							public void onFailure() {
							}
						});
					} else {
						DBConversion.getInstance().getGroup(groupTmp)
								.saveThrows();
					}
				}
			}

		}).start();

	}

	/**
	 * @author LIUBO
	 * @date 2015-4-10上午10:56:28
	 * @TODO 同意组圈邀请
	 * @param flag
	 */
	private void agreeJoinRing(String flag, final Invitations item) {
		RequestParams params = new RequestParams();
		params.add("ringId", item.getGroupId());
		params.add("personId", QYApplication.getPersonId());
		params.add("flag", flag);
		HttpUtil.post(URLs.USER_AOR_ADDRING, params,
				new TextHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, Header[] arg1, String arg2) {
						updateSystemMsg(item);
						updateMessage(item);
						saveMyGroup(item.getGroupId());
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, String arg2,
							Throwable arg3) {
						error();
					}
				});
	}

	/**
	 * @Author LIUBO
	 * @TODO TODO 接受好友添加请求
	 * @Return void
	 */
	private void agreeBecomeFriend(final Invitations item) {
		RequestParams params = new RequestParams();
		params.add("personId", QYApplication.getPersonId());
		params.add("fromPersonId", item.getSendUserId());
		HttpUtil.post(URLs.ACCEPT_FRIEND, params,
				new TextHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, Header[] arg1, String arg2) {
						// 更新邀请表的状态
						updateSystemMsg(item);
						// 保存联系人
						saveContact(item.getSendUserId());
						// 更新消息中心的数据表
						updateMessage(item);
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, String arg2,
							Throwable arg3) {
						error();
					}
				});
	}

	/**
	 * @author LIUBO
	 * @date 2015-4-10上午10:39:30
	 * @TODO 接受组圈请求
	 */
	private void acceptRequestRing(final Invitations item) {
		RequestParams params = new RequestParams();
		params.add("requestId", item.getSendUserId());
		params.add("groupId", item.getGroupId());
		params.add("personId", QYApplication.getPersonId());
		HttpUtil.post(URLs.ACCEPT_RING_REQUEST, params,
				new TextHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, Header[] arg1, String arg2) {
						// 更邀请通知表
						updateSystemMsg(item);
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, String arg2,
							Throwable arg3) {
						error();
					}
				});

	}

	/**
	 * @author LIUBO
	 * @date 2015-4-10上午11:08:49
	 * @TODO 服务请求执行失败
	 */
	private void error() {
		notifyDataSetChanged();
		ToastUtil.show(context, "网络连接异常,请检查网络!", Gravity.TOP);
	}

	/**
	 * @author LIUBO
	 * @date 2015-4-10上午10:38:58
	 * @TODO 拒绝组圈请求
	 * @param requestId
	 * @param groupId
	 */
	private void rejectRequestRing(String requestId, String groupId) {
		RequestParams params = new RequestParams();
		params.add("requestId", requestId);
		params.add("groupId", groupId);
		HttpUtil.post(URLs.REJECT_RING_REQUEST, params,
				new TextHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, Header[] arg1, String arg2) {

					}

					@Override
					public void onFailure(int arg0, Header[] arg1, String arg2,
							Throwable arg3) {
						error();
					}
				});
	}

}
