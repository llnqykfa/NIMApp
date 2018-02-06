package com.nzy.nim.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.SpannableString;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.ShowBigImageActivity;
import com.nzy.nim.activity.main.ChatActivity;
import com.nzy.nim.activity.main.ContextMenu;
import com.nzy.nim.activity.main.FriendsInfoActivity;
import com.nzy.nim.activity.main.MyPersonalInfoActivity;
import com.nzy.nim.activity.main.RingTeamInfoActivity;
import com.nzy.nim.activity.main.ShowNormalFileActivity;
import com.nzy.nim.api.FileUtils;
import com.nzy.nim.constant.MyConstants;
import com.nzy.nim.db.bean.ChatRecord;
import com.nzy.nim.db.bean.Contacts;
import com.nzy.nim.db.bean.HeadTables;
import com.nzy.nim.db.bean.ShareMsgInfo;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.DateUtil;
import com.nzy.nim.tool.common.FaceConversionUtil;
import com.nzy.nim.tool.common.ImageUtil;
import com.nzy.nim.view.MaskImage;
import com.nzy.nim.view.QYUriMatcher;
import com.nzy.nim.view.RoundImageView;
import com.nzy.nim.vo.MsgManager;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.vo.UserInfo;

import java.io.File;
import java.util.List;

/**
 * @author(作者) LIUBO
 * @Date(日期) 2015-1-21 下午8:20:39
 * @classify(类别) 适配器
 * @TODO(功能) TODO 聊天界面消息显示适配器
 * @Param(参数)
 * @Remark(备注)
 */
public class ChatMsgViewAdapter extends BaseAdapter {
    // 文本
    private static final int MESSAGE_TYPE_RECV_TXT = 0;
    private static final int MESSAGE_TYPE_SENT_TXT = 1;
    // 图片
    private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
    private static final int MESSAGE_TYPE_RECV_IMAGE = 3;
    // 文件
    private static final int MESSAGE_TYPE_SENT_FILE = 4;
    private static final int MESSAGE_TYPE_RECV_FILE = 5;
    // 语音
    // private static final int MESSAGE_TYPE_SENT_VOICE = 6;
    // private static final int MESSAGE_TYPE_RECV_VOICE = 7;
    // 视频
    // private static final int MESSAGE_TYPE_SENT_VIDEO = 8;
    // private static final int MESSAGE_TYPE_RECV_VIDEO = 9;
    // 位置
    // private static final int MESSAGE_TYPE_SENT_LOCATION = 10;
    // private static final int MESSAGE_TYPE_RECV_LOCATION = 11;
    // 通话
    // private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 12;
    // private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 13;


    private static final int MESSAGE_TYPE_SENT_SHARE = 14;
    private static final int MESSAGE_TYPE_RECV_SHARE = 15;

    private List<ChatRecord> coll;
    private Context ctx;
    private LayoutInflater mInflater;
    private String userImg = "";// 用户本人头像
    private String contactImg = "";// 联系人头像
    private String title = "";
    private OnRepeatListener onRepeatListener = null;// 重发的接口对象
    // 群聊的聊天头像的路径
    private SparseArray<MemberMsg> memberInfo;
    private String contactId;
    private boolean group;

    public ChatMsgViewAdapter(Context context, List<ChatRecord> coll,
                              boolean isGroup, String mContactId, String title) {
        this.ctx = context;//上下文
        this.coll = coll;//消息记录
        this.title = title;//标题栏
        this.mInflater = LayoutInflater.from(context);
        this.group = isGroup;//是否为组圈
        UserInfo user = DBHelper.getInstance().getUserById(
                QYApplication.getPersonId());
        if (user != null)
            userImg = user.getPhotoPath();
        if (!isGroup) {
            Contacts contact = DBHelper.getInstance().getContact(mContactId);
            if (contact != null)
                contactImg = contact.getPhotoPath();
        } else {
            memberInfo = new SparseArray<MemberMsg>();
        }
        this.contactId = mContactId;//消息类型
    }

    public void setOnRepeatListener(OnRepeatListener listener) {
        this.onRepeatListener = listener;
    }

    public int getCount() {
        return coll == null ? 0 : coll.size();
    }

    public Object getItem(int position) {
        return coll.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    /**
     * 消息数据的类型
     */
    public int getItemViewType(int position) {
        ChatRecord entity = coll.get(position);
        if (entity.getMsgType() == MsgManager.TEXT_TYPE)// 文本类型
            return entity.isCom() ? MESSAGE_TYPE_RECV_TXT
                    : MESSAGE_TYPE_SENT_TXT;
        if (entity.getMsgType() == MsgManager.PICTURE_TYPE) {// 图片类型
            return entity.isCom() ? MESSAGE_TYPE_RECV_IMAGE
                    : MESSAGE_TYPE_SENT_IMAGE;
        }
        if (entity.getMsgType() == MsgManager.FILE_TYPE) {// 文件类型
            return entity.isCom() ? MESSAGE_TYPE_RECV_FILE
                    : MESSAGE_TYPE_SENT_FILE;
        }
        if (entity.getMsgType() == MsgManager.SHARE_TXT_TYPE) {// 文件类型
            return entity.isCom() ? MESSAGE_TYPE_RECV_SHARE
                    : MESSAGE_TYPE_SENT_SHARE;
        }

        return -1;
    }

    public int getViewTypeCount() {
        return 7;
    }

    /**
     * @return
     * @author 刘波
     * @date 2015-3-2下午12:33:43
     * @todo 创建不同类型的布局
     */
    @SuppressLint("InflateParams")
    private View createView(ChatRecord entity, int position) {
        switch (entity.getMsgType()) {
            case MsgManager.TEXT_TYPE:// 文本消息布局
                return entity.isCom() ? mInflater.inflate(
                        R.layout.row_receive_text, null) : mInflater.inflate(
                        R.layout.row_send_text, null);
            case MsgManager.PICTURE_TYPE:// 图片消息布局
                return entity.isCom() ? mInflater.inflate(
                        R.layout.row_receive_picture, null) : mInflater.inflate(
                        R.layout.row_send_picture, null);
            case MsgManager.FILE_TYPE:// 文件布局
                return entity.isCom() ? mInflater.inflate(
                        R.layout.row_receive_file, null) : mInflater.inflate(
                        R.layout.row_send_file, null);
            case MsgManager.FILE_TXT_TYPE:// 图文并茂的布局
                return entity.isCom() ? mInflater.inflate(R.layout.row_pic_txt_rece, null) : mInflater.inflate(R.layout.row_pic_txt_send, null);
            case MsgManager.SHARE_TYPE:// 分享布局
                return entity.isCom() ? mInflater.inflate(R.layout.row_receive_share, null) : mInflater.inflate(R.layout.row_send_share, null);
            default:
                return null;
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ChatRecord entity = coll.get(position);
        int type = entity.getMsgType();
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = createView(entity, position);
            if (type == MsgManager.TEXT_TYPE) {// 关联文本类型的布局
                findTextViewId(convertView, viewHolder);
            } else if (type == MsgManager.PICTURE_TYPE) {// 关联图片类型的布局
                findImageViewId(convertView, viewHolder);
            } else if (type == MsgManager.FILE_TYPE) {// 关联一般文件类型的布局
                findFileViewId(convertView, viewHolder);
            } else if (type == MsgManager.FILE_TXT_TYPE) {// 关联图文并茂的布局
                findFileTxtViewId(convertView, viewHolder);
            } else if (type == MsgManager.SHARE_TYPE) {//关联分享的布局
                findSharedView(convertView, viewHolder);
            }
            if (convertView != null) {
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 设置头像
        setHeadAndName(entity, viewHolder);
        // 两条消息时间间隔小于5分钟时，该条消息的时间不显示
        setMsgTime(position, entity, viewHolder);

        // 根据不同的消息类型分类处理
        switch (entity.getMsgType()) {
            // 文本
            case MsgManager.TEXT_TYPE:
                handleTextMsg(entity, position, viewHolder);
                break;
            // 图片
            case MsgManager.PICTURE_TYPE:
                handlePicMsg(entity, position, viewHolder);
                break;
            // 文件
            case MsgManager.FILE_TYPE:
                handleFileMsg(entity, position, viewHolder);
                break;
            case MsgManager.FILE_TXT_TYPE:// 图文并茂的消息类型（邀请的消息类型）
                handleFileTxtMsg(entity, position, viewHolder);
                break;
            case MsgManager.SHARE_TYPE:// 分享消息类型
                handleShareListMsg(entity, position, viewHolder);
                break;
            default:
                break;
        }
        return convertView;
    }

    /**
     * @param entity
     * @param position
     * @param viewHolder
     * @author LIUBO
     * @date 2015-3-24下午12:08:03
     * @TODO 处理图文并茂的消息类型
     */
    private void handleFileTxtMsg(final ChatRecord entity, final int position,
                                  ViewHolder viewHolder) {
        viewHolder.container.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ContextMenu.actionIntent((Activity) ctx, title,
                        entity, position,
                        ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });

        final String[] split = entity.getContent().split("#");
        if (!DataUtil.isEmpty(split[0]))
            viewHolder.tvContent.setText(split[0].trim());
        if (!DataUtil.isEmpty(split[1]))
            viewHolder.tvTitle.setText(split[1]);
        if (!DataUtil.isEmpty(split[3])) {
            ImageUtil.displayNetImg(split[3],
                    viewHolder.ivContent);
        } else {
            viewHolder.ivContent.setImageResource(R.drawable.ic_applogo);
        }
        //点击分享消息，跳转至组圈信息
        viewHolder.container.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RingTeamInfoActivity.actionIntent(ctx, split[2]);
            }
        });
    }


    /**
     * @param entity
     * @param position
     * @param viewHolder
     * @author LIUBO
     * @date 2015-3-24下午12:08:03
     * @TODO 处理分享消息类型
     */
    private void handleShareListMsg(final ChatRecord entity, final int position, final ViewHolder viewHolder) {
        String content = entity.getContent();
        final ShareMsgInfo shareMsgInfo = new Gson().fromJson(content, ShareMsgInfo.class);
        viewHolder.shareTitle.setText(shareMsgInfo.getTitle());
        String contents = Html.fromHtml(shareMsgInfo.getShareContent()).toString().replaceAll("\\s*", "");
        viewHolder.shareContent.setText(contents);

        if (shareMsgInfo.getStyle() == MyConstants.SHARE_ONLY_TEXT) {//无图分享时，隐藏图片
            viewHolder.shareImg.setVisibility(View.GONE);
        } else {
            if (shareMsgInfo.getShareType().equals(MyConstants.BOOK_REVIEW_TYPE)) {
                viewHolder.shareImg.setImageResource(R.drawable.icon_book_review);//书评图片
            } else if (shareMsgInfo.getShareType().equals(MyConstants.BOOK_SHARE_TYPE)) {
                if (DataUtil.isEmpty(shareMsgInfo.getImgPath())) {
                    viewHolder.shareImg.setImageResource(R.drawable.pic_default_book);
                }else{
                    ImageUtil.displayNetImg(shareMsgInfo.getImgPath(), viewHolder.shareImg);
                }
            } else {
                ImageUtil.displayNetImg(shareMsgInfo.getImgPath(), viewHolder.shareImg);
            }
        }
        viewHolder.shareTip.setText(shareMsgInfo.getShareType());
        //点击分享消息，跳转至组圈信息
        viewHolder.rl_share_container.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                QYUriMatcher.actionUri(ctx, shareMsgInfo.getActionUri());
            }
        });
        viewHolder.rl_share_container.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ContextMenu.actionIntent((Activity) ctx, title,
                        entity, position,
                        ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });
        if (!entity.isCom())
            // 设置消息的发送状态
            setMsgState(entity, position, viewHolder);
    }

    /**
     * @param convertView
     * @param viewHolder
     * @author LIUBO
     * @date 2015-3-24上午11:53:26
     * @TODO 关联图文并茂的布局的组件
     */

    private void findFileTxtViewId(View convertView, ViewHolder viewHolder) {
        viewHolder.container = (LinearLayout) convertView
                .findViewById(R.id.row_pic_txt_container);
        viewHolder.tvTimeStamp = (TextView) convertView
                .findViewById(R.id.row_pic_txt_tv_timestamp);
        viewHolder.headImg = (RoundImageView) convertView
                .findViewById(R.id.row_pic_txt_iv_userhead);
        viewHolder.ivContent = (MaskImage) convertView
                .findViewById(R.id.row_pic_txt_iv_content);
        viewHolder.tvContent = (TextView) convertView
                .findViewById(R.id.row_pic_txt_tv_content);
        viewHolder.tvTitle = (TextView) convertView
                .findViewById(R.id.row_pic_txt_tv_title);
    }


    /**
     * @param convertView
     * @param viewHolder
     * @TODO 关联分享布局
     */
    private void findSharedView(View convertView, ViewHolder viewHolder) {
        viewHolder.headImg = (RoundImageView) convertView
                .findViewById(R.id.row_text_iv_userhead);
        viewHolder.tvTimeStamp = (TextView) convertView
                .findViewById(R.id.row_text_tv_timestamp);
        viewHolder.tvUserName = (TextView) convertView
                .findViewById(R.id.row_text_tv_username);
        viewHolder.shareTip = (TextView) convertView
                .findViewById(R.id.shareTip);
        viewHolder.tvMsgState = (ImageView) convertView
                .findViewById(R.id.row_text_msg_status);
        viewHolder.pb = (ProgressBar) convertView
                .findViewById(R.id.row_text_pb_sending);
        viewHolder.shareImg = (ImageView) convertView.findViewById(R.id.shareImg);
        viewHolder.rl_share_container = (RelativeLayout) convertView.findViewById(R.id.rl_share_container);
        viewHolder.shareTitle = (TextView) convertView.findViewById(R.id.sharedTitle);
        viewHolder.shareContent = (TextView) convertView.findViewById(R.id.shareContent);
    }

    private void setMsgTime(int position, ChatRecord entity,
                            ViewHolder viewHolder) {
        // 两条消息时间间隔小于5分钟时，该条消息的时间不显示
        if (position > 0
                && DateUtil.calculateTimeDifference(entity.getCreatTime()
                .getTime(), coll.get(position - 1).getCreatTime()
                .getTime()) < 5 * DateUtil.MINUTE_TIME_MILLIS) {
            viewHolder.tvTimeStamp.setVisibility(View.GONE);
        } else {
            viewHolder.tvTimeStamp.setVisibility(View.VISIBLE);
            viewHolder.tvTimeStamp.setText(DateUtil.showDate(
                    entity.getCreatTime(), false));
        }
    }

    /**
     * @param entity
     * @param viewHolder
     * @Author liubo
     * @date 2015-3-13下午5:02:11
     * @TODO(功能)处理一般文件类型的消息
     * @mark(备注)
     */
    private void handleFileMsg(final ChatRecord entity, final int position,
                               final ViewHolder viewHolder) {
        viewHolder.container.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ContextMenu.actionIntent((Activity) ctx, title,
                        entity, position,
                        ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });
        viewHolder.tvFileName.setText(entity.getFileName());
        viewHolder.tvFileSize.setText(entity.getFileSize());
        if (entity.getMsgState() == -3) {
            viewHolder.tvFileDownLoadState.setText("未下载");
        } else if (entity.getMsgState() == 3) {
            viewHolder.tvFileDownLoadState.setText("已下载");
        } else {
            viewHolder.tvFileDownLoadState.setText("");
        }
        // 点击文件消息框时，跳转大文件展示界面
        viewHolder.container.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!DataUtil.isEmpty(entity.getLocalFilePath())) {
                    FileUtils.openFile(ctx, new File(entity.getLocalFilePath()));
                } else {
                    ShowNormalFileActivity.actionIntent(ctx, entity);
                }
            }
        });
        if (!entity.isCom())
            // 设置消息的发送状态
            setMsgState(entity, position, viewHolder);
    }

    /**
     * @param convertView
     * @param viewHolder
     * @Author liubo
     * @date 2015-3-9下午6:26:31
     * @TODO(功能) 关联一般文件布局
     * @mark(备注)
     */
    private void findFileViewId(View convertView, ViewHolder viewHolder) {
        viewHolder.headImg = (RoundImageView) convertView
                .findViewById(R.id.row_file_iv_userhead);
        viewHolder.tvTimeStamp = (TextView) convertView
                .findViewById(R.id.row_file_timestamp);
        viewHolder.tvUserName = (TextView) convertView
                .findViewById(R.id.row_file_tv_username);
        viewHolder.tvMsgState = (ImageView) convertView
                .findViewById(R.id.row_file_msg_status);
        viewHolder.tvFileName = (TextView) convertView
                .findViewById(R.id.row_file_tv_file_name);
        viewHolder.tvFileSize = (TextView) convertView
                .findViewById(R.id.row_file_tv_file_size);
        viewHolder.tvFileDownLoadState = (TextView) convertView
                .findViewById(R.id.row_file_tv_file_state);
        viewHolder.pb = (ProgressBar) convertView
                .findViewById(R.id.row_file_pb_sending);
        viewHolder.container = (LinearLayout) convertView
                .findViewById(R.id.row_file_ll_file_container);
    }

    /**
     * @param convertView
     * @param viewHolder
     * @Author liubo
     * @date 2015-3-9下午6:24:09
     * @TODO(功能) 关联图片布局
     * @mark(备注)
     */
    private void findImageViewId(View convertView, ViewHolder viewHolder) {
        viewHolder.headImg = (RoundImageView) convertView
                .findViewById(R.id.row_picture_iv_userhead);
        viewHolder.tvUserName = (TextView) convertView
                .findViewById(R.id.row_picture_iv_username);
        viewHolder.ivContent = (MaskImage) convertView
                .findViewById(R.id.row_picture_iv_sendPicture);
        viewHolder.tvTimeStamp = (TextView) convertView
                .findViewById(R.id.row_picture_timestamp);
        viewHolder.pb = (ProgressBar) convertView
                .findViewById(R.id.row_picture_progressBar);
        viewHolder.tvMsgState = (ImageView) convertView
                .findViewById(R.id.row_picture_msg_status);
    }

    /**
     * @param convertView
     * @param viewHolder
     * @Author liubo
     * @date 2015-3-9下午6:23:52
     * @TODO(功能) 关联文本布局
     * @mark(备注)
     */
    private void findTextViewId(View convertView, ViewHolder viewHolder) {
        viewHolder.headImg = (RoundImageView) convertView
                .findViewById(R.id.row_text_iv_userhead);
        viewHolder.tvTimeStamp = (TextView) convertView
                .findViewById(R.id.row_text_tv_timestamp);
        viewHolder.tvUserName = (TextView) convertView
                .findViewById(R.id.row_text_tv_username);
        viewHolder.tvContent = (TextView) convertView
                .findViewById(R.id.row_text_tv_chatcontent);
        viewHolder.tvMsgState = (ImageView) convertView
                .findViewById(R.id.row_text_msg_status);
        viewHolder.pb = (ProgressBar) convertView
                .findViewById(R.id.row_text_pb_sending);
    }

    /**
     * @param entity
     * @param viewHolder
     * @author 刘波
     * @date 2015-3-2下午5:38:33
     * @todo 设置头像和时间
     */
    private void setHeadAndName(final ChatRecord entity, final ViewHolder viewHolder) {
        String imgUri = "";
        MemberMsg memberMsg = null;
        if (!entity.isCom()) {
            imgUri = userImg;
        } else if (!entity.isGroup()) {
            imgUri = contactImg;
        } else {
            memberMsg = getMemberInfo(entity.getChatUserId());
            if (memberMsg != null)
                imgUri = memberMsg.headUrl;
        }
        ImageUtil.displayHeadImg(imgUri, viewHolder.headImg);
        // 如果是群聊，则显示聊天对象的昵称
        if (entity.isGroup() && entity.isCom() && viewHolder.tvUserName != null) {
            viewHolder.tvUserName.setVisibility(View.VISIBLE);
            if (memberMsg != null && memberMsg.name != null) {
                String contactMarkName = QYApplication.getContactMarkName(entity.getChatUserId());
                if (!DataUtil.isEmpty(contactMarkName)) {
                    viewHolder.tvUserName.setText(contactMarkName);
                } else {
                    viewHolder.tvUserName.setText(memberMsg.name);
                }
            }
        }
        //点击好友头像进入详情
        viewHolder.headImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (entity.isCom()) {
                    FriendsInfoActivity.actionIntent(ctx, entity.getChatUserId());
                } else {
                    MyPersonalInfoActivity.actionIntent(ctx);
                }
            }
        });
    }

    /**
     * @param chatUserId
     * @return
     * @author LIUBO
     * @date 2015-4-16下午10:20:46
     * @TODO 获取群聊的头像
     */
    private MemberMsg getMemberInfo(String chatUserId) {
        MemberMsg member = memberInfo.get(chatUserId.hashCode(), null);
        if (member == null) {// 缓存中为空时，从本地查
            HeadTables head = DBHelper.getInstance().find(HeadTables.class,
                    "userid=?", chatUserId);
            if (head == null) {
                member = null;
            } else {
                member = new MemberMsg();
                member.headUrl = head.getHeadUrl();
                member.name = head.getName();
                memberInfo.put(chatUserId.hashCode(), member);
            }
        }
        return member;
    }

    /**
     * @param entity
     * @param viewHolder
     * @author 刘波
     * @date 2015-3-2下午5:36:31
     * @todo 显示图片
     */

    @SuppressLint("ClickableViewAccessibility")
    private void handlePicMsg(final ChatRecord entity, final int position,
                              final ViewHolder viewHolder) {
        int maskSource = 0;
        if (entity.isCom())
            maskSource = R.drawable.chatfrom_bg_normal;
        else
            maskSource = R.drawable.chatto_bg_normal;
        if (!DataUtil.isEmpty(entity.getLocalFilePath())) {
            ImageUtil.disPlayChatImg(entity.getLocalFilePath(),
                    viewHolder.ivContent, maskSource);
        } else if (!DataUtil.isEmpty(entity.getRemoteFilePath())) {
            ImageUtil.disPlayChatImg(entity.getRemoteFilePath(),
                    viewHolder.ivContent, maskSource);
        }
        viewHolder.ivContent.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    viewHolder.ivContent.setColorFilter(Color
                            .parseColor("#77000000"));
                else if (event.getAction() == MotionEvent.ACTION_UP
                        || event.getAction() == MotionEvent.ACTION_CANCEL)
                    viewHolder.ivContent.setColorFilter(null);
                return false;
            }
        });
        viewHolder.ivContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!DataUtil.isEmpty(entity.getLocalFilePath()))
                    ShowBigImageActivity.actionIntent(ctx,
                            entity.getLocalFilePath());
                else
                    ShowBigImageActivity.actionIntent(ctx,
                            entity.getRemoteFilePath());
            }
        });
        viewHolder.ivContent.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ContextMenu.actionIntent((Activity) ctx, title,
                        entity, position,
                        ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });
        if (!entity.isCom())
            // 设置消息的发送状态
            setMsgState(entity, position, viewHolder);
    }

    /**
     * @param entity
     * @param viewHolder
     * @author 刘波
     * @date 2015-3-2下午3:13:36
     * @todo 处理文本消息
     */
    private void handleTextMsg(final ChatRecord entity, final int position,
                               final ViewHolder viewHolder) {

        SpannableString spannableString = FaceConversionUtil.getInstace()
                .getExpressionString(ctx, entity.getContent(), 60);
        viewHolder.tvContent.setText(spannableString);
        viewHolder.tvContent.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                viewHolder.tvContent.setEnabled(false);
                viewHolder.tvContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewHolder.tvContent.setEnabled(true);
                    }
                },1000);
                ContextMenu.actionIntent((Activity) ctx, title,
                        entity, position,
                        ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });
        if (!entity.isCom())
            // 设置消息的发送状态
            setMsgState(entity, position, viewHolder);
    }

    /**
     * @param entity
     * @param viewHolder
     * @Author liubo
     * @date 2015-3-9下午11:47:13
     * @TODO(功能) 处理发送消息的不同状态
     * @mark(备注)
     */
    private void setMsgState(final ChatRecord entity, int position,
                             ViewHolder viewHolder) {
        viewHolder.pb.setTag(position);
        viewHolder.tvMsgState.setVisibility(View.INVISIBLE);
        viewHolder.pb.setVisibility(View.INVISIBLE);
        if (entity.getMsgState() == MsgManager.MSG_STATE_ONGOING) {// 正在发送中
            viewHolder.pb.setVisibility(View.VISIBLE);
        } else if (entity.getMsgState() == MsgManager.MSG_STATE_SUCESS) {// 消息发送成功
            viewHolder.pb.setVisibility(View.GONE);
            viewHolder.tvMsgState.setVisibility(View.GONE);
        } else if (entity.getMsgState() == MsgManager.MSG_STATE_FAILE) {// 消息发送失败
            viewHolder.pb.setVisibility(View.GONE);
            viewHolder.tvMsgState.setVisibility(View.VISIBLE);
            viewHolder.tvMsgState.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 重发消息
                    if (onRepeatListener != null)
                        onRepeatListener.onRepeatClick(entity.getMsgId());
                }
            });
        }
    }

    // 重发的回调接口
    public interface OnRepeatListener {
        void onRepeatClick(String msgId);
    }

    static class ViewHolder {
        RoundImageView headImg;// 头像
        TextView tvTimeStamp;// 发送时间
        ImageView tvMsgState;// 是否发送成功状态
        TextView tvUserName;// 昵称
        TextView tvContent;// 文本内容
        TextView tvTitle;
        TextView tvCreator;
        MaskImage ivContent;// 图片内容
        ProgressBar pb;// 消息发送进度条
        TextView tvFileName;// 文件名
        TextView tvFileSize;// 文件大小
        TextView tvFileDownLoadState;// 文件是否被下载
        LinearLayout container;// 文件布局容器

        ////////////////////////分享
        RelativeLayout rl_share_container;//分享布局
        ImageView shareImg;//分享的图片
        TextView shareTitle;//分享的标题
        TextView shareContent;//分享的内容
        TextView shareTip;// 分享的类型
    }

    class MemberMsg {
        String headUrl;
        String name;
    }
}
