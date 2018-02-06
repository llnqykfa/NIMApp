package com.nzy.nim.tool.common;

import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nzy.nim.api.URLs;
import com.nzy.nim.http.client.HttpProtocolHandler;
import com.nzy.nim.http.client.HttpRequest;
import com.nzy.nim.http.client.HttpResponse;
import com.nzy.nim.http.client.HttpResultType;
import com.nzy.nim.vo.QYApplication;

import org.apache.commons.httpclient.NameValuePair;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class HttpUtil {
	private static AsyncHttpClient asynclient = new AsyncHttpClient();
	static {
		asynclient.setTimeout(10000);
	}

	/**
	 * 不带参数的get请求，返回json字符串
	 * 
	 * @param url
	 * @param responseHandler
	 */
	public static void get(String url, TextHttpResponseHandler responseHandler) {

		if(NetUtil.checkNetwork(QYApplication.getMyContexts())){
		asynclient.get(url, responseHandler);
		}else{
			ToastUtil.show(QYApplication.getMyContexts(), "无法连接到网络，请检查网络设置", Toast.LENGTH_SHORT);
		}
	}

	/**
	 * 带参数的get请求，返回json字符串
	 * 
	 * @param url
	 * @param params
	 * @param responseHandler
	 */
	public static void get(String url, RequestParams params,
						   TextHttpResponseHandler responseHandler) {
		params.setContentEncoding("utf-8");
		if(NetUtil.checkNetwork(QYApplication.getMyContexts())){
		asynclient.get(url, params, responseHandler);
		}else{
			ToastUtil.show(QYApplication.getMyContexts(), "无法连接到网络，请检查网络设置", Toast.LENGTH_SHORT);
		}
	}

	/**
	 * 不带参数的post请求，返回json字符串
	 * 
	 * @param url
	 * @param responseHandler
	 */
	public static void post(String url, TextHttpResponseHandler responseHandler) {
		if(NetUtil.checkNetwork(QYApplication.getMyContexts())){
		asynclient.post(url, responseHandler);
		}else{
			ToastUtil.show(QYApplication.getMyContexts(), "无法连接到网络，请检查网络设置", Toast.LENGTH_SHORT);
		}
	}

	/**
	 * 带参数的post请求，返回json字符串
	 * 
	 * @param url
	 * @param params
	 * @param responseHandler
	 */
	public static void post(String url, RequestParams params,
							TextHttpResponseHandler responseHandler) {
		params.setContentEncoding("utf-8");
		if(NetUtil.checkNetwork(QYApplication.getMyContexts())){
			asynclient.post(url, params, responseHandler);
		}else{
			ToastUtil.show(QYApplication.getMyContexts(), "无法连接到网络，请检查网络设置", Toast.LENGTH_SHORT);
		}

	}

	/**
	 * 带参数的get请求，返回字节码
	 *
	 * @param url
	 * @param params
	 * @param responseHandler
	 */
	public static void post(String url, RequestParams params,
							BinaryHttpResponseHandler responseHandler) {
		params.setContentEncoding("utf-8");
		if(NetUtil.checkNetwork(QYApplication.getMyContexts())){
			asynclient.post(url, params, responseHandler);
		}else{
			ToastUtil.show(QYApplication.getMyContexts(), "无法连接到网络，请检查网络设置", Toast.LENGTH_SHORT);
		}
	}

	private static SyncHttpClient getSyncHttpClient() {
		return new SyncHttpClient();
	}

	/**
	 * 同步GET请求
	 * 
	 * @param url
	 * @param params
	 * @param responseHandler
	 */
	public static void syncGet(String url, RequestParams params,
							   TextHttpResponseHandler responseHandler) {
		params.setContentEncoding("utf-8");
		if(NetUtil.checkNetwork(QYApplication.getMyContexts())){
			getSyncHttpClient().get(url, params, responseHandler);
		}else{
			ToastUtil.show(QYApplication.getMyContexts(), "无法连接到网络，请检查网络设置", Toast.LENGTH_SHORT);
		}
	}

	/**
	 * 同步无参GET请求
	 * 
	 * @param url
	 * @param responseHandler
	 */
	public static void syncGet(String url,
			TextHttpResponseHandler responseHandler) {
		if(NetUtil.checkNetwork(QYApplication.getMyContexts())){
			getSyncHttpClient().get(url, responseHandler);
		}else{
			ToastUtil.show(QYApplication.getMyContexts(), "无法连接到网络，请检查网络设置", Toast.LENGTH_SHORT);
		}
	}

	/**
	 * 同步有参数POST请求
	 * 
	 * @param url
	 * @param params
	 * @param responseHandler
	 */
	@Deprecated
	public static void syncPost(String url, RequestParams params,
								TextHttpResponseHandler responseHandler) {
		params.setContentEncoding("utf-8");
		if(NetUtil.checkNetwork(QYApplication.getMyContexts())){
			getSyncHttpClient().post(url, params, responseHandler);
		}else{
//			ToastUtil.show(QYApplication.getMyContexts(), "无法连接到网络，请检查网络设置", Toast.LENGTH_SHORT);
		}
	}

	/**
	 * 同步无参POST请求
	 * 
	 * @param url
	 * @param responseHandler
	 */
	@Deprecated
	public static void syncPost(String url,
			TextHttpResponseHandler responseHandler) {
		if(NetUtil.checkNetwork(QYApplication.getMyContexts())){
			getSyncHttpClient().post(url, responseHandler);
		}else{
			ToastUtil.show(QYApplication.getMyContexts(), "无法连接到网络，请检查网络设置", Toast.LENGTH_SHORT);
		}
	}

	/**
	 * @author LIUBO
	 * @date 2015-3-29上午11:20:07
	 * @TODO
	 * @param listStr
	 * @param type
	 * @param listener
	 */

	public static void upLoadFiles(final List<String> listStr,
								   final String type, OnPostListener listener) {
		if (listStr == null && listener != null) {
			listener.onFailure();
			return;
		}
		int len = listStr.size();// 获取List的长度
		File[] files = new File[len];
		for (int i = 0; i < len; i++) {
			// 得到图片路径
			String str = listStr.get(i);
			// 将filePath存放到数组
			files[i] = new File(str);
		}
		HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler
				.getInstance();
		HttpRequest request = new HttpRequest(HttpResultType.STRING);
		request.setUrl(URLs.UPLOAD_URL + "?type=" + type + "");
		request.setCharset("utf-8");
		request.setMethod(HttpRequest.METHOD_POST);
		try {
			HttpResponse response = httpProtocolHandler.execute(request, files);
			if (response != null && listener != null
					&& !DataUtil.isEmpty(response.getStringResult())) {
				listener.onSuccess(response.getStringResult());
			} else {
				listener.onFailure();
			}
		} catch (IOException e) {
			e.printStackTrace();
			if (listener != null)
				listener.onFailure();
		}
	}

	// 向服务器提交数据监听接口
	public interface OnPostListener {
		void onSuccess(String jsonData);

		void onFailure();
	}

	/**
	 * @author LIUBO
	 * @date 2015-3-28下午5:49:27
	 * @TODO 同步方法向服务器提交数据
	 * @param url
	 * @param listener
	 * @param pairs
	 */
	public static void syncPost(String url, OnPostListener listener,
								NameValuePair... pairs) {
		try {
			HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler
					.getInstance();
			HttpRequest request = new HttpRequest(HttpResultType.STRING);
			request.setUrl(url);
			request.setCharset("utf-8");
			request.setMethod(HttpRequest.METHOD_POST);
			request.setParameters(pairs);
			HttpResponse response = httpProtocolHandler.execute(request);
			if (response == null && listener != null)
				listener.onFailure();
			else
				listener.onSuccess(response.getStringResult());
		} catch (IOException e) {
			if (listener != null)
				listener.onFailure();
		}

	}
}