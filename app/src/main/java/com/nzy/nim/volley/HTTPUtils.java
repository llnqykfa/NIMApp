package com.nzy.nim.volley;

/**
 * Copyright 2013 Ognyan Bankov
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
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
 * Helper class that is used to provide references to initialized
 * RequestQueue(s) and ImageLoader(s)
 *
 * @author Ognyan Bankov
 */
public class HTTPUtils {
    private static RequestQueue mRequestQueue;

    private HTTPUtils() {
    }

    private static void init(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);

    }

    public static void post(final Context context, final String url,
                            final Map<String, String> params, final VolleyListener listener) {
        StringRequest myReq = new UTFStringRequest(Method.POST, url,
                new Listener<String>() {
                    public void onResponse(String response) {
                        if (!DataUtil.isEmpty(response)) {
                            listener.onResponse(response);
                            try {
                            int errcode = new JSONObject(response).getInt("errcode");
                                if (errcode == 100) {//需要重新登陆
                                    ((Activity) context).finish();
                                    ToastUtil.showShort(context, "请重新登入");
                                    context.startActivity(new Intent(context, LoginActivity.class));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        if (mRequestQueue == null) {
            init(context);
        }
        // 请用缓存
        myReq.setShouldCache(true);
        // 设置缓存时间10分钟
        // myReq.setCacheTime(10*60);
        mRequestQueue.add(myReq);
    }
    public static void posT(final Context context, final String url,
                            final Map<String, String> params, final VolleyListener listener) {
        StringRequest myReq = new UTFStringRequest(Method.POST, url,
                new Listener<String>() {
                    public void onResponse(String response) {
                        if (!DataUtil.isEmpty(response)) {
                            try {
                            int errcode = new JSONObject(response).getInt("errCode");
                                if(errcode==0){
                                    listener.onResponse(response);
                                }else if (errcode == 100) {//需要重新登陆
                                    ((Activity) context).finish();
                                    ToastUtil.showShort(context, "请重新登入");
                                    context.startActivity(new Intent(context, LoginActivity.class));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        if (mRequestQueue == null) {
            init(context);
        }
        // 请用缓存
        myReq.setShouldCache(true);
        // 设置缓存时间10分钟
        // myReq.setCacheTime(10*60);
        mRequestQueue.add(myReq);
    }

    /**
     * 使用PersonId和AccessToken请求接口
     * @param context
     * @param url
     * @param params   没有参数时，可传入null （默认会添加PersonId和AccessToken）
     * @param listener
     */
    public static void postWithToken(final Context context, final String url,
                                     Map<String, String> params, final VolleyListener listener) {
        if (params==null){//参数传入Null时进行实例化
            params=new HashMap<String, String>();
        }
        final Map<String, String> finalParams = params;
        StringRequest myReq = new UTFStringRequest(Method.POST, url,
                new Listener<String>() {
                    public void onResponse(String response) {
                        if (!DataUtil.isEmpty(response)) {
                            try {
                                int errcode = new JSONObject(response).getInt("errcode");
                                if (errcode == 101) {//需要刷新AccessToken
                                    TokenUtils.getInstance().refreshAccessToken(context, url, finalParams, listener);
                                } else if (errcode == 100) {//需要重新登陆
                                    ((Activity) context).finish();
                                    ToastUtil.showShort(context, "请重新登入");
                                    context.startActivity(new Intent(context, LoginActivity.class));
                                } else {
                                    if (errcode==0){
                                        listener.onResponse(response);
                                    }else{
                                        listener.onResponse(response);
//                                        ToastUtil.showShort(context,new JSONObject(response).getString("errmsg"));
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e("url",url+finalParams);
                listener.onErrorResponse(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //默认添加PersonID和Token
                finalParams.put("personId", QYApplication.getPersonId());
                finalParams.put("accessToken", QYApplication.getAccessToken());
                return finalParams;
            }
        };
        if (mRequestQueue == null) {
            init(context);
        }
        // 请用缓存
        myReq.setShouldCache(true);
        // 设置缓存时间10分钟
        // myReq.setCacheTime(10*60);
        mRequestQueue.add(myReq);
    }
    /**
     * 使用PersonId和AccessToken请求接口
     * @param context
     * @param url
     * @param params   没有参数时，可传入null （默认会添加PersonId和AccessToken）
     * @param listener
     */
    public static void postToken(final Context context, final String url,
                                 Map<String, String> params, final VolleyListener listener) {
        if (params==null){//参数传入Null时进行实例化
            params=new HashMap<String, String>();
        }
        final Map<String, String> finalParams = params;
        StringRequest myReq = new UTFStringRequest(Method.POST, url,
                new Listener<String>() {
                    public void onResponse(String response) {
                        Log.e("url" ,url+finalParams.toString());
                        if (!DataUtil.isEmpty(response)) {
                            try {
                                int errcode = new JSONObject(response).getInt("errCode");
                                if (errcode == 101) {//需要刷新AccessToken
                                    TokenUtils.getInstance().refreshAccessToken(context, url, finalParams, listener);
                                } else if (errcode == 100) {//需要重新登陆
                                    ((Activity) context).finish();
                                    ToastUtil.showShort(context, "请重新登入");
                                    context.startActivity(new Intent(context, LoginActivity.class));
                                } else {
                                    if (errcode==0){
                                        listener.onResponse(response);
                                    }else{
                                        ToastUtil.showShort(context,new JSONObject(response).getString("errMsg"));
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //默认添加PersonID和Token
                finalParams.put("personId", QYApplication.getPersonId());
                finalParams.put("accessToken", QYApplication.getAccessToken());
                return finalParams;
            }
        };
        if (mRequestQueue == null) {
            init(context);
        }
        // 请用缓存
        myReq.setShouldCache(true);
        // 设置缓存时间10分钟
        // myReq.setCacheTime(10*60);
        mRequestQueue.add(myReq);
    }

    public static void get(Context context, String url,
                           final VolleyListener listener) {
        StringRequest myReq = new UTFStringRequest(Method.GET, url,
                new Listener<String>() {
                    public void onResponse(String response) {
                        listener.onResponse(response);
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        });
        if (mRequestQueue == null) {
            init(context);
        }
        mRequestQueue.add(myReq);
    }

    private static RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue != null) {
            return mRequestQueue;
        } else {
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }

    public static void cancelAll(Context context) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(context);
        }
    }

    /**
     * Returns instance of ImageLoader initialized with {@see FakeImageCache}
     * which effectively means that no memory caching is used. This is useful
     * for images that you know that will be show only once.
     *
     * @return
     */
    // public static ImageLoader getImageLoader() {
    // if (mImageLoader != null) {
    // return mImageLoader;
    // } else {
    // throw new IllegalStateException("ImageLoader not initialized");
    // }
    // }
}
