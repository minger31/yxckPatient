package com.patient.ui.patientUi.defineView.defineImgGallery;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.patient.constant.CommonConstant;
import com.patient.library.bitmap.ImageCache;
import com.patient.library.bitmap.ImageCache.ImageCacheParams;
import com.patient.library.bitmap.ImageFetcher;
import com.patient.ui.patientUi.activity.common.ActSendComment;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

public class MultiImageChooserActivity extends AbstractMediaChooserActivity {

	private static final String TAG = "MultiImageChooserActivity";

	public static final String KEY_CHOOSER_TYPE = "chooser_type";
	public static final int CHOOSER_TYPE_IMAGE = 3;
	public static final int CHOOSER_TYPE_VIDEO = 4;

	// 照片墙grid
	private GridView gridView;
	// 照片墙列数
	private static final int IMAGE_COLUMN = 3;
	private static final int IMAGE_COLUMN_LANDSCAPE = 5;
	// 图片适配器
	private ImageAdapter ia;
	private Cursor imageCursor;
	// 图片尺寸
	private int imgSize = 240;
	private int imgSizePortrait = imgSize;
	private int imgSizeLandscape = imgSize;
	// 选中的图片
	private ArrayList<String> imgPathList = new ArrayList<String>();

	// imageid列索引
	private int imageIdColumnIndex;
	// imagedata列索引
	private int imageDataColIdx;

	// 滚动速度很快的场合，先不加载图片，滚动速度较慢，才会加载图片，以保证性能
	private boolean shouldRequestThumb = true;

	private ImageFetcher mImageFetcher = null;

	private LoaderCallbacks<Cursor> loaderCallbacks = null;

	private String bucketId = "";
	// 选择类型
	private int chooserType = CHOOSER_TYPE_IMAGE;

	private Button mBntFinish = null;
	private TextView shareAccount;
	private Button mBntPreview = null;

	// 分享界面已选照片数
	private int shareSelectedCnt = 0;

	public static Activity mltiImageChooserActivity = null;

	@Override
	protected int getContentView() {
		return R.layout.select_multi_image;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mltiImageChooserActivity = this;

		shareSelectedCnt = getIntent().getIntExtra(ActSendComment.KEY_SELECTED_CNT, 0);
		bucketId = getIntent().getStringExtra(MultiBucketChooserActivity.KEY_BUCKETID);
		chooserType = getIntent().getIntExtra(KEY_CHOOSER_TYPE,CHOOSER_TYPE_IMAGE);

		imgPathList.clear();
		titleBar.setTitle("选择图片",0);
		titleBar.enableBack();

		int mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.multi_image_chooser_spacing);
		int mImageThumbPadding = getResources().getDimensionPixelSize(R.dimen.multi_image_chooser_padding);

		int screenWidth = CommonUtil.getDeviceSize(this).x;
		int screenHeight = CommonUtil.getDeviceSize(this).y;

		shareAccount = (TextView) findViewById(R.id.share_account);
		mBntFinish = (Button) findViewById(R.id.bnt_share_photos);
		mBntPreview = (Button) findViewById(R.id.bnt_preview_photos);

		mBntFinish.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (CommonUtil.isFastDoubleClick()) {
					return;
				}
				confirm();
			}
		});
		
		mBntPreview.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (CommonUtil.isFastDoubleClick()) {
					return;
				}
				doPreview();
			}
		});

		setAccount(0);

		gridView = (GridView) findViewById(R.id.gridview);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			gridView.setNumColumns(IMAGE_COLUMN);
			imgSizePortrait = (screenWidth - mImageThumbSpacing
					* (IMAGE_COLUMN - 1) - mImageThumbPadding * 2)
					/ IMAGE_COLUMN;
			imgSizeLandscape = (screenHeight - mImageThumbSpacing
					* (IMAGE_COLUMN_LANDSCAPE - 1) - mImageThumbPadding * 2)
					/ IMAGE_COLUMN_LANDSCAPE;
			imgSize = imgSizePortrait;
		} else {
			gridView.setNumColumns(IMAGE_COLUMN_LANDSCAPE);
			imgSizePortrait = (screenHeight - mImageThumbSpacing
					* (IMAGE_COLUMN - 1) - mImageThumbPadding * 2)
					/ IMAGE_COLUMN;
			imgSizeLandscape = (screenWidth - mImageThumbSpacing
					* (IMAGE_COLUMN_LANDSCAPE - 1) - mImageThumbPadding * 2)
					/ IMAGE_COLUMN_LANDSCAPE;
			imgSize = imgSizeLandscape;
		}

		ImageCacheParams cacheParams = new ImageCacheParams(this,CommonConstant.IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(this, 0.35f); // Set memory cache to
															// 25% of app
		// memory
		mImageFetcher = new ImageFetcher(this, 0);
		mImageFetcher.setLoadingImage(R.drawable.empty_photo);
		mImageFetcher.addImageCache(new ImageCache(cacheParams));

		gridView.setOnItemClickListener(this);
		gridView.setOnScrollListener(new OnScrollListener() {

			private int lastFirstItem = 0;
			private long timestamp = System.currentTimeMillis();

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == SCROLL_STATE_IDLE) {
					// 停止滚动，刷新grid并加载图片
					LogUtil.d("MultiImageChooserActivity IDLE - Reload!");
					mImageFetcher.setPauseWork(false);
					shouldRequestThumb = true;
					ia.notifyDataSetChanged();
				} else if (scrollState == SCROLL_STATE_FLING) {
					LogUtil.d("MultiBucketChooserActivity 列表正在滚动...");
					// list列表滚动过程中，暂停图片上传下载
					mImageFetcher.setPauseWork(true);
				} else {
					mImageFetcher.setPauseWork(false);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				float dt = System.currentTimeMillis() - timestamp;
				if (firstVisibleItem != lastFirstItem) {
					double speed = 1 / dt * 1000;
					lastFirstItem = firstVisibleItem;
					timestamp = System.currentTimeMillis();
					LogUtil.d("MultiImageChooserActivity Speed: " + speed
							+ " elements/second");

					// 滚动速度很快的场合，先不加载图片，滚动速度较慢，才会加载图片，以保证性能
					shouldRequestThumb = speed < visibleItemCount;
				}
			}
		});

		ia = new ImageAdapter(this);
		gridView.setAdapter(ia);

		LoaderManager.enableDebugLogging(false);

		loaderCallbacks = new LoaderCallbacks<Cursor>() {
			@Override
			public void onLoaderReset(Loader<Cursor> loader) {
				imageCursor.close();
				imageCursor = null;
			}

			@Override
			public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
				if (cursor == null) {
					// NULL cursor. This usually means there's no image database
					// yet....
					imgPathList.clear();
					setAccount(0);
					imageCursor = null;
					ia.notifyDataSetChanged();
					return;
				}

				for (int i = imgPathList.size() - 1; i >= 0; i--) {
					File imgFile = new File(imgPathList.get(i));
					if (!imgFile.exists() || imgFile.length() == 0) {
						// 排除不存在的文件
						imgPathList.remove(i);
					}
				}
				setAccount(imgPathList.size());

				switch (loader.getId()) {
				case CHOOSER_TYPE_IMAGE:
					imageCursor = cursor;
					imageIdColumnIndex = imageCursor
							.getColumnIndex(MediaStore.Images.Media._ID);
					imageDataColIdx = imageCursor
							.getColumnIndex(MediaStore.Images.Media.DATA);
					ia.notifyDataSetChanged();
					break;
				case CHOOSER_TYPE_VIDEO:
					imageCursor = cursor;
					imageIdColumnIndex = imageCursor
							.getColumnIndex(MediaStore.Video.Media._ID);
					imageDataColIdx = imageCursor
							.getColumnIndex(MediaStore.Video.Media.DATA);
					ia.notifyDataSetChanged();
					break;
				default:
					break;
				}
			}

			@Override
			public Loader<Cursor> onCreateLoader(int cursorID, Bundle arg1) {
				CursorLoader cl = null;

				ArrayList<String> img = new ArrayList<String>();
				String order = null;
				String[] selectArgs = new String[] { bucketId };
				switch (cursorID) {

				case CHOOSER_TYPE_IMAGE:
					img.add(MediaStore.Images.Media._ID);
					img.add(MediaStore.Images.Media.DATA);
					order = MediaStore.Images.Media.DATE_MODIFIED + " desc ";

					cl = new CursorLoader(MultiImageChooserActivity.this,
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							img.toArray(new String[img.size()]),
							MediaStore.Images.Media.BUCKET_ID + " = ? and "
									+ MediaStore.Images.Media.SIZE + " > 0 ",
							selectArgs, order);
					break;
				case CHOOSER_TYPE_VIDEO:
					img.add(MediaStore.Video.Media._ID);
					img.add(MediaStore.Video.Media.DATA);
					order = MediaStore.Video.Media.DATE_MODIFIED + " desc ";

					cl = new CursorLoader(MultiImageChooserActivity.this,
							MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
							img.toArray(new String[img.size()]),
							MediaStore.Video.Media.BUCKET_ID + " = ? and "
									+ MediaStore.Video.Media.SIZE + " > 0 ",
							selectArgs, order);
					break;
				default:
					break;
				}
				return cl;
			}
		};
		getSupportLoaderManager()
				.initLoader(chooserType, null, loaderCallbacks);

	}

	private void setAccount(int account) {
		
		if (account <= 0) {
			
			shareAccount.clearAnimation();
			shareAccount.setVisibility(View.GONE);
			mBntFinish.setClickable(false);
			mBntPreview.setClickable(false);
			
		} else {
			
			if (View.GONE == shareAccount.getVisibility()){
				shareAccount.setVisibility(View.VISIBLE);
			}
			mBntFinish.setClickable(true);
			mBntPreview.setClickable(true);
			Animation scaleAnimation = AnimationUtils.loadAnimation(this,R.anim.img_selected_cnt_anim);
			shareAccount.startAnimation(scaleAnimation);
			shareAccount.setText(String.valueOf(account));
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Log.d(TAG, "当前屏幕为横屏");
			imgSize = imgSizeLandscape;
			gridView.setNumColumns(IMAGE_COLUMN_LANDSCAPE);
		} else {
			Log.d(TAG, "当前屏幕为竖屏");
			imgSize = imgSizePortrait;
			gridView.setNumColumns(IMAGE_COLUMN);
		}
		ia.notifyDataSetChanged();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mImageFetcher != null) {
			mImageFetcher.setExitTasksEarly(false);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mImageFetcher != null) {
			mImageFetcher.setPauseWork(false);
			mImageFetcher.setExitTasksEarly(true);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		getSupportLoaderManager().destroyLoader(chooserType);

		if (mImageFetcher != null) {
//			 mImageFetcher.flushCache();
//			 mImageFetcher.closeCache();
			mImageFetcher = null;
		}
		mltiImageChooserActivity = null;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		clickPosition(position);
	}

	protected void doPreview() {
		// 预览图片
		ArrayList<String> imagePaths = new ArrayList<String>();
		if (imgPathList.size() > getMaxCnt()) {
			imagePaths.addAll(imgPathList.subList(0, getMaxCnt()));
		} else {
			imagePaths = imgPathList;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtil.d(TAG, "onActivityResult");
		if (resultCode == RESULT_OK) {
			if (requestCode == ActSendComment.REQUEST_CODE_ADD) {
				LogUtil.d(TAG, "onActivityResult REQUEST_CODE_ADD");
				setResult(RESULT_OK, data);
				MultiImageChooserActivity.this.finish();
			}
		}
	}

	// 点击完成按钮，进行确定选择的图片 传到 评论界面
	protected void confirm() {
        // 选择图片
        ArrayList<String> imagePaths = new ArrayList<String>();
//        if (imgPathList.size() > getMaxCnt()) {
//            imagePaths.addAll(imgPathList.subList(0, getMaxCnt()));
//        } else {
//            imagePaths = imgPathList;
//        }
        imagePaths = imgPathList;
        if (MultiBucketChooserActivity.ACTIVITY_START_FOR_RESULT.equals(MultiBucketChooserActivity.activityType)) {
            // 分享界面增加照片/视频的场合
            Intent intent = new Intent();
            intent.putStringArrayListExtra(Intent.EXTRA_STREAM, imagePaths);
            setResult(RESULT_OK, intent);
        } else {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList(Intent.EXTRA_STREAM, imagePaths);
            bundle.putBoolean(ActSendComment.KEY_ACTION_CUSTOM, true);
            if (chooserType == CHOOSER_TYPE_VIDEO) {
                bundle.putInt(ActSendComment.KEY_FOR_SHARE_TYPE,
                		ActSendComment.SHARE_TYPE_VIDEO);
            } else {
                bundle.putInt(ActSendComment.KEY_FOR_SHARE_TYPE,
                		ActSendComment.SHARE_TYPE_PIC);
            }
            Intent intent = new Intent(MultiImageChooserActivity.this,
            		ActSendComment.class);
            intent.putExtras(bundle);
            startActivity(intent);

            if (MultiBucketChooserActivity.bucketChooserActivity != null) {
                MultiBucketChooserActivity.bucketChooserActivity.finish();
            }
        }
        MultiImageChooserActivity.this.finish();
    }

	@Override
	protected int getMaxCnt() {
		return MultiBucketChooserActivity.MAX_IMAGE_COUNT - shareSelectedCnt;
	}

	@Override
	protected boolean needRefresh() {
		return false;
	}

	@Override
	protected int getDataCnt() {
		if (imageCursor == null) {
			return 0;
		} else {
			return imageCursor.getCount();
		}
	}

	private void deselectAll() {
		imgPathList.clear();
		ia.notifyDataSetChanged();
	}

	@Override
	protected void selectPosition(View view, int position) {
	}

	@Override
	protected void clickPosition(int position) {

		if (CommonUtil.isFastDoubleClick()) {
			return;
		}
		if (chooserType == CHOOSER_TYPE_VIDEO) {
			// 视频的场合，点击开始播放
			Intent intent = new Intent(Intent.ACTION_VIEW,getImageUri(position));
			startActivity(intent);
		}

		// else {
		// // 查看原图
		// ArrayList<String> oriImage = new ArrayList<String>();
		// imageCursor.moveToPosition(position);
		// oriImage.add(imageCursor.getString(imageDataColIdx));
		// Intent i = new Intent(this, ViewPhotosActivity.class);
		// i.putStringArrayListExtra(ViewPhotosActivity.KEY_PHOTOS_LIST,
		// oriImage);
		// startActivity(i);
		// }
	}

	private Uri getImageUri(int position) {
		imageCursor.moveToPosition(position);

		try {
			int id = imageCursor.getInt(imageIdColumnIndex);
			return getImageUriByImgId(id);
		} catch (Exception e) {
			return null;
		}
	}

	private Uri getImageUriByImgId(int imgId) {

		try {
			if (chooserType == CHOOSER_TYPE_VIDEO) {
				return Uri
						.withAppendedPath(
								MediaStore.Video.Media.EXTERNAL_CONTENT_URI, ""
										+ imgId);
			} else {
				return Uri.withAppendedPath(CommonConstant.IMAGE_BASEURI, ""
						+ imgId);
			}
		} catch (Exception e) {
			return null;
		}
	}

	public class ImageAdapter extends BaseAdapter {
		
		private LayoutInflater layoutInflater = null;
		private GalleryViewHolder viewHolder = null;

		public ImageAdapter(Context c) {
			this.layoutInflater = (LayoutInflater) c
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			if (imageCursor != null) {
				return imageCursor.getCount();
			} else {
				return 0;
			}
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		// create a new ImageView for each item referenced by the Adapter
		public View getView(int pos, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = (View) layoutInflater.inflate(
						R.layout.multi_image_chooser_pic, parent, false);
				viewHolder = new GalleryViewHolder();
				viewHolder.imgArea = (FrameLayout) convertView
						.findViewById(R.id.img_area);
				viewHolder.imageTarget = (ImageView) convertView
						.findViewById(R.id.image_target);
				viewHolder.videoIcon = (ImageView) convertView
						.findViewById(R.id.video_icon);
				if (chooserType == CHOOSER_TYPE_VIDEO) {
					viewHolder.videoIcon.setVisibility(View.VISIBLE);
				}

				viewHolder.selectedBg = (TextView) convertView.findViewById(R.id.selected_bg);
				viewHolder.checkboxImg = (ImageView) convertView.findViewById(R.id.checkbox_img);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (GalleryViewHolder) convertView.getTag();
			}

			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewHolder.imgArea.getLayoutParams();
			lp.width = imgSize;
			lp.height = imgSize;
			viewHolder.imgArea.setLayoutParams(lp);

			viewHolder.imageTarget.setImageResource(R.drawable.empty_photo);
			viewHolder.selectedBg.setVisibility(View.GONE);

			final int position = pos;
			if (!imageCursor.moveToPosition(position)) {
				return convertView;
			}
			if (imageIdColumnIndex == -1) {
				return convertView;
			}

			final int id = imageCursor.getInt(imageIdColumnIndex);
			final String path = imageCursor.getString(imageDataColIdx);
			if (shouldRequestThumb) {
				Log.d(TAG, "MultiImageChooserActivity getView,position:"
						+ position + "|imageId:" + id + "|imageData:"
						+ imageCursor.getString(imageDataColIdx));
				if (chooserType == CHOOSER_TYPE_VIDEO) {
					mImageFetcher.loadImageThumb(viewHolder.imageTarget, path,
							ImageFetcher.THUMBNAIL_TYPE_VIDEO, null);
				} else {
					mImageFetcher.loadImageThumb(viewHolder.imageTarget, path,
							ImageFetcher.THUMBNAIL_TYPE_IMAGE, null);
				}
			}

			// 使用图片路径作为key来记选择状态
			viewHolder.checkboxImg.setVisibility(View.INVISIBLE);
			if (imgPathList.contains(path)) {
				viewHolder.checkboxImg.setVisibility(View.VISIBLE);
				viewHolder.selectedBg.setVisibility(View.VISIBLE);
				viewHolder.checkboxImg.setImageResource(R.drawable.select_pictures_select_icon_selected);
			} else {
				viewHolder.selectedBg.setVisibility(View.GONE);
//				viewHolder.checkboxImg
//						.setImageResource(R.drawable.select_pictures_select_icon_unselected);
				viewHolder.checkboxImg.setVisibility(View.INVISIBLE);
			}
			
			viewHolder.imgArea.setTag(id + "_" + path);
			LogUtil.d(TAG, "选中图片："+viewHolder.imgArea.getTag());
			viewHolder.imgArea.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if (CommonUtil.isFastDoubleClick()) {
								return;
							}
							// 已选中的场合，点击取消选中
							String idPath = (String) v.getTag();
							int sIdx = idPath.indexOf("_");
							int id = Integer.parseInt(idPath.substring(0, sIdx));
							String path = idPath.substring(sIdx + 1);
							selectItem(id, path);
						}
					});
			return convertView;
		}
	}

	private void selectItem(int imageId, String imagePath) {
		boolean isChecked = imgPathList.contains(imagePath);
//		if (chooserType == CHOOSER_TYPE_VIDEO && !isChecked){
//			deselectAll();
//		}
		
		if (isChecked) {
			imgPathList.remove(imagePath);
		} else {
			// 判断是否达到选择最大个数
			if (imgPathList.size() >= getMaxCnt()) {
				Toast.makeText(MultiImageChooserActivity.this,"您最多只能选择" + getMaxCnt() + "个", Toast.LENGTH_SHORT).show();
				return;
			} else {
				imgPathList.add(imagePath);
			}
		}

		setAccount(imgPathList.size());

		ia.notifyDataSetChanged();
	}

	private static class GalleryViewHolder {
		FrameLayout imgArea;
		ImageView imageTarget;
		ImageView videoIcon;
		TextView selectedBg;
		ImageView checkboxImg;
	}

	@Override
	protected void registerContentObserver(MyContentObserver observer) {
		if (chooserType == CHOOSER_TYPE_VIDEO) {
			getContentResolver()
					.registerContentObserver(
							MediaStore.Video.Media.EXTERNAL_CONTENT_URI, true,
							observer);
			getContentResolver().registerContentObserver(
					MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, true,
					observer);
		} else {
			getContentResolver().registerContentObserver(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true,
					observer);
			getContentResolver().registerContentObserver(
					MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, true,
					observer);
		}
	}

	@Override
	protected boolean needContentObserver() {
		return true;
	}

	@Override
	protected int getCursorId() {
		return chooserType;
	}

	@Override
	protected LoaderCallbacks<Cursor> getLoaderCallbacks() {
		return loaderCallbacks;
	}
}
