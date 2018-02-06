package com.nzy.nim.http.client;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.httpclient.util.IdleConnectionTimeoutThread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class HttpProtocolHandler {

	private static String DEFAULT_CHARSET = "utf-8";

	/** 连接超时时间，由bean factory设置，缺省为9秒钟 */
	private int defaultConnectionTimeout = 10000;

	/** 回应超时时间, 由bean factory设置，缺省为25秒钟 */
	private int defaultSoTimeout = 25000;

	/** 闲置连接超时时间, 由bean factory设置，缺省为50秒钟 */
	private int defaultIdleConnTimeout = 50000;

	private int defaultMaxConnPerHost = 30;

	private int defaultMaxTotalConn = 80;

	private static final long defaultHttpConnectionManagerTimeout = 3 * 10000;

	private HttpConnectionManager connectionManager;

	private static HttpProtocolHandler httpProtocolHandler = new HttpProtocolHandler();

	public static HttpProtocolHandler getInstance() {
		return httpProtocolHandler;
	}

	private HttpProtocolHandler() {
		
		connectionManager = new MultiThreadedHttpConnectionManager();
		connectionManager.getParams().setDefaultMaxConnectionsPerHost(
				defaultMaxConnPerHost);
		connectionManager.getParams().setMaxTotalConnections(
				defaultMaxTotalConn);

		IdleConnectionTimeoutThread ict = new IdleConnectionTimeoutThread();
		ict.addConnectionManager(connectionManager);
		ict.setConnectionTimeout(defaultIdleConnTimeout);

		ict.start();
	}

	/**
	 * 调用例子
	 * 	HttpProtocolHandler httpProtocolHandler=HttpProtocolHandler.getInstance();
		HttpRequest request=new HttpRequest(HttpResultType.STRING);
		request.setUrl("http://localhost:8080/web/upload");
		request.setCharset("utf-8");
		request.setMethod(HttpRequest.METHOD_POST);
		File[] files={new File("d:\\占座平台详细开发计划V1.0(1).xlsx"),new File("d:\\二维码.gif")};
		HttpResponse response=httpProtocolHandler.execute(request,files);
	 * 	 
	 * */
	public HttpResponse execute(HttpRequest request,File...files) throws HttpException, IOException {
		HttpClient httpclient = new HttpClient(connectionManager);

		// 设置连接超时
		int connectionTimeout = defaultConnectionTimeout;
		if (request.getConnectionTimeout() > 0) {
			connectionTimeout = request.getConnectionTimeout();
		}
		httpclient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(connectionTimeout);

		// 设置回应超时
		int soTimeout = defaultSoTimeout;
		if (request.getTimeout() > 0) {
			soTimeout = request.getTimeout();
		}
		httpclient.getHttpConnectionManager().getParams()
				.setSoTimeout(soTimeout);

		httpclient.getParams().setConnectionManagerTimeout(
				defaultHttpConnectionManagerTimeout);

		String charset = request.getCharset();
		charset = charset == null ? DEFAULT_CHARSET : charset;
		HttpMethod method = null;

		
		if (request.getMethod().equals(HttpRequest.METHOD_GET)) {
			method = new GetMethod(request.getUrl());
			method.getParams().setCredentialCharset(charset);
			method.setQueryString(request.getQueryString());
		} else if (null==files||0==files.length) {
			method = new PostMethod(request.getUrl());
			((PostMethod) method).addParameters(request.getParameters());
			method.addRequestHeader("Content-Type","application/x-www-form-urlencoded; text/html; charset="+ charset);
		} else {
			// post模式且带上传文件
			method = new PostMethod(request.getUrl());
			if(null==files||0==files.length)
				return null;
			List<Part> parts = new ArrayList<Part>();
			for(int i=0;i<files.length;i++){
				parts.add(new CodeFilePart(files[i].getName(), files[i]));
			}
			int size=parts.size();
			((PostMethod) method).addParameters(request.getParameters());
			((PostMethod) method).setRequestEntity(new MultipartRequestEntity(parts.toArray(new Part[size]),
					method.getParams()));
			
			
		}
		// 设置Http Header中的User-Agent
		method.addRequestHeader("User-Agent", "Mozilla/4.0");
		HttpResponse response = new HttpResponse();

		try {
			int status=httpclient.executeMethod(method);
			response.setStatus(status);
			if (request.getResultType().equals(HttpResultType.STRING)) {
				response.setStringResult(method.getResponseBodyAsString());
			} else if (request.getResultType().equals(HttpResultType.BYTES)) {
				response.setByteResult(method.getResponseBody());
			}
			response.setResponseHeaders(method.getResponseHeaders());
		} catch (UnknownHostException ex) {

			return null;
		} catch (IOException ex) {

			return null;
		} catch (Exception ex) {
			
			return null;
		} finally {
			method.releaseConnection();
			connectionManager.closeIdleConnections(0);
		}
		return response;
	}

	/**
	 * 将NameValuePairs数组转变为字符串
	 * 
	 * @param nameValues
	 * @return
	 */
	protected String toString(NameValuePair[] nameValues) {
		if (nameValues == null || nameValues.length == 0) {
			return "null";
		}

		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < nameValues.length; i++) {
			NameValuePair nameValue = nameValues[i];

			if (i == 0) {
				buffer.append(nameValue.getName() + "=" + nameValue.getValue());
			} else {
				buffer.append("&" + nameValue.getName() + "="
						+ nameValue.getValue());
			}
		}

		return buffer.toString();
	}
	 class CodeFilePart extends FilePart {
		    public CodeFilePart(String filename, File file)
		            throws FileNotFoundException {
		        super(filename, file);  
		    }  
		    protected void sendDispositionHeader(OutputStream out) throws IOException {
		        super.sendDispositionHeader(out);  
		        String filename = getSource().getFileName();
		        if (filename != null) {  
		            out.write(EncodingUtil.getAsciiBytes(FILE_NAME));
		            out.write(QUOTE_BYTES);  
		            out.write(EncodingUtil.getBytes(filename, "utf-8"));
		            out.write(QUOTE_BYTES);  
		        }  
		    }  
	}  
}
