package com.patient.ui.patientUi.defineView.defineImgGallery;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.patient.constant.CommonConstant;
import com.patient.library.bitmap.ImageCache;
import com.patient.library.bitmap.ImageCache.ImageCacheParams;
import com.patient.library.bitmap.ImageFetcher;
import com.patient.ui.patientUi.activity.common.ActSendComment;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

/**
 * <dl>
 * <dt>MultiImageChooserActivity.java</dt>
 * <dd>Description:图片选择界面（支持多选）</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * </dl>
 * 
 * @author lihs
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MultiBucketChooserActivity extends AbstractMediaFolderChooserActivity {
	
	
    private static final String TAG = "MultiBucketChooserActivity";

    /** 一次最多发送照片数 ，*/
    public static int MAX_IMAGE_COUNT = 9;
    
    public static final String KEY_ACTIVITY_TYPE = "key_activity_type";
    public static final String ACTIVITY_START_FOR_RESULT = "activity_start_for_result";
    
    public static String activityType = null;
    
    // 是单选还是多选 true 是单选 false  
    public static final String KEY_IS_MULTI = "key_is_multi";
    public boolean isMulti = true;

    public static final String KEY_BUCKET_TYPE = "key_bucket_type";
    // 选择图片类型
    public static final int BUCKET_TYPE_IMAGE = 1;
    // 选择 视频
    public static final int BUCKET_TYPE_VIDEO = 2;
    // 每一个相册文件夹的ID
    public static final String KEY_BUCKETID = "key_bucketid";
    /** 列名：相册照片数 */
    private static final String COLUMN_BUCKET_CNT = "BUCKET_CNT";
    public static Activity bucketChooserActivity = null;
    // 照片墙grid
    private GridView gridView;
    // 照片墙列数
    private static final int IMAGE_COLUMN = 2;
    private static final int IMAGE_COLUMN_LANDSCAPE = 4;
    // 图片适配器
    private BucketAdapter ba;
    private Cursor bucketCursor;
    // 系统图库的列索引 
    private int imageIdColumnIndex;
    private int bucketIdColumnIndex;
    private int bucketNameColumnIndex;
    private int bucketCntColumnIndex;
    // 图片尺寸
    private int imgSize = 240;

    private boolean shouldRequestThumb = true;

    private ImageFetcher mImageFetcher = null;
    // 选择状态
    private SparseBooleanArray checkStatus = new SparseBooleanArray();
    private LoaderCallbacks<Cursor> loaderCallbacks = null;
    private int bucketBgMargin = 20;
    // bucket类型，默认为图片选择图片
    private int bucketType = BUCKET_TYPE_IMAGE;
    // 相册对应的封面照片id
    private SparseIntArray bucketImageIds = new SparseIntArray();
    // 分享界面已选照片数
    private int shareSelectedCnt = 0;

    @Override
    protected int getContentView() {
        return R.layout.select_multi_bucket;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bucketChooserActivity = this;

        MAX_IMAGE_COUNT = getIntent().getIntExtra(CommonConstant.KEY_RESLUT, 9);
        
        shareSelectedCnt = getIntent().getIntExtra(ActSendComment.KEY_SELECTED_CNT, 0);
        bucketType = getIntent().getIntExtra(KEY_BUCKET_TYPE, BUCKET_TYPE_IMAGE);
        activityType = getIntent().getStringExtra(KEY_ACTIVITY_TYPE);
        isMulti = getIntent().getBooleanExtra(KEY_IS_MULTI, false);
        
        LogUtil.d(TAG, "已选择图片数："+shareSelectedCnt + "是进行图片分享："+bucketType);
        
        titleBar.enableBack();
        titleBar.setTitle(getString(R.string.image_select_title),0);

        bucketBgMargin = getResources().getDimensionPixelSize(R.dimen.multi_bucket_chooser_bg_margin);
        imgSize = getResources().getDimensionPixelSize(R.dimen.multi_bucket_chooser_size);

        ImageCacheParams cacheParams = new ImageCacheParams(this,CommonConstant.IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(this, 0.35f); // Set memory cache to
        mImageFetcher = new ImageFetcher(this, 0);
        mImageFetcher.addImageCache(new ImageCache(cacheParams));

        gridView = (GridView) findViewById(R.id.gridview);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridView.setNumColumns(IMAGE_COLUMN);
        } else {
            gridView.setNumColumns(IMAGE_COLUMN_LANDSCAPE);
        }

        gridView.setColumnWidth(imgSize);
        gridView.setOnItemClickListener(this);
        gridView.setOnScrollListener(new OnScrollListener() {

            private int lastFirstItem = 0;
            private long timestamp = System.currentTimeMillis();

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    LogUtil.d("MultiImageChooserActivity IDLE - Reload!");
                    shouldRequestThumb = true;
                    mImageFetcher.setPauseWork(false);
                    ba.notifyDataSetChanged();
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

                    shouldRequestThumb = speed < visibleItemCount;
                }
            }
        });

        ba = new BucketAdapter(this);
        gridView.setAdapter(ba);

        LoaderManager.enableDebugLogging(false);

        loaderCallbacks = new LoaderCallbacks<Cursor>() {
            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                bucketCursor.close();
                bucketCursor = null;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                Log.d(TAG, "onLoadFinished");
                if (cursor == null) {
                    // NULL cursor. This usually means there's no image database
                    // yet....
                    return;
                }

                bucketImageIds.clear();

                switch (loader.getId()) {
                case BUCKET_TYPE_IMAGE:
                    bucketCursor = cursor;
                    imageIdColumnIndex = bucketCursor
                            .getColumnIndex(MediaStore.Images.Media._ID);
                    // imageDataColIdx = bucketCursor
                    // .getColumnIndex(MediaStore.Images.Media.DATA);
                    bucketIdColumnIndex = bucketCursor
                            .getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                    bucketNameColumnIndex = bucketCursor
                            .getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                    bucketCntColumnIndex = bucketCursor
                            .getColumnIndex(COLUMN_BUCKET_CNT);
                    ba.notifyDataSetChanged();
                    break;
                case BUCKET_TYPE_VIDEO:
                    bucketCursor = cursor;
                    imageIdColumnIndex = bucketCursor
                            .getColumnIndex(MediaStore.Video.Media._ID);
                    bucketIdColumnIndex = bucketCursor
                            .getColumnIndex(MediaStore.Video.Media.BUCKET_ID);
                    bucketNameColumnIndex = bucketCursor
                            .getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
                    bucketCntColumnIndex = bucketCursor
                            .getColumnIndex(COLUMN_BUCKET_CNT);
                    ba.notifyDataSetChanged();
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
                String selection = null;
                switch (cursorID) {

                case BUCKET_TYPE_IMAGE:
                    img.add(MediaStore.Images.Media._ID);
                    img.add(MediaStore.Images.Media.DATA);
                    img.add(MediaStore.Images.Media.BUCKET_ID);
                    img.add(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                    img.add("COUNT(" + MediaStore.Images.Media.BUCKET_ID
                            + ") AS " + COLUMN_BUCKET_CNT);
                    order = MediaStore.Images.Media.DATE_MODIFIED + " DESC ";
                    selection = MediaStore.Images.Media.SIZE
                            + " > 0) GROUP BY ("
                            + MediaStore.Images.Media.BUCKET_ID;

                    cl = new CursorLoader(MultiBucketChooserActivity.this,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            img.toArray(new String[img.size()]), selection,
                            null, order);
                    break;
                case BUCKET_TYPE_VIDEO:
                    img.add(MediaStore.Video.Media._ID);
                    img.add(MediaStore.Video.Media.DATA);
                    img.add(MediaStore.Video.Media.BUCKET_ID);
                    img.add(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
                    img.add("COUNT(" + MediaStore.Video.Media.BUCKET_ID
                            + ") AS " + COLUMN_BUCKET_CNT);
                    order = MediaStore.Video.Media.DATE_MODIFIED + " desc ";
                    selection = " 0 == 0) GROUP BY ("
                            + MediaStore.Video.Media.BUCKET_ID;

                    cl = new CursorLoader(MultiBucketChooserActivity.this,
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            img.toArray(new String[img.size()]), selection,
                            null, order);
                    break;
                default:
                    break;
                }
                return cl;
            }
        };
        getSupportLoaderManager().initLoader(bucketType, null, loaderCallbacks);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d(TAG,
                    "MultiBucketChooserActivity 当前屏幕为横屏");
            gridView.setNumColumns(IMAGE_COLUMN_LANDSCAPE);
        } else {
            Log.d(TAG,
                    "MultiBucketChooserActivity 当前屏幕为竖屏");
            gridView.setNumColumns(IMAGE_COLUMN);
        }
        ba.notifyDataSetChanged();
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
            mImageFetcher.flushCache();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportLoaderManager().destroyLoader(bucketType);

        if (mImageFetcher != null) {
            mImageFetcher.flushCache();
            mImageFetcher.closeCache();
            mImageFetcher = null;
        }
        bucketChooserActivity = null;
    }

    @Override
    protected boolean needRefresh() {
        return true;
    }

    @Override
    protected int getDataCnt() {
        if (bucketCursor == null) {
            return 0;
        } else {
            return bucketCursor.getCount();
        }
    }

    @Override
    protected void clickPosition(int position) {
        if (CommonUtil.isFastDoubleClick()) {
            return;
        }
        bucketCursor.moveToPosition(position);
        String bucketId = bucketCursor.getString(bucketIdColumnIndex);

        Intent i = new Intent(this, MultiImageChooserActivity.class);
        i.putExtra(KEY_BUCKETID, bucketId);
        i.putExtra(KEY_IS_MULTI,isMulti);
        i.putExtra(MultiImageChooserActivity.KEY_CHOOSER_TYPE, MultiImageChooserActivity.CHOOSER_TYPE_IMAGE);
        if (ACTIVITY_START_FOR_RESULT.equals(activityType)) {
        	// 跳转到单个相册选择数据
            i.putExtra(ActSendComment.KEY_SELECTED_CNT, shareSelectedCnt);
            startActivityForResult(i, ActSendComment.REQUEST_CODE_ADD);
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
                MultiBucketChooserActivity.this.finish();
            }
        }
    }

    @Override
    protected int getMaxCnt() {
        return MAX_IMAGE_COUNT;
    }

    public class BucketAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater = null;
        private GalleryViewHolder viewHolder = null;

        public BucketAdapter(Context c) {
            this.layoutInflater = (LayoutInflater) c
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            if (bucketCursor != null) {
                return bucketCursor.getCount();
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
                        R.layout.multi_bucket_chooser_item, parent, false);
                viewHolder = new GalleryViewHolder();
                viewHolder.imageBg = (ImageView) convertView
                        .findViewById(R.id.image_bg);
                viewHolder.imageTarget = (ImageView) convertView
                        .findViewById(R.id.image_target);
                viewHolder.videoIcon = (ImageView) convertView
                        .findViewById(R.id.video_icon);
                if (bucketType == BUCKET_TYPE_VIDEO) {
                    viewHolder.videoIcon.setVisibility(View.VISIBLE);
                }

                viewHolder.bucketName = (TextView) convertView.findViewById(R.id.bucket_name);

                viewHolder.selectedBg = (TextView) convertView
                        .findViewById(R.id.selected_bg);
                viewHolder.checkboxImg = (ImageView) convertView
                        .findViewById(R.id.checkbox_img);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (GalleryViewHolder) convertView.getTag();
            }

            viewHolder.imageTarget.setBackgroundResource(R.drawable.empty_photo);
            viewHolder.selectedBg.setVisibility(View.GONE);
            viewHolder.checkboxImg.setVisibility(View.GONE);

            final int position = pos;
            if (!bucketCursor.moveToPosition(position)) {
                return convertView;
            }
            if (imageIdColumnIndex == -1) {
                return convertView;
            }

            final int bucketCount = bucketCursor.getInt(bucketCntColumnIndex);
            viewHolder.bucketName
                    .setText(bucketCursor.getString(bucketNameColumnIndex)
                            + "(" + bucketCount + ")");

            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) viewHolder.imageBg
                    .getLayoutParams();
            if (bucketCount > 1) {
                lp.leftMargin = 0;
                lp.topMargin = 0;
                lp.rightMargin = 0;
                lp.bottomMargin = 0;
                viewHolder.imageBg.setLayoutParams(lp);
                viewHolder.imageBg.setImageResource(R.drawable.bucket_img_bg);
            } else {
                lp.leftMargin = bucketBgMargin;
                lp.topMargin = bucketBgMargin;
                lp.rightMargin = bucketBgMargin;
                lp.bottomMargin = bucketBgMargin;
                viewHolder.imageBg.setLayoutParams(lp);
                viewHolder.imageBg.setImageResource(android.R.color.white);
            }

            int bucketId = bucketCursor.getInt(bucketIdColumnIndex);

            int id = bucketImageIds.get(bucketId);
            if (id <= 0) {
                Cursor cursor = null;
                try {
                    if (bucketType == BUCKET_TYPE_VIDEO) {
                        cursor = getContentResolver().query(
                                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                new String[] { MediaStore.Video.Media._ID },
                                MediaStore.Video.Media.BUCKET_ID + " = ? and "
                                        + MediaStore.Video.Media.SIZE + ">0 ",
                                new String[] { "" + bucketId },
                                MediaStore.Video.Media.DATE_MODIFIED + " desc "
                                        + " LIMIT 0,1");
                    } else {
                        cursor = getContentResolver().query(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                new String[] { MediaStore.Images.Media._ID },
                                MediaStore.Images.Media.BUCKET_ID + " = ? and "
                                        + MediaStore.Images.Media.SIZE + ">0 ",
                                new String[] { "" + bucketId },
                                MediaStore.Images.Media.DATE_MODIFIED
                                        + " desc " + " LIMIT 0,1");
                    }
                    if (cursor != null && cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        id = cursor.getInt(0);
                    }
                } catch (Exception e) {
                    LogUtil.e(e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                        cursor = null;
                    }
                }
                bucketImageIds.put(bucketId, id);
            }

            if (shouldRequestThumb) {
                if (bucketType == BUCKET_TYPE_VIDEO) {
 
                    mImageFetcher.loadImageThumb(viewHolder.imageTarget, id,
                            ImageFetcher.THUMBNAIL_TYPE_VIDEO, null);
                } else {
                    mImageFetcher.loadImageThumb(viewHolder.imageTarget, id,
                            ImageFetcher.THUMBNAIL_TYPE_IMAGE, null);
                }
            }
            // TODO:使用id作为key来记住选择状态，否则数据刷新后，可能会导致选择状态混乱
            if (isChecked(pos)) {
                Log.d(TAG,
                        "MultiBucketChooserActivity isChecked:" + pos);
                viewHolder.selectedBg.setVisibility(View.VISIBLE);
                viewHolder.checkboxImg.setVisibility(View.VISIBLE);
            }

            return convertView;
        }
    }

    public boolean isChecked(int position) {
        return checkStatus.get(position);
    }

    private static class GalleryViewHolder {
        // FrameLayout bucketBg;
        ImageView imageBg;
        ImageView imageTarget;
        ImageView videoIcon;
        TextView bucketName;
        // TextView bucketCount;

        TextView selectedBg;
        ImageView checkboxImg;
    }

    @Override
    protected int getCursorId() {
        return bucketType;
    }

    protected LoaderCallbacks<Cursor> getLoaderCallbacks() {
        return loaderCallbacks;
    }

    @Override
    protected void registerContentObserver(MyContentObserver observer) {
        if (bucketType == BUCKET_TYPE_VIDEO) {
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
}
