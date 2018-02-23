package com.lf.view.tools.swiperefresh;

/**
 * 上拉加载更多的接口
 * @author ludeyuan
 *
 */
public interface OnPushLoadMoreListener {
	
	/**
	 * 实现加载更多数据的操作
	 */
	public void onLoadMore();
	
	/**
	 * 手指上拉过程中，界面可以自定义的动画
	 * @param distance
	 */
    public void onPushDistance(int distance);
    
    /**
     * 目前是否可以刷新
     * @param enable true可以刷新
     */
    public void onPushEnable(boolean enable);

}
