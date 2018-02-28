package com.lf.entry.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lf.entry.Entry;
import com.lf.entry.EntryManager;

import java.util.ArrayList;


/**
 * 开屏，随机从入口集合中显示一个入口,通过布局文件初始化时，通过属性url传入入口集合id，也可以动态初始化，通过参数传入入口集合id
 *
 * @author wangwei
 */
public class Splash extends ImageView {

    private String mEntryListId;//入口集合Id
    private Entry mEntry;

    /**
     * 构造
     * @param context
     * @param entryListId 入口集合id
     */
    public Splash(Context context, String entryListId) {
        super(context);
        init(entryListId, true);
    }


    /**
     * 构造
     * @param context
     * @param entryListId 入口集合id
     * @param autoLoad 是否自动加载
     */
    public Splash(Context context, String entryListId, boolean autoLoad) {
        super(context);
        init(entryListId, autoLoad);
    }


    public Splash(Context context, AttributeSet attrs) {
        super(context, attrs);
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

        setScaleType(ScaleType.FIT_XY);
        //注册广播监听入口加载
        IntentFilter filter = new IntentFilter();
        filter.addAction(EntryManager.getInstance(getContext()).getAction());
        getContext().registerReceiver(mReceiver, filter);
        //加载入口集合
        if (autoLoad)
            EntryManager.getInstance(getContext()).load(id);

        setOnClickListener(mOnClickListener);


        ArrayList<Entry> entries = EntryManager.getInstance(getContext()).get(id);
        if (entries.size() > 0) {
            //随机产生一个index，并通过index获取entry,Math.random():[0,1)
            int index = Double.valueOf((entries.size()) * Math.random()).intValue();
            mEntry = entries.get(index);
            //显示开屏图片
            if (null != mEntry) {
                setVisibility(View.VISIBLE);
                Glide.with(getContext()).load(Uri.parse(mEntry.getImage())).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(Splash.this);
            } else
                setVisibility(View.GONE);
        } else {
            setVisibility(View.GONE);

        }

    }


    public BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String id = intent.getStringExtra("id");
            if (null != id && null != mEntryListId && id.contains(mEntryListId)) {
                ArrayList<Entry> entries = EntryManager.getInstance(getContext()).get(mEntryListId);
                if (entries.size() > 0) {
                    if (null != mEntry)
                        return;
                    //随机产生一个index，并通过index获取entry,Math.random():[0,1)
                    int index = Double.valueOf((entries.size()) * Math.random()).intValue();
                    mEntry = entries.get(index);
                    //显示开屏图片
                    if (null != mEntry) {
                        setVisibility(View.VISIBLE);
                        Glide.with(getContext()).load(Uri.parse(mEntry.getImage())).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(Splash.this);
                    } else
                        setVisibility(View.GONE);
                } else {
                    setVisibility(View.GONE);

                }

            }
        }
    };


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //释放资源
        getContext().unregisterReceiver(mReceiver);
        mEntry = null;
        setOnClickListener(null);
    }


    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {

            if (null != mEntry)
                EntryManager.getInstance(getContext()).goTo(getContext(), mEntry);
        }
    };


    public void release() {
        //释放资源
        getContext().unregisterReceiver(mReceiver);
        mEntry = null;
        setOnClickListener(null);
    }
}
