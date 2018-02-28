package com.my.m.user;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.lf.app.App;
import com.lf.controler.tools.download.DownloadCheckTask;
import com.lf.controler.tools.download.helper.LoadUtils;
import com.lf.controler.tools.download.helper.NetLoader;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * 获取用户信息，账号存在则获取成功，账号不存在则获取失败
 * Created by wangwei on 17/12/8.
 */
public class UserInfoLoader extends NetLoader {

    private HashMap<String, User> mUserInfos;

    public UserInfoLoader(Context context) {
        super(context);
    }


//    public void wechat(String wechatId)
//    {
//        HashMap<String, String> postParams = new HashMap<String, String>();
//        postParams.put("wechat", wechatId);
//        loadWithParams(new HashMap<String, String>(), postParams );
//    }
//
//
//    public void qq(String qqId)
//    {
//        HashMap<String, String> postParams = new HashMap<String, String>();
//        postParams.put("qq", qqId);
//        loadWithParams(new HashMap<String, String>(), postParams );
//    }


    public void id(String id) {
        HashMap<String, String> postParams = new HashMap<String, String>();
        postParams.put("user_id", id);
        loadWithParams(new HashMap<String, String>(), postParams);
    }


    public void phone(String phone) {
        HashMap<String, String> postParams = new HashMap<String, String>();
        postParams.put("phone", phone);
        loadWithParams(new HashMap<String, String>(), postParams);
    }


    @Override
    public String parse(String jsonStr, Object... objects) {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);

            String status = jsonObject.getString("status");
            if ("ok".equals(status)) {
                JSONObject data = jsonObject.getJSONObject("data");

                Gson gson = new Gson();
                User user = gson.fromJson(data.toString(), User.class);

                //临时存储获取到的用户信息
                try {
                    HashMap<String, String> params = (HashMap<String, String>) objects[0];
                    if (!TextUtils.isEmpty(params.get("phone")))
                        addUserInfo(params.get("phone"), user);
                    else if (!TextUtils.isEmpty(params.get("user_id")))
                        addUserInfo(params.get("user_id"), user);
                } catch (Exception e) {

                }

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
        task.mUrl = Consts.getHost(getContext()) + "/mall/userGet.json";
        task.addParams("appKey", App.string("app_key"));
        LoadUtils.addUniversalParam(getContext(), task);
        task.addParams("appKey", Consts.getAppKey(getContext()));
        return task;
    }


    /**
     * 获取用户信息
     *
     * @param id 用户的id或手机号或qq或微信id等第三方平台id
     * @return
     */
    public User getUserInfo(String id) {
        if (null == mUserInfos)
            return null;

        else {
            return mUserInfos.get(id);
        }
    }


    /**
     * 添加一条用户信息
     *
     * @param id   用户的id或手机号或qq或微信id等第三方平台id
     * @param user 一条用户信息
     */
    protected void addUserInfo(String id, User user) {
        if (null == mUserInfos)
            mUserInfos = new HashMap<>();

        mUserInfos.put(id, user);
    }
}

