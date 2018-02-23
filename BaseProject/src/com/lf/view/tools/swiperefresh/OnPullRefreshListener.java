package com.lf.view.tools.swiperefresh;

/**
 * 下拉刷新回调
 * @author ludeyuan
 *
 */
public interface OnPullRefreshListener {
	
	/**
	 * 实现刷新的代码
	 */
	public void onRefresh();
	
	/**
	 * 实现移动diatance距离后，界面的处理
	 * @param distance
	 */
    public void onPullDistance(int distance);

    /**
     * 目前是否可以刷新
     * @param enable true 可以刷新
     */
    public void onPullEnable(boolean enable);
}
