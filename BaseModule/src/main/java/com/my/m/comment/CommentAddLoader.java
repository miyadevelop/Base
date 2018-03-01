package com.my.m.comment;

import android.content.Context;

import com.google.gson.Gson;
import com.lf.app.App;
import com.lf.controler.tools.download.DownloadCheckTask;
import com.lf.controler.tools.download.DownloadTask;
import com.lf.controler.tools.download.helper.LoadUtils;
import com.lf.controler.tools.download.helper.NetLoader;
import com.my.shop.Consts;
import com.my.shop.R;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

/**
 * Created by wangwei on 17/9/14.
 */
public class CommentAddLoader extends NetLoader {


    private HashMap<String, Comment> mCommitComments = new HashMap<>();//提交的评论，key是time_stamp

    private static CommentAddLoader mInstance;

    public static CommentAddLoader getInstance() {
        if (null == mInstance)
            mInstance = new CommentAddLoader(App.mContext);
        return mInstance;
    }


    public CommentAddLoader(Context context) {
        super(context);
    }


    /**
     * 提交评论
     *
     * @param commodity
     * @param timeStamp 时间戳
     * @param callback_name 评论写完后，要通知的模块的名称
     * @param callback_id  评论写完后，通知对应模块时，携带的id
     */
    public void commitComment(Comment comment, String timeStamp, String callback_name, String callback_id) {
        HashMap<String, String> postParams = new HashMap<>();
        postParams.put("src_id",comment.src_id);
        postParams.put("text",comment.text);
//        try {
//            String text =  Base64.encodeToString(comment.text.getBytes(),Base64.DEFAULT);
//            postParams.put("text",text);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        postParams.put("text",comment.text);
        postParams.put("image",comment.image);
        postParams.put("at_id",comment.at_id);
        postParams.put("at_name",comment.at_name);
        postParams.put("score", String.valueOf(comment.score));
        postParams.put("time_stamp", timeStamp);

        postParams.put("callback_name", callback_name);
        postParams.put("callback_id", callback_id);
        loadWithParams(new HashMap<String, String>(), postParams);
    }


    @Override
    public String parse(String jsonStr, Object... objects) {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);

            String status = jsonObject.getString("status");
            if ("ok".equals(status)) {
                //将取到的评论信息存入到mCommitComments
                HashMap<String, String> params = (HashMap<String, String>) objects[0];
                String time_stamp = params.get("time_stamp");
                String data = jsonObject.getString("data");
                Gson gson = new Gson();
                Comment comment = gson.fromJson(data, Comment.class);
                mCommitComments.put(time_stamp, comment);
                return NetLoader.OK;
            } else {
                return jsonObject.getString("message");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }


    @Override
    public DownloadCheckTask initDownloadTask() {
        DownloadCheckTask task = new DownloadCheckTask();
        task.mIsSimple = true;
        task.mUrl = Consts.HOST + "/mall/comment.json";
        task.addMustParams("method");
        task.addParams("method", "add");
        task.cookiePath = App.mContext.getFilesDir() + File.separator + "cookie";
        task.cookieStatus = DownloadTask.COOKIE_READABLE;
        LoadUtils.addUniversalParam(App.mContext, task);
        task.addParams("appKey", App.mContext.getString(R.string.app_key));
        return task;
    }


    /**
     * 根据时间戳获取评论
     * @param time_stamp 时间戳
     * @return
     */
    public Comment getComment(String time_stamp) {
        return mCommitComments.get(time_stamp);
    }


}
