package com.my.m.user;

import android.content.Context;

import com.my.m.R;

/**
 * Created by wangwei on 18/2/23.
 */
public class Consts {

    private static String HOST;

    private static String APP_KEY;

    protected static String getHost(Context context)
    {
        return null == HOST ? context.getString(R.string.app_host) : HOST;
    }

    protected static String getAppKey(Context context)
    {
        return null == APP_KEY ? context.getString(R.string.app_key) : APP_KEY;
    }


    protected static void setHost(String host)
    {
        HOST = host;
    }


    protected static void setAppKey(String appKey)
    {
        APP_KEY = appKey;
    }
}
