package com.nzy.nim.volley;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nzy.nim.activity.login.LoginActivity;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.tool.common.TokenUtils;
import com.nzy.nim.vo.QYApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/13.
 */
public class PayHttpUtil {
    private static RequestQueue mRequestQueue;

    private static void init(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }

    /**
     * 使用UserId和AccessToken请求接口
     *
     * @param context
     * @param url
     * @param params   没有参数时，可传入null （默认会添加PersonId和AccessToken）
     * @param listener
     */
    public static void postWithToken(final Context context, final String url,
                                     Map<String, String> params, final VolleyListener listener) {
        if (params == null) {//参数传入Null时进行实例化
            params = new HashMap<String, String>();
        }
        final Map<String, String> finalParams = params;
        StringRequest myReq = new UTFStringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        if (!DataUtil.isEmpty(response)) {
                            listener.onResponse(response);
                        }
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e("url", url + finalParams);
                listener.onErrorResponse(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //默认添加UserID和Token
                finalParams.put("userId", QYApplication.getPersonId());
                finalParams.put("accessToken", QYApplication.getPayToken());
                return finalParams;
            }
        };
        if (mRequestQueue == null) {
            init(context);
        }
        // 请用缓存
        myReq.setShouldCache(true);
        // 设置缓存时间10分钟
        mRequestQueue.add(myReq);
    }
}
