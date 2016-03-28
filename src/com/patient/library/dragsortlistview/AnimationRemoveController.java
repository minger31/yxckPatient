package com.patient.library.dragsortlistview;

import java.util.List;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.patient.library.dragsortlistview.AnimationRemoveListView.ItemRemoveCallBack;

/**
 * Class that starts and stops item drags on a {@link DragSortListView} based on
 * touch gestures. This class also inherits from {@link SimpleFloatViewManager},
 * which provides basic float View creation.
 * 
 * An instance of this class is meant to be passed to the methods
 * {@link DragSortListView#setTouchListener()} and
 * {@link DragSortListView#setFloatViewManager()} of your
 * {@link DragSortListView} instance.
 */
public class AnimationRemoveController {

	// private static final String TAG = "AnimationRemoveController";

	private AnimationRemoveListView mDslv;

	private List<String> delCallBackParams = null;

	/**
	 * By default, sorting is enabled, and removal is disabled.
	 * 
	 * @param dslv
	 *            The DSLV instance
	 * @param dragHandleId
	 *            The resource id of the View that represents the drag handle in
	 *            a list item.
	 */
	public AnimationRemoveController(AnimationRemoveListView dslv) {
		mDslv = dslv;
		mDslv.setItemRemoveCallBack(removeCallBack);
	}

	private int startItem;
	private int endItem;

	public void delMultItem(int start, int end, List<String> params) {
		delCallBackParams = params;
		startItem = start;
		endItem = end;
		int fVisible = mDslv.getFirstVisiblePosition();
		int lVisible = mDslv.getLastVisiblePosition();

		// 这样做的原因：mDslv.getChildAt(index)方法中index范围似乎�?-(可见item个数-1)
		if (startItem > lVisible || endItem < fVisible) {
			// 删除项不可见，不�?��显示动画
			delComplete();
		} else if (startItem < fVisible && endItem <= lVisible) {
			// 部分可见
			startItem = 0;
			endItem = endItem - fVisible;
		} else if (startItem >= fVisible && endItem <= lVisible) {
			// 全部可见
			startItem = startItem - fVisible;
			endItem = endItem - fVisible;
		} else if (startItem >= fVisible && endItem > lVisible) {
			// 部分可见
			startItem = startItem - fVisible;
			endItem = lVisible - fVisible;
		}

		int size = mDslv.getCount();
		if (size > endItem && startItem <= endItem) {
			handler.sendEmptyMessage(startItem);
		}
	}

	public void delAllItem() {
		startItem = mDslv.getFirstVisiblePosition();
		endItem = mDslv.getLastVisiblePosition();

		// 这样做的原因：mDslv.getChildAt(index)方法中index范围似乎�?-(可见item个数-1)
		endItem = endItem - startItem;
		startItem = 0;
		int size = mDslv.getCount() - mDslv.getHeaderViewsCount()
				- mDslv.getFooterViewsCount();
		if (size > startItem && startItem <= endItem) {
			handler.sendEmptyMessage(startItem);
		} else {
			if (delCallBack != null) {
				delCallBack.delCallBack(delCallBackParams);
			}
		}
	}

	private ItemRemoveCallBack removeCallBack = new ItemRemoveCallBack() {

		@Override
		public void itemRemoveCallBack(int position) {
			if ((position >= endItem || position >= mDslv.getCount()
					- mDslv.getHeaderViewsCount() - mDslv.getFooterViewsCount()
					- 1)) {
				Log.d("ItemRemoveCallBack", "itemRemoveCallBack:" + position);
				delComplete();
			}
		}
	};

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int index = msg.what;
			if (index <= endItem) {
				View item = mDslv.getChildAt(index);
				mDslv.removeListItem(item, index);
				handler.sendEmptyMessageDelayed(index + 1, 100);
			}
		}
	};

	public int getListViewHeaderCount() {
		if (mDslv != null) {
			return mDslv.getHeaderViewsCount();
		} else {
			return 0;
		}
	}

	/**
	 * @Title: delComplete
	 * @Description: 删除操作完成
	 * @return: void
	 */
	private void delComplete() {
		if (delCallBack != null) {
			delCallBack.delCallBack(delCallBackParams);
		}
	}

	private ItemDeleteCallBack delCallBack;

	public interface ItemDeleteCallBack {
		public void delCallBack(List<String> params);
	}

	public void setItemDeleteCallBack(ItemDeleteCallBack callBack) {
		this.delCallBack = callBack;
	};
}
