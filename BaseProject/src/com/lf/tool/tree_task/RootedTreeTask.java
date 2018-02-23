package com.lf.tool.tree_task;

import java.util.List;

/**
 * 树型任务的根节点，只可用于最后的一个节点，在此之后不会有下一个节点
 * 
 * @author hzc 2016-2-23
 * 
 */
public abstract class RootedTreeTask extends TreeTask {
	// 标识，用于区分参数值不一致的情况
	private String tag;
	
	protected abstract String setTag();// 设置一个该类型任务的标识，用于区分参数值不一致的情况


	@Override
	public String getId() {
		return super.getId() + tag;
	}

	@Override
	public void setId(String id) {
		tag = setTag();
		super.setId(id + tag);
	}

	/**
	 * 根节点不再设置下一个节点
	 */
	public final void addFootId(String id) {
		Exception e = new Exception("根节点不再具有下一个节点");
		 e.printStackTrace();
	}

	/**
	 * 根节点不再设置下一个节点
	 */
	public final List<String> getFootIds() {
		Exception e = new Exception("根节点不再具有下一个节点");
		 e.printStackTrace();
		return null;
	}

	/**
	 * 根节点不再设置下一个节点
	 */
	public final void setFootIds(List<String> footIds) {
		Exception e = new Exception("根节点不再具有下一个节点");
		 e.printStackTrace();
	}

	/**
	 * 根节点不再设置下一个节点
	 */
	public final void removeFootId(String footId) {
		Exception e = new Exception("根节点不再具有下一个节点");
		 e.printStackTrace();
	}
}
