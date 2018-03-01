package com.my.ui.m;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.lf.controler.tools.download.helper.FenYeMapLoader2;
import com.lf.controler.tools.download.helper.LoadParam;
import com.lf.view.tools.FontHelper;
import com.lf.view.tools.SimpleFenYeFragment3;
import com.my.app.R;
import com.my.m.comment.Comment;
import com.my.m.comment.CommentLoader;

/**
 * 评论列表
 * Created by wangwei on 17/11/13.
 */
public class CommentListFragment2 extends SimpleFenYeFragment3<Comment> {


    @Override
    public FenYeMapLoader2<Comment> getLoader() {
        return CommentLoader.getInstance();
    }

    @Override
    public BaseFenYeModule getRVModule(LoadParam loadParam) {
        return new MyModule(loadParam);
    }


    public class MyModule extends BaseFenYeModule {

        public MyModule(LoadParam loadParam) {
            super(loadParam);
        }

        @Override
        public RVBaseViewHolder getViewHolder(ViewGroup viewGroup, Context context, int itemType) {
            return new MySimpleViewHolder(View.inflate(getContext(), R.layout.base_item_comment2, null));
        }

        @Override
        public void onItemClick(View view, Comment comment) {
        }


        public class MySimpleViewHolder extends SimpleViewHolder {

            public MySimpleViewHolder(View view) {
                super(view, Comment.class);
                FontHelper.applyFont(getContext(), view, FontHelper.APP_FONT);
            }

        }
    }


//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        if (getContext() instanceof ArticleActivity) {
//            return View.inflate(getContext(), R.layout.shop_layout_common_recycler, null);
//        } else
//            return super.onCreateView(inflater, container, savedInstanceState);
//    }
//
//
//    @Override
//    public RecyclerView getRecyclerView() {
//        if (getContext() instanceof ArticleActivity) {
//            return (RecyclerView) getView();
//        } else
//            return super.getRecyclerView();
//    }
//
//
//    @Override
//    public SwipeRefreshLayout getSwipeRefreshLayout() {
//        if (getContext() instanceof ArticleActivity) {
//            return null;
//        } else
//            return super.getSwipeRefreshLayout();
//    }


    @Override
    public boolean isNeedAutoLoad() {
        return false;
    }
}
