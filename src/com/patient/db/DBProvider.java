/**
 * <dl>
 * <dt>SettingProvider.java</dt>
 * <dd>Description:TODO</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company: 安徽青牛信息技术有限公司</dd>
 * <dd>CreateDate: 2014-1-22 上午9:52:24</dd>
 * </dl>
 * 
 * @author chuwx
 */
package com.patient.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.patient.constant.ProviderConstant;
import com.patient.db.table.DiseaseTable;
import com.patient.db.table.ProvCityTable;
import com.patient.db.table.PushMsgTable;
import com.patient.preference.LoginPreference;
import com.patient.util.LogUtil;

public class DBProvider extends ContentProvider {

	private DBHelper dbHelper;
	private SQLiteDatabase db;

	private static final UriMatcher uriMatcher;
	// 对疾病表的数据操作问题
	private static final int DISEASE_ITEM = 1;
	private static final int QUERY_DISEASE_NAME = 2;
	private static final int QUERY_PROV_CITY_HOS = 4;
	private static final int QUERY_MESSAGE = 6;//  查询消息
	private static final int INSERT_MESSAGE = 7;// 插入消息
	private static final int DELETE_MESSAGE = 8;// 删除消息
	
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(ProviderConstant.CACHE_AUTHORITY,DiseaseTable.TABLENAME, DISEASE_ITEM);
		// 查询疾病数据
		uriMatcher.addURI(ProviderConstant.CACHE_AUTHORITY,"QUERY_DISEASE_NAME/*", QUERY_DISEASE_NAME);
		uriMatcher.addURI(ProviderConstant.CACHE_AUTHORITY,"QUERY_PRO_CITY/*", QUERY_PROV_CITY_HOS);
		
		uriMatcher.addURI(ProviderConstant.CACHE_AUTHORITY,"QUERY_MESSAGE/*", QUERY_MESSAGE);
 		uriMatcher.addURI(ProviderConstant.CACHE_AUTHORITY,"INSERT_MESSAGE", INSERT_MESSAGE);
 		uriMatcher.addURI(ProviderConstant.CACHE_AUTHORITY,"DELETE_MESSAGE/*", DELETE_MESSAGE);
	}

	@Override
	public boolean onCreate() {

		LogUtil.d("DBProvider onCreate start");
//		dbHelper = new DBHelper(getContext(),LoginPreference.getUserInfo().partyId);
//		checkUpProvider();
		LogUtil.d("DBProvider onCreate:" + db);
		return (db == null) ? false : true;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {

		case 10000:
			return "vnd.android.cursor.dir/" + DiseaseTable.TABLENAME;
		case 10001:
			return "vnd.android.cursor.item/" + DiseaseTable.TABLENAME;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}
	/**
	 * 初始化数据库
	 * @return
	 */
	public boolean checkUpProvider() {
		LogUtil.d(" checkUpProvider start");
			if((null == db || !db.isOpen())){
				setupProvider(false);
				try {
					db.getVersion();
				} catch (Exception e) {
					LogUtil.d("数据库丢失异常:"+e.getMessage());
					LogUtil.d2File("数据库丢失异常:"+e.getMessage());
					setupProvider(true);
				}
			}
		return (db == null) ? false : true;
	}

	private void setupProvider(boolean isForceOpen) {
		try {
			String loginUserId = LoginPreference.getUserInfo().partyId;
			LogUtil.d("loginUserId："+loginUserId);
			dbHelper = DBHelper.getInstance(loginUserId);
			if(isForceOpen){
				db = dbHelper.getOpenDatabase();
			}else{
				db = dbHelper.getdatabase();
			}
		} catch (Exception e) {
			LogUtil.e(" instanceProvider Error", e);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,String[] selectionArgs, String sortOrder) {
		Cursor cursor = null;
		String sql = "";
		String queryCondition ="";
		checkUpProvider();
		switch (uriMatcher.match(uri)) {
		
		case QUERY_MESSAGE:
			// 根据消息类型查询 是否存在该类型的消息			
			queryCondition = uri.getPathSegments().get(1);
			sql = "SELECT * FROM " + PushMsgTable.TABLENAME + " WHERE "+PushMsgTable.MESSAGETYPE+" = '"+queryCondition+"'";
			LogUtil.d("查询条件是："+sql);
			cursor = db.rawQuery(sql, null);
			LogUtil.d("查询省市的数据："+(cursor == null?0:cursor.getCount()));				
			break;
		case QUERY_DISEASE_NAME:
			// 查询疾病数据，应该是 模糊查询
			  queryCondition = uri.getPathSegments().get(1);
			LogUtil.d("查询条件是："+queryCondition);
			if ("all".equals(queryCondition)) {
				// 查询所有的数据进行处理
				sql = "select * from "+DiseaseTable.TABLENAME ;
			}else{
				sql = "select * from "+DiseaseTable.TABLENAME +" where " +DiseaseTable.DISEASENAME +" like '%"+queryCondition +"%'";
			}
			LogUtil.d("查询条件是："+sql);
			cursor = db.rawQuery(sql, null);
			LogUtil.d("查询疾病的数据："+(cursor == null?0:cursor.getCount()));
			break;
		case QUERY_PROV_CITY_HOS:
			queryCondition = uri.getPathSegments().get(1);
			LogUtil.d("查询条件是："+queryCondition);
			if ("all".equals(queryCondition)) {
				// 查询所有的省
				sql = "SELECT * FROM " + ProvCityTable.GEO_TABLE + " WHERE geo_type_id in ('PROVINCE','MUNICIPALITY')"  ;
			}else{
				sql = "select * from " + ProvCityTable.GEO_TABLE + " where geo_id  in ( select geo_id_to from geo_assoc  where geo_id ="+queryCondition+")";
			}
			LogUtil.d("查询条件是："+sql);
			cursor = db.rawQuery(sql, null);
			LogUtil.d("查询省市的数据："+(cursor == null?0:cursor.getCount()));			
			break;
		default:
			break;
		}
		if (null != cursor) {
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
		}
		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long rowID = 0;
		Uri _uri = null;
		checkUpProvider();
		switch (uriMatcher.match(uri)) {
		
		case INSERT_MESSAGE:
			rowID = db.insert(PushMsgTable.TABLENAME, null, values);
			break;
		case DISEASE_ITEM:
			rowID = db.insert(DiseaseTable.TABLENAME, null, values);
			if (rowID > -1) {
				// 通知监听该URL的数据列表进行刷新
				_uri = ContentUris.withAppendedId(ProviderConstant.DISEASE_URI, rowID);
				getContext().getContentResolver().notifyChange(_uri, null);
			}
			break;
		}
		return _uri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count = 0;
		checkUpProvider();
		switch (uriMatcher.match(uri)) {
		case DELETE_MESSAGE:
			 // 删除充值失败的金钱数据
			count = db.delete(PushMsgTable.TABLENAME, selection, selectionArgs);
			LogUtil.d("删除消息列表");
			break;
		default:
			break;
		}
		if (count > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count = 0;
		checkUpProvider();
		switch (uriMatcher.match(uri)) {
		default:
			break;
		}
		if (count > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return count;
	}

}
