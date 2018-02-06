package com.nzy.nim.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.activity.login.LoginActivity;
import com.nzy.nim.api.URLs;
import com.nzy.nim.db.bean.Users;
import com.nzy.nim.db.tmpbean.SPHelper;
import com.nzy.nim.tool.common.DensityUtil;
import com.nzy.nim.tool.common.HttpUtil;
import com.nzy.nim.tool.common.StringUtil;
import com.nzy.nim.tool.common.ToastUtil;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

public class ForgotPwdActivity extends BaseActivity {
    private EditText newPwd, confirmPwd;
    private Button finishBtn;
    private String phone;
    private TextView tip;
    private EditText code;
    private Button resend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        phone = getIntent().getStringExtra("phone");
        initTopBar();
        initView();
        initResend();
        getVerificationCode();

    }

    private void initView() {
        newPwd = (EditText) findViewById(R.id.activity_forget_password_pwd);
        confirmPwd = (EditText) findViewById(R.id.activity_forget_password_confirm_pwd);
        finishBtn = (Button) findViewById(R.id.activity_forget_password_finish);
        tip = (TextView) findViewById(R.id.activity_register2_hint);
        code = (EditText) findViewById(R.id.activity_register2_code);
        resend = (Button) findViewById(R.id.activity_register2_resend);
        tip.setText("已向手机号:" + phone + "发送了一条验证码");

        //重新发送验证码
        resend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initResend();
                getVerificationCode();
            }
        });
        //提交修改密码信息
        finishBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (check(newPwd.getText(), confirmPwd.getText(), code.getText())) {
                    resetPwd(phone, newPwd.getText().toString(), code.getText().toString());
                }
            }
        });
    }

    /**
     * 可再次发送验证码 倒计时
     */
    private void initResend() {
        resend.setEnabled(false);
        resend.setBackgroundColor(getResources().getColor(R.color.bg_topbar));
        new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                resend.setBackgroundColor(getResources().getColor(R.color.gray));
                resend.setText("重新发送(" + millisUntilFinished / 1000 + "s)");
            }

            @Override
            public void onFinish() {
                resend.setEnabled(true);
                resend.setText("重新发送(60s)");
                resend.setBackgroundColor(getResources().getColor(
                        R.color.bg_topbar));
            }
        }.start();
    }

    private void getVerificationCode() {
        RequestParams param = new RequestParams();
        param.add("phone", phone);
        param.add("type", "1");//找回密码
        Log.e("验证码：", URLs.SEND_PHONE_VERIFY + param);
        HttpUtil.post(URLs.SEND_PHONE_VERIFY, param, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0, Header[] arg1, String arg2) {
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, String arg2,
                                  Throwable arg3) {
            }
        });
    }


    /**
     * @param pwd
     * @param confirmPwd
     * @return
     * @author LIUBO
     * @date 2015-4-14下午9:51:10
     * @TODO 检测新密码
     */

    private boolean check(Editable pwd, Editable confirmPwd,Editable code) {
        String msg = "";
        boolean flag = true;
        if (TextUtils.isEmpty(code)) {
            msg="验证码不能为空";
            flag= false;
        }
        if (TextUtils.isEmpty(pwd)) {
            msg = "密码不能为空!";
            flag = false;
        } else if (!StringUtil.checkInfo(pwd.toString(), StringUtil.regex_pwd)) {
            msg = "密码格式不正确!";
            flag = false;
        } else if (TextUtils.isEmpty(confirmPwd)
                || !pwd.toString().equals(confirmPwd.toString())) {
            msg = "密码不一致!";
            flag = false;
        }
        if (!DensityUtil.isPassWord(pwd.toString().trim())) {
            ToastUtil.showLong(ForgotPwdActivity.this, "请输入合法密码，由6-15位字母（区分大小写）、数字、符号组成");
            return false;
        }
        if (pwd.toString().trim().length() < 6 || pwd.toString().trim().length() > 15) {
            ToastUtil.showLong(ForgotPwdActivity.this, "请输入合法密码，由6-15位字母（区分大小写）、数字、符号组成");
            return false;
        }
        if (!flag)
            ToastUtil.show(this, msg, Gravity.TOP);
        return flag;
    }

    /**
     * @author LIUBO
     * @date 2015-4-14下午9:42:47
     * @TODO 重置密码
     */
    private void resetPwd(final String phone,String newPwd,String code) {
        RequestParams params = new RequestParams();
        params.add("phone", phone);
        params.add("verifyCode", code);
        params.add("password", StringUtil.getMD5ofStr(newPwd));
        HttpUtil.post(URLs.FORGOT_PASSWORD, params,
                new TextHttpResponseHandler() {

                    @Override
                    public void onSuccess(int arg0, Header[] arg1, String arg2) {
                        if (arg2!=null&&!"".equals(arg2)){
                            try {
                                JSONObject jsonObject = new JSONObject(arg2);
                                int errcode = jsonObject.getInt("errcode");
                                if (errcode==0) {
                                    ToastUtil.show(ForgotPwdActivity.this, "密码重置成功！",
                                            Gravity.TOP);
                                    LoginActivity.actionIntent(ForgotPwdActivity.this);
                                    SPHelper.setIsModifyPwd(true, phone);
                                    DataSupport.deleteAll(Users.class);
                                    ForgotPwdActivity.this.finish();
                                }else{
                                    ToastUtil.showShort(ForgotPwdActivity.this,jsonObject.getString("errmsg"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, String arg2,
                                          Throwable arg3) {
                        ToastUtil.show(ForgotPwdActivity.this, "密码重置失败！",
                                Gravity.TOP);
                    }
                });
    }

    /**
     * @author quanyi
     * @date 2015-3-28下午5:59:11
     * @TODO TODO初始化顶部菜单栏的操作
     */
    private void initTopBar() {
        TextView tvContent = (TextView) findViewById(R.id.top_bar_content);
        Button btnNext = (Button) findViewById(R.id.top_bar_next);
        findViewById(R.id.top_back_bg).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvContent.setText("设置新密码");
        btnNext.setVisibility(View.GONE);
    }

    /**
     * @param context
     * @author quanyi
     * @date 2015-3-28下午8:30:01
     * @TODO TODO实现跳转到自身
     */
    public static void actionIntent(Context context, String phone) {
        Intent intent = new Intent(context, ForgotPwdActivity.class);
        intent.putExtra("phone", phone);
        context.startActivity(intent);
    }
}
