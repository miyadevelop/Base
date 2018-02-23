package com.lf.controler.tools.download.helper;

/**
 * 网络加载结果的接口，定义了加载成功和失败的回调
 * @author ludeyuan
 *
 * @param <T>
 */
public abstract interface NetLoaderResult<T> {
	
	/**
	 * 加载网络的数据失败
	 * @param paramInt
	 * @param paramString 失败的原因
	 */
	public abstract void onErr(int paramInt);
	
	/**
	 * 加载网络端的数据成功
	 * @param paramT
	 */
	public abstract void onSuccess(T paramT);
}
