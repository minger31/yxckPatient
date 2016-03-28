package com.patient.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.patient.PatientApplication;
import com.patient.constant.CommonConstant;
import com.patient.constant.DBConstant;
import com.patient.library.bitmap.ImageWorker;

public class CommonUtil {
	
	
	public static PackageInfo getPackageInfo() {

        PackageInfo info = new PackageInfo();
        try {
            info = PatientApplication
                    .getContext()
                    .getPackageManager()
                    .getPackageInfo(
                    		PatientApplication.getContext().getPackageName(),
                            0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
      //  info.versionName;
        return info;
    }
	
	public static String getUUID() {
		String s = UUID.randomUUID().toString();
		// 去掉“-”符号
		return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18)
				+ s.substring(19, 23) + s.substring(24);
	}
	
	
	public static long getFileSize(String path) {
        long size = 0;
        File file = new File(path);
        if (file != null && file.exists()) {
            size = file.length();
            file = null;
        }
        return size;
    }

	public static String getFileSizeString(double size) {
        String sizeString = "";
        if (size < 1024) {
            sizeString = size + " B";
        } else {
            BigDecimal b = null;
            double cutsize = 0;
            if (size >= 1024 * 1024) {
                cutsize = size / (1024 * 1024);
                b = new BigDecimal(cutsize);
                double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP)
                        .doubleValue();
                sizeString = f1 + " MB";
            } else {
                cutsize = size / (1024);
                b = new BigDecimal(cutsize);
                double f2 = b.setScale(2, BigDecimal.ROUND_HALF_UP)
                        .doubleValue();
                sizeString = f2 + " KB";
            }
        }
        return sizeString;
    }
	
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
	
	 public static String trackValue(Object object) {
	        if (object == null || object.toString().trim().length() == 0
	                || object.toString().trim().equals("null")
	                || object.toString().trim().equals("NULL")) {
	            return "";
	        } else {
	            return object.toString();
	        }
	    }
	
  
	private static long lastClickTime;
	
    /**
     * 隐藏界面输入软键盘
     * 
     * @param activity
     */
    public static void hideSoftInputFromWindow(Context activity) {
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            View view = ((Activity) activity).getCurrentFocus();
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }
        }
    }

    
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 600) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	public static String getSDPath() {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			// 获取跟目录
			return Environment.getExternalStorageDirectory().toString();
		} else {
			return "";
		}
	}

	/**
	 * @Description: 获取下载图片的旋转角度，从网络传输图片，只能用带方法
	 * @param path
	 *            :图片路径
	 * @return 返回图片角度
	 */
	public static int getImageRotationFromUrl(String path) {
		int orientation = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				orientation = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				orientation = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				orientation = 270;
				break;
			default:
				orientation = 0;
			}
		} catch (Exception e) {
			LogUtil.e(e);
			orientation = 0;
		}
		return orientation;
	}

	/**
	 * @author: 
	 * @Title: getImageRotationByPath
	 * @Description: 根据图片id获得其旋转角度
	 * @param ctx
	 * @param path
	 * @return
	 * @date: 2013-10-16 下午12:53:34
	 */
	public static int getImageRotationById(Context ctx, int id) {
		int rotation = 0;
		if (id < 0) {
			return rotation;
		}

		Cursor cursor = null;
		try {
			cursor = ctx.getContentResolver().query(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					new String[] { MediaStore.Images.Media.ORIENTATION },
					MediaStore.Images.Media._ID + " = ?",
					new String[] { "" + id }, null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				rotation = cursor.getInt(0);
			}
		} catch (Exception e) {
			LogUtil.e(e);
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}

		return rotation;
	}

	/**
	 * @author: 
	 * @Title: getIdByPath
	 * @Description: 根据文件路径，查询其id
	 * @param ctx
	 * @param path
	 * @return
	 * @date: 2013-9-23 下午5:26:07
	 */
	public static int[] getIdRotationByPath(Context ctx, String path,
			String thumbnailType) {

		int id = -1;
		int rotation = 0;

		Cursor cursor = null;

		try {
			if (ImageWorker.THUMBNAIL_TYPE_VIDEO.equals(thumbnailType)) {
				cursor = ctx.getContentResolver().query(
						MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
						new String[] { MediaStore.Video.Media._ID },
						MediaStore.Video.Media.DATA + " = ? ",
						new String[] { path }, null);
			} else {
				cursor = ctx.getContentResolver().query(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						new String[] { MediaStore.Images.Media._ID,
								MediaStore.Images.Media.ORIENTATION },
						MediaStore.Images.Media.DATA + " = ? ",
						new String[] { path }, null);
			}

			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				id = cursor.getInt(0);
				if (!ImageWorker.THUMBNAIL_TYPE_VIDEO.equals(thumbnailType)) {
					rotation = cursor.getInt(1);
				}
			} else {
				if (!ImageWorker.THUMBNAIL_TYPE_VIDEO.equals(thumbnailType)) {
					rotation = CommonUtil.getImageRotationFromUrl(path);
				}
			}
		} catch (Exception e) {
			LogUtil.e(e);
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}

		return new int[] { id, rotation };
	}

	public static void showError(Throwable error, String msg) {

		if (error instanceof ConnectTimeoutException
				|| error instanceof SocketTimeoutException
				|| error instanceof SocketException) {
			msg = CommonConstant.TIMEOUT;

		} else if (error instanceof UnknownHostException) {
			msg = CommonConstant.UNKNOWHOST;
		} else if (error instanceof ClientProtocolException) {
			msg = CommonConstant.INTERNET_INTERRUPT;
		}
		showToast(msg);
	}
	/**
	 * @author: 
	 * @Title: getImageRotationByPath
	 * @Description: 根据图片路径获得其旋转角度
	 * @param ctx
	 * @param path
	 * @return
	 * @date: 2013-10-16 下午12:53:34
	 */
	public static int getImageRotationByPath(Context ctx, String path) {
		int rotation = 0;
		if (TextUtils.isEmpty(path)) {
			return rotation;
		}

		Cursor cursor = null;
		try {
			cursor = ctx.getContentResolver().query(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					new String[] { MediaStore.Images.Media.ORIENTATION },
					MediaStore.Images.Media.DATA + " = ?",
					new String[] { "" + path }, null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				rotation = cursor.getInt(0);
			} else {
				rotation = getImageRotationFromUrl(path);
			}
		} catch (Exception e) {
			LogUtil.e(e);
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}

		return rotation;
	}

	public static void scanFileAsync(Context ctx, String filePath) {
		LogUtil.d("CommonUtil scanFileAsync,Intent.ACTION_MEDIA_SCANNER_SCAN_FILE发送广播:"
				+ filePath);
		Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		scanIntent.setData(Uri.fromFile(new File(filePath)));
		ctx.sendBroadcast(scanIntent);

	}

	
	public static boolean isTopActivity(Context context, String currentClssName) {
		
		ActivityManager manager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
		if (runningTaskInfos != null){
			String topActivity = runningTaskInfos.get(0).topActivity.getClassName();
			LogUtil.d("activity", "topActivity + currentClssName = "+topActivity + currentClssName );
			if (currentClssName.equals(topActivity)) {
				return true;
			}
		}
		return false;
	}
	 

	public static void showToast(int alertId, Context context) {
		Toast.makeText(context, context.getString(alertId), Toast.LENGTH_SHORT)
				.show();
	}
	
	public static void showToast(String alertId, Context context) {
		Toast.makeText(context, alertId, Toast.LENGTH_SHORT)
				.show();
	}
	
	public static void showToast(String alertId) {
		Toast.makeText(PatientApplication.getContext(), alertId , Toast.LENGTH_SHORT).show();
	}

	 /**
     * @Title: playVideo
     * @Description: 调用系统播放器，播放视频文件
     * @param ctx
     * @param videoPath
     * @date: 2013-9-24 下午3:46:05
     */
    public static void playVideo(Context ctx, String videoPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String strend = "";
        if (videoPath.toLowerCase().endsWith(".mp4")) {
            strend = "mp4";
        } else if (videoPath.toLowerCase().endsWith(".mpg4")) {
            strend = "mp4";
        } else if (videoPath.toLowerCase().endsWith(".3gp")) {
            strend = "3gpp";
        } else if (videoPath.toLowerCase().endsWith(".mov")) {
            strend = "quicktime";
        } else if (videoPath.toLowerCase().endsWith(".wmv")) {
            strend = "wmv";
        } else if (videoPath.toLowerCase().endsWith(".avi")) {
            strend = "x-msvideo";
        } else if (videoPath.toLowerCase().endsWith(".mpe")) {
            strend = "mpeg";
        } else if (videoPath.toLowerCase().endsWith(".mpeg")) {
            strend = "mpeg";
        } else if (videoPath.toLowerCase().endsWith(".mpg")) {
            strend = "mpeg";
        }

        intent.setDataAndType(Uri.fromFile(new File(videoPath)), "video/"
                + strend);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            ctx.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(ctx, "找不到相关应用，打开文件失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @Title: playAudio
     * @Description: 调用系统播放器，播放音频文件
     * @param ctx
     * @param audioPath
     * @date: 2013-9-30 上午9:51:42
     */
    public static void playAudio(Context ctx, String audioPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String strend = "mp3";
        if (audioPath.toLowerCase().endsWith(".mp2")) {
            strend = "x-mpeg";
        } else if (audioPath.toLowerCase().endsWith(".mp3")) {
            strend = "x-mpeg";
        } else if (audioPath.toLowerCase().endsWith(".ogg")) {
            strend = "ogg";
        } else if (audioPath.toLowerCase().endsWith(".wav")) {
            strend = "x-wav";
        } else if (audioPath.toLowerCase().endsWith(".wma")) {
            strend = "x-ms-wma";
        } else if (audioPath.toLowerCase().endsWith(".wmv")) {
            strend = "x-ms-wmv";
        }

        intent.setDataAndType(Uri.fromFile(new File(audioPath)), "audio/"
                + strend);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            ctx.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(ctx, "找不到相关应用，打开文件失败", Toast.LENGTH_SHORT).show();
        }
    }

	
	private static Point deviceSize = null;

	@SuppressLint("NewApi")
	public static Point getDeviceSize(Context context) {
		if (deviceSize == null) {
			deviceSize = new Point(0, 0);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
				((WindowManager) context
						.getSystemService(Context.WINDOW_SERVICE))
						.getDefaultDisplay().getSize(deviceSize);
			} else {
				Display display = ((WindowManager) context
						.getSystemService(Context.WINDOW_SERVICE))
						.getDefaultDisplay();
				deviceSize.x = display.getWidth();
				deviceSize.y = display.getHeight();
				display = null;
			}
		}
		return deviceSize;
	}

	public static final int ROLE_LEADER = 1;
	public static final int ROLE_ACADMIC = 2;
	public static final int ROLE_DOCTOR = 3;

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	private static final String TAG = "AndroidUtil";

	/**
	 * @author: 
	 * @Title: getNumCores
	 * @Description: 获取cpu核心数
	 * @return
	 * @date: 2014-1-21 下午1:21:26
	 */
	public static int getNumCores() {
		// Private Class to display only CPU devices in the directory listing
		class CpuFilter implements FileFilter {
			@Override
			public boolean accept(File pathname) {
				// Check if filename is "cpu", followed by a single digit number
				if (Pattern.matches("cpu[0-9]", pathname.getName())) {
					return true;
				}
				return false;
			}
		}

		try {
			// Get directory containing CPU info
			File dir = new File("/sys/devices/system/cpu/");
			// Filter to only list the devices we care about
			File[] files = dir.listFiles(new CpuFilter());
			Log.d(TAG, "CPU Count: " + files.length);
			// Return the number of cores (virtual CPU devices)
			return files.length;
		} catch (Exception e) {
			// Print exception
			Log.d(TAG, "CPU Count: Failed.");
			e.printStackTrace();
			// Default to return 1 core
			return 1;
		}
	}

	/**
	 * @author: 
	 * @Title: getCpuFrequence
	 * @Description: 获取cpu频率，单位：KHz
	 * @return
	 * @date: 2014-1-21 下午3:02:30
	 */
	public static String getCpuFrequence() {
		String result = "0";
		ProcessBuilder cmd;
		try {
			String[] args = { "/system/bin/cat",
					"/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" };
			cmd = new ProcessBuilder(args);

			Process process = cmd.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			result = reader.readLine();
		} catch (IOException ex) {
			LogUtil.e(ex);
		}
		if (TextUtils.isEmpty(result)) {
			result = "0";
		}
		return result;
	}

	/**
	 * @author: 
	 * @Title: getMinCpuFreq
	 * @Description: 获取cpu最小频率，单位：KHz
	 * @return
	 * @date: 2014-1-21 下午3:03:36
	 */
	public static String getMinCpuFreq() {
		String result = "0";
		ProcessBuilder cmd;
		try {
			String[] args = { "/system/bin/cat",
					"/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq" };
			cmd = new ProcessBuilder(args);
			Process process = cmd.start();
			InputStream in = process.getInputStream();
			byte[] re = new byte[24];
			while (in.read(re) != -1) {
				result = result + new String(re);
			}
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			result = "0";
		}
		return result.trim();
	}

	/**
	 * @author: 
	 * @Title: getCurCpuFreq
	 * @Description: 获取cpu当前频率，单位：KHz
	 * @return
	 * @date: 2014-1-21 下午3:03:59
	 */
	public static String getCurCpuFreq() {
		String result = "0";
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(
					"/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
			br = new BufferedReader(fr);
			String text = br.readLine();
			result = text.trim();
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.toString());
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (fr != null) {
					fr.close();
				}
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
		}
		return result;
	}

	/**
	 * @author: 
	 * @Title: getFreeMemory
	 * @Description: 获取可用运存大小，单位：M
	 * @param context
	 * @return
	 * @date: 2014-1-21 下午3:04:53
	 */
	public static float getFreeMemory(Context context) {
		// 获取android当前可用内存大小
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		// mi.availMem; 当前系统的可用内存
		return mi.availMem / (1024 * 1024.0f);
	}

	/**
	 * @author: 
	 * @Title: getTotalMemory
	 * @Description: 获取系统总内存大小，单位：M
	 * @param context
	 * @return
	 * @date: 2014-1-21 下午3:06:58
	 */
	public static float getTotalMemory(Context context) {
		String str1 = "/proc/meminfo";// 系统内存信息文件
		String str2;
		String[] arrayOfString;
		long initial_memory = 0;
		FileReader localFileReader = null;
		BufferedReader localBufferedReader = null;
		try {
			localFileReader = new FileReader(str1);
			localBufferedReader = new BufferedReader(localFileReader, 8192);
			// 读取meminfo第一行，系统总内存大小
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			for (String num : arrayOfString) {
				Log.i(str2, num + "\t");
			}
			// 获得系统总内存，单位是KB，乘以1024转换为Byte
			initial_memory = Integer.valueOf(arrayOfString[1]).intValue();
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		} finally {
			try {
				if (localBufferedReader != null) {
					localBufferedReader.close();
				}
				if (localFileReader != null) {
					localFileReader.close();
				}
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
		}
		return initial_memory / 1024.0f;
	}

	public static boolean availableExStorageMemory() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {

			/** 获取存储卡路径 */
			File sdcardDir = Environment.getExternalStorageDirectory();
			/** StatFs 看文件系统空间使用情况 */
			StatFs statFs = new StatFs(sdcardDir.getPath());
			/** Block 的 size */
			long blockSize = statFs.getBlockSize();
			/** 总 Block 数量 */
			int totalBlocks = statFs.getBlockCount();
			/** 已使用的 Block 数量 */
			int availableBlocks = statFs.getAvailableBlocks();

			if (availableBlocks * blockSize > 10 * 1024 * 1024) {
				return true;
			}
		}
		return false;
	}

	public static void killAll(Context context) {

		//获取一个ActivityManager 对象
		        ActivityManager activityManager = (ActivityManager) context
		                         .getSystemService(Context.ACTIVITY_SERVICE);
		//获取系统中所有正在运行的进程
		       List<RunningAppProcessInfo> appProcessInfos = activityManager
		                         .getRunningAppProcesses();
	//获取当前activity所在的进程
		      String currentProcess=context.getApplicationInfo().processName;
		//对系统中所有正在运行的进程进行迭代，如果进程名不是当前进程，则Kill掉
		       for (RunningAppProcessInfo appProcessInfo : appProcessInfos) {
		        String processName=appProcessInfo.processName;
		        if (processName.equals(currentProcess)) {
		                 activityManager.killBackgroundProcesses(processName);
		        }
		       }}
	
	public static String getSex(String sex){
		if ("男".equals(sex)) {
			return "1";
		}else if("女".equals(sex)){
			return "2";
		}
		return "1";
	}

	/**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			left = 0;
			bottom = width;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}
	
	
	/** 
	 * @Title: getGraysRoundedCornerBitmap 
	 * @Description: 获取灰色圆角图片
	 */
	public static Bitmap getGraysRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		

		final Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}
	
	
public static int getPhoneValue(Context context){
		
		int phonePerformValue = -1;
		try {
            // cpu核心数
            int cpuCoreCnt = CommonUtil.getNumCores();
            // cpu频率（单位MHz）
            float cpuFreq = Float.parseFloat(CommonUtil.getCpuFrequence())
                    / 1000.0f;
            // 总RAM（单位M）
            float totalRam = CommonUtil.getTotalMemory(context);
            // 可用RAM（单位M）
            float freeRam = CommonUtil.getFreeMemory(context);
            if (cpuCoreCnt < 1 || cpuFreq <= 0 || totalRam <= 0 || freeRam <= 0) {
                // 性能数据未成功取到，做默认处理
                phonePerformValue = -1;
            } else {
                phonePerformValue = (int)getPhonePerformanceValue(cpuCoreCnt, cpuFreq, totalRam, freeRam);
            }
		} catch (Exception e) {
		    LogUtil.e(e);
		    phonePerformValue = -1;
		}
		
		return phonePerformValue;
	}
	
	 private static float getPhonePerformanceValue(int cpuCoreCnt, float cpuFreq,
	            float totalRam, float freeRam) {
	        return (cpuCoreCnt * cpuFreq) * 0.5f + totalRam * 0.05f + freeRam
	                * 0.45f;
	 }
	 
	 
	 /**
		 * 禁止Edittext弹出软件盘，光标依然正常显示。
		 */
		public static void disableShowSoftInput(EditText editText) {
			
			if (android.os.Build.VERSION.SDK_INT <= 10) {
				editText.setInputType(InputType.TYPE_NULL);
			} else {
				Class<EditText> cls = EditText.class;
				Method method;
				try {
					method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
					method.setAccessible(true);
					method.invoke(editText, false);
				} catch (Exception e) {
				}

				try {
					method = cls.getMethod("setSoftInputShownOnFocus",
							boolean.class);
					method.setAccessible(true);
					method.invoke(editText, false);
				} catch (Exception e) {
				}
			}
		}
		
		
		   public static String getDBPathName(String loginUserId) {
		        LogUtil.d("CommonUtil getDBPathName");
		        if (TextUtils.isEmpty(loginUserId)) {
		            return DBConstant.SQLITE_FILE_NAME
		                    + DBConstant.SQLITE_FILE_NAME_EXTENSION;
		        } else {
		            return DBConstant.SQLITE_FILE_NAME
		                    + DBConstant.SQLITE_FILE_CONNECTOR + loginUserId
		                    + DBConstant.SQLITE_FILE_NAME_EXTENSION;
		        }
		    }

		    /**
		     * @author: lihs
		     * @Title: getDdPathPostFix
		     * @Description:获取数据库路径名（区别内存中还是sd卡中）
		     * @param context
		     * @param loginUserId
		     * @return
		     * @date: 2013-7-31 下午6:07:18
		     */
		    public static String getDdPathPostFix(Context context) {
		        LogUtil.d("CommonUtil getDdPathPostFix");
//		        String mark = NetPhoneApplication.getPreference().getKeyValue(
//		                PrefType.LOGIN_NUMBER_CHANGE, "0");
//		        if ("1".equals(mark)) {
//		            // 切换账号的场景下，清空数据存储方式
//		            NetPhoneApplication.getPreference().setKeyValue(
//		                    PrefType.USER_KEY_DBSRC_STORE_TYPE, "");
//		        }
		        String sqliteFilePath = "";
		        if (CommonUtil.isHasSDCard()) {
		        	
		        	// SD 卡存在且大于20M的空间时
		            long sizes = getAvailaleSize();
		            if (sizes >= 20) {
		                if (CommonUtil.isUseSDCard(context)) {
		                    LogUtil.d("数据库保存在sd卡场景");
		                    sqliteFilePath = DBConstant.SQLITE_FILE_SDCARD_FOLDER;
		                } else {
		                    LogUtil.d("数据库保存在内存场景");
		                    sqliteFilePath = DBConstant.SQLITE_FILE_ROM_FOLDER;
		                }
		            } else {
		                LogUtil.d("sd卡空间不足20M,数据库放在内存中");
		                sqliteFilePath = DBConstant.SQLITE_FILE_ROM_FOLDER;
		            }
		        } else {
		            LogUtil.d("sd卡不存在");
		            sqliteFilePath = DBConstant.SQLITE_FILE_ROM_FOLDER;
		        }
		        return sqliteFilePath;
		    }

		    private static long getAvailaleSize() {
		        // 获取sd卡可用空间
		        File path = Environment.getExternalStorageDirectory(); // 取得sdcard文件路径
		        StatFs stat = new StatFs(path.getPath());
		        long blockSize = stat.getBlockSize();
		        long availableBlocks = stat.getAvailableBlocks();
		        long size = ((availableBlocks * blockSize) / 1024 / 1024);
		        LogUtil.d("当前sd卡可用空间为：" + size + "M");
		        return size;
		    }

		    /**
		     * 应用数据是否保存在SD卡
		     * 
		     * @param context
		     * @return
		     */
		    public static boolean isUseSDCard(Context context) {

//		        String dbStoredType = NetPhoneApplication.getPreference().getKeyValue(
//		                PrefType.USER_KEY_DBSRC_STORE_TYPE,
//		                DBConstant.DBSRC_STORE_TYPE_SDCARD);
//
//		        LogUtil.d("isUseSDCard dbStoredType :" + dbStoredType);
//
//		        if (!DBConstant.DBSRC_STORE_TYPE_ROM.equals(dbStoredType)) {
//		            // 首次登录或者以前数据库文件保存在sd卡上的场合，则继续保存在sd卡
//		            return true;
//		        } else {
//		            // 以前数据库文件保存在手机内存的场合，则继续保存在手机内存中
//		            return false;
//		        }
		    	
		    	return true;
		    }

		    /**
		     * 判断外置存储是否可用
		     * 
		     * @return
		     */
		    public static boolean checkExternalStorage(Context context, boolean showTip) {
		        if (isHasSDCard()) {
		            if (getAvailaleSize() > 20) {
		                return true;
		            } else {
		                // 小于20M可用空间，提示用户清理
		                if (showTip) {
		                    Toast.makeText(context, "存储空间不足，请及时清理", Toast.LENGTH_SHORT)
		                            .show();
		                }
		            }
		        } else {
		            if (showTip) {
		                Toast.makeText(context, "SD卡暂不可用，请检查SD卡", Toast.LENGTH_SHORT)
		                        .show();
		            }
		        }
		        return false;
		    }

		    /**
		     * 判断SD卡是否存在
		     * 
		     * @return
		     */
		    public static boolean isHasSDCard() {
		        return android.os.Environment.getExternalStorageState().equals(
		                android.os.Environment.MEDIA_MOUNTED);
		    }

		    /**
		     * 复制数据库文件到SD卡
		     * 
		     * @throws IOException
		     */
		    public static void copySqlite2SDCard(Context context, String fileName)
		            throws Exception {
		        LogUtil.d("CommonUtil.copySqlite2SDCard() begin.");
		        LogUtil.d2File("CommonUtil.copySqlite2SDCard() begin.");
		        FileOutputStream fos = null;
		        try {
		            checkouForder(DBConstant.SQLITE_FILE_SDCARD_FOLDER);

		            File filedb = new File(DBConstant.SQLITE_FILE_SDCARD_FOLDER,
		                    fileName);

		            String newPath = DBConstant.SQLITE_FILE_SDCARD_FOLDER + fileName;
		            if (!filedb.exists()) {
		                checkouForder(DBConstant.SQLITE_FILE_SDCARD_FOLDER);
		                LogUtil.d("将数据库文件复制到SD卡:" + newPath);
		                filedb.createNewFile();
		                byte[] b = new byte[context.getAssets()
		                        .open(DBConstant.SQLITE_FILE_NAME_DEFAULT).available()];
		                context.getAssets().open(DBConstant.SQLITE_FILE_NAME_DEFAULT)
		                        .read(b);
		                fos = new FileOutputStream(filedb);
		                fos.write(b);
		            } else {
		                LogUtil.d("SD卡上存在数据库文件:" + newPath);
		            }
//		            NetPhoneApplication.getPreference().setKeyValue(
//		                    PrefType.USER_KEY_DBSRC_STORE_TYPE,
//		                    DBConstant.DBSRC_STORE_TYPE_SDCARD);

		        } catch (IOException e) {
		            LogUtil.e("CommonUtil.copySqlite2SDCard() ioexception", e);
		            LogUtil.d2File("CommonUtil.copySqlite2SDCard() ioexception:"
		                    + e.getMessage());
		            throw e;
		        } finally {
		            if (fos != null) {
		                try {
		                    fos.close();
		                    fos = null;
		                } catch (IOException e) {
		                    LogUtil.e("fos.close() ioexception", e);
		                }
		            }
		        }
		        LogUtil.d("CommonUtil.copySqlite2SDCard() end.");
		        LogUtil.d2File("CommonUtil.copySqlite2SDCard() end.");
		    }

		    private static void checkouForder(String forderDir) {
		        File forderSqlite = new File(forderDir);
		        if (!forderSqlite.exists()) {
		            boolean mark = forderSqlite.mkdirs();
		            LogUtil.d("新建目录:" + forderDir + mark);
		        }
		    }

		    /**
		     * 复制数据库文件到手机内存
		     * 
		     * @throws IOException
		     */
		    public static void copySqlite2Rom(Context context, String fileName)
		            throws Exception {
		        LogUtil.d("CommonUtil.copySqlite2Rom() begin.");
		        LogUtil.d2File("CommonUtil.copySqlite2Rom() begin.");

		        FileOutputStream fos = null;
		        try {
		            String newPath = DBConstant.SQLITE_FILE_ROM_FOLDER + fileName;
		            File filedb = new File(newPath);
		            if (filedb.exists()) {
		                LogUtil.d("手机内存中存在数据库文件:" + newPath);
		            } else {
		                LogUtil.d("复制数据库文件到手机内存:" + newPath);
		                fos = context.openFileOutput(fileName, Context.MODE_APPEND);
		                byte[] b = new byte[context.getAssets()
		                        .open(DBConstant.SQLITE_FILE_NAME_DEFAULT).available()];
		                context.getAssets().open(DBConstant.SQLITE_FILE_NAME_DEFAULT)
		                        .read(b);
		                fos.write(b);
		            }
//		            NetPhoneApplication.getPreference().setKeyValue(
//		                    PrefType.USER_KEY_DBSRC_STORE_TYPE,
//		                    DBConstant.DBSRC_STORE_TYPE_ROM);

		        } catch (IOException e) {
		            LogUtil.e("CommonUtil.copySqlite2Rom() ioexception", e);
		            LogUtil.d2File("CommonUtil.copySqlite2Rom() ioexception:"
		                    + e.getMessage());
		            throw e;
		        } finally {
		            if (fos != null) {
		                try {
		                    fos.close();
		                    fos = null;
		                } catch (IOException e) {
		                    LogUtil.e("fos.close() ioexception", e);
		                }
		            }
		        }
		        LogUtil.d("CommonUtil.copySqlite2Rom() end.");
		        LogUtil.d2File("CommonUtil.copySqlite2Rom() end.");
		    }

}
