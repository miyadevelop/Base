package com.my.m.user;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.lf.controler.tools.download.DownloadCheckTask;
import com.lf.controler.tools.download.DownloadTask;
import com.lf.controler.tools.download.helper.LoadUtils;
import com.lf.controler.tools.download.helper.NetLoader;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

/**
 * Created by wangwei on 17/9/18.
 */
public class UpdateUserLoader extends NetLoader {

    public UpdateUserLoader(Context context) {
        super(context);
    }


    public void update(String name, String headUrl, String gender, String birthday, String province, String city) {
        HashMap<String, String> loadParam = new HashMap<String, String>();
        if (null != name && !name.isEmpty())
            loadParam.put("name", name);
        if (null != headUrl && !headUrl.isEmpty())
            loadParam.put("icon_url", headUrl);
        if(!TextUtils.isEmpty(gender))
            loadParam.put("gender", gender);
        if(!TextUtils.isEmpty(birthday))
            loadParam.put("birthday", birthday);
        if(!TextUtils.isEmpty(city))
            loadParam.put("city", city);
        if(!TextUtils.isEmpty(province))
            loadParam.put("province", province);
        loadWithParams(new HashMap<String, String>(), loadParam);
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
                UserManager.getInstance(getContext()).setUser(user);

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
        task.mUrl = Consts.getHost(getContext()) + "/mall/userUpdInfo.json";
        task.cookiePath = getContext().getFilesDir() + File.separator + "cookie";
        task.cookieStatus = DownloadTask.COOKIE_READABLE;
        LoadUtils.addUniversalParam(getContext(), task);
        task.addParams("appKey", Consts.getAppKey(getContext()));
        return task;
    }
}
