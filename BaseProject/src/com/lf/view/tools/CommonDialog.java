package com.lf.view.tools;

import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 对系统的Dialog进行封装，使得调用更加方便，快捷
 * 注意：这里涉及到Dialog文件中具体的样式，显示的样式只会在xml文件中体现
 * @author ludeyuan
 *
 */
public class CommonDialog implements OnClickListener{

	private Context mContext;
	private Dialog mDialog;
	private DialogClickListener mListener;
	private String mDialogId;//Dialog的身份ID
	private final int BACKGROUD_COLOR = 0x00000000;	//半透明的黑色
	private View mContentView;


	/**
	 * 设置Dialog中具体View的点击监听
	 */
	public void setCommonDialogListener(DialogClickListener listener,String dialogId){
		mDialogId = dialogId;
		mListener = listener;
	}

	/**
	 * 实例化Dialog
	 * @param idAndContents 存放View中的id（和xml文件中的对应）和内容
	 * @param xmlLayout Dialog需要采用的布局，不能为null
	 */
	public CommonDialog(Context context,HashMap<Integer, String> idAndContents,View xmlLayout){
		mContext = context;
		if(xmlLayout==null){
			throw new NullPointerException("layout in CommonDialog cannot be empty");
		}

		mContentView = xmlLayout;

		//设置Dialog
		mDialog = new Dialog(context);
		mDialog.setCancelable(true);
		Window window = mDialog.getWindow();
		window.requestFeature(Window.FEATURE_NO_TITLE);
		ColorDrawable backGround = new ColorDrawable(BACKGROUD_COLOR);
		window.setBackgroundDrawable(backGround);

		int dialogWidth = ScreenParameter.getDisplayWidthAndHeight(context)[0];
		if(null == mLayoutParams)
			mLayoutParams = new LinearLayout.LayoutParams(
					dialogWidth*9/10,LinearLayout.LayoutParams.WRAP_CONTENT);
		mDialog.setContentView(xmlLayout, mLayoutParams);
		if(idAndContents==null || idAndContents.size()==0){
			return;
		}

		//为xml中的控件赋值，并执行点击回调
		Iterator<Integer> idItera = idAndContents.keySet().iterator();
		while(idItera.hasNext()){
			int id = idItera.next();
			String value = idAndContents.get(id);
			View itemView = (View)xmlLayout.findViewById(id);
			if(itemView instanceof TextView){
				((TextView)itemView).setText(value);
			}else if(itemView instanceof Button){
				((Button) itemView).setText(value);
			}
			else if(itemView instanceof EditText){
				((EditText) itemView).setText(value);
			}
			//设置监听
			itemView.setOnClickListener(this);
		}
	}

	/**
	 * 获取到当前Activity的地址
	 * @return
	 */
	public String getContextName(){
		return mContext.toString();
	}

	/**
	 * 显示Dialog
	 */
	public void onShow(){
		if(((Activity)mContext).isFinishing())
			return;

		if(mDialog!=null&&!mDialog.isShowing())
			mDialog.show();
	}

	/**
	 * 隐藏Dialog
	 */
	public void onCancel(){
		if(((Activity)mContext).isFinishing())
			return;

		if(mDialog!=null&&mDialog.isShowing())
			mDialog.cancel();
	}

	/**
	 * 界面退出后，销毁Dialog
	 */
	public void onDestory(){
		onCancel();//先隐藏Dialog
		mListener = null;
		mContext = null;
		mDialog = null;
		mDialogId =null;
	}

	@Override
	public void onClick(View view) {
		if(mListener!=null)
			mListener.onDialogItemClick(view,mDialogId);
	}


	public View findViewById(int id)
	{
		return mContentView.findViewById(id);
	}


	public void setGravity(int gravity)
	{
		mDialog.getWindow().setGravity(gravity);
	}


	LinearLayout.LayoutParams mLayoutParams;
	public void setContentView(View xmlLayout, LinearLayout.LayoutParams params, HashMap<Integer, String> idAndContents)
	{
		mContentView = xmlLayout;
		mLayoutParams = params;
		mDialog.setContentView(xmlLayout, mLayoutParams);
		//为xml中的控件赋值，并执行点击回调
		Iterator<Integer> idItera = idAndContents.keySet().iterator();
		while(idItera.hasNext()){
			int id = idItera.next();
			String value = idAndContents.get(id);
			View itemView = (View)xmlLayout.findViewById(id);
			if(itemView instanceof TextView){
				((TextView)itemView).setText(value);
			}else if(itemView instanceof Button){
				((Button) itemView).setText(value);
			}
			else if(itemView instanceof EditText){
				((EditText) itemView).setText(value);
			}
			//设置监听
			itemView.setOnClickListener(this);
		}
	}


	public void setWindowAnimations(int resId)
	{
		mDialog.getWindow().setWindowAnimations(resId);
	}

	
	public void setCancelable(boolean b)
	{
		mDialog.setCancelable(b);
	}
}
