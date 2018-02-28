package com.my.m.user;

import android.content.Context;

import com.google.gson.Gson;
import com.lf.controler.tools.download.DownloadCheckTask;
import com.lf.controler.tools.download.DownloadTask;
import com.lf.controler.tools.download.helper.LoadUtils;
import com.lf.controler.tools.download.helper.NetLoader;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

/**
 * 绑定接口，根据cookie信息去绑定手机号、微信等第三方平台账号
 * Created by wangwei on 17/12/8.
 */
public class BindLoader extends NetLoader {

    public BindLoader(Context context) {
        super(context);
    }


    public void wechat(String wechatId, boolean status)
    {
        HashMap<String, String> postParams = new HashMap<String, String>();
        postParams.put("wechat", wechatId);
        if(status)
            postParams.put("status", "true");
        else
            postParams.put("status", "false");
        loadWithParams(new HashMap<String, String>(), postParams );
    }


    public void qq(String qqId, boolean status)
    {
        HashMap<String, String> postParams = new HashMap<String, String>();
        postParams.put("qq", qqId);
        if(status)
            postParams.put("status", "true");
        else
            postParams.put("status", "false");
        loadWithParams(new HashMap<String, String>(), postParams );
    }


    public void phone(String phone, String verficationCode, boolean status)
    {
        HashMap<String, String> postParams = new HashMap<String, String>();
        postParams.put("phone", phone);
        if(null != verficationCode && !"".equals(verficationCode))
            postParams.put("obtain", verficationCode);
        if(status)
            postParams.put("status", "true");
        else
            postParams.put("status", "false");
        loadWithParams(new HashMap<String, String>(), postParams );
    }


    @Override
    public String parse(String jsonStr, Object... objects) {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);

            String status = jsonObject.getString("status");
            if("ok".equals(status))
            {
                JSONObject data = jsonObject.getJSONObject("data");

                Gson gson = new Gson();
                User user = gson.fromJson(data.toString(), User.class);
                UserManager.getInstance(getContext()).setUser(user);

                return NetLoader.OK;
            }
            else
            {
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
        task.mIsSimple  = true;
        task.mUrl = Consts.getHost(getContext()) + "/mall/userBind.json";
        task.cookiePath = getContext().getFilesDir() + File.separator + "cookie";
        task.cookieStatus = DownloadTask.COOKIE_READABLE;
        LoadUtils.addUniversalParam(getContext(), task);
        task.addParams("appKey", Consts.getAppKey(getContext()));
        return task;
    }


    @Override
    public String getAction() {
        return "bind";
    }

}

