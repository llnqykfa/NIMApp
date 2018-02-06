package com.nzy.nim.activity.base;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.nzy.nim.R;

/**
 * Created by Administrator on 2015/10/20.
 */
public class DialogTools {
    public static void setDialogOnClick(DialogOnClickListens l){
        listens = l;
    }
    private static Dialog dialog;
    public static DialogOnClickListens listens;
    public static interface DialogOnClickListens{
        void onDialogClick();
    }


    public static Dialog refuseShow(Activity activity){
        LayoutInflater inflater = LayoutInflater.from(activity);
        View layout = inflater.inflate(R.layout.dialog_deleagate, null);
        RelativeLayout layout_refuse_show = (RelativeLayout)layout.findViewById(R.id.dialog_refuse);
        RelativeLayout sure = (RelativeLayout)layout.findViewById(R.id.btn_sure);
        ImageView cancel = (ImageView)layout.findViewById(R.id.dialog_dismiss);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listens.onDialogClick();
                dialog.dismiss();
            }
        });
        dialog = new Dialog(activity, R.style.FullScreenDialog);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.alpha = 0.8f;

        window.setAttributes(lp);

        dialog.setCancelable(true);// 不可以用“返回键”取消
        dialog.setContentView(layout_refuse_show, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        return dialog;
    }
}
