package com.lf.view.tools;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.LinearLayout;

import com.mobi.tool.MyR;

import java.util.HashMap;

/**
 * 等待的对话框，外界只能调用这里的onShow和onCancel方法
 *
 * @author ldy
 */
public class WaitDialog {

    //    private Dialog mDialog;
    public static HashMap<String, Dialog> mWaitDialogs = new HashMap<>();


    public static void show(Activity activity, String text, boolean cancelAble) {
        String key = activity.toString();
        Dialog waitDialog = mWaitDialogs.get(key);
        if (null == waitDialog) {
            View view = (View) LayoutInflater.from(activity).inflate(
                    MyR.layout(activity, "base_layout_wait_dialog"), null);
//		if(view!=null){
//			mTextView=(TextView)view.findViewById(
//					MyR.id(context,"wait_dialog_text_title"));
//			mTextView.setText(str);
//		}
            waitDialog = new Dialog(activity);
            Window window = waitDialog.getWindow();
            window.requestFeature(Window.FEATURE_NO_TITLE);
            window.setBackgroundDrawable(new ColorDrawable());
            LinearLayout.LayoutParams mainLayoutparams = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            waitDialog.setContentView(view, mainLayoutparams);

            mWaitDialogs.put(key, waitDialog);
        }
        waitDialog.setCancelable(cancelAble);    //点击返回键和其他区域，dialog可以取消
        waitDialog.show();
    }


    public static void cancel(Activity activity) {
        String key = activity.toString();
        Dialog waitDialog = mWaitDialogs.get(key);
        if (null != waitDialog) {
            if (waitDialog.isShowing())
                waitDialog.cancel();
            mWaitDialogs.remove(waitDialog);
        }
    }


    public static boolean isShowing(Activity activity) {
        String key = activity.toString();
        Dialog waitDialog = mWaitDialogs.get(key);
        if (null != waitDialog) {
            return waitDialog.isShowing();
        }
        return false;
    }


    public static Dialog getDialog(Activity activity) {
        String key = activity.toString();
        return mWaitDialogs.get(key);
    }


//    /**
//     * 实例化dialog
//     *
//     * @param context            不能是applicationContext
//     * @param flag               true:点击返回等，dialog可以消失
//     * @param str                等待界面上需要显示的文字,null表示用里面的默认文字
//     * @param needFinishActivity true:dialog消失的时候需要结束界面
//     */
//    public WaitDialog(final Context context,/*String str,*/boolean flag, boolean needFinishActivity) {
//
//        View view = (View) LayoutInflater.from(context).inflate(
//                MyR.layout(context, "base_layout_wait_dialog"), null);
////		if(view!=null){
////			mTextView=(TextView)view.findViewById(
////					MyR.id(context,"wait_dialog_text_title"));
////			mTextView.setText(str);
////		}
//        mDialog = new Dialog(context);
//        mDialog.setCancelable(flag);    //点击返回键和其他区域，dialog可以取消
//        Window window = mDialog.getWindow();
//        window.requestFeature(Window.FEATURE_NO_TITLE);
//        window.setBackgroundDrawable(new ColorDrawable());
//        LinearLayout.LayoutParams mainLayoutparams = new LinearLayout.LayoutParams(
//                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//        mDialog.setContentView(view, mainLayoutparams);
//        if (needFinishActivity) {
//            mDialog.setOnKeyListener(new OnKeyListener() {
//
//                @Override
//                public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
//                    // TODO Auto-generated method stub
//                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//                        ((Activity) context).finish();
//                    }
//                    return false;
//                }
//            });
//
//        }
//    }
//
////	/**
////	 * 替换dialog上的文字
////	 * @param text
////	 */
////	public void replaceText(String text){
////		mTextView.setText(text);
////	}
//
//    /**
//     * 显示对话框
//     */
//    public void onShow() {
//        if (!mDialog.isShowing())
//            mDialog.show();
//    }
//
//    /**
//     * 隐藏对话框
//     */
//    public void onCancle() {
//        if (mDialog.isShowing())
//            mDialog.cancel();
//    }
//
//    /**
//     * 对话框显示的状态
//     *
//     * @return true:正在显示
//     */
//    public boolean getShowStatus() {
//        return mDialog.isShowing();
//    }
}
