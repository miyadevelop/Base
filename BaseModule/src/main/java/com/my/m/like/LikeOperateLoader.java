package com.my.m.like;

import android.content.Context;

import com.lf.app.App;
import com.lf.controler.tools.download.DownloadCheckTask;
import com.lf.controler.tools.download.DownloadTask;
import com.lf.controler.tools.download.helper.LoadUtils;
import com.lf.controler.tools.download.helper.NetLoader;
import com.my.shop.Consts;
import com.my.shop.R;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 点赞或者取消赞
 * Created by wangwei on 18/1/29.
 */
public class LikeOperateLoader extends NetLoader {


    private static LikeOperateLoader mInstance;

    private List<String> mAddLikes = new ArrayList<>();//临时缓存点赞过的内容

    public static LikeOperateLoader getInstance() {
        if (null == mInstance)
            mInstance = new LikeOperateLoader(App.mContext);
        return mInstance;
    }


    public LikeOperateLoader(Context context) {
        super(context);
    }


    /**
     * 点赞
     *
     * @param src_id        点赞对象的id
     * @param timeStamp     时间戳
     * @param callback_name 评论写完后，要通知的模块的名称
     * @param callback_id   评论写完后，通知对应模块时，携带的id
     */
    public void add(String src_id, String timeStamp, String callback_name, String callback_id) {
        HashMap<String, String> postParams = new HashMap<>();
        postParams.put("method", "add");
        postParams.put("src_id", src_id);
        postParams.put("time_stamp", timeStamp);
        postParams.put("callback_name", callback_name);
        postParams.put("callback_id", callback_id);
        loadWithParams(new HashMap<String, String>(), postParams);
    }


    /**
     * 取消赞
     *
     * @param src_id        点赞对象的id
     * @param timeStamp     时间戳
     * @param callback_name 评论写完后，要通知的模块的名称
     * @param callback_id   评论写完后，通知对应模块时，携带的id
     */
    public void cancel(String src_id, String timeStamp, String callback_name, String callback_id) {
        HashMap<String, String> postParams = new HashMap<>();
        postParams.put("method", "del");
        postParams.put("src_id", src_id);
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
                HashMap<String, String> params = (HashMap<String, String>) objects[0];
                String method = params.get("method");
                if ("add".equals(method))//点赞
                    mAddLikes.add(params.get("src_id"));
                else//取消赞
                    mAddLikes.remove(params.get("src_id"));
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
        task.mUrl = Consts.HOST + "/mall/like.json";
        task.cookiePath = App.mContext.getFilesDir() + File.separator + "cookie";
        task.cookieStatus = DownloadTask.COOKIE_READABLE;
        LoadUtils.addUniversalParam(App.mContext, task);
        task.addParams("appKey", App.mContext.getString(R.string.app_key));
        return task;
    }


    private static final String ACTION = "like_operate_loader";

    @Override
    public String getAction() {
        return ACTION;
    }


    public boolean isLike(String src_id)
    {
        return mAddLikes.contains(src_id);
    }
}
