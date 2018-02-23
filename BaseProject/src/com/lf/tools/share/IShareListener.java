package com.lf.tools.share;

/**
 * 分享的回调
 * 
 * @author LinChen,code when 2015年2月11日
 */
public interface IShareListener {
	/**
	 * 开始分享
	 */
	public void onStart();

	/**
	 * 分享成功
	 * 
	 * @param platform
	 *            {@link SharePlatform}
	 */
	public void onSuccess(SharePlatform platform);

	/**
	 * 分享失败
	 * 
	 * @param platform
	 *            {@link SharePlatform}
	 */
	public void onFail(SharePlatform platform);
}
