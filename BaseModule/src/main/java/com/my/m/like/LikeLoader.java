package com.my.m.like;

import android.content.Context;

import com.lf.app.App;
import com.lf.controler.tools.download.DownloadCheckTask;
import com.lf.controler.tools.download.helper.FenYeMapLoader2;
import com.lf.controler.tools.download.helper.LoadUtils;
import com.my.m.R;

/**
 * 加载点赞记录，可以用以判断是否点赞过
 * Created by wangwei on 17/9/14.
 */
public class LikeLoader extends FenYeMapLoader2<Like> {


    private static LikeLoader mInstance;

    public static LikeLoader getInstance() {
        if (null == mInstance)
            mInstance = new LikeLoader(App.mContext);
        return mInstance;
    }

    public LikeLoader(Context context) {
        super(context);
        setPageCount(1);
    }

    @Override
    protected DownloadCheckTask initDownloadTask() {
        DownloadCheckTask task = new DownloadCheckTask();
        task.mIsSimple = true;
        task.mUrl = App.mContext.getString(R.string.app_host) + "/mall/like.json";
        task.addMustParams("method");
        task.addParams("method", "select");
        LoadUtils.addUniversalParam(App.mContext, task);
        task.addParams("appKey", App.mContext.getString(R.string.app_key));
        return task;
    }

    private static final String ACTION = "like_loader";
    @Override
    public String getAction() {
        return ACTION;
    }
}
