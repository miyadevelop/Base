package com.lf.view.tools.settings;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;

/**
 * 功能类似RadioGroup
 * 		配合DrawableRadioButton使用 
 * @author zzq
 *
 */
public class DrawableRadioGroup extends LinearLayout{

	//存储CheckBox集合
	private ArrayList<TagCheckBox> groupList = new ArrayList<TagCheckBox>();
	
	//DrawableRadioGroup选择改变监听
	public interface DrawalbeRadioGroupCheckListener{
		public void onCheckedChange(int checkIndex);
	}
	
	public DrawalbeRadioGroupCheckListener mCheckListener;
	
	
	public DrawableRadioGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public DrawableRadioGroup(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	
	
	public int getCheckedIndex(){
		for(int i = 0; i < groupList.size(); i++){
			TagCheckBox checkBox = groupList.get(i);
			if(checkBox.isChecked()){
				return checkBox.getCheckTagId();
			}
		}
		return 0;
	}
	
	@Override
	public void addView(View child, android.view.ViewGroup.LayoutParams params) {
		try{
			TagCheckBox checkBox = (TagCheckBox)((DrawableRadioButton)child).getTagCheckBox();
			checkBox.setOnCheckedChangeListener(new RadioGroupCheckedListener());
			groupList.add(checkBox);
		}catch(Exception e){
			Log.d("ceshi", "drawableRadioGroup添加了错误的View");
		}
		
		super.addView(child, params);
	}
	
	class RadioGroupCheckedListener implements OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if(isChecked){
				TagCheckBox tagCheckBox = (TagCheckBox) buttonView;
				setCheckBox(tagCheckBox.getCheckTagId());	
				if(mCheckListener != null){
					mCheckListener.onCheckedChange(getCheckedIndex());
				}
			}
		}

	}
	
	private void setCheckBox(int index){
		for(int i = 0; i < groupList.size(); i++){
			if(groupList.get(i).getCheckTagId() == index){
				groupList.get(i).setChecked(true);
			}else{
				groupList.get(i).setChecked(false);
			}
		}
	}
	
	//用于外部设置改变选择监听
	public void setOnDrawableRadioGroupCheckedListener(DrawalbeRadioGroupCheckListener checkListener){
		mCheckListener = checkListener;
	}
	
}
