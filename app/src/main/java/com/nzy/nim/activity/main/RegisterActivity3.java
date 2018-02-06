package com.nzy.nim.activity.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.nzy.nim.api.URLs;
import com.nzy.nim.helper.DialogHelper;
import com.nzy.nim.http.HttpHelper;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.DensityUtil;
import com.nzy.nim.tool.common.HttpUtil;
import com.nzy.nim.tool.common.StringUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.vo.QYApplication;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class RegisterActivity3 extends BaseActivity implements
        HttpHelper.OnLoadFinishListener {
    private EditText nick, code, pwd, confirmPwd;
    private Button finishBtn;
    private String phone;
    private String schoolId = "0001SX1000000000OL15";
    private boolean isLoadComplete = false;
    private boolean isSchoolUser = false;
    private HttpHelper helper;
    public static ProgressDialog pgDialog;
    private TextView tip;
    private Button resend;
    private boolean isRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_3);
        pgDialog = DialogHelper.getSDialog(RegisterActivity3.this, "注册中···", false);
        phone = getIntent().getStringExtra("phone");
        helper = new HttpHelper();
        helper.setOnLoadFinishListener(this);
        initTopBar();
        nick = (EditText) findViewById(R.id.activity_register3_nick);
        pwd = (EditText) findViewById(R.id.activity_register3_pwd);
        confirmPwd = (EditText) findViewById(R.id.activity_register3_confirm_pwd);
        finishBtn = (Button) findViewById(R.id.activity_register3_finish);
        resend = (Button) findViewById(R.id.activity_register2_resend);
        // 判断是校内注册还是校外注册
        if ((Boolean) QYApplication.getInstance().get("isInSchool", false)) {
            isSchoolUser = true;
        } else {
            isSchoolUser = false;
        }
        finishBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (check()) {
                    register();
                }

            }
        });
        tip = (TextView) findViewById(R.id.activity_register2_hint);
        code = (EditText) findViewById(R.id.activity_register2_code);
        tip.setText("已向手机号:" + phone + "发送了一条验证码");
        initResend();
        getVerificationCode();
//		initResend();
        resend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initResend();
                getVerificationCode();
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
        param.add("type", "0");//找回密码
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

    private boolean check() {
        String msg = "";
        boolean flag = true;
        if (TextUtils.isEmpty(code.getText())) {
            msg = "验证码不能为空！";
            flag = false;
        } else if (TextUtils.isEmpty(nick.getText())) {
            msg = "昵称不能为空!";
            flag = false;
        } else if (nick.getText().length() > 7) {
            msg = "昵称长度不能超过7个汉字!";
            flag = false;
        }

        else if (TextUtils.isEmpty(pwd.getText())) {
            msg = "密码不能为空!";
            flag = false;
        } else if (!StringUtil.checkInfo(pwd.getText().toString(),
                StringUtil.regex_pwd)) {
            msg = "密码格式不正确!";
            flag = false;
        } else if (TextUtils.isEmpty(confirmPwd.getText())
                || !pwd.getText().toString()
                .equals(confirmPwd.getText().toString())) {
            msg = "密码不一致!";
            flag = false;
        }
//        String chinese = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+$"; /**这个正则表达式用来判断是否为中文**/
//        String username = "^\\w+$";                              /**此正则表达式判断单词字符是否为：[a-zA-Z_0-9]**/
/**此正则表达式将上面二者结合起来进行判断，中文、大小写字母和数字**/
        String all  = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]{2,10}$";//{2,10}表示字符的长度是2-10
        Pattern pattern = Pattern.compile(all);
        boolean tf = pattern.matcher(nick.getText().toString().trim()).matches();
        if(!tf){
            msg="请输入合法名称，名称由中文、字母、数字组成";
            flag = false;
        }
        if (!DensityUtil.isPassWord(pwd.getText().toString().trim())) {
            msg="请输入合法密码，由6-15位字母（区分大小写）、数字、符号组成";
            flag = false;
        }
        if (pwd.getText().toString().trim().length() < 6 || pwd.getText().toString().trim().length() > 15) {
            msg="请输入合法密码，由6-15位字母（区分大小写）、数字、符号组成";
            flag = false;
        }
        if (!flag)
            ToastUtil.show(this, msg, Gravity.TOP);
        return flag;
    }

    public static void actionIntent(Context context, String phone) {
        Intent intent = new Intent(context, RegisterActivity3.class);
        intent.putExtra("phone", phone);
        context.startActivity(intent);
    }

    private void initTopBar() {
        TextView tvContent = (TextView) findViewById(R.id.top_bar_content);
        Button btnNext = (Button) findViewById(R.id.top_bar_next);
        findViewById(R.id.top_back_bg).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvContent.setText("设置密码");
        btnNext.setVisibility(View.GONE);
    }

    /**
     * @author LIUBO
     * @date 2015-4-18下午4:02:45
     * @TODO 向服务器
     */
    private void register() {
        if(pgDialog!=null){
            pgDialog.show();
        }
        RequestParams params = new RequestParams();
        params.add("phone", phone);
        params.add("password", StringUtil.getMD5ofStr(pwd.getText().toString()));
        params.add("verifyCode", code.getText().toString());
        params.add("userName", nick.getText().toString());
        Log.e("注册",URLs.REGIST_WITH_PHONE+params);
        HttpUtil.post(URLs.REGIST_WITH_PHONE, params, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0, Header[] arg1, String arg2) {
                if (arg2 != null && !"".equals(arg2)) {
                    try {
                        JSONObject jsonObject = new JSONObject(arg2);
                        int errcode = jsonObject.getInt("errcode");
                        if (errcode == 0) {
                            pgDialog.dismiss();
                            ToastUtil.showShort(RegisterActivity3.this, "注册成功");
                            new HttpHelper().login(RegisterActivity3.this, phone, StringUtil.getMD5ofStr(pwd.getText().toString()));
                        } else {
                            pgDialog.dismiss();
                            ToastUtil.showShort(RegisterActivity3.this, jsonObject.getString("errmsg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(int arg0, Header[] arg1, String arg2,
                                  Throwable arg3) {
                pgDialog.dismiss();
                ToastUtil.show(RegisterActivity3.this, "服务器繁忙!", Gravity.TOP);
            }
        });
    }

    @Override
    public void onFinish(String data) {
        if (data == null) {
            ToastUtil.show(this, "注册失败,学号不符合!", Gravity.TOP);
            return;
        }
    }

    private void verifyPerson(String url, String studentId) {
        RequestParams params = new RequestParams();
        params.add("certId", studentId);
        HttpUtil.post(url, params, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0, Header[] arg1, String arg2) {
                if (arg2 == null || !DataUtil.isOk(arg2)) {
                    ToastUtil.show(RegisterActivity3.this, "注册失败,学号不符合！",
                            Gravity.TOP);
                } else {
                    register();
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, String arg2,
                                  Throwable arg3) {
                ToastUtil.show(RegisterActivity3.this, "网络异常，请检查你的网络!",
                        Gravity.TOP);
            }
        });
    }
}
