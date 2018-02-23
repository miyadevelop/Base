package com.lf.controler.tools.download.helper;

import android.content.Context;

import com.google.gson.Gson;
import com.lf.controler.tools.download.DownloadCheckTask;
import com.lf.controler.tools.download.helper.NetLoader.EnumLoadingStatus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 分页加载器，继承于com.lf.controler.tools.download.helper.ListLoader，对需要从服务器端分页获取数据的需求提供
 * 加载下一页、记录当前页码、本地缓存、定期更新、网址检查、加载结束广播通知、数据获取 等功能辅助，
 * 并对数据加载和数据获取做了格式上的限制，进行了统一规范
 *
 * @param <T>
 * @author ludeyuan
 */
public abstract class FenYeLoader<T> extends ListLoader<T> {

    public static final int START_ITEM = 0;
    public static final int START_PAGE = 1;
    private int mPageCount = 36;//一页加载锁屏的数量，默认一页36项
    private int mStartIndex = 0;//起始页面的页码，默认起始页码为0
    private MyNetLoad mMyNetLoader;//网络加载器
    private int mStartMethod = 0;//0，分页加载时，传的是起始项，1,传的是页码
    private int mPageIndex = 0;
    private boolean mReachBottom = false;//数据是否加载到底部，仅供参考的数据标识，并不绝对正确


    /**
     * 构造函数
     *
     * @param context 上下文
     */
    public FenYeLoader(Context context) {
        super(new ArrayList<T>());
        mMyNetLoader = new MyNetLoad(context);
    }


    /**
     * 加载数据，如果本地有缓存，并且没有过期，就会直接从本地读取并返回通知
     */
    public void loadResource() {

        if (mMyNetLoader.getLoadingStaus() == EnumLoadingStatus.Loading) {
            //正在加载，就不处理；且updateBean中替换还是更新数据，也会依据这个值
            return;
        }

        HashMap<String, String> params = new HashMap<String, String>();
        if (mStartMethod == START_ITEM)//按起始项加载
        {
            //加载第一页的数据
            if (null == mBean)
                params.put(getPageIndexNameOnWeb(), mStartIndex + "");
            params.put(getPageIndexNameOnWeb(), mPageIndex * mPageCount + mStartIndex + "");
        } else//按起始页加载
        {
            //计算需要加载的下页的值
            int pageIndex;
            if (mBean == null || mBean.size() == 0)
                pageIndex = mStartIndex;
            else
                pageIndex = mPageIndex + mStartIndex;
            params.put(getPageIndexNameOnWeb(), pageIndex + "");//加载第一页的数据
        }
        params.put(getPageCountNameOnWeb(), mPageCount + "");//加载一页的数量
        mMyNetLoader.loadWithParams(params);
    }


    /**
     * 重新刷新数据，不理会本地的缓存信息
     */
    public void refreshResource() {

        if (mMyNetLoader.getLoadingStaus() == EnumLoadingStatus.Loading) {
            //正在加载，就不处理；且updateBean中替换还是更新数据，也会依据这个值
            return;
        }
        mPageIndex = 0;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(getPageIndexNameOnWeb(), mStartIndex + "");//加载第一页的数据
        params.put(getPageCountNameOnWeb(), mPageCount + "");//加载一页的数量
        mMyNetLoader.refresh(params);
    }


    /**
     * 设置一页加载几项，默认36项
     *
     * @param pageCount 一页几项
     */
    protected final void setPageCount(int pageCount) {
        mPageCount = pageCount;
    }


    /**
     * 获取一页加载几项，默认36项
     *
     * @return 一页几项
     */
    protected final int getPageCount() {
        return mPageCount;
    }


    /**
     * 设置起始页面的页码，默认为1
     *
     * @param startIndex 起始页面的页码
     */
    protected final void setStartIndex(int startIndex) {
        mStartIndex = startIndex;
    }


    /**
     * 获取起始页面的页码，默认为1
     *
     * @return 起始页面的页码
     */
    protected final int getStartIndex() {
        return mStartIndex;
    }


    /**
     * 设置从网络端加载数据的刷新周期（间隔时间内不会访问服务器）
     *
     * @param refreshBean 数据从网络端更新的周期
     */
    protected final void setRefreshTime(NetRefreshBean refreshBean) {
        mMyNetLoader.setRefreshTime(refreshBean);
    }


    /**
     * 获取从网络端加载数据的刷新周期（间隔时间内不会访问服务器）
     *
     * @return refreshTime 数据从网络端更新的周期
     */
    protected final NetRefreshBean getRefreshTime() {
        return mMyNetLoader.getRefreshTime();
    }


    @Override
    protected final void onParseOver(ArrayList<T> newT, Object... objects) {
        if (null == get())//释放后，数据为空
            return;
        //根据解析结果，更新数据
        if (objects.length > 0) {
            try {
                @SuppressWarnings("unchecked")
                //如果是加载的第一页，清空列表
                        HashMap<String, String> params = (HashMap<String, String>) objects[0];
                String indexString = params.get(getPageIndexNameOnWeb());
                int index = Integer.decode(indexString);
                if (index == mStartIndex)
                    get().clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (null == newT || newT.size() == 0) {
            mReachBottom = true;
        } else {
            mReachBottom = false;
        }

        if (null != newT) {
            get().addAll(newT);
            mPageIndex++;
        }
    }


    /**
     * 重写onParse，如果子类对解析错误或者正确的的情况没有特殊的处理需求，就不需要重写onParse处理返回值，仅需处理解析正确的结果
     */
    @Override
    protected Result<ArrayList<T>> onParse(String json) {
        Result<ArrayList<T>> ret = new Result<ArrayList<T>>();
        ret.mBean = new ArrayList<T>();
        try {

            JSONObject jsonObject = new JSONObject(json);

            String status = jsonObject.getString("status");
            if ("ok".equals(status)) {

                String dataString = jsonObject.getString("data");
                if (!"null".equals(dataString))//内容不为null
                {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        T bean = onParseBean(object);
                        if (null != bean)
                            ret.mBean.add(bean);
                    }
                }
                ret.mIsSuccess = true;
            } else {
                ret.mIsSuccess = false;
                ret.mMessage = jsonObject.getString("message");
            }
        } catch (Exception e) {

            ret.mIsSuccess = false;
            ret.mMessage = e.toString();
            e.printStackTrace();
        }
        return ret;
    }


    Gson gson = new Gson();
    /**
     * 解析列表中的Bean，一般情况下，如果子类对整个解析没有特殊需求，就只需重写本方法来处理解析结果。
     *
     * @param object
     * @return
     */
    public T onParseBean(JSONObject object) {
        try {
            Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            return gson.fromJson(object.toString(), entityClass);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 释放分页下载器下面的数据
     */
    public void release() {
        super.release();
        mMyNetLoader.release();

    }


//	/**
//	 * 获取加载完成后，发通知的广播的Action值
//	 */
//	@Override
//	public String getAction() {
//		return mMyNetLoader.getAction();
//	}


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
     * 初始化要进行加载的任务，页码的参数可以不添加进来
     *
     * @return 下载任务的任务描述
     */
    protected abstract DownloadCheckTask initDownloadTask();


    public static final String DEFAUL_START_PAGE = "start_page";
    public static final String DEFAUL_SIZE = "size";

    /**
     * 分页请求时，向服务器请求数据的参数字段中 页数字段的key
     *
     * @return 页数字段的key
     */
    protected String getPageIndexNameOnWeb() {
        return DEFAUL_START_PAGE;
    }


    /**
     * 分页请求时，向服务器请求数据的参数字段中 一页几项字段的key
     *
     * @return 一页几项字段的key
     */
    protected String getPageCountNameOnWeb() {
        return DEFAUL_SIZE;
    }


    /**
     * 设置分页加载时起始值代表起始项 还是 起始也
     *
     * @param method 0,起始项 1，起始页
     */
    protected void setStartMethod(int method) {
        mStartMethod = method;
    }


    /**
     * 获取分页加载时起始值代表起始项 还是 起始也
     */
    protected int getStartMethod() {
        return mStartMethod;
    }

    public class MyNetLoad extends NetLoader {

        public MyNetLoad(Context context) {
            super(context);
        }

        @Override
        public String parse(String jsonStr, Object... objects) {
            //调用ListLoader中的解析方法
            Result<ArrayList<T>> result = FenYeLoader.this.parse(jsonStr, objects);

            if (result.mIsSuccess)//解析成功，返回ok
                return NetLoader.OK;
            return result.mMessage;//解释失败，返回失败的原因
        }

        @Override
        public DownloadCheckTask initDownloadTask() {
            //调用FenYeLoader中的initDownloadTask方法
            return FenYeLoader.this.initDownloadTask();
        }


        @Override
        public String getAction() {
            return FenYeLoader.this.getAction();
        }
    }


    public EnumLoadingStatus getLoadingStatus() {
        return mMyNetLoader.getLoadingStaus();
    }


    public boolean isReachBottom() {
        return mReachBottom;
    }
}
