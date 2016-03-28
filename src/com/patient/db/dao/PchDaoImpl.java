package com.patient.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.patient.constant.ProviderConstant;
import com.patient.db.table.ProvCityTable;
import com.patient.ui.patientUi.entity.baseTable.ProvCityBean;

public class PchDaoImpl implements PchDao {
	
	
	private Context context;
	public PchDaoImpl(Context context) {
		this.context = context;
	}

	@Override
	public List<ProvCityBean> getProCityData(String condition) {
		
		// 模糊查询的
		List<ProvCityBean> allData = null;
		ProvCityBean bean = null;
		Cursor cursor = null;
		try {
			Uri uri = Uri.parse(ProviderConstant.CACHE_URI + "/QUERY_PRO_CITY/" + condition);
			cursor = context.getContentResolver().query(uri, null,null,null,null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				allData = new ArrayList<ProvCityBean>();
				do {
					bean = new ProvCityBean();
					bean.geo_id = cursor.getString(cursor.getColumnIndex(ProvCityTable.GEO_ID));
					bean.geo_name = cursor.getString(cursor.getColumnIndex(ProvCityTable.GEO_NAME));
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
