package com.lf.controler.tools.download;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

/**
 * 请求参数的封装
 * 
 * @author LinChen
 * 
 */
public class RequestParams {

	public static final String CONTENT_TYPE_JSON = "application/json";
	public static final String CONTENT_TYPE_UPLOAD_FILE = "multipart/form-data";

	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";
	public static final String METHOD_DELETE = "DELETE";
	public static final String METHOD_PUT = "PUT";

	/**
	 * 内容类型
	 */
	private String contentType;
	/**
	 * 键值对
	 */
	private Map<String, String> paramMap;
	/**
	 * 文件的键值对
	 */
	private Map<String, File> fileMap;
	/**
	 * 边界标识
	 */
	private String boundary;
	/**
	 * 查询参数
	 */
	private String queryString;
	/**
	 * 请求的方法
	 */
	private String requestMethod = METHOD_GET;

	public RequestParams() {
		paramMap = new HashMap<String, String>();
		fileMap = new HashMap<String, File>();
	}

	public void addBodyParameter(String key, String value) {
		paramMap.put(key, value);
	}

	public void addBodyParameter(String key, File value) {
		if (boundary == null) {
			boundary = UUID.randomUUID().toString();
		}
		requestMethod = METHOD_POST;
		contentType = CONTENT_TYPE_UPLOAD_FILE + ";boundary=" + boundary;
		fileMap.put(key, value);
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public boolean isPost() {
		return METHOD_POST.equals(requestMethod);
	}

	public void setPost(boolean isPost) {
		if (isPost) {
			requestMethod = METHOD_POST;
		} else {
			requestMethod = METHOD_GET;
		}
	}

	public Map<String, String> getParamMap() {
		return paramMap;
	}

	public Map<String, File> getFileMap() {
		return fileMap;
	}

	/**
	 * 获取传递的内容
	 * 
	 * @return
	 */
	public String getQueryString() {
		if (queryString != null) {
			return queryString;
		} else {
			StringBuilder sb = new StringBuilder();
			Iterator<Entry<String, String>> iter = paramMap.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, String> entry = iter.next();
				String key = entry.getKey();
				String value = entry.getValue();
				sb.append(key).append("=").append(value);
				sb.append("&");
			}
			return sb.substring(0, sb.length() - 1);
		}
	}

	/**
	 * 设置传递内容
	 * 
	 * @param queryString
	 */
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	protected String getBoundary() {
		return boundary;
	}

	/**
	 * 获取请求方法
	 * 
	 * @return
	 */
	public String getRequestMethod() {
		return requestMethod;
	}

	/**
	 * 设置请求方法<br>
	 * {@link #METHOD_DELETE}、{@link #METHOD_GET}、{@link #METHOD_POST}、{@link #METHOD_PUT}
	 * 
	 * @param requestMethod
	 */
	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

}
