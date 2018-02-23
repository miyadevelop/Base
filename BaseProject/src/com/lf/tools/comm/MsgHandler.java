package com.lf.tools.comm;

/**
 * 消息的处理类，对事件消息进行具体的处理
 * @author hzc 2016-3-7
 *
 */
public abstract class MsgHandler {
	
	/**
	 * 对消息进行处理
	 * @param bean
	 */
	public abstract void handlerMsg(MsgBean bean);

}
