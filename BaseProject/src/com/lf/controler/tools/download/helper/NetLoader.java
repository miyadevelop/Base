package com.lf.controler.tools.download.helper;

import android.content.Context;

import com.lf.base.R;
import com.lf.controler.tools.NetWorkManager;
import com.lf.controler.tools.download.DownloadCenter;
import com.lf.controler.tools.download.DownloadCheckTask;
import com.lf.tools.log.MyLog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


/**
 * 网络加载
 *
 * @author ludeyuan
 */
public abstract class NetLoader extends BaseLoader {

    /**
     * 加载的状态值
     */
    public enum EnumLoadingStatus {
        UnLoad,    //未加载
        Loading,//正在加载
        LoadSuccess,//加载成功
        LoadFail;    //加载失败
    }

    public final static String OK = "OK";
    private Context mApplicationContext = null;    //存放ApplicationContext,用来发送广播
    private NetRefreshBean mRefreshBean = null;//更新的周期
    private EnumLoadingStatus mLoadStatus = EnumLoadingStatus.UnLoad;    //加载的状态
    private NetLoaderCache mNetLoaderCache;//本地缓存处理


    public NetLoader(Context context) {
        mApplicationContext = context.getApplicationContext();
    }


    /**
     * 执行加载
     *
     * @param params 访问网络时网址中携带的参数的键值对
     */
    public final void loadWithParams(HashMap<String, String> params) {
        load(params, new HashMap<String, String>());
    }

    /**
     * 执行加载
     *
     * @param params     访问网络时网址中携带的参数的键值对
     * @param postParams 访问网络时post提交的参数
     */
    public final void loadWithParams(HashMap<String, String> params, HashMap<String, String> postParams) {
        load(params, postParams);
    }

    /**
     * 加载数据,先判断本地的缓存和更新周期等
     */
    @SuppressWarnings("unchecked")
    @Override
    protected final void load(Object... downTask) {

        //根据当前的加载状态，如果正在加载，就不处理,直接返回
        if (mLoadStatus == EnumLoadingStatus.Loading) {
            return;
        }

        //向服务器请求数据
        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, String> postParams = new HashMap<String, String>();
        if (downTask.length > 0 && null != (HashMap<String, String>) downTask[0])
            params = (HashMap<String, String>) downTask[0];
        if (downTask.length > 1 && null != (HashMap<String, String>) downTask[1])
            postParams = (HashMap<String, String>) downTask[1];

        //新生成一个DownloadCheckTask,传给下载器，防止多次的时候网址出现重复
        final DownloadCheckTask downloadCheckTask = initDownloadTask();
        //添加进普通的参数
        if (params != null) {
            Iterator<Entry<String, String>> paramsIter = params.entrySet().iterator();
            while (paramsIter.hasNext()) {
                Entry<String, String> param = paramsIter.next();
                downloadCheckTask.addParams(param.getKey(), param.getValue());
            }
        }
        //添加进post参数
        if (postParams != null) {
            Iterator<Entry<String, String>> paramsIter = postParams.entrySet().iterator();
            while (paramsIter.hasNext()) {
                Entry<String, String> param = paramsIter.next();
                downloadCheckTask.addPostParams(param.getKey(), param.getValue());
            }
        }

        params.clear();
        params.putAll(downloadCheckTask.getParams());
        params.putAll(downloadCheckTask.getPostParams());

        //TODO：如果是服务器时间缓存机制，并本地有缓存的情况下，往参数里面提交缓存的时间
        if (getRefreshTime().getNetEnumRefreshTime() == NetEnumRefreshTime.Server) {
            String time = getNetLoaderCache().getServerTime(params);
            if (null != time && !"".equals(time))
                downloadCheckTask.addParams(getServerUpdateTimeParamName(), time);
        } else if (getRefreshTime().getNetEnumRefreshTime() != NetEnumRefreshTime.None)//有缓存
        {
            //本地有数据，且在更新的周期内，不需要更新
            boolean needReload = getNetLoaderCache().needReload(params, getRefreshTime());
            if (!needReload) {
                String saveStr = getNetLoaderCache().read(params);
                if (!"".equals(saveStr)) {
                    mLoadStatus = EnumLoadingStatus.Loading;
                    //调用解析的方法，获取到数据（外部实现）,并获取到发送广播中携带的值
                    String result = parse(saveStr);
                    if (OK.equals(result)) {//解析的结果正确，直接返回；否则从服务器器重新获取
                        mLoadStatus = EnumLoadingStatus.LoadSuccess;
                        sendBroadCast(mApplicationContext, true, OK, params);

                        return;
                    }
                }
            }
        }

//        downloadCheckTask.addParams("time_stamp", System.currentTimeMillis() + "");
        downloadCheckTask.checkParams();
        MyLog.d("url:" + downloadCheckTask.mUrl + "&" + downloadCheckTask.queryString);

        onLoad(downloadCheckTask);
    }


//    private Handler mHandler = new Handler();


    /**
     * 从服务器端直接重新加载数据，不管是否需要更新、本地又数据缓存等情况
     */
    private void onLoad(final DownloadCheckTask downloadCheckTask) {
        //根据当前的加载状态，如果正在加载，就不处理,直接返回
        if (mLoadStatus == EnumLoadingStatus.Loading) {
            return;
        }
        mLoadStatus = EnumLoadingStatus.Loading;
        //判断当前的网络，决定是否可以请求服务器的数据（暂不处理连接到wifi,但不能联网的情况）
        if (!NetWorkManager.getInstance(mApplicationContext).isConnect()) {
            //设置失败的原因“网络未连接”
            mLoadStatus = EnumLoadingStatus.LoadFail;
            String failReason = mApplicationContext.getString(R.string.network_connections_disconnection);
            sendBroadCast(mApplicationContext, false, failReason, null);
        }

        //向服务器请求数据
        DownloadCenter.getInstance(mApplicationContext).start(downloadCheckTask, new NetLoaderResponse() {

            @Override
            public void onSuccess(final String paramT) {

                synchronized (this) {
                    if (mApplicationContext == null)//资源已释放就不再处理加载结果
                        return;

//                Runnable runnable = new Runnable() {
//
//                    @Override
//                    public void run() {

                    String result = null;

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.putAll(downloadCheckTask.getParams());
                    params.putAll(downloadCheckTask.getPostParams());

//                        params.remove("time_stamp");//去除时间戳
                    ServerUpdateRet ret = null;
                    //服务器缓存时间缓存机制，根据服务器返回的缓存时间和状态，决定是否从本地读取缓存
                    if (getRefreshTime().getNetEnumRefreshTime() == NetEnumRefreshTime.Server) {
                        params.remove(getServerUpdateTimeParamName());//去掉时间参数，以免影响读取缓存
                        ret = parseServerUpdate(paramT);
                        if (!ret.mNeedUpdate)//解析从本地缓存读取的内容
                            result = parse(getNetLoaderCache().read(params), params);
                    }

                    if (null == result)//解析网络加载的内容
                    {
                        result = parse(paramT, params);
                        //解析数据成功，存储数据
                        if (OK.equals(result) && getRefreshTime().getNetEnumRefreshTime() != NetEnumRefreshTime.None) {
                            getNetLoaderCache().save(paramT, params);    //存储解析的数据到本地

                            //如果是服务器时间缓存，则需要把加载的服务器时间存储到本地，以便下次加载时作为参数提交
                            if (getRefreshTime().getNetEnumRefreshTime() == NetEnumRefreshTime.Server && null != ret && !"".equals(ret.mTime))
                                getNetLoaderCache().saveServerTime(params, ret.mTime);
                        }
                    }

                    if (OK.equals(result)) {//解析数据成功，发送成功的广播
                        mLoadStatus = EnumLoadingStatus.LoadSuccess;
                    } else {
                        mLoadStatus = EnumLoadingStatus.LoadFail;
                    }

                    if (null != mApplicationContext)
                        sendBroadCast(mApplicationContext, OK.equals(result), result, params);
//                    }
//                };
//                mHandler.post(runnable);
                }
            }

            @Override
            public void onErr(int paramInt) {

                if (mLoadStatus == null)//资源已释放就不再处理加载结果
                    return;

                HashMap<String, String> params = new HashMap<String, String>();
                params.putAll(downloadCheckTask.getParams());
                params.putAll(downloadCheckTask.getPostParams());

                mLoadStatus = EnumLoadingStatus.LoadFail;
                String failReason = mApplicationContext.getString(R.string.data_load_fail);
                //实现加载数据失败的处理
                sendBroadCast(mApplicationContext, false, failReason, params);
            }
        });
    }


    /**
     * 返回刷新的周期对象,内部实现了判空的处理
     */
    public final NetRefreshBean getRefreshTime() {
        if (mRefreshBean == null) {
            mRefreshBean = new NetRefreshBean(NetEnumRefreshTime.None);
            mRefreshBean.setTimeValue(0);//默认不缓存
        }
        return mRefreshBean;
    }


    protected final Context getContext() {
        return mApplicationContext;
    }


    /**
     * 获取到当前的加载状态
     */
    public EnumLoadingStatus getLoadingStaus() {
        return mLoadStatus;
    }


    private String mAction;

    /**
     * 获取广播发送的名称
     *
     * @return
     */
    @Override
    public String getAction() {
        if (null == mAction) {
            mAction = new String(initDownloadTask().mUrl);
        }
        return mAction;
    }


    /**
     * 设置从网络端加载数据的刷新周期（间隔时间内不会访问服务器）
     *
     * @param refreshBean
     */
    public final void setRefreshTime(NetRefreshBean refreshBean) {
        mRefreshBean = refreshBean;
    }


    /**
     * 刷新数据
     */
    public void refresh() {
        refresh(null, null);
    }


    /**
     * 刷新数据
     */
    public void refresh(HashMap<String, String> params) {
        refresh(params, null);
    }


    /**
     * 刷新数据，有Post数据
     *
     * @param params
     * @param postParams
     */
    public void refresh(HashMap<String, String> params, HashMap<String, String> postParams) {

        if (null == params)
            params = new HashMap<String, String>();

        if (null == postParams)
            postParams = new HashMap<String, String>();

        //新生成一个DownloadCheckTask,传给下载器，防止多次的时候网址出现重复
        final DownloadCheckTask downloadCheckTask = initDownloadTask();
        //添加进普通的参数
        if (params != null) {
            Iterator<Entry<String, String>> paramsIter = params.entrySet().iterator();
            while (paramsIter.hasNext()) {
                Entry<String, String> param = paramsIter.next();
                downloadCheckTask.addParams(param.getKey(), param.getValue());
            }
        }
        //添加进post参数
        if (postParams != null) {
            Iterator<Entry<String, String>> paramsIter = postParams.entrySet().iterator();
            while (paramsIter.hasNext()) {
                Entry<String, String> param = paramsIter.next();
                downloadCheckTask.addPostParams(param.getKey(), param.getValue());
            }
        }

        params.putAll(postParams);
        //TODO：如果是服务器时间缓存机制，并本地有缓存的情况下，往参数里面提交缓存的时间
        if (getRefreshTime().getNetEnumRefreshTime() == NetEnumRefreshTime.Server) {
            String time = getNetLoaderCache().getServerTime(params);
            downloadCheckTask.addParams(getServerUpdateTimeParamName(), time);
        }

//        downloadCheckTask.addParams("time_stamp", System.currentTimeMillis() + "");
        downloadCheckTask.checkParams();
        MyLog.d("url:" + downloadCheckTask.mUrl + "&" + downloadCheckTask.queryString);

        onLoad(downloadCheckTask);
    }


    public class ServerUpdateRet {
        public String mTime = "";//本次加载时，系统的时间
        public boolean mNeedUpdate = false;//是否需要从本地缓存读取
    }


    /**
     * 解析服务器时间缓存机制中的“时间”和“是否需要更新”这两个字段，用本函数来处理这项功能，主要是为了让子类有机会重写此接口，从而改变读取这两个字段的方法
     *
     * @param paramT
     * @return
     */
    protected ServerUpdateRet parseServerUpdate(String paramT) {
        ServerUpdateRet ret = new ServerUpdateRet();
        if (getRefreshTime().getNetEnumRefreshTime() == NetEnumRefreshTime.Server) {
            try {
                JSONObject jsonObject = new JSONObject(paramT);
                ret.mTime = jsonObject.getString("time");
                ret.mNeedUpdate = jsonObject.getBoolean("need_update");

            } catch (Exception e) {
//                e.printStackTrace();
                ret.mNeedUpdate = true;//如果解析异常，则重新获取内容，避免本地缓存的不可靠
            }
        }
        return ret;
    }


    /**
     * 给参数中添加 服务器时间缓存机制中的 时间字段
     */
    protected String getServerUpdateTimeParamName() {
        return "update_time";
    }


    @Override
    public void release() {
        super.release();
        mApplicationContext = null;
//        mLoadStatus = null;
        mRefreshBean = null;
        mNetLoaderCache = null;
        mLoadStatus = EnumLoadingStatus.UnLoad;
    }


    private NetLoaderCache getNetLoaderCache() {
        if (null == mNetLoaderCache) {
            mNetLoaderCache = new NetLoaderCache(getContext(), initDownloadTask().mUrl);
        }
        return mNetLoaderCache;
    }


    /**
     * 解析json数据
     *
     * @return 返回广播中需要携带的值, 可以为null
     */
    public abstract String parse(String jsonStr, Object... objects);


    /**
     * 实现初始化downloadCheckTask的函数
     */
    public abstract DownloadCheckTask initDownloadTask();


    //目前已把initDownloadTask移到load的开头，以便让task中所有的参数都可以参与数据的区分、广播的回调等
    //但由于initDownloadTask()是外界重写的，类似于url的值随时可能会发生改变，而回调广播以url为Action，所以此值不适合改变

    //NetLoader实际只是负责数据的加载，并不持有bean，所以它的参数可以随意改变，而作为BeanLoader，一般情况下
    //它的数据和加载的参数应该是唯一对应的关系，例如如果是最热分类，bean表示最热分类的列表，它对应的分类id就是“最热”
    //但是参数和bean之间的关联是认为指定匹配的，可以用一个map来存多个分类下的数据，并且利用分类的id作为map的key，这样
    //一个loader中，可以加载更多分类的数据


}
