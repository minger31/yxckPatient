package com.patient.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

public class FileService {
	
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
	public static String SAVE_DIR_NAME = "yxck";
	public static final String FILEPATH = getSDPath()
            + File.separator + SAVE_DIR_NAME 
            + File.separator
            + "log";

    public static final String LOG_FILE_EXTENSION = ".txt";

    public static final int LOG_FILE_DELETE_DELAY = 10;

    public static final boolean bOpenSaveLogToFile = true;
    
    private String curDate = "";

    private File curLogFile = null;

    public synchronized void saveLogToFile(String tag, String content) {

        // 创建日志文件夹
        File dest = new File(FileService.FILEPATH);
        if (!dest.exists()) {
            if (!dest.mkdirs()) {
                // sd卡不可用或者没有权限等原因导致无法创建目录的情况，直接返回不写文件
                Log.d("FileService", "sd卡不可用或者没有权限等原因导致无法创建目录的情况，直接返回不写文件");
                return;
            }
        }
        dest = null;

        // 得到当前时间戳
        String date = DateUtil.getCurrentTimeSpecifyFormat(DateUtil.FORMAT_YYYY_MM_DD_HH_MM_SS_SSS);
        // 日志内容加上时间前缀
        content = date + "---" + tag + ":" + content;

        // 取得YYYY-MM-DD形式的日期
        if (!TextUtils.isEmpty(date) && date.length() >= 10) {
            date = date.substring(0, 10);
        }

        if (TextUtils.isEmpty(curDate)) {
            curDate = date;
            curLogFile = new File(FILEPATH, "Log" + date + LOG_FILE_EXTENSION);
        } else {
            if (!curDate.equals(date)) {
                curDate = date;
                curLogFile = new File(FILEPATH, "Log" + date + LOG_FILE_EXTENSION);
            } else if (curLogFile == null) {
                curLogFile = new File(FILEPATH, "Log" + date + LOG_FILE_EXTENSION);
            }
        }

        BufferedWriter out = null;
        try {
        	if (curLogFile == null) {
        		Log.e("FileService", "log file create error! file path = "+ FILEPATH + "Log" + date + LOG_FILE_EXTENSION);
        		return;
        	}
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(curLogFile, true)));
            out.write("\n");
            out.write(content);
        } catch (Exception e) {
            Log.e("FileService", "write log file error", e);
        } finally {
            try {
                if (null != out) {
                    out.close();
                }
            } catch (IOException e) {
                Log.e("FileService", "close BufferedWriter error", e);
            }
        }
    }

}