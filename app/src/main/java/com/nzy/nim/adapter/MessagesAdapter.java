package com.nzy.nim.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.text.SpannableString;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nzy.nim.R;
import com.nzy.nim.db.bean.SessionMsg;
import com.nzy.nim.http.HttpHelper;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.DateUtil;
import com.nzy.nim.tool.common.FaceConversionUtil;
import com.nzy.nim.tool.common.ImageUtil;
import com.nzy.nim.view.RoundImageView;
import com.nzy.nim.vo.MsgManager;
import com.nzy.nim.vo.QYApplication;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * @author(作者) LIUBO
 * @Date(日期) 2015-1-21 下午2:13:25
 * @classify(类别) 适配器
 * @TODO(功能) TODO 展示消息列表
 * @Param(参数) @param <T>
 * @Remark(备注)
 */
public class MessagesAdapter extends CommonAdapter<SessionMsg> {
    private Context context;
    public SparseArray<String> headMap = new SparseArray<String>();

    public MessagesAdapter(Context context, List<SessionMsg> mDatas,
                           int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
        this.context = context;
    }

    @Override
    public void convert(final ViewHolder helper, final SessionMsg item) {
        ImageView groupFlag = helper
                .getView(R.id.fragment_message_list_item_group_flag);
        TextView unReadNumber = (TextView) helper
                .getView(R.id.fragment_message_list_item_tip);
        RoundImageView logo = helper
                .getView(R.id.fragment_messages_list_item_img);
        //设置昵称
        if (item.getSendUserId() != null) {
            String contactMarkName = QYApplication.getContactMarkName(item.getSendUserId());
            if (!DataUtil.isEmpty(contactMarkName)) {
                helper.setText(R.id.fragment_messages_list_item_name, contactMarkName);
            } else {
                helper.setText(R.id.fragment_messages_list_item_name, item.getTitles());
            }
        } else {
            helper.setText(R.id.fragment_messages_list_item_name, item.getTitles());
        }

        helper.setText(R.id.fragment_messages_list_item_time,
                DateUtil.showDate(item.getCreatTime(), true));
        SpannableString spannableString = FaceConversionUtil.getInstace()
                .getExpressionString(context, item.getNewContent(), 40);
        TextView content = helper
                .getView(R.id.fragment_messages_list_item_sign);
        if (spannableString.toString().startsWith("qy://")) {
            String[] shares = spannableString.toString().split("#");
            content.setText(shares[2]);
        } else {
            content.setText(spannableString);
        }
        if (item.isGroup()) {
            groupFlag.setVisibility(View.VISIBLE);
        } else {
            groupFlag.setVisibility(View.GONE);
        }
        int num = item.getUnLookedMsgCount();
        // badgeView的show方法在此处调用会报错,(show方法中的位置设置用的是FrameLayout.LayoutParams)
        if (num == 0) {
            unReadNumber.setVisibility(View.GONE);
        } else if (num <= 99) {
            unReadNumber.setText(num + "");
            unReadNumber.setVisibility(View.VISIBLE);
        } else {
            unReadNumber.setText("99+");
            unReadNumber.setVisibility(View.VISIBLE);
        }
        // 不同大类消息设置不同头像
        setLogo(item, logo);
    }

    private void setLogo(final SessionMsg item, final RoundImageView logo) {
        int type = item.getMainType();
        if (item.getMainType() == MsgManager.CHAT_TYPE) {
            logo.setTag(getHeadImg(item));
        } else {
            logo.setTag(item.getMainType());
        }

        if (type == MsgManager.CHAT_TYPE) {

            ImageUtil.displayHeadImg(
                    getHeadImg(item), logo);
            return;
        }

        if (type == MsgManager.SYSTEM_TYPE
                && (Integer) logo.getTag() == MsgManager.SYSTEM_TYPE) {// 系统消息
            logo.setImageResource(R.drawable.news_system);
            return;
        }
        if (type == MsgManager.INTEREST_TYPE
                && (Integer) logo.getTag() == MsgManager.INTEREST_TYPE) {// 兴趣
            logo.setImageResource(R.drawable.fun);
            return;
        }
        if (type == MsgManager.SYSTEM_NOTIFICATIONS_TYPE
                && (Integer) logo.getTag() == MsgManager.SYSTEM_NOTIFICATIONS_TYPE) {// 系统推送消息
            logo.setImageResource(R.drawable.news_beyond);
            return;
        }
        if (type == MsgManager.PROJEST_MEETING_TYPE
                && (Integer) logo.getTag() == MsgManager.PROJEST_MEETING_TYPE) {// 专题会议
            logo.setImageResource(R.drawable.ic_hot_books_recommended);
            return;
        }
        if (type == MsgManager.THE_MEETING_TYPE
                && (Integer) logo.getTag() == MsgManager.THE_MEETING_TYPE) {// 会议
            logo.setImageResource(R.drawable.news_meeting);
            return;
        }
    }

    /**
     * 获取聊天对象的头像路径
     *
     * @return
     */
    private String getHeadImg(final SessionMsg item) {
        final String key = item.getSendUserId();
        String value = headMap.get(key.hashCode());
        if (value == null) {
            if (DataUtil.isEmpty(item.getImgUrl())) {
                if (item.isGroup()) {
                    HttpHelper.getRingImgFromNet(context, item.getSendUserId(), new HttpHelper.OnLoadFinishListener() {
                        @Override
                        public void onFinish(String data) {
                            saveImage(data, item, key);
                        }
                    });
                } else {
                    HttpHelper.getUserImgFromNet(context, item.getSendUserId(), new HttpHelper.OnLoadFinishListener() {
                        @Override
                        public void onFinish(String data) {
                            saveImage(data, item, key);
                        }
                    });
                }
            } else {
                value = item.getImgUrl();
            }
        }
        return value;
    }

    private void saveImage(String data, SessionMsg item, String key) {
        ContentValues values = new ContentValues();
        values.put("imgUrl", data);
        DataSupport.updateAll(SessionMsg.class, values, "senduserid=?", item.getSendUserId());
        headMap.put(key.hashCode(), data);
        notifyDataSetChanged();
    }

//    private String getContactHead(String contactId, SessionMsg msg) {
//        Contacts contact = DBHelper.getInstance().find(Contacts.class,
//                "contactid=?", contactId);
//        if (contact != null)
//            return contact.getPhotoPath();
//        else
//            return msg.getImgUrl();
//    }
//
//    private String getGroupHead(String groupId) {
//        MyGroups group = DBHelper.getInstance().find(MyGroups.class,
//                "groupid=?", groupId);
//        if (group != null)
//            return group.getGroupImg();
//        else
//            return null;
//    }
}
