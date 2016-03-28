package com.patient.db.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.patient.constant.ProviderConstant;
import com.patient.db.table.DiseaseTable;
import com.patient.ui.patientUi.entity.extendsTable.DiseaseBean;
import com.patient.util.LogUtil;

public class DiseaseDaoImpl implements DiseaseDao {

	private Context context;

	public DiseaseDaoImpl(Context context) {
		this.context = context;
	}


	@Override
	public boolean saveDisease(List<DiseaseBean> data) {
		
		// 事物来插入数据库表并且按照事物进行数据处理
		ArrayList<ContentProviderOperation> listOperations = null;
		try {
			listOperations = new ArrayList<ContentProviderOperation>();
			for (Iterator iterator = data.iterator(); iterator.hasNext();) {
				DiseaseBean bean = (DiseaseBean) iterator.next();
				// 执行插入一条活动
				LogUtil.d("执行插入:"+bean.disease_Id);
				listOperations.add(ContentProviderOperation.newInsert(ProviderConstant.DISEASE_URI)
						.withValues(DiseaseBean.parseBean(bean)).build());
			}
			if (listOperations.size() > 0) {
				 context.getContentResolver().applyBatch(ProviderConstant.CACHE_AUTHORITY, listOperations);
			 }
			 LogUtil.d("批量处理疾病列表数据success");
			return true;
		} catch (Exception e) {
			 LogUtil.d("批量处理疾病列表数据异常"+e.getLocalizedMessage());
		}
		return false;
	}

	@Override
	public List<DiseaseBean> getAllDisease(String condition) {
		
		// 模糊查询的
		List<DiseaseBean> allData = null;
		DiseaseBean bean = null;
		Cursor cursor = null;
		try {
			Uri uri = Uri.parse(ProviderConstant.CACHE_URI + "/QUERY_DISEASE_NAME/" + condition);
			cursor = context.getContentResolver().query(uri, null,null,null,null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				allData = new ArrayList<DiseaseBean>();
				do {
					bean = new DiseaseBean();
					bean.disease_Id = cursor.getString(cursor.getColumnIndex(DiseaseTable.DISEASEID));
					bean.index_Name = cursor.getString(cursor.getColumnIndex(DiseaseTable.DISEASENAME));
					bean.created_stamp = cursor.getString(cursor.getColumnIndex(DiseaseTable.CREATEDSTAMP));
					allData.add(bean);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			return null;
		} finally {
		    if (cursor != null) {
		        cursor.close();
		        cursor = null;
		    }
		}
		return allData;
	}
	
}
