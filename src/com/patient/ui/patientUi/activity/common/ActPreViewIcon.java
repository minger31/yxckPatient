package com.patient.ui.patientUi.activity.common;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.patient.constant.CommonConstant;
import com.patient.library.viewPhoto.PhotoView;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

// 图片预览
public class ActPreViewIcon extends BaseActivity {

	private static final String TAG = ActPreViewIcon.class.getName();

	public static final String KEY_ALL_ICON = "key_all_icon";
	public static final String KEY_CURRENT_ICON = "key_current_icon";

	private SamplePagerAdapter mAdapter;
	private ArrayList<String> mListPhotoPath = null;
	private int mIndexImage = 0;
	private PreViewPager mViewPager = null;

	private DisplayImageOptions options;
	private ProgressBar spinner;

	private CircleDot cirDot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.act_preview_icon);

		LogUtil.d(TAG, "文章图片预览");

		init();

	}

	private void init() {

		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.ic_heads)
				.showImageOnFail(R.drawable.ic_heads)
				.resetViewBeforeLoading(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.ARGB_8888).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		spinner = (ProgressBar) findViewById(R.id.loading);
		initViewPager();
	}

	private void initViewPager() {

		mIndexImage = getIntent().getIntExtra(KEY_CURRENT_ICON, 0);
		mListPhotoPath = getIntent().getStringArrayListExtra(KEY_ALL_ICON);

		cirDot = new CircleDot(this, (LinearLayout)findViewById(R.id.ltAddDot),mListPhotoPath.size(),mIndexImage);
		cirDot.selected(mIndexImage);
		mViewPager = (PreViewPager) findViewById(R.id.vpIcon);
		mAdapter = new SamplePagerAdapter();
		mAdapter.setItems(mListPhotoPath);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setCurrentItem(mIndexImage, true);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				if (cirDot != null) {
					cirDot.selected(arg0);
				}
				mViewPager.setCurrentItem(arg0, true);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent i = new Intent ();
			i.putExtra(CommonConstant.KEY_RESLUT, false);
			setResult(RESULT_OK, i);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	public class SamplePagerAdapter extends PagerAdapter {
		private List<String> mListLog = null;

		public void setItems(List<String> logs) {
			mListLog = logs;
		}

		@Override
		public int getCount() {
			if (null == mListLog)
				return 0;
			return mListLog.size();
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {

			String photo = mListLog.get(position);
			PhotoView photoView = new PhotoView(container.getContext());
			try {
				LogUtil.d("ShareActivity src:" + photo);
				ImageLoader.getInstance().displayImage(photo, photoView,
						options, new SimpleImageLoadingListener() {
							@Override
							public void onLoadingStarted(String imageUri,
									View view) {
								spinner.setVisibility(View.VISIBLE);
							}

							@Override
							public void onLoadingFailed(String imageUri,
									View view, FailReason failReason) {

								spinner.setVisibility(View.GONE);
							}

							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								spinner.setVisibility(View.GONE);
							}
						});
			} catch (Exception e) {
				LogUtil.e(e);
			}
			container.addView(photoView, 0, new LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.MATCH_PARENT));
			return photoView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
	}
}
