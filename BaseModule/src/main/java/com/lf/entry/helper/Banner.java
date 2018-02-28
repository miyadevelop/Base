package com.lf.entry.helper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.lf.app.App;
import com.lf.entry.Entry;
import com.lf.entry.EntryManager;
import com.lf.view.tools.ScreenParameter;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 入口横幅,通过布局文件初始化时，通过属性url传入入口集合id，也可以动态初始化，通过参数传入入口集合id
 *
 * @author wangwei
 */
public class Banner extends ViewFlow {

    private String mEntryListId;//入口集合Id

    /**
     * 构造
     * @param context
     * @param entryListId 入口集合id
     */
    public Banner(Context context, String entryListId) {
        super(context);
        setVisibility(View.GONE);
        init(entryListId, true);
    }


    /**
     * 构造
     * @param context
     * @param entryListId 入口集合id
     * @param autoLoad 是否自动加载
     */
    public Banner(Context context, String entryListId, boolean autoLoad) {
        super(context);
        setVisibility(View.GONE);
        init(entryListId, autoLoad);
    }

    public Banner(Context context, AttributeSet attrs) {
        super(context, attrs);
        setVisibility(View.GONE);
        String id = attrs.getAttributeValue(null, "url");//读取入口集合的id
        boolean autoLoad = attrs.getAttributeBooleanValue(null, "auto_load", true);//是否自动加载
        if (null != id) {
            init(id, autoLoad);
        }
    }


    /**
     * 根据入口集合id初始化
     *
     * @param id
     */
    private void init(String id, boolean autoLoad) {
        mEntryListId = id;
        ArrayList<Entry> entries = EntryManager.getInstance(getContext()).get(id);
        if (entries.size() > 0) {
            setVisibility(View.VISIBLE);
        }
        EntryAdapter adapter = new EntryAdapter(getContext(), entries);
        setAdapter(adapter);
        setSideBuffer(entries.size());

        //注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(EntryManager.getInstance(getContext()).getAction());
        getContext().registerReceiver(mReceiver, filter);
        //加载入口集合
        if (autoLoad)
            EntryManager.getInstance(getContext()).load(id);
    }


    public BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String id = intent.getStringExtra("id");
            if (null != id && null != mEntryListId && id.contains(mEntryListId)) {
                ArrayList<Entry> entries = EntryManager.getInstance(getContext()).get(mEntryListId);
                //设置ViewFlow缓存的项数，需要在获取到数据后进行调用，否则自动滚动的动画将会异常
                setSideBuffer(entries.size());
                if (entries.size() > 0) {
                    //设置初始显示第0项，必须要设置，否则会崩溃
                    setSelection(0);
                    setVisibility(View.VISIBLE);
                    refreshImageWithUrl(Banner.this, entries.get(0).getImage(), ScreenParameter.getDisplayWidthAndHeight(App.mContext)[0]);
                } else {
                    setVisibility(View.GONE);
                }
                ((EntryAdapter) getAdapter()).notifyDataSetChanged();
            }
        }
    };


    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        if (View.VISIBLE == visibility)//在横幅可见时开始滚动
        {
            stopAutoFlow();
            startAutoFlow();
        } else//界面不可见时停止滚动
        {
            stopAutoFlow();
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //释放资源
        try {
            //onDetachedFromWindow不一定是在界面退出的时候调用，高版本的android会在很多情况下被调用
            //所以要判断一下Activity isFinishing
            if (((Activity) getContext()).isFinishing())
                getContext().unregisterReceiver(mReceiver);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.onInterceptTouchEvent(ev);
    }


    static Pattern p = Pattern.compile("_\\d+x\\d+");
    static Pattern p1 = Pattern.compile("(_)(\\d+)(x)(\\d+)");


    /**
     * 根据imageView要加载的图片的网址中的宽高 和 imageView目前的宽度，来调整imageView的高度，达到适配图片尺寸的效果
     *
     * @param imageView
     * @param url       imageView要加载的图片的网址
     * @param width     image view的实际宽度
     */
    public static void refreshImageWithUrl(View imageView, String url, int width) {
        //修改图片尺寸
        //图片网址示例：image\":\"http://cdn.doubiapp.com/zhuawa/img/20171023172922685_620x620.jpg
        Matcher matcher = p.matcher(url);
        if (matcher.find()) {
            Matcher matcher1 = p1.matcher(matcher.group());
            matcher1.find();
            ViewGroup.LayoutParams lp = imageView.getLayoutParams();
            if (null == lp)
                lp = new LayoutParams(-1, -2);
            lp.height = width * Integer.parseInt(matcher1.group(4)) / Integer.parseInt(matcher1.group(2));
            imageView.setLayoutParams(lp);
        }
    }


    public void release() {
        getContext().unregisterReceiver(mReceiver);
    }
}
