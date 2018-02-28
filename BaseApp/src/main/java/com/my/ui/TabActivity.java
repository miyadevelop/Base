package com.my.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.my.app.R;

import java.util.List;

/**
 * Created by wangwei on 17/9/25.
 */
public class TabActivity extends BaseActivity implements ViewPager.OnPageChangeListener {


    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    public static class Page {
        public Page(String title, Fragment fragment) {
            mTitle = title;
            mFragment = fragment;
        }

        public Page(String title, int iconResId, Fragment fragment) {
            mTitle = title;
            mFragment = fragment;
            mIconResId = iconResId;
        }

        public String mTitle;
        public Fragment mFragment;
        public int mIconResId = -1;
    }


    protected List<Page> mPages;

    public TabLayout getTabLayout() {
        if (null == mTabLayout)
            mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        return mTabLayout;
    }


    public ViewPager getViewPager() {
        if (null == mViewPager)
            mViewPager = (ViewPager) findViewById(R.id.viewPager);
        return mViewPager;
    }


    public void setupViewPager(List<Page> pages) {
        ViewPager viewPager = getViewPager();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        for (Page page : pages) {
            Bundle data = page.mFragment.getArguments();
            if (null == data)
                data = new Bundle();
            data.putInt("id", pages.size());
            data.putString("title", page.mTitle);
            page.mFragment.setArguments(data);
        }
        if (null != mPages)
            mPages.clear();
        mPages = pages;
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(this);

        TabLayout tabLayout = getTabLayout();
        //适合很多tab
        //tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        //tab均分,适合少的tab
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        //tab均分,适合少的tab,TabLayout.GRAVITY_CENTER
        //tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        // 设置ViewPager的数据等
        tabLayout.setupWithViewPager(viewPager);


        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            if (-1 != mPages.get(i).mIconResId) {
                View newtab = LayoutInflater.from(this).inflate(R.layout.base_item_tab_1, null);
                TextView tv = newtab.findViewById(R.id.tab_text);
                tv.setText(mPages.get(i).mTitle);
                ImageView im = newtab.findViewById(R.id.tab_image);
                im.setImageResource(mPages.get(i).mIconResId);
                tabLayout.getTabAt(i).setCustomView(newtab);
            }
        }

        viewPager.setCurrentItem(0);
        onPageSelected(0);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }


    protected int mCurPosition;

    @Override
    public void onPageSelected(int position) {
        mPages.get(position).mFragment.onResume();
        {
            TabLayout.Tab tab = getTabLayout().getTabAt(mCurPosition);
            View view = tab.getCustomView();
            if (null != view)
                view.setActivated(false);
        }
        mCurPosition = position;
        {
            TabLayout.Tab tab = getTabLayout().getTabAt(mCurPosition);
            View view = tab.getCustomView();
            if (null != view)
                view.setActivated(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
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
        public CharSequence getPageTitle(int position) {
            return mPages.get(position).mTitle;
        }
    }


    public Fragment getFragment(int position) {
        return ((FragmentStatePagerAdapter) getViewPager().getAdapter()).getItem(position);
    }
}
