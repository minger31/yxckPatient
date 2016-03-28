package com.patient.ui.patientUi.activity.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.patient.commonent.BottomButtonMenu;
import com.patient.commonent.BottomMenuWindow.MenuClickedListener;
import com.patient.commonent.CommonDialog;
import com.patient.commonent.MyGridView;
import com.patient.commonent.TitleBar;
import com.patient.constant.CommonConstant;
import com.patient.library.bitmap.AsyncTask;
import com.patient.library.bitmap.ImageCache;
import com.patient.library.bitmap.ImageCache.ImageCacheParams;
import com.patient.library.bitmap.ImageFetcher;
import com.patient.library.viewPhoto.Photo;
import com.patient.ui.patientUi.defineView.defineImgGallery.MultiBucketChooserActivity;
import com.patient.util.CommonUtil;
import com.patient.util.FileManager;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

/**
 * <dl>
 * <dt>ShareConfirmActivity.java</dt>
 * <dd>Description:分享照片、视频界面</dd>
 * <dd>Copyright: Copyright (C) 2014</dd>
 * <dd>CreateDate: 2014-11-7 下午1:16:09</dd>
 * </dl>
 * 
 * @author lihs
 */
public class ActSendComment extends BaseActivity {

    private static final String TAG = "ActSendComment";

    public static final String KEY_IS_VIDEO = "key_is_video";
    public static final String KEY_SELECTED_CNT = "key_selected_cnt";
    public static final String KEY_ACTION_CUSTOM = "action_custom";
    public static final String KEY_FOR_SHARE_TYPE = "key_for_sharetype";
    public static final String KEY_SHARE_MAN = "key_share_man";
    public static final String KEY_FILE_PATH = "file_path";

    // 添加照片
    public static final int REQUEST_CODE_ADD = 112;
    // 预览照片
    public static final int REQUEST_CODE_PREVIEW = 111;
    // 选择联系人返回
    public static final int REQUEST_CODE_LINKMAN = 101;
    // 一行显示图片列数
    private static final int IMG_COLUMN_COUNT = 4;
    // 视频文件最大限制：10M
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    // 图片文件最大限制：2M
    public static final long MAX_IMAGEFILE_SIZE = 2 * 1024 * 1024;

    // 分享 图片音频 视频
    public static final int SHARE_TYPE_PIC = 0;
    public static final int SHARE_TYPE_VIDEO = 1;
    public static final int SHARE_TYPE_AUDIO = 2;

    // 是否本地动作（使用app客户端分享，而非系统图库分享）
    private boolean mIsLocalAction = false;
    // 分享类型（图片或视频）,默认分享图片
    private int shareType = SHARE_TYPE_PIC;
    // 分享文件本地路径
    private ArrayList<String> mListPhotoPath = null;
    // 拍照分享照片路径
    private String mCameraPath = null;
    // 照片展示grid
    private MyGridView imgGridView = null;
    private ImageAdapter imgAdapter = null;
    private int imgSize = 0;
    private int maxCount = MultiBucketChooserActivity.MAX_IMAGE_COUNT;
    private ImageFetcher mImageFetcher = null;
    private List<Photo> photos = null;
    
    private static String FILEPATH = "Samsung_filepath";
    private String cameraFilePath = "";
    private File mCurPhotoFile = null;// File For Photo Which Camera Taked

    private static final int ACTION_SHARE_FROM_CAMERA = 1000;
//    public static final int ACTION_SHARE_FROM_MAIN = 1002;
//    public static final int ACTION_VIEWIMAGE_FROM_MAIN = 1003;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_send_comment);

        TitleBar titleBar = getTitleBar();
        
        titleBar.setBack(null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtil.isFastDoubleClick()) {
                    return;
                }
                doBack();
            }
        },0);
        titleBar.setTitle("发表评论",0);
        titleBar.enableRightBtn("发送", 0, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtil.isFastDoubleClick()) {
                    return;
                }
                doShare();
            }
        });

        // 接收数据等
        initParams();

        // 初始化界面控件
        initControl();

        initImageFetcher();

        // 初始化数据
        initData();
    }

    private void doBack() {
        CommonDialog builder = new CommonDialog(ActSendComment.this);
        builder.setTitle(null);
        builder.setMessage("放弃此次评论？");
        builder.setPositiveButton(new CommonDialog.BtnClickedListener() {
            @Override
            public void onBtnClicked() {
                ActSendComment.this.finish();
            }
        }, R.string.ok);
        builder.setCancleButton(null, R.string.cancle);
        builder.showDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mImageFetcher != null) {
            mImageFetcher.setExitTasksEarly(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImageFetcher != null) {
            mImageFetcher.flushCache();
            mImageFetcher.closeCache();
            mImageFetcher = null;
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

    /**
     * @author: lihs
     * @Title: doShare
     * @Description: 分享
     * @date: 2014-11-7 下午5:50:11
     */
    private void doShare() {
         
        if (mListPhotoPath == null || mListPhotoPath.size() == 0) {
            Toast.makeText(getApplicationContext(), "没有选择文件，无法分享",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        for (String path : mListPhotoPath) {
            File file = new File(path);
            if (file != null && file.exists()) {
                long signal_size = file.length();
                if (signal_size > MAX_FILE_SIZE) {
                    showCanntShareDialog();
                    return;
                }
            } else {
                // 文件不存在
                LogUtil.d(TAG, "doShare 文件不存在:" + path);
                showCanntShareDialog();
                return;
            }
        }

        if (createThumbnailTask != null) {
            return;
        }

        photos = new ArrayList<Photo>();
        Photo photoSrc = null;
        for (int i = 0; i < mListPhotoPath.size(); i++) {
            photoSrc = new Photo(mListPhotoPath.get(i));
            photos.add(photoSrc);
        }

        createThumbnailTask = new CreateThumbnailTask();
        createThumbnailTask.execute();
    }

    private void showCanntShareDialog() {
        CommonDialog overSizeDlg = new CommonDialog(ActSendComment.this);
        overSizeDlg.setTitle("提示");
        overSizeDlg.setCancelable(false);
        overSizeDlg.setMessage(R.string.file_no_exist_or_oversize);
        overSizeDlg.setPositiveButton(null, R.string.ok);
        overSizeDlg.showDialog();
    }

    private void initImageFetcher() {
    	
        ImageCacheParams cacheParams1 = new ImageCacheParams(this,
                CommonConstant.IMAGE_CACHE_DIR);
        // Set memory cache to 25% of app memory
        cacheParams1.setMemCacheSizePercent(this, 0.25f);
        int mImageThumbSize = getResources().getDimensionPixelSize(
                R.dimen.image_thumbnail_size);
        mImageFetcher = new ImageFetcher(this, mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        mImageFetcher.addImageCache(new ImageCache(cacheParams1));
    }

    private void initData() {
        int mImageSpacing = getResources().getDimensionPixelSize(
                R.dimen.share_img_spacing);
        int mImageMargin = getResources().getDimensionPixelSize(
                R.dimen.share_img_margin);
        int mImagePadding = getResources().getDimensionPixelSize(
                R.dimen.share_img_padding);

        // 屏幕宽度
        int screenWidth = CommonUtil.getDeviceSize(this).x;
        // 计算得出图片大小
        imgSize = (screenWidth - mImageMargin * 2 - mImagePadding * 2 - (IMG_COLUMN_COUNT - 1)
                * mImageSpacing)
                / IMG_COLUMN_COUNT;
        imgAdapter = new ImageAdapter(ActSendComment.this);
        imgGridView.setAdapter(imgAdapter);

        
    }

    

    private void initControl() {
        imgGridView = (MyGridView) findViewById(R.id.img_grid);
        imgGridView.setNumColumns(IMG_COLUMN_COUNT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
        if (resultCode == RESULT_OK) {
           
        	if (requestCode == REQUEST_CODE_PREVIEW && data != null  && data.getExtras() != null) {
                // 预览返回
                mListPhotoPath = data.getExtras().getStringArrayList(Intent.EXTRA_STREAM);
                imgAdapter.notifyDataSetChanged();
                
            } else if (requestCode == REQUEST_CODE_ADD && data != null && data.getExtras() != null) {
                // 增加照片 
                ArrayList<String> addedPathList = data.getExtras().getStringArrayList(Intent.EXTRA_STREAM);
                if (addedPathList != null && addedPathList.size() > 0) {
                    if (mListPhotoPath == null) {
                        mListPhotoPath = new ArrayList<String>();
                    }
                    int repeatCnt = 0;
                    for (int i = 0; i < addedPathList.size(); i++) {
                        String addedPath = addedPathList.get(i);
                        if (!mListPhotoPath.contains(addedPath)) {
                            mListPhotoPath.add(addedPath);
                        } else {
                            repeatCnt++; 
                        }
                        if (mListPhotoPath.size() >= MultiBucketChooserActivity.MAX_IMAGE_COUNT) {
                            break;
                        }
                    }

                    if (repeatCnt > 0) {
                        Toast.makeText(ActSendComment.this,  "您选的照片有" + repeatCnt + "张重复了，已自动为您移除重复的照片", Toast.LENGTH_SHORT).show();
                    }
                    imgAdapter.notifyDataSetChanged();
                }
            }else if (requestCode == ACTION_SHARE_FROM_CAMERA) {
                if (!TextUtils.isEmpty(cameraFilePath)) {
                    CommonUtil.scanFileAsync(this, cameraFilePath);
                    LogUtil.d(TAG,"拍照返回的图片路径："+ cameraFilePath);
                    
                    if (mListPhotoPath == null) {
                        mListPhotoPath = new ArrayList<String>();
                    }
                    if (!mListPhotoPath.contains(cameraFilePath)) {
                        mListPhotoPath.add(cameraFilePath);
                    } 
                    imgAdapter.notifyDataSetChanged();
                    
                }else{
                	CommonUtil.showToast("解析数据出错", this);
                }
			}
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void initParams() {
    	

//      String ownNumber =  LoginPreference.getKeyValue(LoginType.PARTY_ID, "");
//      if (TextUtils.isEmpty(ownNumber)) {
//          // 尚未登录，不能分享
//          CommonUtil.showToast("请先登录然后再分享", this);
//          Intent intent = new Intent();
////          intent.setClass(this, Ac.class);
//          startActivity(intent);
//          finish();
//      }
    	
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
        	
            mIsLocalAction = extras.getBoolean(KEY_ACTION_CUSTOM);
            shareType = extras.getInt(KEY_FOR_SHARE_TYPE, 0);
            if (!mIsLocalAction) {
                // 从系统相册等分享
                String action = intent.getAction();
                if (Intent.ACTION_SEND.equals(action)) {
                    // 分享单文件
                    singleShare(extras);
                } else if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {
                    // 分享多文件
                    multiShare(extras);
                } else {
                    LogUtil.d(TAG, "initParams Unknow Action...");
                }
            } else {
                // 本地分享
                localAction(extras);
            }
            return;
        } else {
            localAction(extras);
        }
    }

    /**
     * @author: lihs
     * @Title: singleShare
     * @Description: 系统分享单文件
     * @param bundle
     * @date: 2014-11-7 下午3:54:24
     */
    private void singleShare(Bundle bundle) {
        if (bundle.containsKey(Intent.EXTRA_STREAM)) {
            try {
                if (null == mListPhotoPath) {
                    mListPhotoPath = new ArrayList<String>();
                }
                Uri uri = (Uri) bundle.getParcelable(Intent.EXTRA_STREAM);
                String path = FileManager.getPhotoPath(this,
                        uri);
                if (!TextUtils.isEmpty(path)) {
                    if (FileManager.isImageFile(new File(path))) {
                        // 系统分享只能分享照片，排除其他文件
                        mListPhotoPath.add(path);
                        LogUtil.d(TAG, "singleShare Path-->" + path);
                    }
                }
                return;
            } catch (Exception e) {
                LogUtil.e(e);
            }
        } else if (bundle.containsKey(Intent.EXTRA_TEXT)) {
            return;
        }
    }

    /**
     * @author: lihs
     * @Title: multiShare
     * @Description: 系统分享多文件
     * @param bundle
     * @date: 2014-11-7 下午3:57:18
     */
    private void multiShare(Bundle bundle) {
    	
        if (bundle.containsKey(Intent.EXTRA_STREAM)) {
            try {
                ArrayList<Uri> listUri = bundle
                        .getParcelableArrayList(Intent.EXTRA_STREAM);
                if (null == mListPhotoPath)
                    mListPhotoPath = new ArrayList<String>();
                Uri uri = null;
                String path = null;
                int j = 0;
                int max_count = MultiBucketChooserActivity.MAX_IMAGE_COUNT;
                for (int i = 0; i < listUri.size(); i++) {
                    uri = listUri.get(i);
                    path = FileManager.getPhotoPath(this, uri);
                    if (!TextUtils.isEmpty(path)) {
                        if (FileManager.isImageFile(new File(path))) {
                            mListPhotoPath.add(path);
                            j++;
                            if (j == max_count) {
                                break;
                            }
                        }
                        LogUtil.d(TAG, "multiShare Path-->" + path);
                    }
                }
                if (j == max_count && j < listUri.size()) {
                    Toast.makeText(
                            getBaseContext(),
                            "您选择的照片数目超过"
                                    + MultiBucketChooserActivity.MAX_IMAGE_COUNT
                                    + "，已自动帮您移除超出的照片", Toast.LENGTH_SHORT)
                            .show();
                }
                return;
            } catch (Exception e) {
                LogUtil.e(e);
            }
        } else if (bundle.containsKey(Intent.EXTRA_TEXT)) {
            return;
        }
    }

    /**
     * @author: lihs
     * @Title: localAction
     * @Description: 本地分享
     * @param bundle
     * @date: 2014-11-7 下午4:00:20
     */
    private void localAction(Bundle bundle) {
        if (null == mListPhotoPath)
            mListPhotoPath = new ArrayList<String>();

        if (null != bundle) {
            mCameraPath = bundle.getString(KEY_FILE_PATH);
            if (TextUtils.isEmpty(mCameraPath)) {
                if (bundle.containsKey(Intent.EXTRA_STREAM)) {
                    try {
                        mListPhotoPath = bundle.getStringArrayList(Intent.EXTRA_STREAM);
                    } catch (Exception e) {
                        LogUtil.e(e);
                    }
                }
            } else {
                mListPhotoPath.add(mCameraPath);
            }
        }
    }

    public class ImageAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater = null;

        private ImgViewHolder viewHolder = null;

        public ImageAdapter(Context context) {
            layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            if (mListPhotoPath == null) {
                return 1;
            } else {
                if (mListPhotoPath.size() < maxCount) {
                    // 未达到最大个数时，还可以继续添加
                    return mListPhotoPath.size() + 1;
                } else {
                    return mListPhotoPath.size();
                }
            }
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View curView = convertView;

            if (convertView == null) {
                curView = (View) layoutInflater.inflate(
                        R.layout.share_img_item_pic, parent, false);
                viewHolder = new ImgViewHolder();
                viewHolder.imageTarget = (ImageView) curView
                        .findViewById(R.id.image_target);
                viewHolder.videoIcon = (ImageView) curView
                        .findViewById(R.id.video_icon);
                curView.setTag(viewHolder);
            } else {
                viewHolder = (ImgViewHolder) curView.getTag();
            }

            // 显示图片
            RelativeLayout.LayoutParams imgLp = (RelativeLayout.LayoutParams) viewHolder.imageTarget
                    .getLayoutParams();
            imgLp.width = imgSize;
            imgLp.height = imgSize;
            viewHolder.imageTarget.setLayoutParams(imgLp);

            viewHolder.imageTarget.setTag(position);

            if (position == (getCount() - 1)
                    && mListPhotoPath.size() < maxCount) {
                // 还未选满，则最后一个为添加按钮
                viewHolder.imageTarget
                        .setImageResource(R.drawable.share_img_add_selector);
                viewHolder.videoIcon.setVisibility(View.GONE);
            } else {
                // 已选择个数达最大时，不显示添加按钮，最后一个也是正常图片
                if (shareType == SHARE_TYPE_VIDEO) {
                    viewHolder.videoIcon.setVisibility(View.VISIBLE);
                    mImageFetcher.loadImageThumb(viewHolder.imageTarget, 
                            mListPhotoPath.get(position),
                            ImageFetcher.THUMBNAIL_TYPE_VIDEO, null);
                } else {
                    viewHolder.videoIcon.setVisibility(View.GONE);
                    mImageFetcher.loadImageThumb(viewHolder.imageTarget,
                            mListPhotoPath.get(position),
                            ImageFetcher.THUMBNAIL_TYPE_IMAGE, null);
                }
            }

            viewHolder.imageTarget
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (CommonUtil.isFastDoubleClick()) {
                                return;
                            }
                            
                            int position = Integer.parseInt(String.valueOf(v.getTag()));
                            
                            if (position == (getCount() - 1) && mListPhotoPath.size() < maxCount) {
                                // 点击添加按钮，继续选图
                            	sharePhotoMenu();
                                
                            } else {
                                // 点击图片或视频，进入图片/视频预览界面（可移除图片，即不选择）。
                                Bundle bundle = new Bundle();
                                bundle.putStringArrayList(Intent.EXTRA_STREAM,mListPhotoPath);
                                bundle.putBoolean( ActSendComment.KEY_ACTION_CUSTOM,true);
                                bundle.putInt( ActSendComment.KEY_FOR_SHARE_TYPE,shareType);
                                bundle.putString(PreviewActivity.ACTIVITY_FLAG, PreviewActivity.AVITVITY_START_FOR_RESULT);
                                bundle.putInt(PreviewActivity.SELECTED_IMG_INDEX, position);
                                Intent intent = new Intent(ActSendComment.this,PreviewActivity.class);
                                intent.putExtras(bundle);
                                startActivityForResult(intent,REQUEST_CODE_PREVIEW);
                            }
                        }
                    });

            return curView;
        }
    }
    
    
    private void sharePhotoMenu() {
    	
        BottomButtonMenu buttonMenu = new BottomButtonMenu(this);
        buttonMenu.addButtonFirst(new MenuClickedListener() {
            @Override
            public void onMenuClicked() {
                // 拍照
            	if (mListPhotoPath != null && mListPhotoPath.size() >= ActSendComment.IMG_COLUMN_COUNT) {
					CommonUtil.showToast("本次分享的照片最多为4张", ActSendComment.this);
					return;
				}
                shareFromCamera();
            }
        }, "拍照");
        buttonMenu.addButtonSecond(new MenuClickedListener() {
            @Override
            public void onMenuClicked() {

                // 从手机选图
            	 Bundle bundle = new Bundle();
                 bundle.putString( MultiBucketChooserActivity.KEY_ACTIVITY_TYPE,MultiBucketChooserActivity.ACTIVITY_START_FOR_RESULT);
                 bundle.putInt(KEY_SELECTED_CNT, mListPhotoPath.size());
                 Intent intent = new Intent(ActSendComment.this,MultiBucketChooserActivity.class);
                 if (shareType == SHARE_TYPE_PIC) {
                     intent.putExtra(MultiBucketChooserActivity.KEY_BUCKET_TYPE, MultiBucketChooserActivity.BUCKET_TYPE_IMAGE);
                 }else{
                 	//TODO 分享其它的东西
                 }
                 intent.putExtras(bundle);
                 startActivityForResult(intent, REQUEST_CODE_ADD);
            }
        }, "从手机相册选择");
        buttonMenu.show();
    }

    
    private void shareFromCamera() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            doTakePhotoNative();
        } else {
            Toast.makeText(this, "SD卡未发现",
                    Toast.LENGTH_SHORT).show();
        }
    }
    
    protected void doTakePhotoNative() {
        try {// Launch camera to take photo for selected contact

            mCurPhotoFile = new File(Environment.getExternalStorageDirectory(),
                    getPhotoFileName());// attach
            if (mCurPhotoFile != null) {
                 
                final Intent intent = getTakePickIntent(mCurPhotoFile);
                cameraFilePath = mCurPhotoFile.getPath();
                startActivityForResult(intent, ACTION_SHARE_FROM_CAMERA);
            } else {
                Toast.makeText(this, "未发现SD卡",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (ActivityNotFoundException e) {
             LogUtil.d(TAG, "ActivityNotFoundException");
        }
    }

    public static Intent getTakePickIntent(File f) {
        // 部分三星手机，在启动照相机后，onActivityResult返回的intent为空，
        // 不能将照相后的图片传递到本页面,故此处用指定路径的形式做透传
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        return intent;
    }

    private String getPhotoFileName() {
        String timeStr = "IMG_" + System.currentTimeMillis() + ".jpg";
        return timeStr;
    }
    
    
    private static class ImgViewHolder {
        ImageView imageTarget;
        ImageView videoIcon;
    }

    CreateThumbnailTask createThumbnailTask;

    private class CreateThumbnailTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... arg0) {

            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            createThumbnailTask = null;
 
        }
    }

    public static boolean isOverImageSize(String localpath) {
    	
        long size = 0;
        File file = new File(localpath);
        if (file != null && file.exists()) {
            size = file.length();
        }

        if (size >= MAX_IMAGEFILE_SIZE) {
            return true;
        }

        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            doBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
