package com.lf.controler.tools;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AsynEventProcess {
	private String name = "AsynEventProcess";
	private List<Runnable> taskList;
	private Thread process;

	public int arg1;
	public int arg2;

	/**
	 * 存放一些需要保存的数据
	 */
	public Object obj;
	/**
	 * 一个不够，再来一个
	 */
	public Object other;

	public AsynEventProcess(String name) {
		this.name = name;
		taskList = Collections.synchronizedList(new LinkedList<Runnable>());
		process = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true && !Thread.interrupted()) {
					try {
						if (taskList.size() == 0) {
							synchronized (process) {
								process.wait();
							}
						}
						Runnable runnable = taskList.remove(0);
						runnable.run();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}, this.name);
		process.start();
	}

	public AsynEventProcess() {
		this("AsynEventProcess");
	}

	public void addFirst(Runnable run) {
		if (run != null) {
			synchronized (process) {
				taskList.add(0, run);
				nodifyDo();
			}
		}
	}

	public void add(Runnable run) {
		this.addLast(run);
	}

	public void addLast(Runnable run) {
		if (run != null) {
			synchronized (process) {
				taskList.add(run);
				nodifyDo();
			}
		}
	}

	private void nodifyDo() {
		process.notify();
	}

	/**
	 * 告之结束，等运行完所有流程后，释放资源
	 */
	public void end() {
		addLast(new Runnable() {

			@Override
			public void run() {
				obj = null;
				other = null;
				taskList.clear();
				process.interrupt();
			}
		});
	}

	/**
	 * 立即结束，释放资源
	 */
	public void shutdown() {
		process.interrupt();
		addFirst(new Runnable() {

			@Override
			public void run() {
				obj = null;
				other = null;
				taskList.clear();
			}
		});
	}
}
