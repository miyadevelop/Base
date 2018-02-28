package com.my.ui;

import android.support.v4.app.Fragment;

/**
 * 搜索界面
 */
public class SearchActivity extends BaseSearchActivity{

    /**
     * 初始化显示搜索结果的Fragment该Fragment需要implements BaseSearchActivity.LoadInterface
     * @see com.my.ui.BaseSearchActivity.LoadInterface
     */
    @Override
    public Fragment initFragment() {
        //TODO 返回显示搜索结果的Fragment，该Fragment需要implements BaseSearchActivity.LoadInterface
        return new Fragment();
    }

}
