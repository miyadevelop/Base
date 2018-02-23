package com.lf.view.tools;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.lf.base.R;

import java.util.ArrayList;


/**
 * 通用的分页加载时用的Adapter，使用时，继承此类，并且像继承ArrayAdapter一样，重写此类子函数，暂时不支持子类重写getItemViewType，
 * 暂时只能作为ListView 的Adapter使用，可通过本类的setColumnCount函数，实现和GridView一样的布局
 *
 * @param <T>
 * @author wangwei
 */
public abstract class FenYeAdapter<T> extends BaseAdapter {

    private int mColumnCount = 1;//默认列表
    private OnLoadListener mOnLoadListener;
    private Context mContext;
    private ArrayList<T> mResourceData;//源数据
    private ArrayList<T> mData;//列表缓存显示的数据，由于决定是否要显示“正在加载”这个判断方法是position >= getCount() - 1，所以不用另外的缓存的话，会在源数据发生改变 但 notify尚未生效前，会影响判断的正确性，从而导致获取不到正确的view type
    private OnItemClickListener mOnItemClickListener;
    private static final int TYPE_ITEM = 0;//常规item
    public static final int TYPE_LOADING = 1; //正在加载
    public static final int TYPE_LOADED = 2;//加载完成
    public static final int TYPE_LOAD_FAILD = 3;//加载失败
    private int mLoadType = TYPE_LOADING;//当前的加载状态
    private int mPaddingPx;//每一行的左右缩进
    private int mIndex = 0;
    private int mAnimationY;//动画纵向移动距离

    public FenYeAdapter(Context context, ArrayList<T> objects) {
        super();
        mContext = context;
        mResourceData = objects;
        mData = new ArrayList<T>();
        mData.addAll(mResourceData);
        mAnimationY = ScreenParameter.getDisplayWidthAndHeight(getContext())[1] / 5;
    }


    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {

        int type = getItemViewType(position);
        if (type == TYPE_LOADING)//正在加载
        {
            if (mLoadingView == null) {
                mLoadingView = getLoadingView();
            }
            convertView = mLoadingView;
            if (null != mOnLoadListener)
                mOnLoadListener.onLoad();
        } else if (type == TYPE_LOADED)//加载完成
        {
            if (mLoadedView == null) {
                mLoadedView = getLoadedView();
            }
            convertView = mLoadedView;
        } else if (type == TYPE_LOAD_FAILD)//加载失败
        {
            if (mLoadFailedView == null) {
                mLoadFailedView = getLoadFailedView();

                mLoadFailedView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        if (null != mOnLoadListener)//点击重新加载
                            mOnLoadListener.onLoad();

                    }
                });
            }
            convertView = mLoadFailedView;
        } else //普通的item
        {
            if (convertView == null) {
                if (mColumnCount == 1) {
                    convertView = getView(position, null);
                    convertView.setOnClickListener(mOnClickListener);
                    convertView.setId(position);
//                    mIndex++;
//                    Animation animation = new TranslateAnimation(0, 0, mAnimationY, 0);
//                    animation.setDuration(300);
//                    animation.setStartOffset(100);
//                    animation.setInterpolator(new DecelerateInterpolator());
//                    convertView.setTag(MyR.id(getContext(), "tag_bean2view_feilder_holder"), animation);
                } else {
                    TableRow row = new TableRow(mContext);
                    mIndex++;
                    for (int i = 0; i < mColumnCount; i++) {
                        int realPositon = position * mColumnCount + i;
                        View itemView;
                        if (realPositon < mData.size())
                            itemView = getView(realPositon, null);
                        else//在数据越界的情况下，position传0，让view装载第一项数据，并将View设置为不可见
                        {
                            itemView = getView(0, null);
                            itemView.setVisibility(View.INVISIBLE);
                        }
                        itemView.setOnClickListener(mOnClickListener);
                        itemView.setId(realPositon);
                        row.addView(itemView);
                    }
                    row.setPadding(mPaddingPx, 0, mPaddingPx, 0);
                    convertView = row;
                    //给每个row赋值动画
                    Animation animation = new TranslateAnimation(0, 0, mAnimationY, 0);
                    animation.setDuration(300);
                    animation.setStartOffset(100);
                    animation.setInterpolator(new DecelerateInterpolator());
                    row.setTag(animation);
                }
            } else {
                if (mColumnCount == 1) {
                    getView(position, convertView).setId(position);

//                    //播放动画，index用来保证只有向下滑动时才有动画,mAnimationY > 300保证只有大屏高配的手机才会播放动画
//                    if (mIndex == position && mAnimationY > 300) {
//                        mIndex++;
//                        Animation animation = (Animation) convertView.getTag(MyR.id(getContext(), "tag_bean2view_feilder_holder"));
//                        animation.reset();
//                        convertView.startAnimation(animation);
//                    }

                } else {
                    TableRow row = (TableRow) convertView;
                    for (int i = 0; i < row.getChildCount(); i++) {
                        int realPositon = position * mColumnCount + i;
                        if (realPositon < mData.size()) {
                            row.getChildAt(i).setVisibility(View.VISIBLE);
                            getView(realPositon, row.getChildAt(i)).setId(realPositon);
                        } else {
                            row.getChildAt(i).setVisibility(View.INVISIBLE);
                        }
                    }

                    //播放动画，index用来保证只有向下滑动时才有动画,mAnimationY > 300保证只有大屏高配的手机才会播放动画
                    if (mIndex == position && mAnimationY > 300) {
                        mIndex++;
                        Animation animation = (Animation) row.getTag();
                        animation.reset();
                        row.startAnimation(animation);
                    }
                }
            }
        }

        return convertView;
    }


    @Override
    public int getCount() {
        return mData.size() / mColumnCount + (mData.size() % mColumnCount > 0 ? 1 : 0) + 1;
    }


    @Override
    public final int getItemViewType(int position) {

        if (position < getCount() - 1)//非最后一项
            return TYPE_ITEM;
        else //最后一项，mLoadType的值就代表view type
            return mLoadType;
    }


    @Override
    public final int getViewTypeCount() {
        return 4;
    }


    /**
     * 获取真实的数据项数
     *
     * @return
     */
    public int getRealCount() {
        return mData.size();
    }


    /**
     * 触发加载的回调
     *
     * @author wangwei
     */
    public interface OnLoadListener {
        public void onLoad();
    }


    /**
     * 设置调用加载的回调
     *
     * @param listener
     */
    public void setOnLoadListener(OnLoadListener listener) {
        mOnLoadListener = listener;
    }


    /**
     * 设置数据当前加载状态
     *
     * @param loadType 正在加载：TYPE_LOADING = 1; 加载完成：TYPE_LOADED = 2；加载失败：TYPE_LOAD_FAILD = 3
     */
    public final void setLoadType(int loadType) {
        mLoadType = loadType;
        super.notifyDataSetChanged();
    }


    /**
     * 设置几列
     *
     * @param count
     */
    public final void setColumnCount(int count) {
        if (count < 1)
            return;
        mColumnCount = count;
    }


    /**
     * 让子类可以像重写常规的Adapter一样去重写本类
     *
     * @param position
     * @param convertView
     * @return
     */
    public abstract View getView(int position, View convertView);


    /**
     * 获取指定的一项数据
     */
    public T getItem(int position) {
        return mData.get(position);
    }


    /**
     * 设置点击监听
     *
     * @param clickListener
     */
    public void setOnItemClickListener(OnItemClickListener clickListener) {
        mOnItemClickListener = clickListener;
    }


    public final Context getContext() {
        return mContext;
    }


    @Override
    public void notifyDataSetChanged() {
        //把源数据加仅要显示的数据中
        mData.clear();
        mData.addAll(mResourceData);
        super.notifyDataSetChanged();
    }


    @Override
    public long getItemId(int arg0) {
        return 0;
    }


    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            if (null != mOnItemClickListener)
                mOnItemClickListener.onItemClick(null, view, view.getId(), 0);
        }
    };


    private View mLoadingView;

    /**
     * 获取正在加载的布局，子类可重新实现
     *
     * @return
     */
    public View getLoadingView() {
        int px = UnitConvert.DpToPx(getContext(), 40);
        ProgressBar progressBar = new ProgressBar(getContext());
        progressBar.setLayoutParams(new AbsListView.LayoutParams(-1, px));
        progressBar.setPadding(0, px / 5, 0, px / 5);
        return progressBar;
    }


    private View mLoadedView;
    /**
     * 获取加载结束的布局，子类可重新实现
     *
     * @return
     */
    public static String mLoadOverText = "~ End ~";

    public View getLoadedView() {
        int px = UnitConvert.DpToPx(getContext(), 40);
        TextView loadedView = new TextView(getContext());
        loadedView.setTextColor(getContext().getResources().getColor(R.color.module_1_text_3));
        loadedView.setText(mLoadOverText);
        loadedView.setGravity(Gravity.CENTER);
        loadedView.setLayoutParams(new AbsListView.LayoutParams(-1, px));
        return loadedView;
    }


    private View mLoadFailedView;
    /**
     * 获取加载结束的布局，子类可重新实现
     *
     * @return
     */
    public static String mLoadLoadFailedText = "Load failed，try again!";

    public View getLoadFailedView() {
        int px = UnitConvert.DpToPx(getContext(), 40);
        TextView loadFailedView = new TextView(getContext());
        loadFailedView.setText(mLoadLoadFailedText);
        loadFailedView.setTextColor(getContext().getResources().getColor(R.color.module_1_text_3));
        loadFailedView.setGravity(Gravity.CENTER);
        loadFailedView.setLayoutParams(new AbsListView.LayoutParams(-1, px));
        return loadFailedView;
    }


    public void release() {
        mData.clear();
        mLoadingView = null;
        mLoadFailedView = null;
        mLoadedView = null;
        mOnItemClickListener = null;
        mOnLoadListener = null;
        mContext = null;
    }

    /**
     * 设置左右缩进
     */
    public void setPaddingDp(int paddingDp) {
        mPaddingPx = UnitConvert.DpToPx(getContext(), paddingDp);
    }
}
