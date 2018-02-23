package com.lf.tool.tree_task;

import java.util.ArrayList;
import java.util.List;

import com.lf.tool.tree_task.TreeTaskManager.TreeTaskListener;

/**
 * 树型任务结构的节点。
 * @author hzc 2016-2-23
 *
 */
public abstract class TreeTask {
	// 该任务的id
	private String id;
	private String frontId;
	private List<String> footIds;
	// public Object task;
	private int statue;// 任务状态,包括未做,正在做,已完成,等待执行.
	private TreeTaskListener mTreeTaskListener;
	
//	private List<String> mParamsList;
    
	
	protected abstract void doTask();// 子类实现具体怎么做这个任务
	protected abstract void taskErr();// 子类实现失败的处理

	// 任务的状态
	public static final int PREPARE_DO = 0;// 准备执行
	public static final int NOT_FINISHED = 1;// 没有完成
	public static final int IS_DOING = 2;// 正在做
	public static final int FINISHED = 3;// 已完成

	public TreeTask() {
		footIds = new ArrayList<String>();
//		mParamsList = new  ArrayList<String>();
		statue = NOT_FINISHED;
	}
	

	/**
	 * 准备执行该任务,会先去执行该任务之前的任务
	 */
	public void prepareToDo() {
		mTreeTaskListener.taskStart(id);
	}

	// 任务完成后后,由实现的地方去调用
	public void taskFinished() {
		mTreeTaskListener.taskFinished(id);
	}
	
	public void setTaskErr() {
		mTreeTaskListener.taskErr(id);
	}

	public void addFootId(String id) {
		if (!footIds.contains(id))
			footIds.add(id);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFrontId() {
		return frontId;
	}

	public void setFrontId(String frontId) {
		this.frontId = frontId;
	}

	public List<String> getFootIds() {
		return footIds;
	}

	public void setFootIds(List<String> footIds) {
		this.footIds = footIds;
	}
	

	public void removeFootId(String footId) {
		if(this.footIds.contains(footId)){
			this.footIds.remove(footId);
		}
	}

	public int getStatue() {
		return statue;
	}

	public void setStatue(int statue) {
		this.statue = statue;
	}

	/**
	 * 不允许外界调用设置监听器的方法
	 */
	protected void setmTreeTaskListener(TreeTaskListener mTreeTaskListener) {
		this.mTreeTaskListener = mTreeTaskListener;
	}

}
