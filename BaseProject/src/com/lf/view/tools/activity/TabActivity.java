package com.lf.view.tools.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.lf.app.App;
import com.lf.base.R;
import com.lf.view.tools.MyImageButton;

import java.util.ArrayList;
import java.util.List;


/**
 * ViewPager+Fragment+Tab，常规的主页框架
 *
 * @author wangwei
 */
public abstract class TabActivity extends FragmentActivity implements OnItemClickListener, OnPageChangeListener {

    /**
     * 一页数据
     *
     * @author wangwei
     */
    public class Page {
        public Fragment mFragment;
        public String mImage;
        public String mText;
    }

    public ArrayList<Page> mPages;//所有页面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());

        mPages = new ArrayList<Page>();
        //初始化页面
        initPage();

        //初始化底部Tab
        GridView tab = getTab();
        tab.setChoiceMode(1);//只能选中一项
        TabAdapter mGroupAdapter = new TabAdapter(this, mPages);
        tab.setAdapter(mGroupAdapter);
        tab.setOnItemClickListener(this);
        tab.setSelection(0);
        tab.setNumColumns(mPages.size());
        tab.setItemChecked(0, true);
        tab.setFadingEdgeLength(0);
        tab.setSelector(new ColorDrawable(Color.TRANSPARENT));

        //初始化ViewPager
        ViewPager viewPager = getViewPager();
        viewPager.addOnPageChangeListener(this);
        viewPager.setAdapter(new MyFrageStatePagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(3);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        getViewPager().setCurrentItem(position);
    }


    /**
     * 添加一页
     *
     * @param text
     * @param image
     * @param fragment
     */
    protected void addPage(String text, String image, Fragment fragment) {
        Page page = new Page();
        page.mText = text;
        page.mImage = image;
        page.mFragment = fragment;
        mPages.add(page);
    }


    /**
     * 添加一页
     *
     * @param text
     * @param image
     * @param fragment
     */
    protected void addPage(String text, String image, Fragment fragment, int index) {
        Page page = new Page();
        page.mText = text;
        page.mImage = image;
        page.mFragment = fragment;
        mPages.add(index, page);
    }


    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    protected int mCurPosition = 0;

    @Override
    public void onPageSelected(int position) {

        //这里通过改变GridView的Selection和Checked来改变按钮的颜色，但这种方法仅在targetSdkVersion为11以上才有效
        GridView tab = getTab();
        tab.setSelection(position);
        tab.setItemChecked(position, true);
        View view = tab.getChildAt(position);
        if (null != view)
            view.setActivated(true);
        mCurPosition = position;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPages.clear();
    }


    /**
     * 底部Tab的Adapter
     *
     * @author wangwei
     */
    public class TabAdapter extends ArrayAdapter<Page> {

        public TabAdapter(Context context, List<Page> objects) {
            super(context, 0, objects);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (null == convertView) {
                convertView = getTabItemView();
                viewHolder = new ViewHolder();
                viewHolder.mTextView = (TextView) convertView.findViewById(App.id("tab_text"));
                viewHolder.mImageView = (MyImageButton) convertView.findViewById(App.id("tab_image"));
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.mTextView.setText(getItem(position).mText);
            viewHolder.mImageView.setImageDrawable(App.drawable(getItem(position).mImage));
            return convertView;
        }


        public class ViewHolder {
            public TextView mTextView;
            public MyImageButton mImageView;
        }
    }


    /**
     * 定义自己的ViewPager适配器。
     * 也可以使用FragmentPagerAdapter。关于这两者之间的区别，可以自己去搜一下。
     */
    class MyFrageStatePagerAdapter extends FragmentStatePagerAdapter {

        public MyFrageStatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mPages.get(position).mFragment;
        }

        @Override
        public int getCount() {
            return mPages.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;//这里重写是为了让调用nortifyDataSetChanged能刷新
        }
    }

    protected abstract void initPage();


//	protected abstract View getContentView();
//	
//	
//	protected abstract ViewPager getViewPager();
//	
//	
//	protected abstract GridView getTab();
//	
//	
//	protected abstract View getTabItemView();

    protected View getContentView() {
        return View.inflate(this, R.layout.base_activity_main, null);
    }


    public ViewPager getViewPager() {

        return (ViewPager) findViewById(App.id("main_viewpager"));
    }


    public GridView getTab() {
        return (GridView) findViewById(App.id("main_grid_tab"));
    }


    protected View getTabItemView() {
        return View.inflate(this, R.layout.base_item_tab_1, null);
    }


    public Fragment getFragment(int position)
    {
        return ((MyFrageStatePagerAdapter)getViewPager().getAdapter()).getItem(position);
    }

//    @Override
//    public void recreate() {
//        try {//避免重启太快 恢复
//            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
//                fragmentTransaction.remove(fragment);
//            }
//            fragmentTransaction.commitNow();
//        } catch (Exception e) {
//        }
//
//        findViewById(android.R.id.content).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                TabActivity.super.recreate();
//            }
//        }, 3000);
//
//    }

}
