package com.lf.view.tools;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mobi.tool.MyR;

/**
 * 请求数据失败的界面,内部包含了一个刷新按钮，和一个重新设置网络按钮
 * @author ldy
 */
public class RequestFailView extends LinearLayout implements OnClickListener{
	
	private RelativeLayout mRefreshText;	//刷新按钮
	private RelativeLayout mSetText;		//设置网络按钮
	
	private ClickListener  mLisntener;
	
	public RequestFailView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView(context);
	}
	
	public RequestFailView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		
	}
	
	
	private void initView(Context context){
		View view=(View)LayoutInflater.from(getContext()).inflate(
				MyR.layout(getContext(),"layout_request_fail"),null);
		mRefreshText=(RelativeLayout)view.findViewById(MyR.id(
				getContext(),"include_mainactivity_exchange_layout_refresh"));
		mRefreshText.setOnClickListener(this);
		mSetText=(RelativeLayout)view.findViewById(MyR.id(
				getContext(),"include_mainactivity_exchange_layout_set"));
		mSetText.setOnClickListener(this);
		
		addView(view,new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
	}

	@Override
	public void onClick(View view) {
		if(view==mRefreshText){//刷新界面
			if(mLisntener!=null)
				mLisntener.refreshDatas();
		}else if(view==mSetText){
			Intent wifiSettingsIntent = new Intent(
					android.provider.Settings.ACTION_WIRELESS_SETTINGS);
			getContext().startActivity(wifiSettingsIntent);  // 如果在设置完成后需要再次进行操作，可以重写操作代码，在这里不再重写
		}
	}
	
	public void setClickListener(ClickListener listener){
		mLisntener=listener;
	}
	
	public interface ClickListener{
		
		/**
		 * 重新刷新获取数据
		 */
		public void refreshDatas();
	}

}
