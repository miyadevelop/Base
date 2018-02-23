package com.lf.view.tools.settings;

import java.lang.reflect.Constructor;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * 可扩展的设置布局，在界面上值显示设置的标题和当前设置的值，具体修改设置的代码交给点击
 * 此布局后显示的界面去实现。至于点击后会呈现哪一个对话框或者activity是由mAction和mActionType决定，
 * mAction和mActionType的值是在布局文件中决定的，通过AttributeSet传进来。
 * 
 * @author ww
 * 
 */
public class BaseExpandableLayout extends BaseSettingLayout<BaseSetting>
implements View.OnClickListener {

	private TextViewTitle mTitleText;// 标题
	private TextViewSummary mSummaryText;// 状态摘要
	private ExpandIconModule mExpandableIcon;// 提示用户此条设置可以点击的小图标（向右的箭头）
//	private ImageView mUpdateImage;	//更新的提示
	private UpdateImageModule mUpdateImage;//更新的提示
	private String mAction = "";// 决定此布局点击后跳转到哪里的属性
	private String mActionType = "dialog";// 决定点此布局是跳转到界面还是跳转到对话框

	public final static String ATTR_ACTION = "settings_action";// 在布局文件中属性的key，这个属性的值就是mAction的值
	public final static String ATTR_ACTION_TYPE = "settings_action_type";// 在布局文件中属性的key，这个属性的值就是mAction的值

	// mActionType的值
	public final static String ACTION_TYPE_ACTIVITY = "activity";// 表示跳转到activity
	public final static String ACTION_TYPE_DIALOG = "dialog";// 表示跳转到dialog

	public BaseExpandableLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		String action = attrs.getAttributeValue(null, ATTR_ACTION);
		if (null != action)
			mAction = action;

		String actionType = attrs.getAttributeValue(null, ATTR_ACTION_TYPE);
		if (null != actionType)
			mActionType = actionType;
	}

	@Override
	public void onSelfRefresh() {

		String summary = getSetting().getFullSummary();

		if ("".equals(summary))
			mSummaryText.setText("点击此处进行编辑");
		else
			mSummaryText.setText(summary);

		setEnabled(getSetting().isEnable());

		//判断是不是需要显示更新的提示
		if(!getSetting().getNeedUpdate())mUpdateImage.setVisibility(View.GONE);
		else mUpdateImage.setVisibility(View.VISIBLE);
	}

	@Override
	public void initView() {

		// 创建并添加子控件
		LinearLayout linearLayout = new LinearLayout(getContext());
		linearLayout.setOrientation(LinearLayout.VERTICAL);

		// 初始化标题的控件
		mTitleText = new TextViewTitle(getContext());
		// 初始化摘要控件
		mSummaryText = new TextViewSummary(getContext());
		mUpdateImage=new UpdateImageModule(getContext());
		LinearLayout horLinearLayout=new LinearLayout(getContext());
		horLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		horLinearLayout.addView(mTitleText);
		LinearLayout.LayoutParams updateParams=new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		updateParams.gravity=Gravity.CENTER_VERTICAL;
		updateParams.setMargins(Dp2Px(25),0,0,0);
		horLinearLayout.addView(mUpdateImage,updateParams);
		
		
		linearLayout.addView(horLinearLayout);
		linearLayout.addView(mSummaryText);

		// 初始化可扩展图标
		mExpandableIcon = new ExpandIconModule(getContext());
		mExpandableIcon.setScaleType(ScaleType.FIT_XY);
		mExpandableIcon.setId(1);
		RelativeLayout.LayoutParams lp = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(ALIGN_PARENT_RIGHT);
		lp.addRule(CENTER_VERTICAL);
		addView(mExpandableIcon, lp);

		RelativeLayout.LayoutParams lp1 = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp1.addRule(ALIGN_PARENT_LEFT);
		lp1.addRule(LEFT_OF, 1);
		addView(linearLayout, lp1);

		// 初始化设置的标题
		mTitleText.setText(getSetting().getTitle());
		// 显示设置的状态
		onSelfRefresh();

		setOnClickListener(this);
	}

	/**
	 * 布局被点击了之后根据mAction跳转到相应的对话框或者activity
	 */
	@Override
	public void onClick(View v) {

		//更改更新提示，取消更新,如果已经取消了，就不要再提示了
		Settings.getInstance(getContext()).setBooleanUpdateValue(getSetting().getKey(),false);
		if (ACTION_TYPE_DIALOG.equals(mActionType))// 跳转到类名为mAction对话框
		{
			try {
				// 根据类名获取到对话框的类
				Class<?> c = Class.forName(mAction);
				// 获取对话框类的构造函数
				Constructor<?> constructor = c.getConstructor(new Class[] {
						Context.class, BaseSetting.class });
				// 获取对话框类的实例
				Object instance = constructor.newInstance(new Object[] {
						getContext(), getSetting() });
				// 显示对话框
				Dialog dialog = (Dialog) instance;
				dialog.show();

			} catch (Exception e) {

				e.printStackTrace();
				// 如果没有与类名匹配的正确的对话框，则根据设置的类型匹配相适应的常规对话框
				try {
					IntSetting setting = (IntSetting) getSetting();
					RadioGroupDialog dialog = new RadioGroupDialog(
							getContext(), setting);
					dialog.show();
					return;
				} catch (Exception e1) {
				}
				try {
					StringSetting setting = (StringSetting) getSetting();
					StringEditDialog dialog = new StringEditDialog(
							getContext(), setting);
					dialog.show();
					return;
				} catch (Exception e1) {}
			}

		} else {// 跳转到自定义的activity，并将设置的key传给该activity

			Intent intent = new Intent();
			intent.setClassName(getContext(), mAction);
			intent.putExtra(BaseSetting.ATTR_KEY, getSetting().getKey());
			getContext().startActivity(intent);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		mTitleText.setEnabled(enabled);
		mSummaryText.setEnabled(enabled);
		mExpandableIcon.setEnabled(enabled);
	}
	
	public int Dp2Px(float dp) {
		final float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	@Override
	public void setTitle(String title) {
		mTitleText.setText(title);
	}

	@Override
	public void setSummary(String summary) {
		mSummaryText.setText(summary);
	}
}

