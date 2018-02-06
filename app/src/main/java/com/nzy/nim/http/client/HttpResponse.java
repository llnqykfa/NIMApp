package com.nzy.nim.http.client;

import org.apache.commons.httpclient.Header;

import java.io.UnsupportedEncodingException;

public class HttpResponse {
	private static final String input_charset = "utf-8";

	/**
	 * 返回中的Header信息
	 */
	private Header[] responseHeaders;

	/**
	 * String类型的result
	 */
	private String stringResult;

	/**
	 * btye类型的result
	 */
	private byte[] byteResult;

	/**
	 * 
	 * 响应状态码
	 */
	private int status;

	public Header[] getResponseHeaders() {
		return responseHeaders;
	}

	public void setResponseHeaders(Header[] responseHeaders) {
		this.responseHeaders = responseHeaders;
	}

	public byte[] getByteResult() {
		if (byteResult != null) {
			return byteResult;
		}
		if (stringResult != null) {
			return stringResult.getBytes();
		}
		return null;
	}

	public void setByteResult(byte[] byteResult) {
		this.byteResult = byteResult;
	}

	public String getStringResult() throws UnsupportedEncodingException {
		if (stringResult != null) {
			return stringResult;
		}
		if (byteResult != null) {
			return new String(byteResult, input_charset);
		}
		return null;
	}

	public void setStringResult(String stringResult) {
		this.stringResult = stringResult;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
