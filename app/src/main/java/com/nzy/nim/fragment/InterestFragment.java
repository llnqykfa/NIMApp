package com.nzy.nim.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nzy.nim.R;
import com.nzy.nim.activity.main.CommonInterestActivity;
import com.nzy.nim.adapter.CommonAdapter;
import com.nzy.nim.adapter.ViewHolder;
import com.nzy.nim.api.URLs;
import com.nzy.nim.db.bean.Interests;
import com.nzy.nim.db.tmpbean.DBConversion;
import com.nzy.nim.helper.DialogHelper;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.tool.common.HttpUtil;
import com.nzy.nim.tool.common.ImageUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.view.RoundImageView;
import com.nzy.nim.vo.HobbyVO;
import com.nzy.nim.vo.MyInterests;
import com.nzy.nim.vo.QYApplication;

import org.apache.http.Header;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class InterestFragment extends Fragment {
	private final int REFLUSH = 0;
	private final int ANIMATION_DEFAULT = 0;
	private final int ANIMATION_SHOW = 1;
	private final int ANIMATION_HIDE = 2;
	private ListView listView;
	private ProgressDialog pgDialog;
	private List<Interests> interstations = new ArrayList<Interests>();// 兴趣集合
	private int myInterestCount = 0;// 我的兴趣的计数
	// private boolean isStartAnimation = false;
	private boolean isShowDirectionFlag = true;
	private int showEditFlag = ANIMATION_DEFAULT;
	private boolean isFromMsgCenter = false;// 是否来自消息中心

	private CommonAdapter<Interests> adapter;
	private List<MyInterests> myInterests = new ArrayList<MyInterests>();// 个人兴趣集合
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFLUSH:
				adapter.notifyDataSetChanged();
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.group_members_list, container,
				false);
		Bundle bundle = getArguments();
		isFromMsgCenter = bundle.getBoolean("isFromMsgCenter_flag", false);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initTopBar(getView());
		initView(getView());
		lazyLoad();
	}

	/**
	 * @author LIUBO
	 * @date 2015-4-15上午11:57:26
	 * @TODO 初始化title
	 */
	private void initTopBar(View view) {
		final View back = view.findViewById(R.id.top_back_bg);
		TextView title = (TextView) view.findViewById(R.id.top_bar_content);
		final Button next = (Button) view.findViewById(R.id.top_bar_next);
		if (isFromMsgCenter) {
			title.setText("兴趣中心");
			next.setText("添加");
			next.setVisibility(View.VISIBLE);
		} else {
			title.setText("添加兴趣");
			next.setVisibility(View.GONE);
		}
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// isStartAnimation = true;
				if (next.getText().toString().equals("添加")) {
					next.setText("完成");
					showEditFlag = ANIMATION_SHOW;
					isShowDirectionFlag = false;
					back.setVisibility(View.INVISIBLE);
					handler.sendEmptyMessage(REFLUSH);
				} else {
					next.setText("添加");
					isShowDirectionFlag = true;
					back.setVisibility(View.VISIBLE);
					showEditFlag = ANIMATION_HIDE;
					handler.sendEmptyMessage(REFLUSH);
				}

			}
		});
	}

	/**
	 * @author LIUBO
	 * @date 2015-4-13下午3:21:58
	 * @TODO 初始布局中的控件
	 * @param view
	 */
	private void initView(View view) {
		listView = (ListView) view.findViewById(R.id.group_member_list_lv);
		listView.setSelector(R.drawable.listview_selector_bg_1);
		pgDialog = DialogHelper.getSDialog(getActivity(), "加载中···", false);
		initAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position < myInterestCount) {
					Interests it = adapter.getItem(position);
					CommonInterestActivity.actionIntent(getActivity(),
							it.getName(), it.getInterestId());
				}
			}
		});
	}

	/**
	 * @author LIUBO
	 * @date 2015-4-13下午4:17:10
	 * @TODO 初始化adapter
	 */
	private void initAdapter() {
		adapter = new CommonAdapter<Interests>(getActivity(), interstations,
				R.layout.activity_childitem_list) {
			@Override
			public void convert(final ViewHolder helper, final Interests item) {
				LinearLayout baseContainer = helper
						.getView(R.id.childitem_list_base_layout);// 动画容器布局
				TextView header = helper.getView(R.id.childitem_header);
				// header.setTextSize(DimensionUtil.spToPx(getActivity(), 8));
				final ImageView direction = helper
						.getView(R.id.child_item_to_flag);// 引导标识
				RoundImageView icon = helper.getView(R.id.child_image);// 头像
				final ImageView addOrSub_header = helper
						.getView(R.id.child_left_edit_bar);// 添加标识——头
				ImageView addOrSub_tail = helper
						.getView(R.id.child_select_flag);// 添加标示-尾部
				TextView tv = helper.getView(R.id.child_sign);// 无用的控件隐藏
				tv.setVisibility(View.GONE);

				// header显示的控制
				header.setTextColor(Color.GRAY);
				if (helper.getPosition() == 0) {
					header.setVisibility(View.VISIBLE);
					header.setText("我的兴趣");
				} else if (helper.getPosition() > 0) {
					if (helper.getPosition() == myInterestCount
							|| (myInterestCount == 0 && helper.getPosition() == 1)) {
						header.setVisibility(View.VISIBLE);
						header.setText("未添加兴趣");
					} else {
						header.setVisibility(View.GONE);
					}
				}
				// 用户没有兴趣时的提示
				if (myInterestCount == 0 && helper.getPosition() == 0) {
					// addOrSub_header.setVisibility(View.GONE);
					// addOrSub_tail.setVisibility(View.GONE);
					icon.setImageResource(R.drawable.ic_no_fun_tip);
					helper.setText(R.id.child_name, "快来添加兴趣,寻找同兴趣的圈友吧!");
				} else {
					ImageUtil.displayHeadImg(item.getIconUrl(), icon);
					helper.setText(R.id.child_name, item.getName());
				}

				if (isFromMsgCenter) {// 判断是否从社版跳转过来
					if (showEditFlag == ANIMATION_DEFAULT) {
						addOrSub_header.setVisibility(View.GONE);
						addOrSub_tail.setVisibility(View.GONE);
						setHeadEditBtn(helper.getPosition(), direction,
								addOrSub_header);

					} else if (showEditFlag == ANIMATION_SHOW) {
						addOrSub_header.setVisibility(View.VISIBLE);
						setHeadEditBtn(helper.getPosition(), direction,
								addOrSub_header);
						// showAnimation(baseContainer, direction,
						// addOrSub_header);
						// isStartAnimation = false;
					} else if (showEditFlag == ANIMATION_HIDE) {
						addOrSub_header.setVisibility(View.GONE);
						setHeadEditBtn(helper.getPosition(), direction,
								addOrSub_header);
						// hideAnimation(baseContainer, direction,
						// addOrSub_header);
						// isStartAnimation = false;
					}
				} else {
					addOrSub_header.setVisibility(View.GONE);
					addOrSub_tail.setVisibility(View.VISIBLE);
					setTailBtn(helper.getPosition(), addOrSub_tail, direction);
				}
				setOnClickListener(helper, item, addOrSub_header, addOrSub_tail);
			}

		};
	}

	/**
	 * 设置尾部编辑按钮
	 * 
	 * @param position
	 * @param addOrSub_tail
	 */
	private void setTailBtn(int position, ImageView addOrSub_tail,
			ImageView direction) {
		if (myInterestCount == 0 && position == 0) {
			addOrSub_tail.setVisibility(View.GONE);
		} else {
			if (position >= myInterestCount) {
				addOrSub_tail.setImageResource(R.drawable.ic_add);
				direction.setVisibility(View.GONE);
			} else {
				if (isShowDirectionFlag)
					direction.setVisibility(View.VISIBLE);
				addOrSub_tail.setImageResource(R.drawable.ic_sub);
			}
		}
	}

	/**
	 * 设置头部编辑按钮
	 * 
	 * @param position
	 * @param addOrSub_header
	 */
	private void setHeadEditBtn(int position, final ImageView direction,
			final ImageView addOrSub_header) {
		if (myInterestCount == 0 && position == 0) {
			addOrSub_header.setVisibility(View.GONE);
		} else {
			if (position >= myInterestCount) {
				direction.setVisibility(View.GONE);
				addOrSub_header.setImageResource(R.drawable.ic_add);
			} else {
				if (isShowDirectionFlag){
					direction.setVisibility(View.VISIBLE);
					addOrSub_header.setVisibility(View.GONE);
				}
				else{
					direction.setVisibility(View.GONE);
				}
				addOrSub_header.setImageResource(R.drawable.ic_sub);
			}
		}
	}

	/**
	 * 设置点击监听
	 * 
	 * @param helper
	 * @param item
	 * @param addOrSub_header
	 * @param addOrSub_tail
	 */
	private void setOnClickListener(final ViewHolder helper,
			final Interests item, final ImageView addOrSub_header,
			ImageView addOrSub_tail) {
		addOrSub_header.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (helper.getPosition() >= myInterestCount) {
					// 点击添加
					addNewHobby(item.getInterestId());
				} else {
					// 点击减去
					showDeleteDialog(item.getName(), item.getInterestId(),
							helper.getPosition());
				}
			}
		});

		addOrSub_tail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (helper.getPosition() >= myInterestCount) {
					// 点击添加
					addNewHobby(item.getInterestId());
				} else {
					// 点击减去
					showDeleteDialog(item.getName(), item.getInterestId(),
							helper.getPosition());
				}
			}
		});
	}

	protected void lazyLoad() {
		initDatas();
		pgDialog.dismiss();
	}

	/**
	 * @author LIUBO
	 * @date 2015-4-13下午4:13:28
	 * @TODO 初始化数据
	 */
	private void initDatas() {
		myInterests = DBHelper.getInstance().findAll(MyInterests.class,
				"userid=?", QYApplication.getPersonId());
		// 判断所有兴趣是否是已经存到本地了
		if (!DBHelper.getInstance().isInitInterestData()) {
			// 从服务器上拉取
			getAllInterests();
		} else {
			interstations.addAll(DBHelper.getInstance()
					.findAll(Interests.class));
			refreshDatas();
		}
	}

	/**
	 * @author LIUBO
	 * @date 2015-4-13下午7:43:15
	 * @TODO 刷新数据
	 */
	private void refreshDatas() {
		List<Interests> tmpList = new ArrayList<Interests>();
		if (myInterests != null) {
			for (int i = 0; i < myInterests.size(); i++) {
				for (Interests in : interstations) {// 循环比较是否在我的兴趣中已有
					if (myInterests.get(i).getInterestId()
							.equals(in.getInterestId())) {
						tmpList.add(in);
						interstations.remove(in);
						break;
					}
				}
			}
			myInterestCount = tmpList.size();
			interstations.addAll(0, tmpList);
		}
		handler.sendEmptyMessage(REFLUSH);
	}

	/**
	 * @author LIUBO
	 * @date 2015-4-15下午12:32:41
	 * @TODO 获取所有兴趣
	 */
	private void getAllInterests() {
		pgDialog.show();
		RequestParams params = new RequestParams();
		params.add("class", "HobbyVO");
		HttpUtil.post(URLs.LIST_BY_CLASS, params,
				new TextHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, Header[] arg1, String arg2) {
						if (arg2 != null) {
							List<HobbyVO> hobbys = JSON.parseArray(arg2,
									HobbyVO.class);
							List<Interests> list = DBConversion.getInstance()
									.getAllInterests(hobbys);
							interstations.clear();
							interstations.addAll(list);
							DataSupport.saveAll(list);
							refreshDatas();
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, String arg2,
							Throwable arg3) {
						ToastUtil.showShort(getActivity(), "更新失败,请检查网络!");
					}
				});
	}

	/**
	 * @author LIUBO
	 * @date 2015-4-15下午2:36:33
	 * @TODO 添加新兴趣
	 * @param hobbyId
	 */
	private void addNewHobby(final String hobbyId) {
		pgDialog = DialogHelper.getSDialog(getActivity(), "添加兴趣中···", false);
		pgDialog.show();
		RequestParams params = new RequestParams();
		params.add("personId", QYApplication.getPersonId());
		params.add("hobbyIds", hobbyId);
		HttpUtil.post(URLs.ADD_HOBBY, params, new TextHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, String arg2) {
				MyInterests me = new MyInterests();
				me.setInterestId(hobbyId);
				me.setUserId(QYApplication.getPersonId());
				me.saveThrows();
				myInterests.add(me);
				refreshDatas();
				pgDialog.dismiss();
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, String arg2,
					Throwable arg3) {
				pgDialog.dismiss();
				ToastUtil.show(getActivity(), "添加失败!", Gravity.CENTER);
			}
		});
	}

	/**
	 * @author LIUBO
	 * @date 2015-4-15下午2:37:27
	 * @TODO 移除兴趣
	 * @param hobbyId
	 */
	private void removeMyHobby(final String hobbyId, final int position) {
		RequestParams params = new RequestParams();
		params.add("personId", QYApplication.getPersonId());
		params.add("hobbyIds", hobbyId);
		HttpUtil.post(URLs.REMOVE_HOBBY, params, new TextHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] arg1, String arg2) {
				DBHelper.getInstance().delete(MyInterests.class,
						"userid=? and interestid=?",
						QYApplication.getPersonId(), hobbyId);
				myInterests.remove(position);
				refreshDatas();
				pgDialog.dismiss();

			}

			@Override
			public void onFailure(int arg0, Header[] arg1, String arg2,
					Throwable arg3) {
				ToastUtil.show(getActivity(), "兴趣移除失败!", Gravity.CENTER);
			}
		});
	}

	/**
	 * @author LIUBO
	 * @date 2015-4-15下午3:05:03
	 * @TODO 显示删除对话框
	 * @param title
	 * @param hobbyId
	 */
	private void showDeleteDialog(String title, final String hobbyId,
			final int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(title);
		builder.setMessage("是否要将该兴趣从我的兴趣中移除？");
		builder.setNegativeButton("否", null)
				.setPositiveButton("是",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								removeMyHobby(hobbyId, position);
							}

						}).create().show();
	}
}
