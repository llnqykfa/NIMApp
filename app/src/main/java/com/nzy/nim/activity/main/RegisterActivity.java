package com.nzy.nim.activity.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.api.URLs;
import com.nzy.nim.tool.common.HttpUtil;
import com.nzy.nim.tool.common.StringUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.view.Code;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends BaseActivity {
    private EditText editText;
    private Button nextBtn;
    private boolean isRegister;
    private ImageView vc_image;
    private Button vc_shuaixi;
    private String getCode=null;
    private EditText vc_code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        isRegister = getIntent().getBooleanExtra("isRegister", true);
        initTopBar();
        editText = (EditText) findViewById(R.id.register_phone_number);
        nextBtn = (Button) findViewById(R.id.register_next);
        vc_image=(ImageView)findViewById(R.id.vc_image);
        vc_image.setImageBitmap(Code.getInstance().getBitmap());
        vc_code=(EditText) findViewById(R.id.vc_code);

        getCode=Code.getInstance().getCode(); //获取显示的验证码
        vc_shuaixi=(Button)findViewById(R.id.vc_shuaixi);
        vc_shuaixi.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                vc_image.setImageBitmap(Code.getInstance().getBitmap());
                getCode=Code.getInstance().getCode();
            }
        });
        nextBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String	v_code=vc_code.getText().toString().trim();
                if(v_code==null||v_code.equals("")){
                    ToastUtil.showShort(RegisterActivity.this,"没有填写验证码");
                }else if(!v_code.equalsIgnoreCase(getCode)){
                    ToastUtil.showShort(RegisterActivity.this,"验证码填写不正确");
                }else {
                    if (!TextUtils.isEmpty(editText.getText())) {
                        if (StringUtil.checkMobileNumber(editText.getText()
                                .toString())) {
                            if (isRegister) {
                                //验证是否注册
                                isRegister(editText.getText().toString());
                            } else {
                                ForgotPwdActivity.actionIntent(RegisterActivity.this,
                                        editText.getText().toString());
                                RegisterActivity.this.finish();
                            }
                        } else {
                            ToastUtil.show(getApplicationContext(), "手机号格式不正确!",
                                    Gravity.TOP);
                        }
                    } else {
                        ToastUtil.show(getApplicationContext(), "手机号不能为空!",
                                Gravity.TOP);
                    }
                }
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence != null && !"".equals(charSequence)) {
                    if (StringUtil.checkMobileNumber(editText.getText().toString())) {
                        nextBtn.setBackgroundColor(Color.rgb(0, 119, 217));
                    } else {
                        nextBtn.setBackgroundColor(Color.rgb(157, 157, 157));
                    }
                } else {
                    nextBtn.setBackgroundColor(Color.rgb(157, 157, 157));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void isRegister(String phone) {
        RequestParams params = new RequestParams();
        params.add("phone", phone);
        Log.e("手机注册验证：", URLs.REGISTRABLE_PHONE + params);
        HttpUtil.post(URLs.REGISTRABLE_PHONE, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                if (s != null && !"".equals(s)) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        int errcode = jsonObject.getInt("errcode");
                        if (errcode == 0) {
                            boolean registrable = jsonObject.getBoolean("registrable");
                            if (registrable) {
                                RegisterActivity3.actionIntent(RegisterActivity.this,
                                        editText.getText().toString());
                                RegisterActivity.this.finish();
                            } else {
                                ToastUtil.showShort(RegisterActivity.this, "该手机号已被注册！");
                            }
                        } else {
                            ToastUtil.showShort(RegisterActivity.this, jsonObject.getString("errmsg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
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
        if (isRegister)
            tvContent.setText("手机号验证");
        else
            tvContent.setText("找回密码");
        btnNext.setVisibility(View.GONE);
    }

    /**
     * @param context
     * @author quanyi
     * @date 2015-3-28下午6:28:12
     * @TODO TODO跳转到自身
     */
    public static void actionIntent(Context context, boolean isRegister) {
        Intent intent = new Intent(context, RegisterActivity.class);
        intent.putExtra("isRegister", isRegister);
        context.startActivity(intent);
    }
}
