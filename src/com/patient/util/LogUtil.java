package com.patient.util;

import android.text.TextUtils;
import android.util.Log;

/**
 * <dl>
 * <dt>LogUtil.java</dt>
 * <dd>Description:日志缓存本地文件</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company:CMCC </dd>
 * <dd>CreateDate: 2014-9-12 下午2:20:38</dd>
 * </dl>
 * 
 * @author lihs
 */
public class LogUtil {

	public static final String TAG = "yxck";
	
	public static final boolean bOpenLog = true;
	
	public static final boolean bOpenSaveLogToFile = false;

	private static FileService fileService = null;

	public static int d(String msg) {
		if (bOpenLog) {
			return Log.d(TAG, msg);
		} else {
			return 0;
		}
	}

    public static int d2File(String msg) {
        int ret = d("d2File:" + msg);
        saveLogToFile("", msg);
        return ret;
    }

    public static int d2File(String tag, String msg) {
        int ret = -1;
        if(TextUtils.isEmpty(tag)){
            ret = d(tag ,msg,false);
        }else{
            ret = d(tag ,msg,true);
        }
        saveLogToFile(tag, msg);
        return ret;
    }

	public static int d(String tag, String msg) {
		if (bOpenLog) {
			return Log.d(TAG, tag + ":" +msg);
		} else {
			return 0;
		}
	}
	
	public static int d(String tag, String msg,boolean mark) {
		if (bOpenLog) {
			if(mark){
			  return Log.d(tag,msg);
			}else{
			  return Log.d(TAG, tag + ":" +msg);
			}
		} else {
			return 0;
		}
	}

	public static int i(String msg) {
		if (bOpenLog) {
			return Log.i(TAG, msg);
		} else {
			return 0;
		}
	}

	public static int w(String msg) {
		if (bOpenLog) {
			return Log.w(TAG, msg);
		} else {
			return 0;
		}
	}

	public static int e(String msg) {
		if (bOpenLog) {
		    int ret = Log.e(TAG, msg);
	        saveLogToFile("", msg);
			return ret;
		} else {
			return 0;
		}
	}

	public static int e(Throwable e) {
		if (bOpenLog) {
		    int ret = Log.e(TAG, "", e);
	        saveLogToFile("", e.getLocalizedMessage());
			return ret;
		} else {
			return 0;
		}
	}

	public static int e(String tag, String msg) {
		if (bOpenLog) {
            int ret = Log.e(TAG, tag + ":" + msg);
	        saveLogToFile(tag, msg);
			return ret;
		} else {
			return 0;
		}
	}

	public static int e(String msg, Throwable e) {
		if (bOpenLog) {
            int ret = Log.e(TAG, msg, e);
	        saveLogToFile("", msg + ":" + e.getLocalizedMessage());
			return ret;
		} else {
			return 0;
		}
	}

	 
	private static void saveLogToFile(String tag, String strMessage) {
		if (FileService.bOpenSaveLogToFile) {
		    if (fileService == null) {
		        fileService = new FileService();
		    }
		    fileService.saveLogToFile(tag, strMessage);
		}
	}
}
