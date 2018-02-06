package com.nzy.nim.activity.main;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.api.URLs;
import com.nzy.nim.tool.common.DensityUtil;
import com.nzy.nim.tool.common.StringUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.volley.HTTPUtils;
import com.nzy.nim.volley.VolleyListener;

import com.nzy.nim.R;

import java.util.HashMap;

public class UpdatePwdActivity extends BaseActivity {

    private EditText et_old_password;
    private EditText et_new_password;
    private EditText et_again_password;
    private String oldPwd;
    private String newPwd;
    private String againPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass_word);
        QYApplication.ACTIVITY_NAME="修改密码";
        initTopBar();
        intUI();
    }

    private void intUI() {
        et_old_password = (EditText) findViewById(R.id.et_old_password);
        et_new_password = (EditText) findViewById(R.id.et_new_password);
        et_again_password = (EditText) findViewById(R.id.et_again_password);

    }
    private boolean check(){
        oldPwd = et_old_password.getText().toString();
        newPwd = et_new_password.getText().toString();
        againPwd = et_again_password.getText().toString();
        if (oldPwd.equals("")) {
            ToastUtil.showShort(UpdatePwdActivity.this, "请输入原密码！");
            return false;
        }
        if (newPwd.equals("")) {
            ToastUtil.showShort(UpdatePwdActivity.this, "请输入新密码！");
            return false;
        }
        if (againPwd.equals("")) {
            ToastUtil.showShort(UpdatePwdActivity.this, "请再输一次新密码");
            return false;
        }
        if(oldPwd.length()<6||oldPwd.length()>15){
            showTips();
            return false;
        }
        if(newPwd.length()<6||newPwd.length()>15){
            showTips();
            return false;
        }
        if(againPwd.length()<6|| againPwd.length()>15){
            showTips();
            return false;
        }
        if(oldPwd.equals(newPwd)){
            ToastUtil.showShort(UpdatePwdActivity.this, "新密码不能和旧密码一样");
            return false;
        }
        if(!newPwd.equals(againPwd)){
            ToastUtil.showShort(UpdatePwdActivity.this, "两次输入密码不一样");
            return false;
        }
        if(!DensityUtil.isPassWord(oldPwd)){
            showTips();
            return false;
        }
        if(!DensityUtil.isPassWord(newPwd)){
            showTips();
            return false;
        }
        if(!DensityUtil.isPassWord(againPwd)){
            showTips();
            return false;
        }
        return true;
    }

    private void showTips() {
        ToastUtil.showShort(UpdatePwdActivity.this, "请输入合法密码，由6-15位字母（区分大小写）、数字、符号组成");
    }


    private void initTopBar() {
        TextView titleContent = (TextView) findViewById(R.id.top_bar_content);
        Button next = (Button) findViewById(R.id.top_bar_next);
        titleContent.setText("修改密码");
        next.setText("提交");
        findViewById(R.id.top_back_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check()){
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("oldPassword", StringUtil.getMD5ofStr(oldPwd));
                    map.put("newPassword",StringUtil.getMD5ofStr(newPwd));
                    HTTPUtils.postWithToken(UpdatePwdActivity.this, URLs.UPDATE_PASSWORD, map, new VolleyListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            ToastUtil.showShort(UpdatePwdActivity.this,"密码修改失败！");
                        }

                        @Override
                        public void onResponse(String s) {
                            ToastUtil.showShort(UpdatePwdActivity.this,"密码修改成功！");
                            UpdatePwdActivity.this.finish();
                        }
                    });
                }
            }
        });
    }
}
