/**   
* @Title: ApkDownloadManager.java 
* @Package com.channelsoft.netphone.ui.activity 
* @author miaolikui   
* @date 2013-9-22 下午5:28:10 
* @version V1.0   
*/ 
package com.patient.softUpdate;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Toast;

import com.patient.constant.CommonConstant;
import com.patient.preference.LoginPreference;
import com.patient.preference.LoginPreference.LoginType;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;

 
public class ApkDownloadManager {

	// true  正在升级；false 可以升级
	public static boolean isDownLoading = false;
	
	private Context mContext = null;
	// 下载地址
	private String downLoadAddress;
	
	// 服务端版本号
	private String versionNo;
	
	
	public ApkDownloadManager(Context context){
		mContext = context;
	}
	
	public static void setDownLoadingFlag(boolean downloading){
		isDownLoading = downloading;
	}
	
	public static boolean isDownLoading(){
		return isDownLoading;
	}
	
	public void setDownLoadUrl(String downloadUrl){
		downLoadAddress = downloadUrl;
	}
	
	public void setFilename(String filename){
		versionNo = filename;
	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	public void downLoadFile(String url, String versionName) {

		isDownLoading = true;
		DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
		Uri uri = Uri.parse(url);
		Request request = new Request(uri);
		// 设置允许使用的网络类型，这里是移动网络和wifi都可以
		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE| DownloadManager.Request.NETWORK_WIFI);
		// 显示下载界面
		request.setVisibleInDownloadsUi(true);
		/*
		 * 设置下载后文件存放的位置,如果sdcard不可用，那么设置这个将报错，因此最好不设置;如果sdcard可用，下载后的文件
		 * 在/mnt/sdcard/Android/data/packageName/files目录下面
		 * 如果sdcard不可用,设置了下面这个将报错，不设置，下载后的文件在/cache这个 目录下面
		 */
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// 3.0 以上才有这个属性设置  TODO 11..
			request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		}
		File file = new File(CommonConstant.downLoadUrl);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(CommonConstant.downLoadUrl, CommonConstant.SOFT_NAME + versionName + ".apk");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		request.setDestinationUri(Uri.fromFile(file));
		long id = downloadManager.enqueue(request);
		LogUtil.d("下载的id:" + id);
		LoginPreference.getInstance().setString(LoginType.CCME_APK_ID, "" + id);
	}
	
	/****
	 * 
	* @Title: isInstallOrLoadApk 
	* @Description: 
	* @param @param showDownloadDailog 在APK没有下载的情况下，是否需要先弹出一个对话框，
	* 由用户决定是否开始下载
	* @param @return    设定文件 
	* @return boolean  
	* @throws
	 */
	public boolean isInstallOrLoadApk(boolean showDownloadDailog) {

		if (isDownLoading) {
			// 如果正在下载就提示正在下载中
			Toast.makeText(mContext, "软件正在下载升级中...", Toast.LENGTH_SHORT).show();
			return true;
		}
		
		/**
		 * 这里要判断是否本地文件已经存在，如果存在判断 文件是否是最新版本号，如果不是最新版本号则进行下载安装 如果是是最新版本号则直接安装
		 */
		String path = LoginPreference.getKeyValue(LoginType.KEY_DOWN_LOAD_APK_FILE_PATH, "");
		String fileApk = "";
		LogUtil.d("文件路径：" + path);
		if (!TextUtils.isEmpty(path)) {
			fileApk = path.substring(path.lastIndexOf("/") + 1);
		}
		if ((CommonConstant.SOFT_NAME + versionNo + ".apk").equals(fileApk)) {
			// 如果服务端版本号和本地保存的版本号相同
			if (!TextUtils.isEmpty(path)) {
				if (path.startsWith("file")) {
					path = path.substring(path.indexOf(":") + 1);
				}
				File file = new File(path);
				if (file.exists()) {
					// 如果本地文件存在则直接安装
					LogUtil.d("文件已存在进行安装");
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setDataAndType(Uri.fromFile(new File(path)),
							"application/vnd.android.package-archive");
					mContext.startActivity(intent);
					return true;
				} else {
					// 如果不存在  手动删除的时候 继续下载
					if (!showDownloadDailog) {

						LogUtil.d("文件不存在，进行下载");
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
							downLoadFile(downLoadAddress, versionNo);
						} else {
							webDownLoadApk(downLoadAddress);
						}
						return true;
					} else {
						return false;
					}
				}
			}
		} else {
			if (CommonUtil.isHasSDCard()) {
				// 删除之前的apk 文件
				File file = new File(CommonConstant.downLoadUrl);
				LogUtil.d("安装包被卸载删除apk 包文件" + CommonConstant.downLoadUrl);
				if (file.exists()) { // 判断文件是否存在
					if (file.isFile()) { // 判断是否是文件
						file.delete(); // delete()方法
					} else if (file.isDirectory()) { // 否则如果它是一个目录
						File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
						if (files != null && files.length > 0) {
							for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
								mContext.deleteFile(files[i].getName()); // 把每个文件删除
							}
						}
					}
					file.delete();
				}
			}
			if (!showDownloadDailog) {
				// 如果没有在升级中 在进行升级
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					// 进行下载
					downLoadFile(downLoadAddress, versionNo);
				} else {
					webDownLoadApk(downLoadAddress);
				}
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	public void webDownLoadApk(String apkUrl){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(apkUrl)); 
		mContext.startActivity(intent);
	}
	
	/**
	 * @author: lihs
	 * @Title: isDownLoadPackage
	 * @Description: 服务端版本号是否大于本地版本号
	 * @param localVersion
	 *            本地版本号
	 * @param serviceVersion
	 *            服务端版本号
	 * @return true 服务端>本地
	 * @date: 2013-8-2 下午4:16:01
	 */
	public static boolean isDownLoadPackage(String localVersion, String serviceVersion) {

		if(TextUtils.isEmpty(serviceVersion)){
			return false;
		}
		
		String localArray[] = localVersion.split("\\.");
		String serviceArray[] = serviceVersion.split("\\.");
		for (int i = 0; i < localArray.length; i++) {
			if (Integer.parseInt(serviceArray[i]) > Integer
					.parseInt(localArray[i])) {
				return true;
			}
		}
		return false;
	}
	
	/** 
	 * @author: Administrator
	 * @Title: downLoadPackage  本地版本号比服务端版本号大
	 * @Description:  
	 * @param localVersion
	 * @param serviceVersion
	 * @return 
	 * @date: 2014-2-22 上午11:50:36
	 */
	public static boolean downLoadPackage(String localVersion, String serviceVersion) {

		if(TextUtils.isEmpty(serviceVersion)){
			return false;
		}
		String localArray[] = localVersion.split("\\.");
		String serviceArray[] = serviceVersion.split("\\.");
		for (int i = 0; i < localArray.length; i++) {
			if (Integer.parseInt(localArray[i]) > Integer
					.parseInt(serviceArray[i])) {
				return true;
			}
		}
		return false;
	}
}
