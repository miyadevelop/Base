package com.lf.controler.tools.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

/**
 * 一个下载任务的相关数据<br>
 * 如果想同时下载多个任务,可以用createMulti创建多个任务
 * 
 * @author ww
 * 
 */
public class DownloadTask {

	/**
	 * cookie可写
	 */
	public static final int COOKIE_WRITEABLE = 1;
	/**
	 * cookie可读
	 */
	public static final int COOKIE_READABLE = 2;
	/**
	 * cookie可读可写
	 */
	public static final int COOKIE_ENABLE = 3;

	/**
	 * 任务的标签，通常表示下载的什么内容，例如下载的是通讯录中的好友头像
	 */
	public Object mTag;
	/**
	 * 任务的id，和mTag共同作用，不同的任务，在mTag相同的情况下，mId不能相同，例如mTag表示下载的是通讯录中的好友头像，mId就表示是哪一个好友的头像
	 */
	public Object mId;
	/**
	 * 任务的路径。如果包含多个任务，用
	 */
	public String mUrl;
	/**
	 * 下载后存储的路径，此值为null时不进行存储，将下载获取的InputStream反馈给下载监听者
	 */
	public String mPath;
	/**
	 * 是否是简单的任务，简单的任务只能通知下载完成
	 */
	public boolean mIsSimple;
	/**
	 * 是否是断点下载
	 */
	public boolean mIsBreakPoint;
	/**
	 * 下载的优先级，默认为高优先级<br>
	 * 取值PRIORITY_HIGH或者PRIORITY_LOW
	 */
	public int mPriority = PRIORITY_HIGH;

	/**
	 * 设置下载任务超时的时间
	 */
	public long mTimeout = 20000l;

	/**
	 * 设置请求参数，以后将用这个参数代替queryString、isPost等
	 */
	public RequestParams requestParams;

	public int retryTime = 0;
	/**
	 * 是否是post的方式提交数据
	 */
	@Deprecated
	public boolean isPost = false;
	/**
	 * post方式传递的数据
	 */
	public String queryString = "";
	/**
	 * 错误的信息
	 */
	public Throwable throwable;
	/**
	 * 设置路径后能够保存和获取cookie属性
	 */
	public String cookiePath;
	/**
	 * cookie是否能用
	 */
	public int cookieStatus;
	/**
	 * 设置请求方法
	 */
	public String requestMethod;
	
	/**
	 * 访问的头部
	 */
	public HashMap<String,String> head;

	/**
	 * 较高优先级，立刻下载
	 */
	public static final int PRIORITY_HIGH = 0;
	/**
	 * 低优先级，等待前面的下载任务完成后才下载
	 */
	public static final int PRIORITY_LOW = 1;

	/**
	 * 下载的全局属性
	 */
	private Properties downloadProperties;

	private File propertyFile;

	protected void setFileLength(long length) {
		Properties properties = getProperties();
		properties.setProperty("length", String.valueOf(length));

		try {
			FileOutputStream fos = new FileOutputStream(propertyFile);
			properties.store(fos, "record your properties");
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void delPropertyFile() {
		if (propertyFile != null) {
			propertyFile.delete();
		}
	}

	public long getFileLength() {
		Properties properties = getProperties();
		String strLong = properties.getProperty("length", "");
		return "".equals(strLong) ? 0l : Long.parseLong(strLong);
	}

	private Properties getProperties() {
		if (downloadProperties == null) {
			downloadProperties = new Properties();
			propertyFile = new File(mPath + ".record");
			if (propertyFile.exists()) {
				try {
					downloadProperties.load(new FileInputStream(propertyFile));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return downloadProperties;
	}

	protected void release() {
		mTag = null;
		mId = null;
		downloadProperties = null;
	}


	/**
	 * 用来获取下载任务的标识
	 * 
	 * @return
	 */
	@Deprecated//ww删除于16.5.2，该函数主要用在task的比较重，故重写了equals函数来代替此函数，避免返回的标签和mPath、mId、mTag几个概念混淆
	protected String getSign() {
		if (mPath != null) {
			if (mPath.indexOf(".bu") == -1) {
				return mPath;
			}
			return mPath.substring(0, mPath.length() - 3);
		}
		String tag = mTag == null ? "" : mTag.toString();
		return mId.toString() + tag;
	}
	
	
	@Override
	public boolean equals(Object object)
	{
		if(null == object)
			return false;
		
		if(!(object instanceof DownloadTask))
			return false;
		
		DownloadTask task = (DownloadTask)object;
		
		String path;
		if(null != mPath)//如果两者的path相同，则返回true
		{
			if (mPath.indexOf(".bu") == -1) {
				path = mPath;
			}else{
				path = mPath.substring(0, mPath.length() - 3);
			}
			
			if(path.equals(task.mPath))
				return true;
		}
		
		
		if(null != mTag && mTag.equals(task.mTag))//tag相同
		{
			if(null == mId)//都没有id标识，则返回true，否则返回false
			{
				return null == task.mId;
			}
			else//有id标识，并且id相同返回true，否则返回false
			{
				return mId.equals(task.mId);
			}
		}
		
		return false;
	
	}

	
	public void addHead(String key, String value)
	{
		if(null == head)
			head = new HashMap<String, String>();
		head.put(key, value);
	}
}
