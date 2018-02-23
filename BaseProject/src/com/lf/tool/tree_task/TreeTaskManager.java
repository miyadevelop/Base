package com.lf.tool.tree_task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TreeTaskManager {
	public static TreeTaskManager mTreeTaskManager;
	private Map<String, TreeTask> mTaskMap;// 以id为key,存储任务
	private TreeTaskListener mTreeTaskListener;

	private TreeTaskManager() {
		mTaskMap = new HashMap<String, TreeTask>();
		mTreeTaskListener = new TreeTaskListener() {

			@Override
			public synchronized void taskStart(String id) {
				/*
				 * 任务开始的时候,需要先去查看该任务之前的任务,未完成的话,先做之前的任务
				 */
				doFrontTask(mTaskMap.get(id));

			}

			@Override
			public synchronized void taskFinished(String id) {
				//任务完成后,去做该节点下得分支任务
				mTaskMap.get(id).setStatue(TreeTask.FINISHED);	
				doFootTask(mTaskMap.get(id));
			}

			@Override
			public void taskErr(String id) {
				// TODO Auto-generated method stub
				setFootErr(mTaskMap.get(id));
			}
		};

	}

	/**
	 * 递归的去找树形任务中,上一个任务中没有完成的任务
	 * @param task
	 */
	private void doFrontTask(TreeTask task) {
		//没有上一个任务的id,则表示这个任务已经是第一个任务了,因此直接执行
		if(task.getFrontId()==null||task.getFrontId().equals("")){
			task.setStatue(TreeTask.IS_DOING);
			task.doTask();
			return;
		}
		TreeTask frontTask = mTaskMap.get(task.getFrontId());
		//如果上一个任务的状态是已完成,那么就做当前的这个任务
		if (frontTask.getStatue() == TreeTask.FINISHED) {
			task.setStatue(TreeTask.IS_DOING);
			task.doTask();
		}
		//如果上一个任务未完成,那么就先做上一个任务
		else if (frontTask.getStatue() == TreeTask.NOT_FINISHED) {
			doFrontTask(frontTask);
			task.setStatue(TreeTask.PREPARE_DO);
		}
		//如果上一个任务正在做,或者等待被做,那么就将自己也设为等待执行
		else if(frontTask.getStatue() == TreeTask.IS_DOING||frontTask.getStatue() == TreeTask.PREPARE_DO){
			task.setStatue(TreeTask.PREPARE_DO);
		}
	}
	
	/**
	 * 任务完成后,去查找树形任务中,该任务节点之后的任务,如果处于待做的状态,则执行
	 * @param task
	 */
	private void doFootTask(TreeTask task){
		List<String> footIds = task.getFootIds();
		for(String id : footIds){
			TreeTask footTask = mTaskMap.get(id);
			if(footTask.getStatue()==TreeTask.PREPARE_DO){
				footTask.setStatue(TreeTask.IS_DOING);				
				footTask.doTask();
			}
		}
		
	}
	
	private void setFootErr(TreeTask task){
		List<String> footIds = task.getFootIds();
		for(String id : footIds){
			TreeTask footTask = mTaskMap.get(id);
			if(footTask.getStatue()==TreeTask.PREPARE_DO){
				footTask.taskErr();
			}
		}
		
	}

	public static TreeTaskManager getInstance() {
		if (mTreeTaskManager == null)
			mTreeTaskManager = new TreeTaskManager();
		return mTreeTaskManager;
	}

	/**
	 * 将新的treeTask添加到manager中
	 * @param task
	 */
	public void putTreeTask(TreeTask task) {
		//添加的时候会去判断是否已经有了这个任务，没有的话直接添加进去，如果有了的话，则不添加
		if(mTaskMap.containsKey(task.getId())){
			return;
		}else{
			mTaskMap.put(task.getId(), task);
			task.setmTreeTaskListener(mTreeTaskListener);
		}
	}
	

	interface TreeTaskListener {
		public void taskStart(String id);

		public void taskFinished(String id);
		
		public void taskErr(String id);
	}

}
