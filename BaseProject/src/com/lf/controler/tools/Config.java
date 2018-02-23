package com.lf.controler.tools;

import android.content.Context;

import com.lf.base.R;
import com.lf.controler.tools.download.DownloadCheckTask;
import com.lf.controler.tools.download.helper.LoadUtils;
import com.lf.controler.tools.download.helper.NetLoader;

import org.json.JSONObject;

import java.io.File;

//免更新获取参数
//缓存
//默认值，本地配置文件集中修改
//解析配置，重写获取结果（0 1 true false）

/**
 * 在线参数工具，在服务端添加参数，本地解析读取，采用JSONObject 键值对的方式存储各个配置的值，新增配置时，无需添加额外的代码
 *
 * @author wangwei
 */
public class Config extends NetLoader {

    private static JSONObjectTool mJsonObjectTool;

    public Config(Context context) {
        super(context);
//        //本地缓存机制
//        NetRefreshBean bean = new NetRefreshBean(NetEnumRefreshTime.Time_Hour);
//        bean.setTimeValue(10);
//        setRefreshTime(bean);
    }

    @Override
    public String parse(String jsonStr, Object... objects) {

        try {
            JSONObject json = new JSONObject(jsonStr);
            String data = json.getString("data");
            mJsonObjectTool = new JSONObjectTool(data);
            return OK;

        } catch (Exception e) {
            return e.toString();
        }
    }

    @Override
    public DownloadCheckTask initDownloadTask() {
        DownloadCheckTask task = new DownloadCheckTask();
        task.mIsSimple = true;
        String host = getContext().getString(R.string.app_host);
        if (host.endsWith(File.separator))
            task.mUrl = host + "entrance/config.json";
        else
            task.mUrl = host + "/entrance/config.json";
        LoadUtils.addUniversalParam(getContext(), task);
        task.addParams("appKey", getContext().getString(R.string.app_key));
        task.addParams("codeVersion", "1");//代码版本
        return task;
    }


    public static JSONObjectTool getConfig() {
        try {
            if (null == mJsonObjectTool)
                mJsonObjectTool = new JSONObjectTool("{}");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mJsonObjectTool;
    }
}
