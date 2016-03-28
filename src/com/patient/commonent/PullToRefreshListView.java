package com.patient.commonent;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.patient.library.bitmap.ImageFetcher;
import com.patient.library.dragsortlistview.AnimationRemoveListView;
import com.patient.util.DateUtil;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

public class PullToRefreshListView extends AnimationRemoveListView implements
		OnScrollListener {

	private static final String TAG = "listview";
	
	// 当前第几页,默认是从第一页加载的数据，初始值为1
	private  int currentPage  = 1;
	// 总页数
	private  int totalPage = 1;
	
	private ImageFetcher mImageFetcher;
	public void setImageFetcher(ImageFetcher fetcher) {
		this.mImageFetcher = fetcher;
	}

	private final static int RELEASE_To_REFRESH = 0;
	private final static int PULL_To_REFRESH = 1;
	private final static int REFRESHING = 2;
	private final static int DONE = 3;
	private final static int LOADING = 4;

	// 实际的padding的距离与界面上偏移距离的比例
	private final static int RATIO = 3;

	private LayoutInflater inflater;

	private LinearLayout headView;

	private TextView tipsTextview;
	private TextView lastUpdatedTextView;
	private ImageView arrowImageView;
	private ProgressBar progressBar;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean isRecored;

	private int headContentWidth;
	private int headContentHeight;

	private int startY;

	private int state;

	private boolean isBack;

	private OnRefreshListener refreshListener;

	private boolean isRefreshable;

	public PullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		setCacheColorHint(context.getResources().getColor(android.R.color.transparent));
		inflater = LayoutInflater.from(context);
		// 给listview 添加上啦加载更多的功能
		footer = inflater.inflate(R.layout.listview_footer, null);
		loadFull = (TextView) footer.findViewById(R.id.loadFull);
		noData = (TextView) footer.findViewById(R.id.noData);
		more = (TextView) footer.findViewById(R.id.more);
		loading = (ProgressBar) footer.findViewById(R.id.loading);
		// 默认隐藏上啦刷新
		footer.setVisibility(View.GONE);
		 
		headView = (LinearLayout) inflater.inflate(R.layout.pull_to_refresh_header, null);
		arrowImageView = (ImageView) headView.findViewById(R.id.head_arrowImageView);
		arrowImageView.setMinimumWidth(70);
		arrowImageView.setMinimumHeight(50);
		progressBar = (ProgressBar) headView.findViewById(R.id.head_progressBar);
		tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
		lastUpdatedTextView = (TextView) headView.findViewById(R.id.head_lastUpdatedTextView);

		measureView(headView);
		headContentHeight = headView.getMeasuredHeight();
		headContentWidth = headView.getMeasuredWidth();
		
		// 隐藏 headview 的高度
		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		headView.invalidate();

		Log.v("size", "width:" + headContentWidth + " height:" + headContentHeight);

		addHeaderView(headView, null, false);
	    addFooterView(footer);
		setOnScrollListener(this);
		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(1000);
		animation.setFillAfter(false);

		reverseAnimation = new RotateAnimation(0, -180, 
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(1000);
		reverseAnimation.setFillAfter(false);

		state = DONE;
		isRefreshable = false;
	}

	public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2,int arg3) {
		this.firstItemIndex = firstVisiableItem;
	}

	public boolean onTouchEvent(MotionEvent event) {
		firstItemIndex = getFirstVisiblePosition();
		if (isRefreshable) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (firstItemIndex == 0 && !isRecored) {
					isRecored = true;
					startY = (int) event.getY();
					Log.v(TAG, "在down时候记录当前位置");
				}
				break;
			case MotionEvent.ACTION_UP:
				if (state != REFRESHING && state != LOADING) {
					if (state == DONE) {
						// 什么都不做
					}
					if (state == PULL_To_REFRESH) {
						state = DONE;
						changeHeaderViewByState();
						Log.v(TAG, "由下拉刷新状态，到done状态");
					}
					if (state == RELEASE_To_REFRESH) {
						state = REFRESHING;
						changeHeaderViewByState();
						onRefresh();
						Log.v(TAG, "由松开刷新状态，到done状态");
					}
				}

				isRecored = false;
				isBack = false;
				break;
			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();
				if (!isRecored && firstItemIndex == 0) {
					Log.v(TAG, "在move时候记录下位置");
					isRecored = true;
					startY = tempY;
				}

				if (state != REFRESHING && isRecored && state != LOADING) {
					// 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动

					// 可以松手去刷新了
					if (state == RELEASE_To_REFRESH) {

						setSelection(0);

						// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
						if (((tempY - startY) / RATIO < headContentHeight)
								&& (tempY - startY) > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();

							Log.v(TAG, "由松开刷新状态转变到下拉刷新状态");
						}
						// 一下子推到顶了
						else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();

							Log.v(TAG, "由松开刷新状态转变到done状态");
						}
						// 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
						else {
							// 不用进行特别的操作，只用更新paddingTop的值就行了
						}
					}
					// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
					if (state == PULL_To_REFRESH) {

						setSelection(0);

						// 下拉到可以进入RELEASE_TO_REFRESH的状态
						if ((tempY - startY) / RATIO >= headContentHeight) {
							state = RELEASE_To_REFRESH;
							isBack = true;
							changeHeaderViewByState();

							Log.v(TAG, "由done或者下拉刷新状态转变到松开刷新");
						}
						// 上推到顶了
						else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();

							Log.v(TAG, "由DOne或者下拉刷新状态转变到done状态");
						}
					}

					// done状态下
					if (state == DONE) {
						if (tempY - startY > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
						}
					}

					// 更新headView的size
					if (state == PULL_To_REFRESH) {
						headView.setPadding(0, -1 * headContentHeight
								+ (tempY - startY) / RATIO, 0, 0);

					}

					// 更新headView的paddingTop
					if (state == RELEASE_To_REFRESH) {
						headView.setPadding(0, (tempY - startY) / RATIO
								- headContentHeight, 0, 0);
					}

				}
				break;
			}
		}
		return super.onTouchEvent(event);
	}
	
	

	/**
	 * 这个方法是根据结果的大小来决定footer显示的。
	 * <p>
	 * 这里假定每次请求的条数为10。如果请求到了10条。则认为还有数据。如过结果不足10条，则认为数据已经全部加载，这时footer显示已经全部加载
	 * </p>
	 * 
	 * @param resultSize
	 */
	public void setResultSize(int resultSize) {
		
		if (resultSize == 0) {
			isLoadFull = true;
			loadFull.setVisibility(View.GONE);
			loading.setVisibility(View.GONE);
			more.setVisibility(View.GONE);
			noData.setVisibility(View.VISIBLE);
		} else if (resultSize > 0 && resultSize < pageSize) {
			isLoadFull = true;
			loadFull.setVisibility(View.VISIBLE);
			loading.setVisibility(View.GONE);
			more.setVisibility(View.GONE);
			noData.setVisibility(View.GONE);
		} else if (resultSize == pageSize) {
			isLoadFull = false;
			loadFull.setVisibility(View.GONE);
			loading.setVisibility(View.VISIBLE);
			more.setVisibility(View.VISIBLE);
			noData.setVisibility(View.GONE);
		}
	}

	// 当状态改变时候，调用该方法，以更新界面
	private void changeHeaderViewByState() {
		switch (state) {
		case RELEASE_To_REFRESH:
			arrowImageView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			arrowImageView.clearAnimation();
			arrowImageView.startAnimation(animation);

			tipsTextview.setText("松开立即刷新");

			Log.v(TAG, "当前状态，松开刷新");
			break;
		case PULL_To_REFRESH:
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			if (isBack) {
				isBack = false;
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(reverseAnimation);
				tipsTextview.setText("下拉刷新");
			} else {
				tipsTextview.setText("下拉刷新");
			}
			Log.v(TAG, "当前状态，下拉刷新");
			break;

		case REFRESHING:
			headView.setPadding(0, 0, 0, 0);
			progressBar.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.GONE);
			tipsTextview.setText("正在刷新...");
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			Log.v(TAG, "当前状态,正在刷新...");
			break;
		case DONE:
			headView.setPadding(0, -1 * headContentHeight, 0, 0);
			progressBar.setVisibility(View.GONE);
			arrowImageView.clearAnimation();
			arrowImageView.setImageResource(R.drawable.pull_arrow_icon);
			tipsTextview.setText("下拉刷新");
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			Log.v(TAG, "当前状态，done");
			break;
		}
	}

	public void setOnRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}

	public interface OnRefreshListener {
		public void onRefresh();
	}

	public void onRefreshComplete() {
		
		// 删除底部的footer 重新加载
		hideFooter();
		state = DONE;
		lastUpdatedTextView.setText("最近更新:"+ DateUtil.getCurrentTimeSpecifyFormat(DateUtil.FORMAT_HH_MM_SS));
		changeHeaderViewByState();
	}

	
	private void hideFooter(){
		
		footer.setVisibility(View.GONE);
//		isLoadFull = false;
//		loadFull.setVisibility(View.GONE);
//		loading.setVisibility(View.GONE);
//		more.setVisibility(View.GONE);
//		noData.setVisibility(View.GONE);
	}
	
	
	private void showFooter(){
		
		footer.setVisibility(View.VISIBLE);
		loadFull.setVisibility(View.GONE);
		loading.setVisibility(View.VISIBLE);
		more.setVisibility(View.VISIBLE);
		noData.setVisibility(View.GONE);
	}
	
	public void onUnRefreshComplete() {
		state = DONE;
		changeHeaderViewByState();
	}

	private void onRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
			if (removeFirst) {
				addFooterView(footer);
				removeFirst = false;
			}
		}
	}

	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	public void setAdapter(BaseAdapter adapter) {
		lastUpdatedTextView.setText("最近更新:"
				+ DateUtil
						.getCurrentTimeSpecifyFormat(DateUtil.FORMAT_HH_MM_SS));
		super.setAdapter(adapter);
	}

	/************************ 上啦加载更多刷新的数据处理逻辑 ****************************/
	// 区分当前操作是刷新还是加载
	public static final int REFRESH = 0;
	public static final int LOAD = 1;
	// 只有在listview第一个item显示的时候（listview滑到了顶部）才进行下拉刷新， 否则此时的下拉只是滑动listview
	private boolean isLoading ;// 判断是否正在加载
	private boolean loadEnable = false;// 开启或者关闭加载更多功能，默认是开启上啦加载更多
	private boolean isLoadFull;// 加载全部
	private int pageSize = 10;
	private OnLoadListener onLoadListener;
	private View footer;
	
	private int firstItemIndex;
	private int scrollStates;
	
	private TextView noData;
	private TextView loadFull;
	private TextView more;
	private ProgressBar loading;
 
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
		if (mImageFetcher != null) {
			if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
				// list列表滚动过程中，暂停图片上传下载 ，将后台线程挂起
				mImageFetcher.setPauseWork(true);
			} else {
				// list列表滚动过程中，暂停图片上传下载 ，将后台线程唤醒
				mImageFetcher.setPauseWork(false);
			}
		}
		this.scrollStates = scrollState;
		LogUtil.d(TAG, "滚动状态"+scrollStates);
		ifNeedLoad(view, scrollStates);
	}

	// 加载更多监听
	public void setOnLoadListener(OnLoadListener onLoadListener) {
		this.loadEnable = true;
		this.isLoading = false;
		this.isLoadFull = false;
		this.onLoadListener = onLoadListener;
	}

	public boolean isLoadEnable() {
		return loadEnable;
	}

	// 这里的开启或者关闭加载更多，并不支持动态调整
	public void setLoadEnable(boolean loadEnable) {
		this.loadEnable = loadEnable;
		this.removeFooterView(footer);
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void onLoad() {
		if (onLoadListener != null) {
			LogUtil.d(TAG, "即将加载的页数 currentPage="+currentPage +" :总页数totalPage="+totalPage);
			loading.startAnimation(reverseAnimation);
			onLoadListener.onLoad();
		}
	}
	
    private boolean removeFirst = false;
	public void setLoadFull(boolean isLoadFull) {
		if (loadEnable) {
			
			this.isLoadFull = isLoadFull;
			if (isLoadFull) {
				removeFooterView(footer);
				removeFirst = true;
			} 
		}
	}

	// 用于加载更多结束后的回调
	public void onLoadComplete() {
		
		loading.clearAnimation();
		loadEnable = true;
		isLoading = false;
//		isLoadFull = false;
		// 加载更多完成的时候就隐藏 footer
		footer.setVisibility(View.GONE);
		
//		if (currentPage == totalPage) {
//			if (totalPage == 1 && currentPage == 1) {
//				footer.setVisibility(View.GONE);
//				isLoadFull = true;
//			}
//			
//		} else{
//			footer.setVisibility(View.VISIBLE);
//			isLoadFull = false;
//			loadFull.setVisibility(View.GONE);
//			loading.setVisibility(View.VISIBLE);
//			more.setVisibility(View.VISIBLE);
//			noData.setVisibility(View.GONE);
//		}
//		setResultSize(resultSize);
	}

	// 根据listview滑动的状态判断是否需要加载更多
	private void ifNeedLoad(AbsListView view, int scrollState) {
		
		if (!loadEnable) {
			// 控制是否需要 上拉加载更多的显示
			return;
		}
		
		
		try {
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
					&& !isLoading
					&& view.getLastVisiblePosition() == view.getPositionForView(footer)) {
				
				if (isLoadFull) {
//					CommonUtil.showToast("已经加载全部数据");
					return;
				}
				// 不是正在加载且 没有加载全部的时候
				showFooter();
				onLoad();
				isLoading = true;
			}
		} catch (Exception e) {
			LogUtil.d(TAG, "需要上拉刷新加载更多exception"+e.getLocalizedMessage());
		}
	}

	/*
	 * 定义加载更多接口
	 */
	public interface OnLoadListener {
		public void onLoad();
	}

}
