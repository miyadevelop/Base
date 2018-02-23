package com.lf.tools.log;

import java.util.ArrayList;

import android.content.Context;

/**
 * 封装系统的log输出，同时输出手机的基本信息、崩溃的信息并记录到文件上（SD和内存）
 * 注意：如果需要继承
 * @author ludeyuan
 *
 */
public class MyLog{
	
	/**log_switcher
	 * 添加新的TAG，否则自定义的tag禁止输出
	 * 以集合的形式添加，即使只有一项也是这样
	 */
	public static void addSubTag(ArrayList<String> subTags){
		MyLogManager.getInstance().addSubTag(subTags);
	}

	/**
	 * 输出content，并记录到文件
	 * @param content 需要打印的内容
	 */
	public static final void i(String content){
		i(MyLogManager.getInstance().getDefaultTag(),content);
	}

	/**
	 * 输出content，并记录到文件
	 * @param TAG 输出时，log中的第一个参数
	 * 			  写入文件时，作为KEY
	 * @param content 需要打印的内容 
	 */
	public static final void i(String tag,String content){
		MyLogManager.getInstance().i(tag, content);
	}

	/**
	 * 
	 * 输出content，并记录到文件
	 * @param TAG 输出时，log中的第一个参数
	 * 			  写入文件时，作为KEY
	 * @param context Activity的上下文
	 * @param content 需要打印的内容 
	 */
	public static final void i(String tag,Object object,String content){
		i(tag,object.getClass().getSimpleName()+"_"+content);//输出日志文件
	}
	
	/**
	 * 输出content，并记录到文件
	 * @param content 需要打印的内容
	 */
	public static final void d(String content){
		d(MyLogManager.getInstance().getDefaultTag(),content);
	}

	/**
	 * 输出content，并记录到文件
	 * @param TAG 输出时，log中的第一个参数
	 * 			  写入文件时，作为KEY
	 * @param content 需要打印的内容 
	 */
	public static final void d(String tag,String content){
		MyLogManager.getInstance().d(tag, content);
	}

	/**
	 * 
	 * 输出content，并记录到文件
	 * @param TAG 输出时，log中的第一个参数
	 * 			  写入文件时，作为KEY
	 * @param context Activity的上下文
	 * @param content 需要打印的内容 
	 */
	public static final void d(String tag,Context context,String content){
		d(tag,context.getClass().getSimpleName()+"_"+content);//输出日志文件
	}
	
	/**
	 * 输出content，并记录到文件
	 * @param content 需要打印的内容
	 */
	public static final void e(String content){
		e(MyLogManager.getInstance().getDefaultTag(),content);
	}

	/**
	 * 输出content，并记录到文件
	 * @param TAG 输出时，log中的第一个参数
	 * 			  写入文件时，作为KEY
	 * @param content 需要打印的内容 
	 */
	public static final void e(String tag,String content){
		MyLogManager.getInstance().e(tag, content);
	}

	/**
	 * 
	 * 输出content，并记录到文件
	 * @param TAG 输出时，log中的第一个参数
	 * 			  写入文件时，作为KEY
	 * @param context Activity的上下文
	 * @param content 需要打印的内容 
	 */
	public static final void e(String tag,Context context,String content){
		e(tag,context.getClass().getSimpleName()+"_"+content);//输出日志文件
	}
}
