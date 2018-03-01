package com.my.m.comment;

import android.content.Context;

import com.google.gson.Gson;
import com.lf.app.App;
import com.lf.controler.tools.download.DownloadCheckTask;
import com.lf.controler.tools.download.helper.FenYeMapLoader2;
import com.lf.controler.tools.download.helper.LoadUtils;
import com.my.m.R;

import org.json.JSONObject;

/**
 * Created by wangwei on 17/9/14.
 */
public class CommentLoader extends FenYeMapLoader2<Comment> {


    private static CommentLoader mInstance;

    public static CommentLoader getInstance() {
        if (null == mInstance)
            mInstance = new CommentLoader(App.mContext);
        return mInstance;
    }

    public CommentLoader(Context context) {
        super(context);
        setPageCount(16);
    }

    @Override
    protected DownloadCheckTask initDownloadTask() {
        DownloadCheckTask task = new DownloadCheckTask();
        task.mIsSimple = true;
        task.mUrl = App.mContext.getString(R.string.app_host) + "/mall/comment.json";
        task.addMustParams("method");
        task.addParams("method", "select");
        LoadUtils.addUniversalParam(App.mContext, task);
        task.addParams("appKey", App.mContext.getString(R.string.app_key));
        return task;
    }


    @Override
    public Comment onParseBean(JSONObject object) {
        Gson gson = new Gson();
        Comment comment = gson.fromJson(object.toString(), Comment.class);
        return comment;

    }

    @Override
    protected String getPageIndexNameOnWeb() {
        return "start_page";
    }

    @Override
    protected String getPageCountNameOnWeb() {
        return "size";
    }



}
