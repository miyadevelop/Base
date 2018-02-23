package com.lf.controler.tools.download.helper;


/**
 * 解析结果
 * @author ww
 *
 * 2016-5-26
 */
public class Result<T>
{
	public boolean mIsSuccess = false;//是否解析成功
	public String mMessage = "";//解析失败的原因
	public T mBean;//解析获得到的bean
}