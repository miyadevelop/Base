package com.lf.view.tools.settings;

import com.lf.view.tools.UnitConvert;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * 标题、概要、勾选框组成的设置组件，设置的内容是BooleanSetting
 * 
 * @author ww
 *
 */
public class CheckBoxBigLayout extends BaseSettingLayout<BooleanSetting> implements
		View.OnClickListener {

	private TextViewTitle mTitleText;// 标题
	private TextViewSummary mSummaryText;// 状态摘要
	private UpdateImageModule mUpdateImage; // 更新的提示
	private CheckBoxModule mCheckBox;// 勾选框

	public CheckBoxBigLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 初始化控件
	 */
	@Override
	public void initView() {
		// 创建并添加子控件
		LinearLayout linearLayout = new LinearLayout(getContext());
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		mTitleText = new TextViewTitle(getContext());
		mUpdateImage = new UpdateImageModule(getContext());
		LinearLayout horLinearLayout = new LinearLayout(getContext());
		horLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		horLinearLayout.addView(mTitleText);
		LinearLayout.LayoutParams updateParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		updateParams.gravity = Gravity.CENTER_VERTICAL;
		updateParams.setMargins(Dp2Px(25), 0, 0, 0);
		horLinearLayout.addView(mUpdateImage, updateParams);
		linearLayout.addView(horLinearLayout);
		mSummaryText = new TextViewSummary(getContext());
		RelativeLayout relativeLayout = new RelativeLayout(getContext());
		relativeLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(ALIGN_PARENT_LEFT);
		lp.addRule(CENTER_VERTICAL);
		relativeLayout.addView(mSummaryText, lp);
		//20160719：这里的长宽做了临时的调整，适应素材的大小
		RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(UnitConvert.DpToPx(getContext(), 50),UnitConvert.DpToPx(getContext(), 30));
		// rp.gravity=Gravity.CENTER_VERTICAL|Gravity.RIGHT;
		rp.addRule(ALIGN_PARENT_RIGHT);
		rp.addRule(CENTER_VERTICAL);
		mCheckBox = new CheckBoxModule(getContext());
		mCheckBox.setOnClickListener(this);
		relativeLayout.addView(mCheckBox, rp);

		linearLayout.addView(relativeLayout);

		addView(linearLayout);

		// RelativeLayout.LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT);
		// lp.addRule(ALIGN_PARENT_RIGHT);
		// lp.addRule(CENTER_VERTICAL);
		// addView(mCheckBox, lp);
		// 初始化设置的标题
		mTitleText.setText(getSetting().getTitle());
		// 显示设置的状态
		onSelfRefresh();
	}

	@Override
	public void onClick(View arg0) {
		// 更改更新提示，取消更新,如果已经取消了，就不要再提示了
		Settings.getInstance(getContext()).setBooleanUpdateValue(getSetting().getKey(), false);

		// 更改设置
		Boolean value = mCheckBox.isChecked();
		Settings.getInstance(getContext()).setBooleanSettingValue(getSetting().getKey(), value);

	}

	/**
	 * 当自己设置改变后，调用这里
	 */
	@Override
	public void onSelfRefresh() {
		mSummaryText.setText(getSetting().getFullSummary());
		mCheckBox.setChecked(getSetting().getValue());
		setEnabled(getSetting().isEnable());

		// 判断是不是需要显示更新的提示
		if (!getSetting().getNeedUpdate())
			mUpdateImage.setVisibility(View.GONE);
		else
			mUpdateImage.setVisibility(View.VISIBLE);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		mTitleText.setEnabled(enabled);
		mSummaryText.setEnabled(enabled);
		mCheckBox.setEnabled(enabled);
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
