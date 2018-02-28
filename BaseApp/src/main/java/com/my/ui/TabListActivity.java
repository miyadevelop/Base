package com.my.ui;

import android.os.Bundle;

import com.lf.controler.tools.download.helper.LoadParam;
import com.lf.view.tools.SimpleFenYeFragment3;
import com.my.app.R;

import java.util.List;

/**
 * 通用的Tab + SimpleFenYeFragment3 工具界面
 * Created by wangwei on 18/1/18.
 */
public class TabListActivity extends TabActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity_tab_1);
    }


    @Override
    public void onPageSelected(final int position) {
        super.onPageSelected(position);

        getViewPager().postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleFenYeFragment3<?> fragment = (SimpleFenYeFragment3<?>) getSupportFragmentManager().getFragments().get(position);
                fragment.goToLoad(mLoadParams.get(position));
            }
        }, 200);
    }

    List<LoadParam> mLoadParams;
    public void setupViewPager(List<Page> pages , List<LoadParam> params)
    {
        mLoadParams = params;
        setupViewPager(pages);

        //这里如果不使用delay，会导致Fragment还没加入到界面，就调用了goToLoad，进而导致一些空指针问题
        getViewPager().postDelayed(new Runnable() {
            @Override
            public void run() {
                onPageSelected(0);
            }
        }, 300);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoadParams.clear();
    }
}
