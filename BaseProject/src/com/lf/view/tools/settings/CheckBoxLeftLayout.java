package com.lf.view.tools.settings;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * 标题、概要、勾选框(在左侧)组成的设置组件，设置的内容是GroupSettings
 * @author ldy
 *
 */
public class CheckBoxLeftLayout extends BaseSettingLayout<GroupSettings> implements OnClickListener{
	private CheckLeftModule mCheckBox;	//按钮，类似勾选框
	private TextViewTitle mTitleText;//标题
	private TextViewSummary mSummaryText;//状态摘要
	private UpdateImageModule mUpdateImage;	//更新的提示
	private String mAction = "";// 决定此布局点击后跳转到哪里的属性
	private String mActionType = "dialog";// 决定点此布局是跳转到界面还是跳转到对话框
	public final static String ATTR_ACTION = "settings_action";// 在布局文件中属性的key，这个属性的值就是mAction的值
	public final static String ATTR_ACTION_TYPE = "settings_action_type";// 在布局文件中属性的key，这个属性的值就是mAction的值
	// mActionType的值
	public final static String ACTION_TYPE_ACTIVITY = "activity";// 表示跳转到activity
	public final static String ACTION_TYPE_DIALOG = "dialog";// 表示跳转到dialog
	private ExpandIconModule mExpandableIcon;// 提示用户此条设置可以点击的小图标（向右的箭头）
	public CheckBoxLeftLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		String action = attrs.getAttributeValue(null, ATTR_ACTION);
		if (null != action)
			mAction = action;

		String actionType = attrs.getAttributeValue(null, ATTR_ACTION_TYPE);
		if (null != actionType)
			mActionType = actionType;
	}

	/**
	 * 初始化组件
	 */
	@Override
	public void initView() {
		//创建并添加子控件
		LinearLayout linearLayout = new LinearLayout(getContext());
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);

		mCheckBox=new CheckLeftModule(getContext());
		mCheckBox.setEnabled(false);
		mCheckBox.setFocusable(false);
		mCheckBox.setClickable(false);
//		mCheckBox.setOnClickListener(this);
		LinearLayout.LayoutParams checkParams=new LinearLayout.LayoutParams(
				Dp2Px(25),Dp2Px(25));
		checkParams.gravity=Gravity.CENTER_VERTICAL|Gravity.LEFT;
		checkParams.setMargins(Dp2Px(10),0,Dp2Px(10),0);
		linearLayout.addView(mCheckBox,checkParams);

		LinearLayout messageLayout=new LinearLayout(getContext());
		messageLayout.setOrientation(LinearLayout.VERTICAL);

		LinearLayout titleLayout= new LinearLayout(getContext());
		titleLayout.setOrientation(LinearLayout.HORIZONTAL);


		mTitleText = new TextViewTitle(getContext());
		mSummaryText = new TextViewSummary(getContext());
		titleLayout.addView(mTitleText);

		mUpdateImage=new UpdateImageModule(getContext());
		LinearLayout.LayoutParams updateParams=new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		updateParams.gravity=Gravity.CENTER_VERTICAL;
		updateParams.setMargins(Dp2Px(25),0,0,0);
		titleLayout.addView(mUpdateImage,updateParams);
		messageLayout.addView(titleLayout);

		if(!"".equals(getSetting().getFullSummary())){
			messageLayout.addView(mSummaryText);
		}
		LinearLayout.LayoutParams messageParams=new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		messageParams.gravity=Gravity.CENTER_VERTICAL;
		linearLayout.addView(messageLayout, messageParams);
		addView(linearLayout);

		// 初始化可扩展图标
		mExpandableIcon = new ExpandIconModule(getContext());
		mExpandableIcon.setScaleType(ScaleType.FIT_XY);
		mExpandableIcon.setId(1);
		RelativeLayout.LayoutParams lp = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(ALIGN_PARENT_RIGHT);
		lp.addRule(CENTER_VERTICAL);
		addView(mExpandableIcon, lp);	

		//初始化设置标题
		mTitleText.setText(getSetting().getTitle());
		//显示设置的状态
		onSelfRefresh();
	}

	//重写父类方法
	@Override
	public void onSettingRefresh(String key) {
		String selfKey = getSetting().getGroupId();
		if(null != selfKey &&  selfKey.equals(key))
		{
			onSelfRefresh();
		}
	}

	/**
	 * 当自己设置改变后，调用这里
	 */
	@Override
	public void onSelfRefresh() {
		setEnabled(getSetting().isEnable());
		//判断是不是已经勾选了其他的选项
		String chooseKey=Settings.getInstance(getContext()).getStringSettingValue(getSetting().getGroupId());
		if(chooseKey==null&&getSetting().getDefValue()){
			Settings.getInstance(getContext()).setStringSettingValue(getSetting().getGroupId(),getSetting().getKey());
		}else if(getSetting().getKey().equals(chooseKey))getSetting().setValue(true);
		else getSetting().setValue(false);
		mSummaryText.setText(getSetting().getFullSummary());
		mCheckBox.setChecked(getSetting().getValue());

		//判断是不是需要显示更新的提示
		if(!getSetting().getNeedUpdate())mUpdateImage.setVisibility(View.GONE);
		else mUpdateImage.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View view) {
		if(!mCheckBox.isChecked()){
			mCheckBox.setChecked(true);
			//			return;
		}
		//更改更新提示，取消更新,如果已经取消了，就不要再提示了
		Settings.getInstance(getContext()).setBooleanUpdateValue(getSetting().getKey(),false);
		Settings.getInstance(getContext()).setStringSettingValue(
				getSetting().getGroupId(),getSetting().getKey());
		if (ACTION_TYPE_ACTIVITY.equals(mActionType))// 跳转到类名为mAction对话框
		{
			Intent intent = new Intent();
			intent.setClassName(getContext(), mAction);
			intent.putExtra(BaseSetting.ATTR_KEY, getSetting().getKey());
			getContext().startActivity(intent);
		}
	}

	public int Dp2Px(float dp) {
		final float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		mTitleText.setEnabled(enabled);
		mSummaryText.setEnabled(enabled);
//		mCheckBox.setEnabled(enabled);
		mExpandableIcon.setEnabled(enabled);
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
