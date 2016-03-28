package com.patient.db.table;

import java.io.Serializable;

public class ProvCityTable implements Serializable {

	/**
	 * 省市县三级表关联
	 */
	private static final long serialVersionUID = -7486556333398664314L;
	
	public static final String GEO_TABLE = "geo";
	public static final  String GEO_ID ="GEO_ID";// 两个表通用
	public static final  String GEO_TYPE_ID ="GEO_TYPE_ID";
	public static final  String GEO_NAME ="GEO_NAME";
	
	public static final String GEO_ASSOC_TABLE = "geo_assoc";
	public static final  String GEO_ID_TO ="GEO_ID_TO";// 表关联字段
	 
}
