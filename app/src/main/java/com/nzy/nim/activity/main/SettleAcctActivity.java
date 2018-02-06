package com.nzy.nim.activity.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.android.volley.VolleyError;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.constant.BizConstants;
import com.nzy.nim.db.bean.PayResult;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.view.RoundImageView;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.volley.PayHttpUtil;
import com.nzy.nim.volley.VolleyListener;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/13.
 */
public class SettleAcctActivity extends BaseActivity implements
        View.OnClickListener {

    private Button btnOther;// 确认
    private TextView tvTitleContent;// 内容

    /**
     * 注册单号
     */
    private String bizId;
    /**
     * 注册用户
     */
    private String regName;
    /**
     * 注册分类
     */
    private String regCatalogCode;
    private String regCatalogName;
    /**
     * 注册费用
     */
    private String regFee;
    /**
     * 页面组件
     */
    private TextView regNameTxt;
    private TextView regCatalogTxt;
    private TextView regFeeTxt;
    private Button payBtn;
    /**
     * 支付选项
     */
    private RadioGroup payTypeGroup;
    private RadioButton alipayRadio;
    private RadioButton weixinRadio;
    private int choosePayType;

    /**
     * 阿里订单加签串
     */
    private String orderSign;

    final int SIGN_ALI_FAILE = 0;// ALI加签失败
    final int SIGN_ALI_SUCCESS = 1;// ALI加签成功
    final int SDK_ALI_FLAG = 2;//调用ALI支付

    final int SIGN_WEIXIN_FAILE = 3;// 微信加签失败
    final int SIGN_WEIXIN_SUCCESS = 4;// 微信加签成功
    final int SDK_WEIXIN_FLAG = 5;//调用微信支付

    public static SettleAcctActivity settleAcctActivityObj = null;

    private int canPayFlag = 1;//支付按钮可点击 0不可点击 1/可点击

    /**
     * 微信订单加签串
     */
    private IWXAPI weixinApi;//微信支付接口

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle_acct);
        QYApplication.ACTIVITY_NAME="注册费支付";
        Intent intent = getIntent();
        bizId = intent.getStringExtra("bizId");
        regName = intent.getStringExtra("regName");
        regCatalogCode = intent.getStringExtra("regCatalogCode");
        regCatalogName = intent.getStringExtra("regCatalogName");
        regFee = intent.getStringExtra("regFee");
        initTopBar();
        initView();
        //阿里沙箱环境设置
        //EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
        //微信支付设置
        weixinApi = WXAPIFactory.createWXAPI(this, BizConstants.WEIXIN_APP_ID);
        settleAcctActivityObj = this;
        canPayFlag = 1;
    }

    private void initTopBar() {
        btnOther = (Button) findViewById(R.id.top_bar_next);
        tvTitleContent = (TextView) findViewById(R.id.top_bar_content);
        btnOther.setVisibility(View.GONE);
        tvTitleContent.setText("注册缴费");
        findViewById(R.id.top_back_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(0, new Intent());
                finish();
            }
        });
        payTypeGroup = (RadioGroup) findViewById(R.id.settle_reg_pay_choose);
        alipayRadio = (RadioButton) findViewById(R.id.pay_alipay_radio) ;
        weixinRadio = (RadioButton) findViewById(R.id.pay_weixin_radio) ;
        payTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==alipayRadio.getId()){
                    choosePayType = 0; //阿里支付
                }else if(checkedId==weixinRadio.getId()){
                    choosePayType = 1; //微信支付
                }
            }
        });
    }

    private void initView() {
        regNameTxt = (TextView) findViewById(R.id.settle_reg_name_txt);
        regCatalogTxt = (TextView) findViewById(R.id.settle_reg_catalog_txt);
        regFeeTxt = (TextView) findViewById(R.id.settle_reg_fee_txt);
        payBtn = (Button) findViewById(R.id.settle_pay_btn);
        payBtn.setOnClickListener(this);
        regNameTxt.setText(regName);
        regCatalogTxt.setText(regCatalogName);
        regFeeTxt.setText(regFee + "元");
    }

    private void thirdPayEnable(boolean cando) {
        if (cando) {
            payBtn.setBackgroundResource(R.drawable.app_login);
            payBtn.setEnabled(true);
            canPayFlag = 1;
        }else{
            payBtn.setBackgroundResource(R.drawable.app_disbale);
            payBtn.setEnabled(false);
            canPayFlag = 0;
        }
    }

    public static void actionIntent(Context context, String bizId, String regName, String regCatalogCode, String regCatalogName, String regFee) {
        Intent intent = new Intent(context, SettleAcctActivity.class);
        intent.putExtra("bizId",bizId);
        intent.putExtra("regName",regName);
        intent.putExtra("regCatalogCode",regCatalogCode);
        intent.putExtra("regCatalogName",regCatalogName);
        intent.putExtra("regFee",regFee);
        ((Activity) context).startActivityForResult(intent, 999);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.settle_pay_btn:{
                if (bizId==null || bizId=="") {
                    ToastUtil.show(SettleAcctActivity.this, "注册信息不存在", Gravity.TOP);
                    return;
                }
                //判断是否可点击
                if (canPayFlag!=1) {
                    return;
                }else{
                    this.thirdPayEnable(false);
                }
                if (choosePayType == 0) {
                    //阿里支付
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("catalog",regCatalogCode);
                    map.put("bizId", bizId);
                    PayHttpUtil.postWithToken(SettleAcctActivity.this, BizConstants.BIZSERVER_ADDRESS + BizConstants.PAY_SIGN_URL, map, new VolleyListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Message msg = new Message();
                            msg.what = SIGN_ALI_FAILE;
                            Bundle bundle = new Bundle();
                            bundle.putString("errmsg","加签失败");
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void onResponse(String s) {
                            try {
                                JSONObject jsonObj = new JSONObject(s);
                                if (jsonObj.getInt("errcode") == 0) {
                                    orderSign = jsonObj.getString("tradeorder");
                                    handler.sendEmptyMessage(SIGN_ALI_SUCCESS);
                                }else{
                                    Message msg = new Message();
                                    msg.what = SIGN_ALI_FAILE;
                                    Bundle bundle = new Bundle();
                                    bundle.putString("errmsg",jsonObj.getString("errmsg"));
                                    msg.setData(bundle);
                                    handler.sendMessage(msg);
                                }
                            }catch(Exception ex){
                                Message msg = new Message();
                                msg.what = SIGN_ALI_FAILE;
                                Bundle bundle = new Bundle();
                                bundle.putString("errmsg","加签失败");
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                            }
                        }
                    });
                }else{
                    //微信支付
                    //判断是否安装微信支付
                    Toast.makeText(SettleAcctActivity.this, "开始验证微信版本...", Toast.LENGTH_SHORT).show();
                    boolean isPaySupported = weixinApi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
                    if (!isPaySupported) {
                        this.thirdPayEnable(true);
                        ToastUtil.show(SettleAcctActivity.this, "微信版本不支持支付", Gravity.TOP);
                        return;
                    }
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("catalog",regCatalogCode);
                    map.put("bizId", bizId);
                    PayHttpUtil.postWithToken(SettleAcctActivity.this, BizConstants.BIZSERVER_ADDRESS + BizConstants.WEIXIN_SIGN_URL, map, new VolleyListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Message msg = new Message();
                            msg.what = SIGN_WEIXIN_FAILE;
                            Bundle bundle = new Bundle();
                            bundle.putString("errmsg","加签失败");
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void onResponse(String s) {
                            try {
                                JSONObject jsonObj = new JSONObject(s);
                                if (jsonObj.getInt("errcode") == 0) {
                                    orderSign = jsonObj.getString("tradeorder");
                                    handler.sendEmptyMessage(SIGN_WEIXIN_SUCCESS);
                                }else{
                                    Message msg = new Message();
                                    msg.what = SIGN_WEIXIN_FAILE;
                                    Bundle bundle = new Bundle();
                                    bundle.putString("errmsg",jsonObj.getString("errmsg"));
                                    msg.setData(bundle);
                                    handler.sendMessage(msg);
                                }
                            }catch(Exception ex){
                                Message msg = new Message();
                                msg.what = SIGN_WEIXIN_FAILE;
                                Bundle bundle = new Bundle();
                                bundle.putString("errmsg","加签失败");
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                            }
                        }
                    });
                }
                break;
            }
        }
    }

    private Handler handler = new Handler() {
        String errmsg = "";
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SIGN_ALI_FAILE:
                    settleAcctActivityObj.thirdPayEnable(true);
                    orderSign = "";
                    errmsg = msg.getData().getString("errmsg");
                    ToastUtil.show(SettleAcctActivity.this, errmsg, Gravity.TOP);
                    break;
                case SIGN_ALI_SUCCESS:
                    //ToastUtil.show(SettleAcctActivity.this, orderSign,
                            //Gravity.TOP);
                    Runnable payRunnable = new Runnable() {

                        @Override
                        public void run() {
                            PayTask alipay = new PayTask(SettleAcctActivity.this);
                            Map<String, String> result = alipay.payV2(orderSign, true);

                            Message msg = new Message();
                            msg.what = SDK_ALI_FLAG;
                            msg.obj = result;
                            handler.sendMessage(msg);
                        }
                    };
                    Thread payThread = new Thread(payRunnable);
                    payThread.start();
                    break;
                case SDK_ALI_FLAG:
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(SettleAcctActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        setResult(1, new Intent());
                        finish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(SettleAcctActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        settleAcctActivityObj.thirdPayEnable(true);
                    }
                    break;
                case SIGN_WEIXIN_FAILE:
                    orderSign = "";
                    errmsg = msg.getData().getString("errmsg");
                    ToastUtil.show(SettleAcctActivity.this, errmsg, Gravity.TOP);
                    settleAcctActivityObj.thirdPayEnable(true);
                    break;
                case SIGN_WEIXIN_SUCCESS:
                    try {
                        JSONObject jsonObj = new JSONObject(orderSign);
                        PayReq req = new PayReq();
                        req.appId = jsonObj.getString("appid");
                        req.partnerId = jsonObj.getString("partnerid");
                        req.prepayId = jsonObj.getString("prepayid");
                        req.nonceStr = jsonObj.getString("noncestr");
                        req.timeStamp = jsonObj.getString("timestamp");
                        req.packageValue = jsonObj.getString("package");
                        req.sign = jsonObj.getString("sign");
                        weixinApi.registerApp(BizConstants.WEIXIN_APP_ID);
                        weixinApi.sendReq(req);
                    }catch (Exception ex){
                       Toast.makeText(SettleAcctActivity.this, "微信支付调用失败", Toast.LENGTH_LONG).show();
                        settleAcctActivityObj.thirdPayEnable(true);
                    }
                    break;
            }
        };
    };

    public static void doWeixinResponse(int flag) {
        if (flag == 0) {
            Toast.makeText(settleAcctActivityObj, "支付成功", Toast.LENGTH_SHORT).show();
            settleAcctActivityObj.setResult(1, new Intent());
            settleAcctActivityObj.finish();
        }else{
            Toast.makeText(settleAcctActivityObj, "支付失败", Toast.LENGTH_SHORT).show();
            settleAcctActivityObj.thirdPayEnable(true);
        }
    }
}
