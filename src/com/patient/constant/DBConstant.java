package com.patient.constant;

import android.os.Environment;


/**
 * <dl>
 * <dt>DBConstant.java</dt>
 * <dd>Description:数据库访问常量</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company: </dd>
 * <dd>CreateDate: 2015-1-2 下午1:12:47</dd>
 * </dl>
 * 
 * @author lihs
 */
public class DBConstant {

    /**
     * 应用内所有DB日志标签
     */
    public static final String NETPHONE_DB_TAG = "YXCK_DB";

    public static final String SD_CARD_ROOT = getSDPath();

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

    // 数据库文件存放方式(1:手机内存,2:sd卡)
    public static String KEY_DBSRC_STORE_TYPE = "key_dbsrc_stored_type";
    public static String DBSRC_STORE_TYPE_ROM = "1";
    public static String DBSRC_STORE_TYPE_SDCARD = "2";

    /** 数据库名与佰库帐号之间的连接符 */
    public static final String SQLITE_FILE_CONNECTOR = "_";
    /** sd卡上数据库文件存放目录 */
    public static final String SQLITE_FILE_SDCARD_FOLDER = SD_CARD_ROOT+ "/yxck/sqlite/";
    /** 手机内存中数据库文件存放目录 */
    public static final String SQLITE_FILE_ROM_FOLDER = "data/data/com.yxck.patient/files/";
    
    /** 数据库文件名 */
    public static final String SQLITE_FILE_NAME = "yxck_patient";
    /** 数据库文件扩展名 */
    public static final String SQLITE_FILE_NAME_EXTENSION = ".sqlite";
    /** 数据库文件名（默认） */
    public static final String SQLITE_FILE_NAME_DEFAULT = SQLITE_FILE_NAME + SQLITE_FILE_NAME_EXTENSION;
    /** sd卡上数据库文件 */
    public static final String SQLITE_FILE_SDCARD_PATH = SQLITE_FILE_SDCARD_FOLDER
            + SQLITE_FILE_NAME_DEFAULT;
    /** 手机内存中数据库文件 */
    public static final String SQLITE_FILE_ROM_PATH = SQLITE_FILE_ROM_FOLDER
            + SQLITE_FILE_NAME_DEFAULT;
}
