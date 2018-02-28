package com.my.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.my.app.R;

import java.util.List;

/**
 * Created by wangwei on 17/9/25.
 */
public class TabFragment extends Fragment implements ViewPager.OnPageChangeListener {


    public static class Page {
        public Page(String title, Fragment fragment) {
            mTitle = title;
            mFragment = fragment;
        }

        public String mTitle;
        public Fragment mFragment;
    }

    private List<Page> mPages;

    public TabLayout getTabLayout() {
        return (TabLayout) getView().findViewById(R.id.tabLayout);
    }


    public ViewPager getViewPager() {
        return (ViewPager) getView().findViewById(R.id.viewPager);
    }


    public void setupViewPager(List<Page> pages) {
        ViewPager viewPager = getViewPager();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        for (Page page : pages) {
            Bundle data = new Bundle();
            data.putInt("id", pages.size());
            data.putString("title", page.mTitle);
            page.mFragment.setArguments(data);
        }
        if (null != mPages)
            mPages.clear();
        mPages = pages;
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(mPages.size());
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
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mPages.get(position).mFragment.onResume();
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


    public Fragment getFragment(int position)
    {
        return ((FragmentStatePagerAdapter)getViewPager().getAdapter()).getItem(position);
    }

}
