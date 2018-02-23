package com.lf.view.tools.swiperefresh;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ScrollView;

/**
 * 具有滑动属性的View，对滑动位置的判断
 * @author ludeyuan
 *
 */
public class ScrollableStaus {
	/**
	 * tagrgetView是否已经滑动到顶部
	 * @param tagrgetView:AbsListView、ScrollView或RecyleView
	 * @return true滑动到顶部
	 */
	public static boolean isScrollToTop(View targetView){
		if(targetView==null){
			return false;
		}
		if(android.os.Build.VERSION.SDK_INT<14){
			if(targetView instanceof AbsListView){
				final AbsListView absListView = (AbsListView)targetView;
				return !(absListView.getChildCount() > 0 && (absListView
                        .getFirstVisiblePosition() > 0 || absListView
                        .getChildAt(0).getTop() < absListView.getPaddingTop()));
			}else{
				return !(targetView.getScrollY()>0);
			}
		}else{
			return !ViewCompat.canScrollVertically(targetView,-1);
		}
	}
	
	/**
	 * tagrgetView是否已经滑动到底部
	 * @param tagrgetView:AbsListView、ScrollView或RecyleView
	 * @return true滑动到底部
	 */
	public static boolean isScrollToBottom(View targetView){
		if(targetView==null){
			return false;
		}
		if(isScrollToTop(targetView)){
			//已经滑动到顶部，即使不满一屏，也按没有滑动到底部处理
			return false;
		}
		if(targetView instanceof RecyclerView){
			RecyclerView recyleView = (RecyclerView)targetView;
			LayoutManager layoutManager = recyleView.getLayoutManager();
			int count = recyleView.getAdapter().getItemCount();
			if(layoutManager instanceof LinearLayoutManager && count>0){
				//如果是线性布局，且子布局的数量大于0，判断最后一个View的位置
				LinearLayoutManager linearLayoutManager = (LinearLayoutManager)
						layoutManager;
				if(linearLayoutManager.findLastVisibleItemPosition() ==count-1){
					return true;
				}
			}else if(layoutManager instanceof StaggeredGridLayoutManager){
				//瀑布流,如果超过2列，不知道是会有问题
				StaggeredGridLayoutManager staggerdGridLayoutManager = 
						(StaggeredGridLayoutManager)layoutManager;
				int[] items = new int[2];
				staggerdGridLayoutManager.findLastVisibleItemPositions(items);
				int lastItem = Math.max(items[0], items[1]);
				if(lastItem == count-1){
					return true;
				}
			}
			return false;
		}else if(targetView instanceof AbsListView){
			AbsListView absListView = (AbsListView)targetView;
			int count = absListView.getAdapter().getCount();
			int firstPos = absListView.getFirstVisiblePosition();
			//整体的高度刚刚超过一个屏幕
			if(firstPos==0 && absListView.getChildAt(0).getTop() >=absListView
					.getPaddingTop()){
				return false;
			}
			int lastPos = absListView.getLastVisiblePosition();
			if(lastPos>0 && count>0 && lastPos==count-1){
				return true;
			}
			return false;
		}else if(targetView instanceof ScrollView){
			ScrollView scrollView = (ScrollView)targetView;
			View view = (View)scrollView.getChildAt(scrollView.getChildCount()-1);
			if(view!=null){
				int diff = view.getBottom() -(scrollView.getHeight() +
						scrollView.getScrollY());
				if(diff==0){
					return true;
				}
			}
			return false;
		}else{
			return false;
		}
	}
}
