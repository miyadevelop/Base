package com.lf.view.tools.swiperefresh;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

import com.lf.view.tools.ScreenParameter;
import com.lf.view.tools.UnitConvert;

/**
 * 仿照系统的SwipeRefreshLayout
 * 扩展：支
 * @author ludeyuan
 *
 */
public class SuperSwipeRefreshLayout extends ViewGroup{

	private final int INVALID_POINTER = -1;
	private final float DECELERATE_INTERPOLATION_FACTOR = 2f;//动画的执行速率
	private final DecelerateInterpolator mDecelerateInterpolator;//动画器
	private final int DEFAULT_CIRCLE_TARGET = 64;			//停留的位置
	private final float DRAG_RATE = 0.5f;					//手指拖动和界面移动的比例

	private boolean mScale;
	private float mStartingScale;
	private int mActivePointerId = INVALID_POINTER;		//手指触摸事件
	private float mInitialMotionY;
	private boolean mIsBeingDragged;					//开始拖动

	private boolean mNotify;
	private boolean mReturningToStart;					//回到原来的位置
	private boolean mRefreshing;
	private boolean mLoadingMore;

	private final int HEADER_OR_FOOTER_VIEW_HEIGHT = 50;//头部、尾部界面的高度
	private int mHeaderViewWidth,mHeaderViewHeight;//headerView的宽、高
	private int mFooterViewWidth,mFooterViewHeight;//footerView的宽、高
	private boolean mUsingDefaultHeader = true;	   //使用默认的Header
	private boolean mUsingCustomStart;
	private HeaderViewContainer mHeaderViewContainer;//头部的容器
	private RelativeLayout mFooterViewContainer;	 //底部的容器 
	private int mHeaderViewIndex=-1,mFooterViewIndex=-1;

	private boolean mTargetScrollWithLayout = true;//目标View跟随手指移动 
	private int mOriginalOffsetTop;
	private boolean mOriginalOffsetCalculated = false;

	private int mTouchSlop;	//最小的触碰间距
	private int mPushDistance = 0;//拖动的距离
	private int mCurrentTargetOffsetTop;//当前距离顶部的偏移量
	private float mSpinnerFinalOffset;//刷新时最后停留的位置，与DEFAULT_CIRCLE_TARGET成正比
	private int mMediumAnimationDuration;
	private float mTotalDragDistance = -1;	//最大的滑动距离

	private final int SCALE_DOWN_DURATION = 150;
	private final int ANIMATE_TO_TRIGGER_DURATION = 200;
	private final int ANIMATE_TO_START_DURATION = 200;
	private Animation mScaleAnimation;
	private Animation mScaleDownAnimation;
	private Animation mScaleDownToStartAnimation;

	//SuperSwipeRefreshLayout内的目标View，比如RecyclerView,ListView,ScrollView,GridView
	private View mTarget;
	private int mFrom;
	private boolean mProgressEnable=true;		//当前没有执行动画，或者动画已经结束
	private OnPullRefreshListener mRefreshListener;
	private OnPushLoadMoreListener mLoadListener;
	private CircleProgressView mCircleProgress;		//加载等待progress

	public SuperSwipeRefreshLayout(Context context){
		this(context,null);
	}

	public SuperSwipeRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mMediumAnimationDuration = getResources().getInteger(
                android.R.integer.config_mediumAnimTime);
		setWillNotDraw(false);
		mDecelerateInterpolator = new DecelerateInterpolator(
				DECELERATE_INTERPOLATION_FACTOR);

		//获取xml中定义的属性
		final TypedArray typeArray = context.obtainStyledAttributes(attrs,new int[]{
				android.R.attr.enabled
		});
		setEnabled(typeArray.getBoolean(0,true));
		typeArray.recycle();

		//获取手机屏幕的宽、高,赋值给头部和尾部的布局
		int[] screenParams = ScreenParameter.getDisplayWidthAndHeight(context);
		final DisplayMetrics metrics = getResources().getDisplayMetrics();
		mHeaderViewWidth = mFooterViewWidth = screenParams[0];
		mHeaderViewHeight = (int)(HEADER_OR_FOOTER_VIEW_HEIGHT*metrics.density);
		mFooterViewHeight = mHeaderViewHeight;
		mCircleProgress = new CircleProgressView(getContext());
		createHeaderViewContainer();
		createFooterViewContainer();
		ViewCompat.setChildrenDrawingOrderEnabled(this, true);
		mSpinnerFinalOffset = DEFAULT_CIRCLE_TARGET * metrics.density;
		mTotalDragDistance = mSpinnerFinalOffset;
	}
	
	/**
	 * 获取默认的加载等待样式，可以修改里面的颜色
	 * @return
	 */
	public CircleProgressView getCircleProgress(){
		if(mCircleProgress==null){
			mCircleProgress = new CircleProgressView(getContext());
		}
		return mCircleProgress;
	}

	/**
	 * 孩子节点绘制的顺序
	 */
	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		//将新添加的节点，放到最后绘制
		if(mHeaderViewIndex<0 && mFooterViewIndex<0){
			return i;
		}
		if(i==childCount-2){
			return mHeaderViewIndex;
		}
		if(i==childCount-1){
			return mFooterViewIndex;
		}
		int bigIndex = mFooterViewIndex > mHeaderViewIndex ? mFooterViewIndex
				: mHeaderViewIndex;
		int smallIndex = mFooterViewIndex < mHeaderViewIndex ? mFooterViewIndex
				: mHeaderViewIndex;
		if (i >= smallIndex && i < bigIndex - 1) {
			return i + 1;
		}
		if (i >= bigIndex || (i == bigIndex - 1)) {
			return i + 2;
		}
		return i;
	}
	
	@Override
	public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
		
	}

	/**
	 * 创建头部的容器
	 */
	private void createHeaderViewContainer(){
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				(int) (mHeaderViewHeight * 0.8),  (int) (mHeaderViewHeight * 0.8));
		//设置水平居中、垂直居底
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params.setMargins(0, 0, 0, UnitConvert.DpToPx(getContext(), 20));
		//定义容易，添加默认的界面（ProgressBar）
		mHeaderViewContainer = new HeaderViewContainer(getContext());
		mHeaderViewContainer.setVisibility(View.GONE);
		mCircleProgress.setVisibility(View.VISIBLE);
		mCircleProgress.setOnDraw(false);
		mHeaderViewContainer.addView(mCircleProgress,params);
		addView(mHeaderViewContainer);
	}

	/**
	 * 创建底部容器
	 */
	private void createFooterViewContainer(){
		mFooterViewContainer = new RelativeLayout(getContext());
		mFooterViewContainer.setVisibility(View.GONE);
		addView(mFooterViewContainer);
	}

	/**
	 * 确保target不为空
	 * mTarget一般是可滑动的View，如ScrollView、ListView、RecyleView等
	 */
	private void ensureTarget(){
		if(mTarget==null){
			for(int i=0;i<getChildCount();i++){
				View child = getChildAt(i);
				if(!child.equals(mHeaderViewContainer)
						&& !child.equals(mFooterViewContainer)){
					mTarget = child;
					break;
				}
			}
		}
	}

	/**
	 * 修改底部布局的位置-敏感pushDistance
	 */
	private void updateFooterViewPosition() {
		mFooterViewContainer.setVisibility(View.VISIBLE);
		mFooterViewContainer.bringToFront();
		mFooterViewContainer.offsetTopAndBottom(-mPushDistance);
		updatePushDistanceListener();
	}

	private void updatePushDistanceListener() {
		if (mLoadListener != null) {
			mLoadListener.onPushDistance(mPushDistance);
		}
	}

	/**
	 * 处理上拉加载更多的处理
	 */
	private boolean handlerLoadMoreTouchEvent(MotionEvent ev, int action){
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
			mIsBeingDragged = false;
			break;
		case MotionEvent.ACTION_MOVE:{
			final int pointerIndex = MotionEventCompat.findPointerIndex(ev,
					mActivePointerId);
			if(pointerIndex<0){
				return false;
			}
			float y = MotionEventCompat.getY(ev, pointerIndex);
			float overScollBottom = (mInitialMotionY - y) * DRAG_RATE;
			if (mIsBeingDragged) {
				mPushDistance = (int) overScollBottom;
				updateFooterViewPosition();
				if (mLoadListener != null) {
					mLoadListener.onPushEnable(mPushDistance >= mFooterViewHeight);
				}
			}
			break;
		}
		case MotionEventCompat.ACTION_POINTER_DOWN: {
			final int index = MotionEventCompat.getActionIndex(ev);
			mActivePointerId = MotionEventCompat.getPointerId(ev, index);
			break;
		}

		case MotionEventCompat.ACTION_POINTER_UP:
			onSecondaryPointerUp(ev);
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL: {
			if (mActivePointerId == INVALID_POINTER) {
				return false;
			}
			final int pointerIndex = MotionEventCompat.findPointerIndex(ev,
					mActivePointerId);
			final float y = MotionEventCompat.getY(ev, pointerIndex);
			final float overscrollBottom = (mInitialMotionY - y) * DRAG_RATE;// 松手是下拉的距离
			mIsBeingDragged = false;
			mActivePointerId = INVALID_POINTER;
			if (overscrollBottom < mFooterViewHeight
					|| mLoadListener == null) {// 直接取消
				mPushDistance = 0;
			} else {// 下拉到mFooterViewHeight
				mPushDistance = mFooterViewHeight;
			}
			if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
				updateFooterViewPosition();
				if (mPushDistance == mFooterViewHeight
						&& mLoadListener != null) {
					mLoadingMore = true;
					mLoadListener.onLoadMore();
				}
			} else {
				animatorFooterToBottom((int) overscrollBottom, mPushDistance);
			}
			return false;
		}
		}
		return true;
	}

	/**
	 * 处理下拉刷新
	 */
	private boolean handlerRefreshTouchEvent(MotionEvent ev, int action){
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
			mIsBeingDragged = false;
			break;
		case MotionEvent.ACTION_MOVE:{
			final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
			if(pointerIndex<0){
				return false;
			}
			float y = MotionEventCompat.getY(ev, pointerIndex);
			float overscrollTop = (y-mInitialMotionY)*DRAG_RATE;
			if(mIsBeingDragged){
				float originalDragPercent = overscrollTop/mTotalDragDistance;
				if (originalDragPercent < 0) {
					return false;
				}
				float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
				float extraOS = Math.abs(overscrollTop) - mTotalDragDistance;
				float slingshotDist = mUsingCustomStart ? mSpinnerFinalOffset
						- mOriginalOffsetTop : mSpinnerFinalOffset;
				float tensionSlingshotPercent = Math.max(0,
						Math.min(extraOS, slingshotDist * 2) / slingshotDist);
				float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math
						.pow((tensionSlingshotPercent / 4), 2)) * 2f;
				float extraMove = (slingshotDist) * tensionPercent * 2;

				int targetY = mOriginalOffsetTop
						+ (int) ((slingshotDist * dragPercent) + extraMove);
				if (mHeaderViewContainer.getVisibility() != View.VISIBLE) {
					mHeaderViewContainer.setVisibility(View.VISIBLE);
				}
				if (!mScale) {
					ViewCompat.setScaleX(mHeaderViewContainer, 1f);
					ViewCompat.setScaleY(mHeaderViewContainer, 1f);
				}
				if (mUsingDefaultHeader) {
					float alpha = overscrollTop / mTotalDragDistance;
					if (alpha >= 1.0f) {
						alpha = 1.0f;
					}
					ViewCompat.setScaleX(mCircleProgress, alpha);
					ViewCompat.setScaleY(mCircleProgress, alpha);
					ViewCompat.setAlpha(mCircleProgress, alpha);
				}
				if (overscrollTop < mTotalDragDistance) {
					if (mScale) {
						setAnimationProgress(overscrollTop / mTotalDragDistance);
					}
					if (mRefreshListener != null) {
						mRefreshListener.onPullEnable(false);
					}
				} else {
					if (mRefreshListener != null) {
						mRefreshListener.onPullEnable(true);
					}
				}
				setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop,
						true);
			}
			break;
		}
		case MotionEventCompat.ACTION_POINTER_DOWN: {
			final int index = MotionEventCompat.getActionIndex(ev);
			mActivePointerId = MotionEventCompat.getPointerId(ev, index);
			break;
		}
		case MotionEventCompat.ACTION_POINTER_UP:
			onSecondaryPointerUp(ev);
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL: {
			if (mActivePointerId == INVALID_POINTER) {
				return false;
			}
			final int pointerIndex = MotionEventCompat.findPointerIndex(ev,
					mActivePointerId);
			final float y = MotionEventCompat.getY(ev, pointerIndex);
			final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
			mIsBeingDragged = false;
			if (overscrollTop > mTotalDragDistance) {
				setRefreshing(true, true);
			} else {
				mRefreshing = false;
				Animation.AnimationListener listener = null;
				if (!mScale) {
					listener = new Animation.AnimationListener() {

						@Override
						public void onAnimationStart(Animation animation) {
						}

						@Override
						public void onAnimationEnd(Animation animation) {
							if (!mScale) {
								startScaleDownAnimation(null);
							}
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}

					};
				}
				animateOffsetToStartPosition(mCurrentTargetOffsetTop, listener);
			}
			mActivePointerId = INVALID_POINTER;
			return false;
		}
		default:
			break;
		}
		return true;
	}

	private float getMotionEventY(MotionEvent ev, int activePointerId) {
		final int index = MotionEventCompat.findPointerIndex(ev,
				activePointerId);
		if (index < 0) {
			return -1;
		}
		return MotionEventCompat.getY(ev, index);
	}

	private void onSecondaryPointerUp(MotionEvent ev) {
		final int pointerIndex = MotionEventCompat.getActionIndex(ev);
		final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
		if (pointerId == mActivePointerId) {
			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			mActivePointerId = MotionEventCompat.getPointerId(ev,
					newPointerIndex);
		}
	}

	private void setAnimationProgress(float progress){
		if(!mUsingDefaultHeader){
			progress=1;
		}
		ViewCompat.setScaleX(mHeaderViewContainer, progress);
		ViewCompat.setScaleY(mHeaderViewContainer, progress);
	}

	private void setRefreshing(boolean refreshing, final boolean notify) {
		if (mRefreshing != refreshing) {
			mNotify = notify;
			ensureTarget();
			mRefreshing = refreshing;
			if (mRefreshing) {
				animateOffsetToCorrectPosition(mCurrentTargetOffsetTop,
						mRefreshAniListener);
			} else {
				startScaleDownAnimation(mRefreshAniListener);
			}
		}
	}
	
	/**
     * 设置停止加载
     *
     * @param loadMore
     */
    public void setLoadMore(boolean loadMore) {
        if (!loadMore && mLoadingMore) {// 停止加载
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            	mLoadingMore = false;
                mPushDistance = 0;
                updateFooterViewPosition();
            } else {
                animatorFooterToBottom(mFooterViewHeight, 0);
            }
        }
    }

	private void setTargetOffsetTopAndBottom(int offset,boolean requiresUpdate){
		mHeaderViewContainer.bringToFront();
		mHeaderViewContainer.offsetTopAndBottom(offset);
		mCurrentTargetOffsetTop = mHeaderViewContainer.getTop();
		if(requiresUpdate && android.os.Build.VERSION.SDK_INT<11){
			invalidate();
		}
		updateListenerCallBack();
	}

	private void startScaleDownAnimation(Animation.AnimationListener listener) {
		mScaleDownAnimation = new Animation() {
			@Override
			public void applyTransformation(float interpolatedTime,
					Transformation t) {
				setAnimationProgress(1 - interpolatedTime);
			}
		};
		mScaleDownAnimation.setDuration(SCALE_DOWN_DURATION);
		mHeaderViewContainer.setAnimationListener(listener);
		mHeaderViewContainer.clearAnimation();
		mHeaderViewContainer.startAnimation(mScaleDownAnimation);
	}

	/**
	 * 判断View是否跟随手指移动
	 * @return
	 */
	public boolean isTargetScrollWithLayout(){
		return mTargetScrollWithLayout;
	}

	/**
	 * 设置目标View（AbsListView、ScrollView或RecrclerView）跟随手势移动而移动
	 */
	public void setTragetScrollViewLayout(boolean targetScrollWithLayout){
		mTargetScrollWithLayout = targetScrollWithLayout;
	}

	/**
	 * 添加头布局,在下拉刷新的时候出现
	 */
	public void setHeaderView(View child){
		if(child==null || mHeaderViewContainer==null)
			return;

		mUsingDefaultHeader = false;
		mHeaderViewContainer.removeAllViews();
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				mHeaderViewWidth, mHeaderViewHeight);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		mHeaderViewContainer.addView(child, layoutParams);
	}

	/**
	 * 设置底部的布局，在上拉加载更多数据的时候出现
	 * @param view
	 */
	public void setFooterView(View child){
		if (child == null || mFooterViewContainer == null) {
			return;
		}

		mFooterViewContainer.removeAllViews();
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				mFooterViewWidth, mFooterViewHeight);
		mFooterViewContainer.addView(child, layoutParams);
	}
	
	/**
	 * 设置下拉刷新的接口
	 */
	public void setOnPullRefreshListener(OnPullRefreshListener listener){
		mRefreshListener = listener;
	}
	
	/**
	 * 设置上拉加载的接口
	 */
	public void setOnPullLoadMoreListener(OnPushLoadMoreListener listener){
		mLoadListener = listener;
	}

	/**
	 * Notify the widget that refresh state has changed. Do not call this when
	 * refresh is triggered by a swipe gesture.
	 * @param refreshing
	 */
	public void setRefreshing(boolean refreshing) {
		if (refreshing && mRefreshing != refreshing) {
			// scale and show
			mRefreshing = refreshing;
			int endTarget = 0;
			if (!mUsingCustomStart) {
				endTarget = (int) (mSpinnerFinalOffset + mOriginalOffsetTop);
			} else {
				endTarget = (int) mSpinnerFinalOffset;
			}
			setTargetOffsetTopAndBottom(endTarget - mCurrentTargetOffsetTop,
					true /* requires update */);
			mNotify = false;
			startScaleUpAnimation(mRefreshAniListener);
		} else {
			setRefreshing(refreshing, false /* notify */);
			if (mUsingDefaultHeader) {
				mCircleProgress.setOnDraw(false);
			}
		}
	}

	/**
	 * 主要判断是否需要拦截子View的事件
	 * 如果拦截，交给OnTouchEvent处理
	 * 否则交给子View处理
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		ensureTarget();//找到具有滑动功能的View(如ListView)

		final int action = MotionEventCompat.getActionMasked(ev);
		if(mReturningToStart && action==MotionEvent.ACTION_DOWN){
			mReturningToStart = false;
		}
		if(!isEnabled() || mReturningToStart || mRefreshing || mLoadingMore ||
				(!ScrollableStaus.isScrollToTop(mTarget) &&
						!ScrollableStaus.isScrollToBottom(mTarget))){
			// 如果子View可以滑动，不拦截事件，交给子View处理-下拉刷新
			// 或者子View没有滑动到底部不拦截事件-上拉加载更多
			return false;
		}
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			//回复HeaderView最初的位置
			setTargetOffsetTopAndBottom(mOriginalOffsetTop-mHeaderViewContainer.getTop()
					,true);
			mActivePointerId = MotionEventCompat.getPointerId(ev,0);
			mIsBeingDragged = false;
			float initiaMotionY = getMotionEventY(ev, mActivePointerId);
			if(initiaMotionY == -1){
				return false;
			}
			mInitialMotionY = initiaMotionY;
			break;
		case MotionEvent.ACTION_MOVE:
			if(mActivePointerId == INVALID_POINTER){
				return false;
			}

			float y=getMotionEventY(ev,mActivePointerId);
			if(y==-1){
				return false;
			}
			float diffY=0;
			if(ScrollableStaus.isScrollToBottom(mTarget)){
				diffY = mInitialMotionY - y;	//计算上拉的距离
				if(diffY > mTouchSlop && !mIsBeingDragged){//判断下拉距离足够
					mIsBeingDragged = true;	//正在上拉
				}
			}else{
				diffY = y-mInitialMotionY;	//计算下拉的距离
				if(diffY>mTouchSlop && !mIsBeingDragged){
					mIsBeingDragged = true;	//正在上拉
				}
			}
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mIsBeingDragged = false;
			mActivePointerId = INVALID_POINTER;
			break;

		case MotionEventCompat.ACTION_POINTER_UP:
			onSecondaryPointerUp(ev);
			break;

		default:
			break;
		}
		return mIsBeingDragged;
	}

	/**
	 * 负责处理滑动事件
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int action = MotionEventCompat.getActionMasked(event);

		if(mReturningToStart && action==MotionEvent.ACTION_DOWN){
			mReturningToStart = false;
		}
		if (!isEnabled() || mReturningToStart
				|| (!ScrollableStaus.isScrollToTop(mTarget) 
						&& !ScrollableStaus.isScrollToBottom(mTarget))) {
			// 如果子View可以滑动，不拦截事件，交给子View处理
			return false;
		}
		if(ScrollableStaus.isScrollToBottom(mTarget)){//上拉加载更多
			return handlerLoadMoreTouchEvent(event, action);
		}else{//下拉刷新
			return handlerRefreshTouchEvent(event, action);
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int right, int top, int bottom) {
		final int width = getMeasuredWidth();
		final int height = getMeasuredHeight();
		if(getChildCount()==0){
			return;
		}
		if(mTarget==null){//找到目标中可以滑动View
			ensureTarget();
		}
		if(mTarget==null){
			return;
		}
		int distance = mCurrentTargetOffsetTop + mHeaderViewContainer.getHeight();
		if(!mTargetScrollWithLayout){
			distance = 0;
		}
		final View child = mTarget;	//设置需要移动目标View
		final int childLeft = getPaddingLeft();
		//根据偏移量distance
		final int childTop = getPaddingTop() + distance - mPushDistance;
		final int childWidth = width - getPaddingLeft() - getPaddingRight();
		final int childHeight = height - getPaddingTop() - getPaddingBottom();
		child.layout(childLeft,childTop,childLeft+childWidth,childTop+childHeight);
		int headViewWidth = mHeaderViewContainer.getMeasuredWidth();
		int headViewHeight = mHeaderViewContainer.getMeasuredHeight();
		//更新头部的位置
		mHeaderViewContainer.layout(width/2-headViewWidth/2, mCurrentTargetOffsetTop,
				width/2+headViewWidth/2, mCurrentTargetOffsetTop + headViewHeight);
		int footViewWidth = mFooterViewContainer.getMeasuredWidth();
		int footViewHeight = mFooterViewContainer.getMeasuredHeight();
		mFooterViewContainer.layout((width / 2 - footViewWidth / 2), height
				- mPushDistance, (width / 2 + footViewWidth / 2), height
				+ footViewHeight - mPushDistance);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (mTarget == null) {
			ensureTarget();
		}
		if (mTarget == null) {
			return;
		}
		mTarget.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth()
				-getPaddingLeft() -getPaddingRight(),MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(getMeasuredHeight() 
						-getPaddingTop() -getPaddingBottom(), MeasureSpec.EXACTLY));
		mHeaderViewContainer.measure(
				MeasureSpec.makeMeasureSpec(mHeaderViewWidth, MeasureSpec.EXACTLY), 
				MeasureSpec.makeMeasureSpec(3*mHeaderViewHeight, MeasureSpec.EXACTLY));
		mFooterViewContainer.measure(
				MeasureSpec.makeMeasureSpec(mFooterViewWidth, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(mFooterViewHeight, MeasureSpec.EXACTLY));
		if(!mUsingCustomStart && !mOriginalOffsetCalculated){
			mOriginalOffsetCalculated = true;
			mCurrentTargetOffsetTop = mOriginalOffsetTop = 
					-mHeaderViewContainer.getMeasuredHeight();
			updateListenerCallBack();
		}
		//找到HeaderView和FooterView在父布局中的索引值
		mHeaderViewIndex = -1;
		for(int i=0;i<getChildCount();i++){
			if(getChildAt(i)==mHeaderViewContainer){
				mHeaderViewIndex = i;
				break;
			}
		}
		mFooterViewIndex = -1;
		for(int i=0;i<getChildCount();i++){
			if(getChildAt(i)==mFooterViewContainer){
				mFooterViewIndex = i;
				break;
			}
		}
	}

	/**
	 * 更新回调
	 */
	private void updateListenerCallBack(){
		int distance = mCurrentTargetOffsetTop +mHeaderViewContainer.getHeight();
		if(mRefreshListener!=null){
			mRefreshListener.onPullDistance(distance);
		}
		if(mUsingDefaultHeader && mProgressEnable){
			mCircleProgress.setPullDistance(distance);
		}
	}

	/**
	 * 下拉时，超过距离之后，弹回来的动画监听器
	 */
	private Animation.AnimationListener mRefreshAniListener = new Animation.AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
			mProgressEnable = false;
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			mProgressEnable = true;
			if (mRefreshing) {
				if (mNotify) {
					if (mUsingDefaultHeader) {
						ViewCompat.setAlpha(mCircleProgress, 1.0f);
						mCircleProgress.setOnDraw(true);
						new Thread(mCircleProgress).start();
					}
					if (mRefreshListener != null) {
						mRefreshListener.onRefresh();
					}
				}
			} else {
				mHeaderViewContainer.setVisibility(View.GONE);
				if (mScale) {
					setAnimationProgress(0);
				} else {
					setTargetOffsetTopAndBottom(mOriginalOffsetTop
							- mCurrentTargetOffsetTop, true);
				}
			}
			mCurrentTargetOffsetTop = mHeaderViewContainer.getTop();
			updateListenerCallBack();
		}
	};

	private final Animation mAnimateToCorrectPosition = new Animation() {
		@Override
		public void applyTransformation(float interpolatedTime, Transformation t) {
			int targetTop = 0;
			int endTarget = 0;
			if (!mUsingCustomStart) {
				endTarget = (int) (mSpinnerFinalOffset - Math
						.abs(mOriginalOffsetTop));
			} else {
				endTarget = (int) mSpinnerFinalOffset;
			}
			targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
			int offset = targetTop - mHeaderViewContainer.getTop();
			setTargetOffsetTopAndBottom(offset, false /* requires update */);
		}

		@Override
		public void setAnimationListener(AnimationListener listener) {
			super.setAnimationListener(listener);
		}
	};

	private final Animation mAnimateToStartPosition = new Animation() {
		@Override
		public void applyTransformation(float interpolatedTime, Transformation t) {
			moveToStart(interpolatedTime);
		}
	};

	private void animateOffsetToCorrectPosition(int from,
			AnimationListener listener) {
		mFrom = from;
		mAnimateToCorrectPosition.reset();
		mAnimateToCorrectPosition.setDuration(ANIMATE_TO_TRIGGER_DURATION);
		mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
		if (listener != null) {
			mHeaderViewContainer.setAnimationListener(listener);
		}
		mHeaderViewContainer.clearAnimation();
		mHeaderViewContainer.startAnimation(mAnimateToCorrectPosition);
	}

	/**
	 * 松手之后，使用动画将Footer从距离start变化到end
	 *
	 * @param start
	 * @param end
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void animatorFooterToBottom(int start, final int end) {
		ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
		valueAnimator.setDuration(150);
		valueAnimator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				// update
				mPushDistance = (Integer) valueAnimator.getAnimatedValue();
				updateFooterViewPosition();
			}
		});
		valueAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				if (end > 0 && mLoadListener != null) {
					mLoadingMore = true;
					mLoadListener.onLoadMore();
				} else {
					resetTargetLayout();
					mLoadingMore = false;
				}
			}
		});
		valueAnimator.setInterpolator(mDecelerateInterpolator);
		valueAnimator.start();
	}

	private void animateOffsetToStartPosition(int from,
			AnimationListener listener) {
		if (mScale) {
			startScaleDownReturnToStartAnimation(from, listener);
		} else {
			mFrom = from;
			mAnimateToStartPosition.reset();
			mAnimateToStartPosition.setDuration(ANIMATE_TO_START_DURATION);
			mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
			if (listener != null) {
				mHeaderViewContainer.setAnimationListener(listener);
			}
			mHeaderViewContainer.clearAnimation();
			mHeaderViewContainer.startAnimation(mAnimateToStartPosition);
		}
		resetTargetLayoutDelay(ANIMATE_TO_START_DURATION);
	}

	/**
	 * 重置Target位置
	 *
	 * @param delay
	 */
	public void resetTargetLayoutDelay(int delay) {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				resetTargetLayout();
			}
		}, delay);
	}

	/**
	 * 重置Target的位置
	 */
	public void resetTargetLayout() {
		final int width = getMeasuredWidth();
		final int height = getMeasuredHeight();
		final View child = mTarget;
		final int childLeft = getPaddingLeft();
		final int childTop = getPaddingTop();
		final int childWidth = child.getWidth() - getPaddingLeft()
				- getPaddingRight();
		final int childHeight = child.getHeight() - getPaddingTop()
				- getPaddingBottom();
		child.layout(childLeft, childTop, childLeft + childWidth, childTop
				+ childHeight);

		int headViewWidth = mHeaderViewContainer.getMeasuredWidth();
		int headViewHeight = mHeaderViewContainer.getMeasuredHeight();
		mHeaderViewContainer.layout((width / 2 - headViewWidth / 2),
				-headViewHeight, (width / 2 + headViewWidth / 2), 0);// 更新头布局的位置
		int footViewWidth = mFooterViewContainer.getMeasuredWidth();
		int footViewHeight = mFooterViewContainer.getMeasuredHeight();
		mFooterViewContainer.layout((width / 2 - footViewWidth / 2), height,
				(width / 2 + footViewWidth / 2), height + footViewHeight);
	}

	private void moveToStart(float interpolatedTime) {
		int targetTop = 0;
		targetTop = (mFrom + (int) ((mOriginalOffsetTop - mFrom) * interpolatedTime));
		int offset = targetTop - mHeaderViewContainer.getTop();
		setTargetOffsetTopAndBottom(offset, false /* requires update */);
	}

	private void startScaleDownReturnToStartAnimation(int from,
			Animation.AnimationListener listener) {
		mFrom = from;
		mStartingScale = ViewCompat.getScaleX(mHeaderViewContainer);
		mScaleDownToStartAnimation = new Animation() {
			@Override
			public void applyTransformation(float interpolatedTime,
					Transformation t) {
				float targetScale = (mStartingScale + (-mStartingScale * interpolatedTime));
				setAnimationProgress(targetScale);
				moveToStart(interpolatedTime);
			}
		};
		mScaleDownToStartAnimation.setDuration(SCALE_DOWN_DURATION);
		if (listener != null) {
			mHeaderViewContainer.setAnimationListener(listener);
		}
		mHeaderViewContainer.clearAnimation();
		mHeaderViewContainer.startAnimation(mScaleDownToStartAnimation);
	}

	private void startScaleUpAnimation(AnimationListener listener) {
		mHeaderViewContainer.setVisibility(View.VISIBLE);
		mScaleAnimation = new Animation() {
			@Override
			public void applyTransformation(float interpolatedTime,
					Transformation t) {
				setAnimationProgress(interpolatedTime);
			}
		};
		mScaleAnimation.setDuration(mMediumAnimationDuration);
		if (listener != null) {
			mHeaderViewContainer.setAnimationListener(listener);
		}
		mHeaderViewContainer.clearAnimation();
		mHeaderViewContainer.startAnimation(mScaleAnimation);
	}
}
