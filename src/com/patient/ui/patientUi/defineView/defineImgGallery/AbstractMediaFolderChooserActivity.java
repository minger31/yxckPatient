package com.patient.ui.patientUi.defineView.defineImgGallery;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;

import com.patient.commonent.TitleBar;
import com.patient.ui.patientUi.fragment.BaseFragmentActivity;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

/**
 * <dl>
 * <dt>AbstractMediaFolderChooserActivity.java</dt>
 * <dd>Description:媒体文件选择，支持单击查看，长按进入选择模式</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>CreateDate: 2013-10-9 下午1:44:31</dd>
 * </dl>
 * 
 * @author lihs
 */
public abstract class AbstractMediaFolderChooserActivity extends BaseFragmentActivity implements OnItemClickListener {
	
	private static final String TAG = AbstractMediaFolderChooserActivity.class.getName();

    private ImageButton refreshBtn = null;

    // 是否正在扫描
    private boolean isMediaScanning = false;

    // sd卡扫描状态监听
    private ScanSdFilesReceiver scanSdReceiver = null;
    // 内容监听
    private MyContentObserver observer = null;
    // 标题
    protected TitleBar titleBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());

        titleBar = getTitleBar();

        if (canRefresh() && needRefresh()) {
            refreshBtn = (ImageButton) findViewById(R.id.action_refresh);
            refreshBtn.setVisibility(View.VISIBLE);
            refreshBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshAnimation();
                    // 扫描sd卡
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri
                            .parse("file://"
                                    + Environment.getExternalStorageDirectory()
                                            .getAbsolutePath())));
                }
            });

            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);
            intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
            intentFilter.addDataScheme("file");
            scanSdReceiver = new ScanSdFilesReceiver();
            registerReceiver(scanSdReceiver, intentFilter);
        }

        if (needContentObserver()) {
            observer = new MyContentObserver();
            registerContentObserver(observer);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (canRefresh() && needRefresh()) {
            unregisterReceiver(scanSdReceiver);
        }

        if (observer != null) {
            getContentResolver().unregisterContentObserver(observer);
        }
    }

    private boolean canRefresh() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return true;
        } else {
            return false;
        }
    }

    protected abstract void registerContentObserver(MyContentObserver observer);

    protected abstract boolean needRefresh();

    protected abstract boolean needContentObserver();

    protected abstract int getDataCnt();

    protected abstract int getMaxCnt();

    protected abstract void clickPosition(int position);

    protected abstract int getContentView();

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        // 浏览模式
        clickPosition(position);
    }

    private void refreshAnimation() {
        final RotateAnimation mRotateAnimation = new RotateAnimation(0.0F,
                360.0F, 1, 0.5F, 1, 0.5F);
        mRotateAnimation.setFillAfter(false);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRotateAnimation.setDuration(1000);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        refreshBtn.setImageResource(R.drawable.refresh_ico_anim);
        refreshBtn.post(new Runnable() {
            @Override
            public void run() {
                isMediaScanning = true;
                refreshBtn.startAnimation(mRotateAnimation);
            }
        });
    }

    private class ScanSdFilesReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_MEDIA_SCANNER_STARTED.equals(action)) {
                Log.d(TAG, "ScanSdFilesReceiver 开始扫描...");
                // 开始扫描
                if (!isMediaScanning) {
                    isMediaScanning = true;
                    refreshAnimation();
                }
            } else if (Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)) {
                Log.d(TAG, "ScanSdFilesReceiver 扫描结束...");
                // 扫描结束
                if (isMediaScanning) {
                    isMediaScanning = false;
                    refreshBtn.clearAnimation();
                    refreshBtn.setImageResource(R.drawable.refresh_ico);
                }
            }
        }
    }

    protected class MyContentObserver extends ContentObserver {
        public MyContentObserver() {
            super(new Handler());
        }

        @SuppressLint("NewApi")
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            LogUtil.d("AbstractMediaChooserActivity Content数据发生变更");

            // 刷新界面显示
            getSupportLoaderManager().restartLoader(getCursorId(), null, getLoaderCallbacks());
        }
    }

    protected abstract int getCursorId();

    protected abstract LoaderCallbacks<Cursor> getLoaderCallbacks();
}
