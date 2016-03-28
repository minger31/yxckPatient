package com.patient.constant;

import android.net.Uri;

import com.patient.db.table.DiseaseTable;
 
/**
 * <dl>
 * <dt>ProviderConstant.java</dt>
 * <dd>Description:授权的头部表</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company: </dd>
 * <dd>CreateDate: 2015-1-2 下午2:19:08</dd>
 * </dl>
 * 
 * @author lihs
 */
public class ProviderConstant {

	public static final String CACHE_AUTHORITY ="com.patient.db.cache.provider";
	public static final Uri CACHE_URI = Uri.parse("content://" + CACHE_AUTHORITY);
	
	// 疾病
	public static final Uri DISEASE_URI = Uri.withAppendedPath(CACHE_URI, DiseaseTable.TABLENAME); 
	
 
	
}
