package com.patient.ui.patientUi.activity.common;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

import com.patient.constant.CommonConstant;
import com.patient.ui.patientUi.defineView.defineImgGallery.MultiBucketChooserActivity;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

public class PopPicDialog {

	private static final String TAG = PopPicDialog.class.getName();
	public static final int BASIC_INFO = 1;// 是基本信息
	public static final int CURE_INFO = 2; // 治疗信息
	public static final int CHECK_INFO = 3;// 检查信息
	
	// 表示 选择了基本信息
	public static int chose_type = BASIC_INFO;
	
	public static String cameraFilePath ="";
 
    private View contentView;
    private Dialog dialog;
    
    

    public void setDialog(Dialog dialog) {
		this.dialog = dialog;
	}

	private Context context;
    // 添加照片
    public static final int REQUEST_CODE_ADD = 112;
    public static final int ACTION_SHARE_FROM_CAMERA = 1000;

    private int sizes;//还可以选择的图片个数，传值到MultiBucketChooserActivity
    /**
     * size 传过来当前已有的照片个数
     * */
    private int MAX_IMAGE_COUNT = 9;
    public PopPicDialog(final Context context,int choseType , int size) {
    	this.sizes = MAX_IMAGE_COUNT - size;
    	this.chose_type = choseType;
    	LogUtil.d(TAG,"PopPicDialog 图片选择dialog");
    	cameraFilePath = "";
    	
        this.context = context;
        dialog = new Dialog(context, R.style.CustomProgressDialog);
        contentView = LayoutInflater.from(context).inflate(R.layout.topic_layout, null);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        params.width = CommonUtil.getDeviceSize(context).x - 40;
        params.height = LayoutParams.WRAP_CONTENT;
        dialog.addContentView(contentView, params);
        dialog.setCanceledOnTouchOutside(true);
        dialog.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_BACK));
        dialog.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_BACK));
        contentView.findViewById(R.id.patientLt).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shareFromCamera(context);
			 	dismiss();
			}
		});
        contentView.findViewById(R.id.topicLt).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				  // 从手机选图
           	    Bundle bundle = new Bundle();
                bundle.putString( MultiBucketChooserActivity.KEY_ACTIVITY_TYPE,MultiBucketChooserActivity.ACTIVITY_START_FOR_RESULT);
                bundle.putBoolean(MultiBucketChooserActivity.KEY_IS_MULTI, true);
                Intent intent = new Intent(context,MultiBucketChooserActivity.class);
                intent.putExtra(MultiBucketChooserActivity.KEY_BUCKET_TYPE, MultiBucketChooserActivity.BUCKET_TYPE_IMAGE);
                intent.putExtra(CommonConstant.KEY_RESLUT, sizes);
                intent.putExtras(bundle);
            	((Activity)context).startActivityForResult(intent, REQUEST_CODE_ADD);
			   	dismiss();

			}
		});
    }
     
    
    private void shareFromCamera(Context context) {
    	
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            doTakePhotoNative(context);
        }  
    }
    
    protected void doTakePhotoNative(Context context) {
        try {// Launch camera to take photo for selected contact

           File mCurPhotoFile = new File(Environment.getExternalStorageDirectory(),getPhotoFileName());// attach
            if (mCurPhotoFile != null) {
                final Intent intent = getTakePickIntent(mCurPhotoFile);
                cameraFilePath = mCurPhotoFile.getPath();
                ((Activity)context).startActivityForResult(intent, ACTION_SHARE_FROM_CAMERA);
            } 
        } catch (ActivityNotFoundException e) {
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
    
    
    public void showDg(){
    	if(dialog!=null) 
            dialog.show();
    } 

    public Dialog getDialog() {
        return dialog;
    }

    

    public View getContentView() {
        return contentView;
    }

    public void setCancelable(boolean cancelable) {
    	if(dialog!=null){
        dialog.setCancelable(cancelable);
    }
    }

   

    public boolean isShowing() {
    	if(dialog!=null){
        return dialog.isShowing();
    	}else{
    		return false;
    	}
    }

    public void dismiss() {
    	if(dialog!=null){
          dialog.dismiss();
       }
    }
}
