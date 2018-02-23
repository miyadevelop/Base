package com.lf.tools.log;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 捕捉错误异常
 * @author ludeyuan
 *
 */
public class UncaughtException implements Thread.UncaughtExceptionHandler{
		
	public UncaughtException(String tag){
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable exception) {
		StringWriter stackTrace = new StringWriter();
		new PrintWriter(stackTrace);
		
		//输出错误信息
		MyLogManager.getInstance().e(MyLogManager.getInstance().getDefaultTag()
				, stackTrace.toString());
	}
	
}
