package com.patient.library.bitmap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.Hashtable;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.patient.util.BitmapManager;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;
import com.yxck.patient.BuildConfig;



/**
 * This class wraps up completing some arbitrary long running work when loading a bitmap to an
 * ImageView. It handles things like using a memory and disk cache, running the work in a background
 * thread and setting a placeholder image.
 */
 
public abstract class ImageWorker {
	
    private static final String TAG = "ImageWorker";
    private static final int FADE_IN_TIME = 100;
    public static final int TARGET_SIZE_MINI_THUMBNAIL = getThumbnailImgSize();

    private ImageCache mImageCache;
    private ImageCache.ImageCacheParams mImageCacheParams;
    private Bitmap mLoadingBitmap;
    private boolean mFadeInBitmap = true;
    private boolean mExitTasksEarly = false;
    protected boolean mPauseWork = false;
    private final Object mPauseWorkLock = new Object();
    private final Hashtable<Integer, Bitmap> loadingBitmaps = new Hashtable<Integer, Bitmap>(2);

    protected Resources mResources;

    private static final int MESSAGE_CLEAR = 0;
    private static final int MESSAGE_INIT_DISK_CACHE = 1;
    private static final int MESSAGE_FLUSH = 2;
    private static final int MESSAGE_CLOSE = 3;

    public static final String THUMBNAIL_TYPE_IMAGE = "image";
    public static final String THUMBNAIL_TYPE_VIDEO = "video";

    private boolean mDiskCacheLocalImg = true;

    protected Context mContext = null;

    /** 
     * @author: zhaguitao
     * @Title: getThumbnailImgSize 
     * @Description: 根据手机性能，显示不同大小缩略图
     * @return 
     * @date: 2014-1-21 下午3:54:22
     */
    private static int getThumbnailImgSize() {
//        if (DoctorApplication.phonePerformValue < 0) {
//            return 120;
//        } else if (DoctorApplication.phonePerformValue <= CommonConstant.PHONE_PERFORMANCE_LOW) {
//            return 80;
//        } else if (DoctorApplication.phonePerformValue <= CommonConstant.PHONE_PERFORMANCE_MIDDLE1) {
//            return 120;
//        } else if (DoctorApplication.phonePerformValue <= CommonConstant.PHONE_PERFORMANCE_MIDDLE2) {
//            return 200;
//        } else {
//            return 320;
//        }
        
            return 320;
    }

    protected int getThumbnailType() {
//        if (DoctorApplication.phonePerformValue < 0) {
//            return Thumbnails.MICRO_KIND;
//        } else if (DoctorApplication.phonePerformValue <= CommonConstant.PHONE_PERFORMANCE_MIDDLE1) {
//            return Thumbnails.MICRO_KIND;
//        } else {
//            return Thumbnails.MINI_KIND;
//        }
//        
            return Thumbnails.MINI_KIND;
    }

    protected ImageWorker(Context context) {
    	mContext = context;
        mResources = context.getResources();
    }
    
    protected void setDiskCacheLocalImg(boolean diskCacheLocalImg) {
        mDiskCacheLocalImg = diskCacheLocalImg;
    }

    public interface OnImgLoadAfterListener {
        public void onImgLoadAfter();
    }

    public interface OnBitmapListener {
        public void onImgLoadAfter(Bitmap bitmap);
    }
    
    /** 
     * @author: zhaguitao
     * @Title: loadLocalImage 
     * @Description: 加载指定尺寸的本地图片
     * @param imageView
     * @param path
     * @param reqWidth
     * @param reqHeight
     * @param listener 
     * @date: 2014-4-24 上午11:15:54
     */
    public void loadLocalImage(ImageView imageView, String path, int reqWidth,
            int reqHeight, OnImgLoadAfterListener listener) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        Log.d(TAG, "loadLocalImage:" + path);

        ImageParams params = new ImageParams();
        params.loadType = ImageParams.LOAD_TYPE_LOCAL;
        params.path = path;
        params.reqWidth = reqWidth;
        params.reqHeight = reqHeight;
        params.loadAfterListener = listener;
        loadImage(imageView, params);
    }

    public void loadLocalImage(ImageView imageView, String path, int reqWidth, int reqHeight) {
    	
        loadLocalImage(imageView, path, reqWidth, reqHeight, new OnImgLoadAfterListener() {
			
			@Override
			public void onImgLoadAfter() {
				// TODO Auto-generated method stub
				
			}
		});
    }
    
    
    public void loadLocalBitImage(ImageView imageView, String path, int reqWidth,
            int reqHeight) {
        loadLocalImage(imageView, path, reqWidth, reqHeight, new OnImgLoadAfterListener() {
			
			@Override
			public void onImgLoadAfter() {
				
			}
		});
        
//        loadLocalImage(imageView, path, reqWidth, reqHeight, null);
    }
    
    /** 
     * @author: lihs
     * @Title: loadLocalImage 
     * @Description: 加载指定尺寸的本地图片
     * @param imageView
     * @param path
     * @param reqWidth
     * @param reqHeight
     * @param listener 
     * @date: 2014-4-24 上午11:15:54
     */
    public void loadLocalImage(ImageView imageView, String path, int reqWidth,
            int reqHeight, OnBitmapListener listener) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        Log.d(TAG, "loadLocalImage:" + path);

        ImageParams params = new ImageParams();
        params.loadType = ImageParams.LOAD_TYPE_LOCAL;
        params.path = path;
        params.reqWidth = reqWidth;
        params.reqHeight = reqHeight;
        params.bitmapListener = listener;
        loadImage(imageView, params);
    }


    /** 
     * @author: zhaguitao
     * @Title: loadImageThumb 
     * @Description: 加载指定ID的缩略图
     * @param imageView
     * @param thumbId 
     * @param thumbType
     * @param listener
     * @date: 2014-4-24 上午11:16:15
     */
    public void loadImageThumb(ImageView imageView, int thumbId,
            String thumbType, OnImgLoadAfterListener listener) {
        if (thumbId < 0) {
            return;
        }
        Log.d(TAG, "loadImageThumb:" + thumbId);
        ImageParams params = new ImageParams();
        params.loadType = ImageParams.LOAD_TYPE_THUMB_ID;
        params.thumbType = thumbType;
        params.loadAfterListener = listener;
        params.thumbId = thumbId;
        loadImage(imageView, params);
    }

    /** 
     * @author: zhaguitao
     * @Title: loadImageThumb 
     * @Description: 加载指定路径图片的缩略图
     * @param imageView
     * @param path
     * @param thumbType
     * @param listener 
     * @date: 2014-4-24 上午11:16:34
     */
    public void loadImageThumb(ImageView imageView, String path,
            String thumbType, OnImgLoadAfterListener listener) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        Log.d(TAG, "loadImageThumb:" + path);

        ImageParams params = new ImageParams();
        params.loadType = ImageParams.LOAD_TYPE_THUMB_PATH;
        params.path = path;
        params.thumbType = thumbType;
        params.loadAfterListener = listener;
        loadImage(imageView, params);
    }

    /** 
     * @author: zhaguitao
     * @Title: loadHttpImage 
     * @Description: 加载指定地址和尺寸的网络图片
     * @param imageView
     * @param path
     * @param reqWidth
     * @param reqHeight
     * @param listener 
     * @date: 2014-4-24 上午11:16:59
     */
    public void loadHttpImage(ImageView imageView, String path, int reqWidth,
            int reqHeight, OnImgLoadAfterListener listener) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        Log.d(TAG, "loadHttpImage:" + path);

        ImageParams params = new ImageParams();
        params.loadType = ImageParams.LOAD_TYPE_HTTP;
        params.path = path;
        params.reqWidth = reqWidth;
        params.reqHeight = reqHeight;
        params.loadAfterListener = listener;
        loadImage(imageView, params);
    }

    
    /** 
     * @author: zhaguitao
     * @Title: loadHttpImage 
     * @Description: 加载指定地址和尺寸的网络图片
     * @param imageView
     * @param path
     * @param reqWidth
     * @param reqHeight
     * @param listener 
     * @date: 2014-4-24 上午11:16:59
     */
    public void loadHttpImage(ImageView imageView, String path, int reqWidth,
            int reqHeight, OnBitmapListener listener) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        Log.d(TAG, "loadHttpImage:" + path);

        ImageParams params = new ImageParams();
        params.loadType = ImageParams.LOAD_TYPE_HTTP;
        params.path = path;
        params.reqWidth = reqWidth;
        params.reqHeight = reqHeight;
        params.bitmapListener = listener;
        loadImage(imageView, params);
    }
    
    private void loadImage(ImageView imageView, ImageParams params) {
        if (imageView == null) {
            return;
        }

        Bitmap bitmap = null;

        if (mImageCache != null) {
            // 加载内存中缓存图片
            if (params.loadType == ImageParams.LOAD_TYPE_LOCAL) {
                bitmap = mImageCache.getBitmapFromMemCache(params.path
                        + params.reqWidth + params.reqHeight);
            } else if (params.loadType == ImageParams.LOAD_TYPE_THUMB_ID) {
                bitmap = mImageCache
                        .getBitmapFromMemCache(params.thumbType
                                + params.thumbId);
            } else if (params.loadType == ImageParams.LOAD_TYPE_THUMB_PATH) {
                bitmap = mImageCache.getBitmapFromMemCache(params.path);
            } else if (params.loadType == ImageParams.LOAD_TYPE_HTTP) {
                bitmap = mImageCache.getBitmapFromMemCache(params.path
                        + params.reqWidth + params.reqHeight);
            }
        }

        if (bitmap != null) {
            if("0".equals(imageView.getTag())) {
                // 变成黑白的
                bitmap = BitmapManager.toGrayscale(bitmap);
            }
            imageView.setImageBitmap(bitmap);
            if (params.loadAfterListener != null) {
                params.loadAfterListener.onImgLoadAfter();
            }
            if (params.bitmapListener != null) {
                params.bitmapListener.onImgLoadAfter(bitmap);
            }
        } else if (cancelPotentialWork(
                TextUtils.isEmpty(params.path) ? params.thumbId : params.path, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(mResources, mLoadingBitmap, task);
            imageView.setImageDrawable(asyncDrawable);

            // NOTE: This uses a custom version of AsyncTask that has been pulled from the
            // framework and slightly modified. Refer to the docs at the top of the class
            // for more info on what was changed.
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        }
    }

    /**
     * Set placeholder bitmap that shows when the the background thread is running.
     *
     * @param bitmap
     */
    @Deprecated
    public void setLoadingImage(Bitmap bitmap) {
        mLoadingBitmap = bitmap;
    }

    /**
     * Set placeholder bitmap that shows when the the background thread is running.
     *
     * @param resId
     */
    public void setLoadingImage(int resId) {
    	if (!loadingBitmaps.containsKey(resId)) {
  			// Store loading bitmap in a hash table to prevent continual decoding
  			loadingBitmaps.put(resId, BitmapFactory.decodeResource(mResources, resId));
  		}
        mLoadingBitmap = loadingBitmaps.get(resId);
    }

    /**
     * Adds an {@link ImageCache} to this worker in the background (to prevent disk access on UI
     * thread).
     * @param fragmentManager
     * @param cacheParams
     */
    public void addImageCache(FragmentManager fragmentManager,
            ImageCache.ImageCacheParams cacheParams) {
        mImageCacheParams = cacheParams;
        setImageCache(ImageCache.findOrCreateCache(fragmentManager, mImageCacheParams));
        new CacheAsyncTask().execute(MESSAGE_INIT_DISK_CACHE);
    }

    /**
     * Adds an {@link ImageCache} to this worker in the background (to prevent disk access on UI
     * thread).
     * @param fragmentManager
     * @param cacheParams
     */
    public void addImageCache(ImageCache cache) {
        mImageCache = cache;
        new CacheAsyncTask().execute(MESSAGE_INIT_DISK_CACHE);
    }

    /**
     * Sets the {@link ImageCache} object to use with this ImageWorker. Usually you will not need
     * to call this directly, instead use {@link ImageWorker#addImageCache} which will create and
     * add the {@link ImageCache} object in a background thread (to ensure no disk access on the
     * main/UI thread).
     *
     * @param imageCache
     */
    public void setImageCache(ImageCache imageCache) {
        mImageCache = imageCache;
    }

    /**
     * If set to true, the image will fade-in once it has been loaded by the background thread.
     */
    public void setImageFadeIn(boolean fadeIn) {
        mFadeInBitmap = fadeIn;
    }

    public void setExitTasksEarly(boolean exitTasksEarly) {
        mExitTasksEarly = exitTasksEarly;
    }

    /**
     * Subclasses should override this to define any processing or work that must happen to produce
     * the final bitmap. This will be executed in a background thread and be long running. For
     * example, you could resize a large bitmap here, or pull down an image from the network.
     *
     * @param data The data to identify which image to process, as provided by
     *            {@link ImageWorker#loadImage(Object, ImageView)}
     * @return The processed bitmap
     */
    protected abstract Bitmap processBitmap(Object data);

    protected abstract Bitmap processBitmap(Object data, int reqWidth, int reqHeight);

    /**
     * Cancels any pending work attached to the provided ImageView.
     * @param imageView
     */
    public static void cancelWork(ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if (bitmapWorkerTask != null) {
            bitmapWorkerTask.cancel(true);
            if (BuildConfig.DEBUG) {
                final Object bitmapData = bitmapWorkerTask.data;
                Log.d(TAG, "cancelWork - cancelled work for " + bitmapData);
            }
        }
    }

    /**
     * Returns true if the current work has been canceled or if there was no work in
     * progress on this image view.
     * Returns false if the work in progress deals with the same data. The work is not
     * stopped in that case.
     */
    public static boolean cancelPotentialWork(Object data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final Object bitmapData = bitmapWorkerTask.data;
            if (bitmapData == null || !bitmapData.equals(data)) {
                bitmapWorkerTask.cancel(true);
                
                Log.v(TAG, "cancelPotentialWork - cancelled work for " + data);
                
            } else {
                // The same work is already in progress.
                return false;
            }
        }
        return true;
    }

    public static boolean cancelPotentialWork(int oriId, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final int bitmapOriId = bitmapWorkerTask.oriId;
            if (bitmapOriId != oriId) {
                bitmapWorkerTask.cancel(true);
                
                Log.v(TAG, "cancelPotentialWork - cancelled work for oriId:" + oriId);
                
            } else {
                // The same work is already in progress.
                return false;
            }
        }
        return true;
    }

    /**
     * @param imageView Any imageView
     * @return Retrieve the currently active work task (if any) associated with this imageView.
     * null if there is no such task.
     */
    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    /**
     * The actual AsyncTask that will asynchronously process the image.
     */
    private class BitmapWorkerTask extends AsyncTask<Object, Void, Bitmap> {
		private Object data;
        private int oriId;
        private int rotation;
        private OnImgLoadAfterListener loadAfterListener;
        private OnBitmapListener bitmapListener;
        private final WeakReference<ImageView> imageViewReference;

        public BitmapWorkerTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        /**
         * Background processing.
         * @throws FileNotFoundException 
         */
        @Override
        protected Bitmap doInBackground(Object... params) {

        	Log.d(TAG, "doInBackground - starting work:" + System.currentTimeMillis());

            // Wait here if work is paused and the task is not cancelled
            synchronized (mPauseWorkLock) {
                while (mPauseWork && !isCancelled()) {
                    try {
                        Log.v(TAG, "doInBackground - mPauseWorkLock.wait()");
                        mPauseWorkLock.wait();
                    } catch (InterruptedException e) {}
                }
            }
            
            if (params == null || params.length == 0) {
                return null;
            }

            ImageParams imageParam = (ImageParams) params[0];
            bitmapListener = imageParam.bitmapListener;
            loadAfterListener = imageParam.loadAfterListener;
           
            String key = "";
            
            if (imageParam.loadType == ImageParams.LOAD_TYPE_LOCAL
                    || imageParam.loadType == ImageParams.LOAD_TYPE_HTTP) {
                // 加载本地/网络图片
                data = imageParam.path;
                key = imageParam.path + imageParam.reqWidth + imageParam.reqHeight;
            } else if (imageParam.loadType == ImageParams.LOAD_TYPE_THUMB_ID) {
                oriId = imageParam.thumbId;
                rotation = CommonUtil.getImageRotationById(mContext, oriId);
                key = imageParam.thumbType + imageParam.thumbId;
            } else if (imageParam.loadType == ImageParams.LOAD_TYPE_THUMB_PATH) {
                // 根据路径获得ID
                int[] result = CommonUtil.getIdRotationByPath(
                        mContext, imageParam.path, imageParam.thumbType);
                oriId = result[0];
                rotation = result[1];
                key = imageParam.path;
            }

            Bitmap bitmap = null;

            // If the image cache is available and this task has not been cancelled by another
            // thread and the ImageView that was originally bound to this task is still bound back
            // to this task and our "exit early" flag is not set then try and fetch the bitmap from
            // the cache
            if (mImageCache != null && !isCancelled() && getAttachedImageView() != null
                    && !mExitTasksEarly) {
                Log.d(TAG, "doInBackground - getBitmapFromDiskCache");
                
                if (imageParam.loadType == ImageParams.LOAD_TYPE_HTTP) {
                    bitmap = mImageCache.getBitmapFromDiskCache(key);
                } else {
                    if (mDiskCacheLocalImg) {
                        bitmap = mImageCache.getBitmapFromDiskCache(key);
                    }
                }
            }

            // If the bitmap was not found in the cache and this task has not been cancelled by
            // another thread and the ImageView that was originally bound to this task is still
            // bound back to this task and our "exit early" flag is not set, then call the main
            // process method (as implemented by a subclass)
            if (bitmap == null && !isCancelled() && getAttachedImageView() != null
                    && !mExitTasksEarly) {
                Log.v(TAG, "doInBackground - processBitmap");
                if (imageParam.loadType == ImageParams.LOAD_TYPE_LOCAL) {
                    // 加载本地图片
                    bitmap = ImageResizer.decodeSampledBitmapFromFile(
                            imageParam.path, imageParam.reqWidth, imageParam.reqHeight);
                    if (bitmap != null) {
                        rotation = CommonUtil.getImageRotationByPath(mContext,
                                imageParam.path);
                        LogUtil.d(TAG, "取原图文件旋转角度:" + rotation);
                    }
                } else if (imageParam.loadType == ImageParams.LOAD_TYPE_THUMB_ID
                        || imageParam.loadType == ImageParams.LOAD_TYPE_THUMB_PATH) {

                    if (oriId < 0) {
                        // 没有缩略图信息，则自动生成缩略图
                        if (THUMBNAIL_TYPE_VIDEO.equals(imageParam.thumbType)) {
                            // 生成视频缩略图
                            if (!TextUtils.isEmpty(imageParam.path)) {
                                bitmap = ThumbnailUtils.createVideoThumbnail(
                                        imageParam.path, getThumbnailType());
                                // 通知系统生成缩略图，以便下次能直接取缩略图
                                CommonUtil.scanFileAsync(mContext, imageParam.path);
                            }
                        } else {
                            if (!TextUtils.isEmpty(imageParam.path)) {
                                // 图片缩略图
                                bitmap = ThumbnailUtils.extractThumbnail(
                                        ImageWorker.decodeFile(new File(imageParam.path)),
                                        ImageWorker.TARGET_SIZE_MINI_THUMBNAIL,
                                        ImageWorker.TARGET_SIZE_MINI_THUMBNAIL,
                                        ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                                // 通知系统生成缩略图，以便下次能直接取缩略图
                                CommonUtil.scanFileAsync(mContext, imageParam.path);
                            }
                            if (bitmap != null) {
                                rotation = CommonUtil.getImageRotationByPath(
                                        mContext, imageParam.path);
                                LogUtil.d(TAG, "取原图文件旋转角度:" + rotation);
                            }
                        }
                    } else {
                        // 获取缩略图
                        if (THUMBNAIL_TYPE_VIDEO.equals(imageParam.thumbType)) {
                            bitmap = MediaStore.Video.Thumbnails.getThumbnail(
                                    mContext.getContentResolver(), oriId,
                                    12345L, getThumbnailType(), null);
                        } else {
                            bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                                    mContext.getContentResolver(), oriId,
                                    12345L, getThumbnailType(), null);
                        }

                        if (bitmap == null) {
                            // 获取缩略图失败，生成缩略图
                            if (THUMBNAIL_TYPE_VIDEO.equals(imageParam.thumbType)) {
                                String videoPath = imageParam.path;
                                if (TextUtils.isEmpty(videoPath)) {
                                    videoPath = ImageWorker
                                            .getVideoPathById(mContext, oriId);
                                }
                                if (!TextUtils.isEmpty(videoPath)) {
                                    bitmap = ThumbnailUtils
                                            .createVideoThumbnail(videoPath,
                                                    getThumbnailType());
                                    // 通知系统生成缩略图，以便下次能直接取缩略图
                                    CommonUtil.scanFileAsync(mContext, videoPath);
                                }
                            } else {
                                String imagePath = imageParam.path;
                                if (TextUtils.isEmpty(imagePath)) {
                                    imagePath = ImageWorker
                                            .getImagePathById(mContext, oriId);
                                }
                                if (!TextUtils.isEmpty(imagePath)) {
                                    bitmap = ThumbnailUtils.extractThumbnail(
                                            ImageWorker.decodeFile(new File(
                                                    imagePath)),
                                            ImageWorker.TARGET_SIZE_MINI_THUMBNAIL,
                                            ImageWorker.TARGET_SIZE_MINI_THUMBNAIL,
                                            ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                                    // 通知系统生成缩略图，以便下次能直接取缩略图
                                    CommonUtil.scanFileAsync(mContext, imagePath);
                                    if (bitmap != null) {
                                        rotation = CommonUtil.getImageRotationFromUrl(imagePath);
                                        LogUtil.d(TAG, "取原图文件旋转角度:" + rotation);
                                    }
                                }
                            }
                        }
                    }
                } else if (imageParam.loadType == ImageParams.LOAD_TYPE_HTTP) {
                    bitmap = processBitmap(imageParam.path, imageParam.reqWidth, imageParam.reqHeight);
                }

                // 摆正其旋转角度
                if (bitmap != null && rotation != 0) {
                    LogUtil.d(TAG, "图片旋转角度:" + rotation);
                    Matrix m = new Matrix();
                    m.setRotate(rotation);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                            bitmap.getHeight(), m, true);
                }
            }

            // If the bitmap was processed and the image cache is available, then add the processed
            // bitmap to the cache for future use. Note we don't check if the task was cancelled
            // here, if it was, and the thread is still running, we may as well add the processed
            // bitmap to our cache as it might be used again in the future
            if (bitmap != null && mImageCache != null) {
                Log.d(TAG, "doInBackground - addBitmapToCache");

                if (imageParam.loadType == ImageParams.LOAD_TYPE_HTTP) {
                    mImageCache.addBitmapToCache(key, bitmap);
                } else if (imageParam.loadType == ImageParams.LOAD_TYPE_LOCAL
                        || imageParam.loadType == ImageParams.LOAD_TYPE_THUMB_ID
                        || imageParam.loadType == ImageParams.LOAD_TYPE_THUMB_PATH) {
                    if (mDiskCacheLocalImg) {
                        mImageCache.addBitmapToCache(key, bitmap);
                    } else {
                        mImageCache.addBitmapToMemCache(key, bitmap);
                    }
                }
            }

            Log.d(TAG, "doInBackground - finished work:type=" + imageParam.loadType + "|path=" + imageParam.path);

            return bitmap;
        }

        /**
         * Once the image is processed, associates it to the imageView
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // if cancel was called on this task or the "exit early" flag is set then we're done
            if (isCancelled() || mExitTasksEarly) {
                bitmap = null;
            }

            final ImageView imageView = getAttachedImageView();
            if (imageView != null) {
                if (bitmap != null) {
                    Log.v(TAG, "onPostExecute - setting bitmap");
                    setImageBitmap(imageView, bitmap);
//                } else {
//                    setImageBitmap(imageView, BitmapFactory.decodeResource(
//                            mResources, R.drawable.wrong_photo));
                }
                data = null;
                oriId = -1;
                final Bitmap temp  =  bitmap;
                imageView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (loadAfterListener != null) {
                            loadAfterListener.onImgLoadAfter();
                        }
                         if (bitmapListener != null) {
                        	 bitmapListener.onImgLoadAfter(temp);
						}
                    }
                }, FADE_IN_TIME);
            }
        }

        @Override
        protected void onCancelled(Bitmap bitmap) {
            super.onCancelled(bitmap);
            synchronized (mPauseWorkLock) {
                mPauseWorkLock.notifyAll();
            }
        }

        /**
         * Returns the ImageView associated with this task as long as the ImageView's task still
         * points to this task as well. Returns null otherwise.
         */
        private ImageView getAttachedImageView() {
            final ImageView imageView = imageViewReference.get();
            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

            if (this == bitmapWorkerTask) {
                return imageView;
            }

            return null;
        }

    }

    /**
     * A custom Drawable that will be attached to the imageView while the work is in progress.
     * Contains a reference to the actual worker task, so that it can be stopped if a new binding is
     * required, and makes sure that only the last started worker process can bind its result,
     * independently of the finish order.
     */
    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    /**
     * Called when the processing is complete and the final bitmap should be set on the ImageView.
     *
     * @param imageView
     * @param bitmap
     */
    private void setImageBitmap(ImageView imageView, Bitmap bitmap) {
        if("0".equals(imageView.getTag())){
            // 变成黑白的
            bitmap = BitmapManager.toGrayscale(bitmap);
        }
        if (mFadeInBitmap) {
            // Transition drawable with a transparent drwabale and the final bitmap
            final TransitionDrawable td =
                    new TransitionDrawable(new Drawable[] {
                            new ColorDrawable(android.R.color.transparent),
                            new BitmapDrawable(mResources, bitmap)
                    });
            // Set background to loading bitmap
            //TODO: miaolikui delete 20121229
//            imageView.setBackgroundDrawable(
//                    new BitmapDrawable(mResources, mLoadingBitmap));

            Log.d(TAG, "imageView.setImageDrawable:" + System.currentTimeMillis());
            imageView.setImageDrawable(td);
            td.startTransition(FADE_IN_TIME);
        } else {
            Log.d(TAG, "imageView.setImageBitmap:" + System.currentTimeMillis());
            imageView.setImageBitmap(bitmap);
        }
    }

    public void setPauseWork(boolean pauseWork) {
        synchronized (mPauseWorkLock) {
            mPauseWork = pauseWork;
            if (!mPauseWork) {
                mPauseWorkLock.notifyAll();
            }
        }
    }

    protected class CacheAsyncTask extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... params) {
            switch ((Integer)params[0]) {
                case MESSAGE_CLEAR:
                    clearCacheInternal();
                    break;
                case MESSAGE_INIT_DISK_CACHE:
                    initDiskCacheInternal();
                    break;
                case MESSAGE_FLUSH:
                    flushCacheInternal();
                    break;
                case MESSAGE_CLOSE:
                    closeCacheInternal();
                    break;
            }
            return null;
        }
    }

    protected void initDiskCacheInternal() {
        if (mImageCache != null) {
            mImageCache.initDiskCache();
        }
    }

    protected void clearCacheInternal() {
        if (mImageCache != null) {
            mImageCache.clearCache();
        }
    }

    protected void flushCacheInternal() {
        if (mImageCache != null) {
            mImageCache.flush();
        }
    }

    protected void closeCacheInternal() {
        if (mImageCache != null) {
            mImageCache.close();
            //TODO:在部分情况下，关闭应用调用到closeCache,然而由于异步下载解码显示（doInBackground）可能仍在执行
            //在mImageCache置为空后，异步doInBackground执行缓存操作，会出现NullPointerException
            //mImageCache = null;
        }
    }

    public void clearCache() {
        new CacheAsyncTask().execute(MESSAGE_CLEAR);
    }

    public void flushCache() {
        new CacheAsyncTask().execute(MESSAGE_FLUSH);
    }

    public void closeCache() {
        new CacheAsyncTask().execute(MESSAGE_CLOSE);
    }

    public static Bitmap decodeFile(File f) {
        if (!f.exists() || !f.isFile()) {
            return null;
        }
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_HEIGHT = TARGET_SIZE_MINI_THUMBNAIL;
            final int REQUIRED_WIDTH = TARGET_SIZE_MINI_THUMBNAIL;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;

            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_WIDTH
                        && height_tmp / 2 < REQUIRED_HEIGHT)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale++;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            LogUtil.e(e);
        } catch (OutOfMemoryError error) {
            LogUtil.e(error);
        }
        return null;
    }
    public static String getImagePathById(Context ctx, int imageId) {

        Cursor cursor = null;
        try {
            cursor = ctx.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[] { MediaStore.Images.Media.DATA },
                    MediaStore.Images.Media._ID + " = ?",
                    new String[] { "" + imageId },
                    null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                return cursor.getString(0);
            }
        } catch (Exception e) {
            LogUtil.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return "";
    }

    public static String getVideoPathById(Context ctx, int videoId) {

        Cursor cursor = null;
        try {
            cursor = ctx.getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    new String[] { MediaStore.Video.Media.DATA },
                    MediaStore.Video.Media._ID + " = ?",
                    new String[] { "" + videoId },
                    null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                return cursor.getString(0);
            }
        } catch (Exception e) {
            LogUtil.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        return "";
    }


    /**
     * <dl>
     * <dt>ImageWorker.java</dt>
     * <dd>Description:加载图片参数</dd>
     * <dd>Copyright: Copyright (C) 2014</dd>
     * <dd>Company: </dd>
     * <dd>CreateDate: 2014-4-24 上午10:13:10</dd>
     * </dl>
     * 
     * @author zhaguitao
     */
    private class ImageParams {
        public static final int LOAD_TYPE_LOCAL = 10;
        public static final int LOAD_TYPE_THUMB_PATH = 20;
        public static final int LOAD_TYPE_THUMB_ID = 21;
        public static final int LOAD_TYPE_HTTP = 30;
        
//        public static final String THUMB_ID_KEY_PREFIX = "thumbnail_";

        public int loadType = -1;
        public String path = "";
        public int thumbId = -1;
        public int reqWidth = 0;
        public int reqHeight = 0;
        public String thumbType = "";
        public OnImgLoadAfterListener loadAfterListener = null;
        public OnBitmapListener bitmapListener = null;
    }
}
