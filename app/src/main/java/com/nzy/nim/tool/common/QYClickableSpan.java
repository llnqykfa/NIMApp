package com.nzy.nim.tool.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.nzy.nim.activity.main.QYWebviewAvtivity;

/**
 * Created by mac on 16/1/13.
 */
public class QYClickableSpan extends ClickableSpan {

    public enum Type
    {
        URL,PHONE
    }

    public QYClickableSpan(Context context, String action, Type type)
    {
        this.context = context;
        this.action = action;
        this.type = type;
    }

    private Type type;

    private String action;

    private Context context;
    //    回复弹出框
    private void showResumeDialog(final String phone) {
        final String[] items = {"拨打 "+phone };
        AlertDialog.Builder builder = new AlertDialog.Builder(
                context);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(phone));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        break;
                }
            }
        }).create().show();
    }
    @Override
    public void onClick(View view) {
        switch (type)
        {
            case URL:
                if (action.startsWith("tel")) {
                    showResumeDialog(action);
                    return;
                }
                QYWebviewAvtivity.loadUrl(context,action);
                break;
            case PHONE:
                showResumeDialog(action);
                break;
        }
    }
    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.linkColor= Color.rgb(60, 93, 212);
    }
}
