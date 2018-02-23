package com.lf.controler.tools.download.helper;

/**
 * 更新的周期,在网络加载的时候使用
 * @author ludeyuan
 *
 */
public enum NetEnumRefreshTime {
	None,//不缓存
	Activity,	//按照Applicaiotn或者Activity的生命周期
	Time_Second,//按照描述的间隔
	Time_Hour,	//计算时间的间隔
	Time_Day,//计算天数的间隔
	Server;	//在执行网络加载时，将上次加载的时间提交给服务端，由服务端决定是否要更新
}
