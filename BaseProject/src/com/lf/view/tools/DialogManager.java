package com.lf.view.tools;

import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.view.View;

/**
 * 管理Dialog的显示和隐藏
 * @author ludeyuan
 *
 */
public class DialogManager {

	private static DialogManager mManager;
	private HashMap<String,CommonDialog> mShowDialogs;//正在显示的dialog

	private DialogManager(){
		mShowDialogs = new HashMap<String, CommonDialog>();
	}

	/**
	 * 获取到DialogManager对象
	 */
	public static DialogManager getDialogManager(){
		if(mManager==null){
			mManager = new DialogManager();
		}
		return mManager;
	}

	public CommonDialog init(Activity activity,View xmlLayout,HashMap<Integer, String> idAndContents
			,String dialogTag,DialogClickListener listener){
		//避免不同界面用了相同的tag导致对话框无法正常弹出
		dialogTag = dialogTag + activity.toString();
		//如果已经显示的,就不需要再显示
		if(mShowDialogs.containsKey(dialogTag)){
			return mShowDialogs.get(dialogTag);
		}

		CommonDialog dialog = new CommonDialog(activity, idAndContents, xmlLayout);
		dialog.setCommonDialogListener(listener, dialogTag);
		mShowDialogs.put(dialogTag, dialog);
		return dialog;
	}

	/**
	 * 显示Dialog对话框
	 * @param activity Activity的上下文
	 * @param xmlLayout Dialog中的布局文件
	 * @param idAndContents xmlLayout布局中的控件的id和对应的值
	 * @param dialogTag dialog的标示，在隐藏Dialog时，需要传入同样值，才可以隐藏Dialog
	 * @param listener 监听Dialog中的点击
	 */
	public void onShow(Activity activity,View xmlLayout,HashMap<Integer, String> idAndContents
			,String dialogTag,DialogClickListener listener){
		//避免不同界面用了相同的tag导致对话框无法正常弹出
		dialogTag = dialogTag + activity.toString();
		//如果已经显示的,就不需要再显示
		if(mShowDialogs.containsKey(dialogTag)){
			CommonDialog dialog = mShowDialogs.get(dialogTag);
			dialog.onShow();
			return;
		}

		CommonDialog dialog = new CommonDialog(activity, idAndContents, xmlLayout);
		dialog.setCommonDialogListener(listener, dialogTag);
		mShowDialogs.put(dialogTag, dialog);
		dialog.onShow();
	}

	/**
	 * 隐藏已经显示的Dialog
	 * @param dialogTag Dialog的tag，需要和onShow中的tag保持一致
	 */
	public void onCancel(Activity activity, String dialogTag){
		//避免不同界面用了相同的tag导致对话框无法正常弹出
		dialogTag = dialogTag + activity.toString();

		CommonDialog dialog = mShowDialogs.get(dialogTag);
		if(dialog==null){//没有获取到Dialog,就不处理
			return;
		}
		mShowDialogs.remove(dialogTag);
		//在DialogOnCancel的时候，释放Dialog,这样可以确保浪费内存
		dialog.onCancel();
		dialog.onDestory();
	}

	/**
	 * 销毁缓存中所有的Dialog
	 */
	public void onDestory(){
		Iterator<String> keys=mShowDialogs.keySet().iterator();
		while(keys.hasNext()){
			String hashMapKey=keys.next();
			CommonDialog dialog = mShowDialogs.get(hashMapKey);
			dialog.onDestory();
		}
		mShowDialogs.clear();
		mManager = null;
	}

	/**
	 * 销毁缓存中Activity界面上的所有的dialog
	 * @param activity
	 */
	public void onDestory(Activity activity){
		if(activity==null){
			return;
		}

		Iterator<String> keys=mShowDialogs.keySet().iterator();
		while(keys.hasNext()){
			String hashMapKey=keys.next();
			CommonDialog dialog = mShowDialogs.get(hashMapKey);
			if(dialog.getContextName().equals(activity.toString())){
				mShowDialogs.remove(hashMapKey);
				dialog.onDestory();
			}

		}
	}


	/**
	 * 返回对话框中的view
	 * @param activity
	 * @param dialogTag 对话框的标识
	 * @param id view 的id
	 * @return
	 */
	public View findViewById(Activity activity , String dialogTag, int id)
	{
		//避免不同界面用了相同的tag导致对话框无法正常弹出
		dialogTag = dialogTag + activity.toString();

		CommonDialog dialog = mShowDialogs.get(dialogTag);
		if(dialog==null){//没有获取到Dialog,就不处理
			return null;
		}
		return dialog.findViewById(id);
	}
}
