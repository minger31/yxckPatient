package com.patient.ui.patientUi.entity.extendsTable;

import java.io.Serializable;

import android.content.ContentValues;

import com.patient.db.table.DiseaseTable;

// 疾病实体表数据进行处理 
public class DiseaseBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6477407503326980123L;
	// 疾病表表名字
	public  String tableName;
	// 疾病ID
	public  String disease_Id;
	// 疾病名称
	public  String index_Name;
	//疾病时间
	public String created_stamp;
	
	public static ContentValues parseBean(DiseaseBean bean) {
		if (bean == null) {
			return null;
		}
		ContentValues values = new ContentValues();
		values.put(DiseaseTable.DISEASEID, bean.disease_Id);
		values.put(DiseaseTable.DISEASENAME, bean.index_Name);
		values.put(DiseaseTable.CREATEDSTAMP, bean.created_stamp);
		return values;
	}

}
