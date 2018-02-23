package com.lf.controler.tools.download;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.lf.controler.tools.download.AbsDownload.RunOverListener;

import android.content.Context;

/**
 * 下载中心，处理一切下载任务，对下载进行宏观管理，处理下载异常，保证下载的稳定性和可控性
 * 
 * @author ww
 * 
 */
public class DownloadCenter implements RunOverListener {

	private List<AbsDownload> mThreads; // 下载线程
	private Context mContext;
	private String cookiePath;

	private static final int MAX_RUNNING_TASK_COUNT = 5; // 最大同时执行的任务数
	private volatile int mRunningTaskCount = 0; // 当前正在执行的下载任务数，这个值有可能大于MAX_RUNNING_TASK_COUNT

	private static DownloadCenter mInstance; // 单例

	private DownloadCenter(Context context) {
		mContext = context;
		mThreads = Collections.synchronizedList(new ArrayList<AbsDownload>());
	}

	/**
	 * 单例
	 * 
	 * @return 本类实例
	 */
	public static DownloadCenter getInstance(Context context) {
		if (null == mInstance)
			mInstance = new DownloadCenter(context.getApplicationContext());
		return mInstance;
	}

	/**
	 * 获取当前的所有下载线程
	 * 
	 * @return
	 */
	public List<AbsDownload> getAbsDownloads() {
		return mThreads;
	}

	/**
	 * 开始同步下载，此下载不受DownloadCenter管理。阻塞当前线程
	 * 
	 * @param task
	 * @param listener
	 */
	public void startSync(DownloadTask task, DownloadListener listener) {
		if (task != null && listener != null) {
			if (cookiePath != null) {
				if (task.cookiePath == null) {
					task.cookiePath = cookiePath;
				}
			}
			AbsDownload thread = null;
			// 这里实现了一个空的下载结束的监听，因为在下载线程中没有对RunOverListener判空
			RunOverListener nullOperate = new RunOverListener() {

				@Override
				public void onRunOver(AbsDownload thread) {
				}
			};
			if (task.mIsBreakPoint) {
				thread = new BreakpointDownloadThread(mContext, task, listener, nullOperate);
			} else {
				if (task.mIsSimple) {
					thread = new SimpleDownloadThread(mContext, task, listener, nullOperate);
				} else {
					thread = new ComplexDownloadThread(mContext, task, listener, nullOperate);
				}
			}
			thread.run();
		}
	}

	/**
	 * 开始下载，开启新线程执行
	 * 
	 * @param task
	 *            下载的任务
	 * @param listener
	 *            下载过程的监听器
	 */
	public AbsDownload start(DownloadTask task, DownloadListener listener) {
		AbsDownload thread = null;
		synchronized (mThreads) {
			// 判断任务是否已经在任务列表中了
			if (-1 == getTaskIndex(task)) {
				if (cookiePath != null) {
					if (task.cookiePath == null) {
						task.cookiePath = cookiePath;
					}
				}
				// 将任务添加到下载队列
				if (task.mIsBreakPoint) {
					thread = new BreakpointDownloadThread(mContext, task, listener, this);
				} else {
					if (task.mIsSimple) {
						thread = new SimpleDownloadThread(mContext, task, listener, this);
					} else {
						thread = new ComplexDownloadThread(mContext, task, listener, this);
					}
				}

				mThreads.add(thread);

				// 如果当前正在下载的线程小于最大线程数 或者 任务的优先级为优先 就启动下载线程
				if (mRunningTaskCount <= MAX_RUNNING_TASK_COUNT
						|| task.mPriority == DownloadTask.PRIORITY_HIGH) {
					try {
						thread.start();
						++mRunningTaskCount;
					} catch (Exception e) {
						e.printStackTrace();
						mThreads.remove(thread);
						thread.release();
						thread = null;
					}
				}
			}
		}
		return thread;
	}

	/**
	 * 暂停下载
	 * 
	 * @param id
	 *            下载任务的id
	 */
	public void pause(Object id) {
		if (id != null)
			for (AbsDownload downloadThread : mThreads) {
				if (id.equals(downloadThread.getTask().mId)) {
					downloadThread.pause();
					break;
				}
			}
	}

	/**
	 * 继续下载
	 * 
	 * @param id
	 *            下载任务的id
	 */
	public void keepOn(Object id) {
		if (id != null)
			for (AbsDownload downloadThread : mThreads) {
				if (id.equals(downloadThread.getTask().mId)) {
					downloadThread.keepOn();
					break;
				}
			}
	}

	/**
	 * 关闭所有的下载线程
	 */
	public void cancel() {
		for (AbsDownload task : mThreads) {
			task.cancel();
		}
	}

	/**
	 * 关闭对应任务id的下载
	 * 
	 * @param id
	 */
	public void cancel(Object id) {
		if (id != null)
			for (AbsDownload downloadThread : mThreads) {
				if (id.equals(downloadThread.getTask().mId)) {
					downloadThread.cancel();
					break;
				}
			}
	}

	/**
	 * 一个下载任务结束
	 * 
	 * @param id
	 */
	@Override
	public void onRunOver(AbsDownload thread) {
		// 对此方法加锁，避免多个线程同时结束后扔手榴弹。
		synchronized (mThreads) {
			mThreads.remove(thread);
			if (--mRunningTaskCount <= MAX_RUNNING_TASK_COUNT) {
				for (int i = 0; i < mThreads.size(); i++) {
					AbsDownload download = mThreads.get(i);

					if (!download.isAlive() && !download.isRunning()) {
						try {
							download.start();
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}

						break;
					}
				}
			}
		}
	}

	/**
	 * 任务队列中是否包含该任务
	 * 
	 * @return 返回任务的索引
	 */
	private int getTaskIndex(DownloadTask downloadTask) {
		for (int i = 0; i < mThreads.size(); i++) {

			AbsDownload thread = mThreads.get(i);
			DownloadTask myTask = thread.getTask();
			if (downloadTask.equals(myTask)) {//ww修改于16.5.2，重写了task的equals函数，去除了task的getSign()函数
				return i;
			}
		}
		return -1;
	}

	/**
	 * 获取对应下载任务的下载线程
	 * 
	 * @param downloadTask
	 * @return
	 */
	public AbsDownload getDownload(DownloadTask downloadTask) {
		for (int i = 0; i < mThreads.size(); i++) {

			AbsDownload thread = mThreads.get(i);
			DownloadTask myTask = thread.getTask();
			if (downloadTask.equals(myTask)) {//ww修改于16.5.2，重写了task的equals函数，去除了task的getSign()函数
				return thread;
			}
		}
		return null;
	}

	/**
	 * 是否包含这个下载任务
	 * 
	 * @param downloadTask
	 * @return
	 */
	public boolean hasDownloadTask(DownloadTask downloadTask) {
		if (getTaskIndex(downloadTask) == -1) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 销毁，释放资源
	 */
	public void release() {
		for (int i = 0; i < mThreads.size(); i++) {
			AbsDownload thread = mThreads.get(i);
			if (thread.isAlive() && !thread.isRunning()) {
				thread.cancel();
				thread.release();
			}
		}
		mThreads.clear();

		mInstance = null;
	}

	/**
	 * 获取cookie的路径，没有则返回null
	 * 
	 * @return
	 */
	public String getCookiePath() {
		return cookiePath;
	}

	/**
	 * 设置保存cookie的文件，是文件
	 * 
	 * @param cookiePath
	 */
	public void setCookiePath(String cookiePath) {
		this.cookiePath = cookiePath;
	}

}
