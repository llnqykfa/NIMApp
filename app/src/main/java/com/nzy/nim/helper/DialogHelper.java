package com.nzy.nim.helper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.nzy.nim.R;

/**
 * Created by Administrator on 2016/11/25.
 */
public class DialogHelper {
    /**
     *
     * @author 刘波
     * @date 2015-2-28下午4:44:31
     * @todo 显示信息对话框
     * @param context
     * @param msgId
     */
    public static void showMsgDialog(Context context, int msgId) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(R.string.notify_msg);
        dialog.setMessage(msgId);
        dialog.setNeutralButton(R.string.close, null).create().show();
    }

    /**
     *
     * @author 刘波
     * @date 2015-2-28下午4:56:18
     * @todo 显示信息对话框
     * @param context
     * @param message
     */
    public static void showMsgDialog(Context context, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(R.string.notify_msg);
        dialog.setMessage(message);
        dialog.setNeutralButton(R.string.close, null).create().show();
    }

    /**
     * @author LIUBO
     * @date 2015-4-1下午4:01:12
     * @TODO 等待对话框，使用时调用show方法
     * @param context
     * @param flag
     *            是否点击外部可以取消对话框
     * @return
     */
    public static Dialog getDialog(Context context, boolean flag) {
        Dialog mDialog = new Dialog(context, R.style.dialog);
        LayoutInflater in = LayoutInflater.from(context);
        View viewDialog = in.inflate(R.layout.view_loading_dialog, null);
        viewDialog.setBackgroundColor(0x7f000000);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 这里可以设置dialog的大小，当然也可以设置dialog title等
        // LayoutParams layoutParams = new LayoutParams(width * 80 / 100, 50);
        // mDialog.setContentView(viewDialog, layoutParams);
        mDialog.setContentView(viewDialog);
        mDialog.setCanceledOnTouchOutside(flag);
        return mDialog;
    }

    /**
     * @author LIUBO
     * @date 2015-4-1下午4:04:52
     * @TODO 圆形进度提示框
     * @param context
     * @param str
     *            提示内容
     * @param flag
     *            点击外面是否可以取消对话框
     * @return
     */
    public static ProgressDialog getSDialog(Context context, String str,
                                            Boolean flag) {
        ProgressDialog mpDialog = new ProgressDialog(context);
        mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mpDialog.setMessage(str);
        mpDialog.setCanceledOnTouchOutside(flag);
        mpDialog.setIndeterminate(true);
        mpDialog.setCancelable(true);
        return mpDialog;
    }

    /**
     * @author LIUBO
     * @date 2015-4-1下午4:06:33
     * @TODO 水平精度提示对话框
     * @param context
     * @param str
     *            提示信息
     * @param flag
     *            点击是否可以取消
     * @return
     */
    public static ProgressDialog getHDialog(Context context, String str,
                                            Boolean flag) {
        ProgressDialog mpDialog = new ProgressDialog(context);
        mpDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
        mpDialog.setCancelable(flag);// 设置是否可以通过点击Back键取消
        mpDialog.setCanceledOnTouchOutside(flag);// 设置在点击Dialog外是否取消Dialog进度条
        mpDialog.setTitle(str);
        mpDialog.setMax(100);
        return mpDialog;
    }
}
