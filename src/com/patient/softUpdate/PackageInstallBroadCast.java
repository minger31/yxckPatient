package com.patient.softUpdate;

import java.io.File;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.patient.constant.CommonConstant;
import com.patient.preference.LoginPreference;
import com.patient.preference.LoginPreference.LoginType;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;

@TargetApi(9)
public class PackageInstallBroadCast extends BroadcastReceiver {
	 
	@Override
	public void onReceive(Context arg0, Intent intent) {
		LogUtil.d("包下载完成");
		String action = intent.getAction();
		if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
			// 是家视通下载是，下载队列的ID
			long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
			String apkId =  LoginPreference.getKeyValue(LoginType.CCME_APK_ID, "");
			if (TextUtils.isEmpty(apkId)) {
				apkId = "-1000";
			}
			if (id == Long.parseLong(apkId)) {
				// 清空ID
				LoginPreference.getInstance().setString(LoginType.CCME_APK_ID, "");
				ApkDownloadManager.setDownLoadingFlag(false);
				LogUtil.d("是刚才下载的apkid apkId=" + apkId + "\n" + "id==" + id);
				Cursor cursor = null;
				try {
					Toast.makeText(arg0, "文件已被保存在'"+CommonConstant.downLoadUrl+"'文件夹下", Toast.LENGTH_SHORT).show();
					Query query = new Query();
					query.setFilterById(id);
					DownloadManager downloadManager = (DownloadManager) arg0.getSystemService(Context.DOWNLOAD_SERVICE);
					cursor = downloadManager.query(query);
					int columnCount = cursor.getColumnCount();
					// 这里把所有的列都打印一下，有什么需求，就怎么处理,文件的本地路径就是path
					String path = null;
					while (cursor.moveToNext()) {
						for (int j = 0; j < columnCount; j++) {
							String columnName = cursor.getColumnName(j);
							String string = cursor.getString(j);
							if (columnName.equals("local_uri")) {
								 path = string;
								 LoginPreference.getInstance().setString(LoginType.KEY_DOWN_LOAD_APK_FILE_PATH,path);
								 break;
							}
						}
					}
					
					if (!TextUtils.isEmpty(path)) {
						if (path.startsWith("file")) {
							path = path.substring(path.indexOf(":")+1);
						}
						File file =new File(path);
						if (file.exists()) {
							intent = new Intent(Intent.ACTION_VIEW);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
							arg0.startActivity(intent);
						} 
					}

				} catch (Exception e) {
					LogUtil.e("安装异常：" + e.getLocalizedMessage());
				}finally{
					if (cursor != null) {
						cursor.close();
						cursor = null;
					}
				}
			}
		} else if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
			// 包被卸载时 把APK 文件目录删除
			LogUtil.d("安装包被卸载删除apk 包文件");
			if (CommonUtil.isHasSDCard()) {
				File file = new File(CommonConstant.downLoadUrl);
				LogUtil.d("安装包被卸载删除apk 包文件"+CommonConstant.downLoadUrl);
				if (file.exists()) { // 判断文件是否存在
					if (file.isFile()) { // 判断是否是文件
						file.delete(); // delete()方法  
					} else if (file.isDirectory()) { // 否则如果它是一个目录
						File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
						for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
							arg0.deleteFile(files[i].getAbsolutePath()); // 把每个文件
						}
					}
					file.delete();
				}
			}
		} 
	}
 }
