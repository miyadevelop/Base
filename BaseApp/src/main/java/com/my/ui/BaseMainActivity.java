package com.my.ui;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.lf.app.App;
import com.lf.controler.tools.download.DownloadCheckTask;
import com.lf.tools.datacollect.DataCollect;
import com.lf.view.tools.CustomToastShow;
import com.lf.view.tools.update.UpdateManager;
import com.my.app.R;

public abstract class BaseMainActivity extends TabActivity {

    protected UpdateManager mManager;//检测更新

//	private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity_tab_2);
//		mHandler.postDelayed(mRunnable, 2000);

        // 检测更新
        if (null == mManager)
            mManager = new UpdateManager(BaseMainActivity.this);
        mManager.checkUpdate(getUpdateDownloadTask(), true);
    }


//	private Runnable mRunnable = new Runnable() {
//		@Override
//		public void run() {
//			// 检测更新
//			if (null == mManager)
//				mManager = new UpdateManager(BaseMainActivity.this);
//			mManager.checkUpdate(getUpdateDownloadTask(), true);
//		}
//	};

    //双击返回退出
    private CustomToastShow mToast = new CustomToastShow();
    private static long mLastClickTime = 0;//双击返回退出

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long time = System.currentTimeMillis();
            long timeD = Math.abs(time - mLastClickTime);
            mLastClickTime = time;
            if (timeD < 2000) {
                if (null != mToast)
                    mToast.cancel();
                finish();
            }
            if (null == mToast)
                mToast = new CustomToastShow();
            mToast.showToast(this, getString(R.string.main_quit), Toast.LENGTH_SHORT);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mManager)
            mManager.release();
//		mHandler.removeCallbacks(mRunnable);
    }


    @Override
    protected void onResume() {
        super.onResume();
        DataCollect.getInstance(this).onResume(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        DataCollect.getInstance(this).onPause(this);
    }


    /**
     * 自动更新接口
     */
    public static DownloadCheckTask getUpdateDownloadTask() {
        DownloadCheckTask task = new DownloadCheckTask();
        task.mIsSimple = true;
        task.mUrl = App.string("app_host")  + "/money/moneyGetApk.json";
        task.addParams("id", "17");
        task.addMustParams("id");
        return task;
    }
}
