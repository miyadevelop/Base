package com.my.m.user;

import android.content.Context;

import com.google.gson.Gson;
import com.lf.controler.tools.download.DownloadCheckTask;
import com.lf.controler.tools.download.DownloadTask;
import com.lf.controler.tools.download.helper.LoadUtils;
import com.lf.controler.tools.download.helper.NetLoader;
import com.my.m.R;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

/**
 * 注册接口，没有账号就注册，没有就注册失败
 * Created by wangwei on 17/12/8.
 */
public class RegistLoader extends NetLoader {

    public RegistLoader(Context context) {
        super(context);
    }


    public void wechat(String wechatId)
    {
        HashMap<String, String> postParams = new HashMap<String, String>();
        postParams.put("wechat", wechatId);
        loadWithParams(new HashMap<String, String>(), postParams );
    }


    public void qq(String qqId)
    {
        HashMap<String, String> postParams = new HashMap<String, String>();
        postParams.put("qq", qqId);
        loadWithParams(new HashMap<String, String>(), postParams );
    }


    public void phone(String phone, String verficationCode)
    {
        HashMap<String, String> postParams = new HashMap<String, String>();
        postParams.put("phone", phone);
        if(null != verficationCode && !"".equals(verficationCode))
            postParams.put("obtain", verficationCode);
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
        task.mUrl = Consts.HOST + "/mall/userRegister.json";
        task.cookiePath = getContext().getFilesDir() + File.separator + "cookie";
        task.cookieStatus = DownloadTask.COOKIE_WRITEABLE;
        LoadUtils.addUniversalParam(getContext(), task);
        task.addParams("appKey", getContext().getString(R.string.app_key));
        return task;
    }


    @Override
    public String getAction() {
        return "regist";
    }

}

