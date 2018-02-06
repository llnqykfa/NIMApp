package com.nzy.nim.wxapi;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.nzy.nim.activity.main.SettleAcctActivity;
import com.nzy.nim.constant.BizConstants;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.nzy.nim.R;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weixin_pay_result);
    	api = WXAPIFactory.createWXAPI(this, BizConstants.WEIXIN_APP_ID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(final BaseResp resp) {
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			if (resp.errCode == 0) {
				SettleAcctActivity.doWeixinResponse(0);
				finish();
			}else{
				SettleAcctActivity.doWeixinResponse(-1);
				finish();
			}
		}
	}
}