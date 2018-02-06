package com.nzy.nim.helper;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.nzy.nim.R;
import com.nzy.nim.activity.main.ChatActivity;
import com.nzy.nim.activity.main.MainActivity;
import com.nzy.nim.db.tmpbean.NotificationBean;
import com.nzy.nim.db.tmpbean.SPHelper;
import com.nzy.nim.vo.MsgManager;
import com.nzy.nim.vo.QYApplication;

/**
 * Created by Administrator on 2016/11/23.
 */
public class NotificationHelper {
    public static final String CHATACTIVITY_NAME = "com.nzy.nim.activity.main.ChatActivity";
    public static final String MAINACTIVITY_NAME = "com.nzy.nim.activity.main.MainActivity";
    private static NotificationHelper notificationHelper;
    private NotificationHelper() {
    }

    public static NotificationHelper getInstance() {
        if (notificationHelper == null)
            synchronized (NotificationHelper.class) {
                if (notificationHelper == null)
                    notificationHelper = new NotificationHelper();
            }
        return notificationHelper;
    }
    private long lastTime;
    /**
     * @author LIUBO
     * @date 2015-4-17下午6:01:10
     * @TODO 通过通知栏提示
     * @param context
     * @param intent
     * @param sendId
     * @param title
     * @param content
     */
    @SuppressLint("NewApi")
    private void showNotification(Context context, Intent intent,
                                  String sendId, String title, String content) {
        // 获取该条通知的消息数量
        int num = (Integer) QYApplication.getInstance().get("notify_" + sendId,
                1);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);
        builder.setContentIntent(contentIntent).setTicker("您有一条新的消息")
                .setSmallIcon(R.drawable.ic_applogo)
                .setWhen(System.currentTimeMillis()).setAutoCancel(true)
                .setOngoing(true).setContentTitle(title)
                .setContentText(content).setNumber(num);
        QYApplication.getInstance().put("notify_" + sendId, num + 1);
        Notification notification = builder.build();
        notification.flags=Notification.FLAG_AUTO_CANCEL;
        if (lastTime == 0) {
            lastTime = System.currentTimeMillis();
        }
        long thisTime = System.currentTimeMillis();
        if (thisTime - lastTime > 1000) {
            // 添加系统默认声音
            notification.defaults = Notification.DEFAULT_SOUND;
        }
        // 获取通知管理者
        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // 发送通知，（发送标签为发送者Id的哈希码）
        nm.notify(sendId.hashCode(), notification);
        lastTime = System.currentTimeMillis();
    }
    /**
     * @Author liubo
     * @date 2015-3-15下午5:43:37
     * @TODO(功能) 提示用户有新消息到来
     * @mark(备注)
     */
    public void notifyUser(Context context, NotificationBean bean) {
        // 跳转意图
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);

        switch (bean.getMsgType()) {
            case MsgManager.CHAT_WITH_FRIEND:// 聊天消息
                if (bean.getIsGroupMsg() == NotificationBean.ISGROUP
                        && !SPHelper.getNotifyRingMsg(bean.getSendId())) {
                    // 如果用户设置不通知该群的消息则抛掉
                    return;
                } else {
                    handleChatMsgNotify(context, bean, intent);
                }
                break;
            default:
                handleDefaultMsgNotify(context, bean, intent);
                break;
        }

    }

    private void handleDefaultMsgNotify(Context context, NotificationBean bean,
                                        Intent intent) {
        intent.setClass(context, MainActivity.class);
        showNotification(context, intent, bean.getSendId(), bean.getTitle(),
                bean.getContent());
    }

    private void handleChatMsgNotify(Context context, NotificationBean bean,
                                     Intent intent) {
        String content = "";
        intent.setClass(context, ChatActivity.class);
        intent.putExtra("contactId", bean.getSendId());
        intent.putExtra("title", bean.getTitle());
        if (bean.getIsGroupMsg() == NotificationBean.ISGROUP) {
            content = bean.getSendUserName() + ":" + bean.getContent();
            intent.putExtra("isGroup", true);
        } else {
            intent.putExtra("isGroup", false);
            content = bean.getContent();
        }
        showNotification(context, intent, bean.getSendId(), bean.getTitle(),
                content);
    }

    /**
     *
     * @Author LIUBO
     * @TODO TODO 取消通知栏
     * @param sendUserId
     * @Date 2015-1-31
     * @Return void
     */
    public void cancleNotification(String sendUserId) {
        NotificationManager nm = (NotificationManager) QYApplication
                .getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(sendUserId.hashCode());
    }
}
