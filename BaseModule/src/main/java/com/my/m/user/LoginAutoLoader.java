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
 * 自动登录接口
 * Created by wangwei on 17/12/8.
 */
public class LoginAutoLoader extends NetLoader {

    public LoginAutoLoader(Context context) {
        super(context);
    }


    public void autoLogin()
    {
        loadWithParams(new HashMap<String, String>(),  new HashMap<String, String>());
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
        task.mUrl = Consts.getHost(getContext()) + "/mall/userLogin.json";
        task.cookiePath = getContext().getFilesDir() + File.separator + "cookie";
        task.cookieStatus = DownloadTask.COOKIE_ENABLE;
        LoadUtils.addUniversalParam(getContext(), task);
        task.addParams("appKey", Consts.getAppKey(getContext()));
        return task;
    }


    @Override
    public String getAction() {
        return "login";
    }

}

