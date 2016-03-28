package com.patient.db;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.patient.PatientApplication;
import com.patient.constant.DBConstant;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;

/**
 * <dl>
 * <dt>DBHelper.java</dt>
 * <dd>Description:缓存一些  省市区的数据 ，共联想搜索使用 防止 多次调用网络数据耗费流量 和执行速度过慢的问题
 * 1. 第一次显示可能会比较慢 ，但是一旦取到数据就会很快的数据</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company: </dd>
 * <dd>CreateDate: 2014-11-21 下午3:49:48</dd>
 * </dl>
 * 
 * @author lihs
 */
public class DBHelper extends SQLiteOpenHelper {
    
	public String DB_NAME = "";
	
	private String sqlite_data_filepath = "";

	public static final int DATABASE_VERSION = 1;

	private static SQLiteDatabase myDataBase = null;
    public static DBHelper instance = null;

	private static String loginUserId = "";

	private Context mContext;
	
	private final int POINT_VERSION = 1;
	
	private static int copyCount = 0;//数据库文件损坏case下,拷贝次数
	private static int maxCopyCount = 3;//数据库文件损坏case下，最大拷贝次数

	public DBHelper(Context context, String _loginUserId) {
		
		super(context, CommonUtil.getDBPathName(_loginUserId), null, DATABASE_VERSION);
		setUserInfo(_loginUserId);
		mContext = context;
		loginUserId = _loginUserId;
		copyCount = 0;
		LogUtil.d("DBHelper  instanse");
		LogUtil.d2File("DBHelper  instanse");
	}
	
	public static DBHelper getInstance(String _loginUserId){
		if(instance == null || !loginUserId.equals(_loginUserId)){
			synchronized (DBHelper.class) {
				if(instance == null || !loginUserId.equals(_loginUserId)){
					instance = null;
					LogUtil.d("DBHelper  getInstance : create instance");
					LogUtil.d2File("DBHelper  getInstance : create instance");
					if(!loginUserId.equals(_loginUserId)){
						if(myDataBase != null && myDataBase.isOpen()){
							LogUtil.d2File("DBHelper  close other instance");
							myDataBase.releaseReference();
							myDataBase.close();
							myDataBase = null;
						}
					}
					instance = new DBHelper(PatientApplication.getContext(),_loginUserId);	
				}else{
					LogUtil.d2File("DBHelper  getInstance : return other instance");
					LogUtil.d("DBHelper  getInstance : return other instance");
				}
			}
		}else{
			LogUtil.d2File("DBHelper  getInstance : return other instance");
			LogUtil.d("DBHelper  getInstance : return other instance");
		}
		return instance;
	}
	
	/** 
	 * @author: lihs
	 * @Title: getdatabase 
	 * @Description:获取dataBase 实例
	 * @return 
	 * @date: 2013-8-1 上午10:03:30
	 */
	public SQLiteDatabase getdatabase() {
		try {
			if (myDataBase == null) {
				LogUtil.d2File("SQLiteDatabase getdatabase(),myDataBase == null 的场景");
				openDataBase();
			} else {
				if (!myDataBase.isOpen()) {
					LogUtil.d2File("SQLiteDatabase getdatabase(),myDataBase关闭的场景");
					openDataBase();
				}else{
					LogUtil.d2File("SQLiteDatabase getdatabase(),myDataBase打开的场景，直接返回db对象");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return myDataBase;
	}
	
	public void setUserInfo(String loginUserId){
		DB_NAME = CommonUtil.getDBPathName(loginUserId);
		sqlite_data_filepath = CommonUtil.getDdPathPostFix(mContext);
		LogUtil.d("sqlite_date_path is : " + sqlite_data_filepath,"DB_NAME is :"+DB_NAME);
	}
	
	public SQLiteDatabase getOpenDatabase(){
		// 数据丢失的场景下使用
		openDataBase();
		return myDataBase;
	}

	/** 
	 * @author: lihs
	 * @Title: openDataBase 
	 * @Description:打开数据库
	 * @throws SQLException 
	 * @date: 2013-8-1 上午10:03:43
	 */
	public void openDataBase() throws SQLException {
		LogUtil.d2File("打开数据库  openDataBase start ");
		
		synchronized(PatientApplication.sqliteLock){
			try {
				File filepath = new File(sqlite_data_filepath);
				if (!filepath.exists()) {
					 filepath.mkdirs();
				}
				LogUtil.d2File("openDataBase getWritableDatabase,获取数据库锁 ");
				getWritableDatabase();
			
				LogUtil.d2File("得到的数据库路径为：" + sqlite_data_filepath);
				if (!TextUtils.isEmpty(sqlite_data_filepath)) {
						File databases_dir = new File(sqlite_data_filepath +  DB_NAME);
						if (!databases_dir.exists()) {
							createDB();
						}
						if (null == myDataBase || !myDataBase.isOpen()) {
							myDataBase = SQLiteDatabase.openDatabase(sqlite_data_filepath
									+ DB_NAME, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
						}	
				}
				LogUtil.d2File("得到的数据库,myDataBase:" + myDataBase);
				
			}catch(SQLiteDatabaseCorruptException e){
				LogUtil.d2File("打开数据库   openDataBase  异常："+e.toString());
				super.close();
				// 3.14号在中信手机上遇到一个问题，数据库文件损毁，不能打开》》》  处理逻辑：删除损坏数据库，重新拷贝一份【丢失数据】        >>>>>最大拷贝次数为3
				if (copyCount >= maxCopyCount) {
					LogUtil.d2File("打开数据库   openDataBase  SQLiteDatabaseCorruptException异常：拷贝数据库次数大于3");
//					handler.postDelayed(new Runnable() {
//						@Override
//						public void run() {
//							Toast.makeText(mContext, "数据库文件损毁，请检查存储设备", Toast.LENGTH_LONG).show();
//						}
//					}, 10000);
				}else if(!CommonUtil.isFastDoubleClick()){
					LogUtil.d2File("打开数据库   openDataBase  SQLiteDatabaseCorruptException异常：开始重新拷贝数据库:"+copyCount);
					if (myDataBase != null) {
						myDataBase.close();
						myDataBase = null;
					}
					++copyCount;
					copySqlite();
				}
			}catch (Exception ex) {
				LogUtil.d2File("打开数据库   openDataBase  异常："+ex.toString());
			}finally{
				super.close();
			}
	    }
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		LogUtil.d("onCreate start");
		copySqlite();
		LogUtil.d("onCreate end");
	}
	
	private void copySqlite() {
		File file = new File(sqlite_data_filepath);
        if (file.exists()) {
            for (File files : file.listFiles()) {
            	LogUtil.d("DB_NAME:"+DB_NAME);
                String fileName = files.getName();
                if(TextUtils.isEmpty(loginUserId)){ loginUserId = "";}
                if (files.isFile() && fileName.endsWith(loginUserId+".sqlite")) {
                    boolean deleteSuccess = files.delete();
        			LogUtil.d("数据库更新前,删除【" + sqlite_data_filepath + DB_NAME + "】是否成功:"
        					+ deleteSuccess);
                }
            }
        }
		createDB();
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		LogUtil.d2File("DBHelper.onUpgrade start");
		if (newVersion != oldVersion) {
			// 升级数据库
			upGradeDB(oldVersion);

			LogUtil.d("DBHelper.onUpgrade end");
		}
	}

	private void upGradeDB(int _version) {
        /*11.21数据库升级逻辑：
		* 1.版本号为23或以下，数据库升级策略为,从assets直接拷贝
		* 2.版本号为24或以上，数据库升级测略为，逐步升级策略（比如从24》25，再从25》26）
		* 3.目前数据库升级，比如简单的数据库字段的增删查改，直接使用sql操作【遇到特殊场景，需要保留用户数据时，使用备份》创建》拷贝的方式】
		*/
		int version = _version;
		if(POINT_VERSION > version){
			try {
				if(myDataBase != null){
					myDataBase.close();
					myDataBase = null;
				}
				copySqlite();
				
			} catch (Exception e) {
				LogUtil.d2File("升级linkman.db出现异常");
				LogUtil.e("升级linkman.db出现异常", e);
			}
		}else{
			LogUtil.d2File("数据库软升级 开始");
			try {
				if (null == myDataBase || !myDataBase.isOpen()) {
					myDataBase = SQLiteDatabase.openDatabase(
							sqlite_data_filepath + DB_NAME, null,SQLiteDatabase.NO_LOCALIZED_COLLATORS);
				}
			    if (23 == version) {  
			        // 11.21号版本开始创建，新增一个表 
			        String sqlCreateTActivity = "CREATE TABLE IF NOT EXISTS t_activity (timestamp TEXT,extendStr TEXT," +
			        		"description TEXT,url TEXT,activityMark TEXT,activityType TEXT,activityExpireTime TEXT,isDeleted TEXT," +
			        		"activityInitialTime TEXT,serviceId TEXT,orderTime TEXT,orderType TEXT,type INTEGER,activityId TEXT)";
			        myDataBase.execSQL(sqlCreateTActivity);
			        version = 24;
			    }
				// base on 1.5 版本开始创建，新增通话记录表 ，add by majj
				String sqlCreateTActivity = "CREATE TABLE IF NOT EXISTS t_callrecords (contactId TEXT,nubeNumber TEXT,"
						+ "number TEXT,name TEXT,callDirection INT(4),callType INT(4),time TIMESTAMP,headUrl TEXT,lastTime TEXT,"
						+ "dataType INT(4))";
				myDataBase.execSQL(sqlCreateTActivity);
				
				if ((version >= 24) &&(version <= 27)) {
					// 2.27 针对1.0版本向1.5版本数据库升级bug
					
					String newFriendDDL = "select sql from sqlite_master where type='table' AND name='t_newfriend' ";
					Cursor cursorDDL = null;
					cursorDDL = myDataBase.rawQuery(newFriendDDL, null);
					if(cursorDDL != null && cursorDDL.getCount() > 0){
						cursorDDL.moveToFirst();
						String sqlDDL = cursorDDL.getString(0);
						if(!TextUtils.isEmpty(sqlDDL)){
							sqlDDL = sqlDDL.toLowerCase();
							if(sqlDDL.indexOf("visible") <= -1){
								LogUtil.d("t_newfriend 表不存在  visible 字段");
								if (cursorDDL != null){ 
									cursorDDL.close();
									cursorDDL = null;
								}
								// 新增字段
								String sql = "ALTER TABLE [t_newfriend] ADD COLUMN [visible] int(11)";
								myDataBase.execSQL(sql);
							}
						}
					}
					// 通话记录
					String tCallrecordsDDL = "select sql from sqlite_master where type='table' AND name='t_callrecords' ";
					cursorDDL = myDataBase.rawQuery(tCallrecordsDDL, null);
					if(cursorDDL != null && cursorDDL.getCount() > 0){
						cursorDDL.moveToFirst();
						String sqlDDL = cursorDDL.getString(0);
						if(!TextUtils.isEmpty(sqlDDL)){
							sqlDDL = sqlDDL.toLowerCase();
							if(sqlDDL.indexOf("datatype") <= -1){
								LogUtil.d("t_callrecords 表不存在  dataType 字段");
								if (cursorDDL != null){ 
									cursorDDL.close();
									cursorDDL = null;
								}
								// 新增字段
								String sql = "ALTER TABLE [t_callrecords] ADD COLUMN [dataType] int(4)";
								myDataBase.execSQL(sql);
							}
						}
					}
					version = 28;
				}
			    LogUtil.d2File("数据库升级 完毕");
			} catch (Exception e) {
				e.printStackTrace();
				LogUtil.d2File("升级数据库，出错");
				
				if(myDataBase != null){
					myDataBase.close();
					myDataBase = null;
				}
				copySqlite();
			}
		}
	}
	
	/** 
	 * @author: lihs
	 * @Title: createDB 
	 * @Description:向指定的目录创建DB文件
	 * @date: 2013-8-1 上午10:50:56
	 */
	private void createDB() {
		LogUtil.d("DBHelper createDB start");
		try {
			 if (DBConstant.SQLITE_FILE_SDCARD_FOLDER.equals(sqlite_data_filepath)) {
				// 拷贝数据库到sd卡
				CommonUtil.copySqlite2SDCard(mContext, DB_NAME);
			 } else {
				// 拷贝数据库到内存
				CommonUtil.copySqlite2Rom(mContext, DB_NAME);
		     }
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.d2File("内存空间不足，数据库拷贝失败");
			PatientApplication.sendNoSpaceNotifacation();
		}
		LogUtil.d("DBHelper createDB end");
	}
}
