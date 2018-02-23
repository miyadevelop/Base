package com.lf.view.tools.settings;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.lf.view.tools.UnitConvert;

/**
 * 设置中的对话框，默认自带了设置标题和确认按钮，具体需要用到设置对话框时，继承此类
 * 通过实现setContentView()往对话框的布局中添加除了标题和确认按钮以外的设置内容布局，
 * 同时实现changeSetting函数，用来在用户点击了确定按钮之后，修改设置内容
 * 
 * @author ww
 * 
 */
public abstract class BaseSettingDialog<T extends BaseSetting> extends Dialog
		implements android.view.View.OnClickListener {

	private TextViewDialogTitle mTitleText;// 标题
	private ButtonModule mButton;// 确定按钮
	private T mSetting;// 设置内容

	@SuppressWarnings("unchecked")
	public BaseSettingDialog(Context context, BaseSetting setting) {
		super(context);

		mSetting = (T) setting;

		// 设置对话框无系统自带标题
		Window window = getWindow();
		window.requestFeature(Window.FEATURE_NO_TITLE);

//		// 设置背景颜色
//		if (SettingsTheme.mDialogBg > 0)
//			window.setBackgroundDrawableResource(SettingsTheme.mDialogBg);
		window.setBackgroundDrawable(new ColorDrawable());
		setContentView();
	}

	/**
	 * 向对话框中添加内容布局，内容布局时在标题栏的下面，”确定“按钮的上面
	 */
	public void setContentView(/* View view,LinearLayout.LayoutParams lp */) {
		
		LinearLayout layout = new LinearLayout(getContext());
		
		layout.setOrientation(LinearLayout.VERTICAL);
		int width= getWindow().getWindowManager().getDefaultDisplay().getWidth()*8/10;
		LinearLayout.LayoutParams lplayout = new LinearLayout.LayoutParams(
				width, LayoutParams.WRAP_CONTENT);
		layout.setLayoutParams(lplayout);
		
		LinearLayout messagelayout = new LinearLayout(getContext());
		messagelayout.setOrientation(LinearLayout.VERTICAL);
		messagelayout.setLayoutParams(lplayout);
		messagelayout.setBackgroundResource(SettingsTheme.mDialogBg);
		messagelayout.setPadding(10, 10, 10, 10);
		// 添加标题
		mTitleText = new TextViewDialogTitle(getContext());
		mTitleText.setGravity(Gravity.CENTER);
	
		mTitleText.setHeight(UnitConvert.DpToPx(getContext(),50));
		LinearLayout.LayoutParams lp0 = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		layout.addView(mTitleText, lp0);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		View view = getContentView();
//		view.setBackgroundResource(SettingsTheme.mDialogBg);
		// 主动调用一次measure函数
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		if (view.getMeasuredHeight() > getWindow().getWindowManager()
				.getDefaultDisplay().getHeight() *2/3) {
			lp.height = (int) (getWindow().getWindowManager()
					.getDefaultDisplay().getHeight() * 2/3);
		}
		//view.setPadding(160, 0, 0, 0);
		lp.leftMargin = 80;
		lp.rightMargin = 0;
		lp.topMargin = 5;
		// 添加设置内容布局
//		layout.addView(view, lp);
		messagelayout.addView(view, lp);
		// 添加确定按钮
		mButton = new ButtonModule(getContext());
		mButton.setOnClickListener(this);
		LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, UnitConvert.DpToPx(getContext(), 45));
		lp2.leftMargin = UnitConvert.DpToPx(getContext(), 20);
		lp2.rightMargin = UnitConvert.DpToPx(getContext(), 20);
		lp2.bottomMargin = UnitConvert.DpToPx(getContext(), 5);
		lp2.topMargin = UnitConvert.DpToPx(getContext(), 5);
//		layout.addView(mButton, lp2);
		messagelayout.addView(mButton, lp2);
		layout.addView(messagelayout);
		// 初始化设置的标题
		mTitleText.setText(getSetting().getTitle());

		// 设置按钮文字
		mButton.setText("确定");
		LayoutParams lp3 = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		// 主动调用一次measure函数
		layout.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		
		
		getWindow().setContentView(layout, lp3);
	}

	/**
	 * 获取设置内容
	 * 
	 * @return
	 */
	public T getSetting() {
		return mSetting;
	}

	/**
	 * 点击确定按钮后的反应 关闭对话框，修改设置
	 */
	@Override
	public void onClick(View arg0) {
		changeSetting();
		dismiss();
	}

	/**
	 * 修改设置，需要子类具体实现，主要是再用户点击了确定按钮后 从内容布局中获取设置的值，并将设置的值修改到设置中
	 */
	public abstract void changeSetting();

	/**
	 * 设置内容布局，除标题和确认按钮以外的布局，比如一个输入框
	 */
	public abstract View getContentView();

}
