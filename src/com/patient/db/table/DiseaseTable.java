package com.patient.db.table;

/**
 * <dl>
 * <dt>ActivityTable.java</dt>
 * <dd>Description:活动表</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company: 安徽青牛信息技术有限公司</dd>
 * <dd>CreateDate: 2013-11-18 下午6:37:45</dd>
 * </dl>
 * 
 * @author qn-lihs
 */
public class DiseaseTable {

	// <entity package-name="org.ofbiz.yxck" entity-name="Disease" title="疾病表">
	// <field name="diseaseId" type="id"><description>ID</description></field>
	// <field name="partyId" type="id"><description>医生Id</description></field>
	// <field name="indexName"
	// type="name"><description>疾病名称</description></field>
	// <field name="indexSpell"
	// type="name"><description>疾病拼音</description></field>
	// <field name="description"
	// type="long-varchar"><description>疾病备注</description></field>
	// <field name="diseaseStatus"
	// type="name"><description>疾病状态（1、私有的2、公用的）</description></field>

	// 疾病表表名字
	public static final String TABLENAME = "Disease";
	// 疾病ID
	public static final String DISEASEID = "disease_Id";
	// 疾病名称
	public static final String DISEASENAME = "index_Name";
	// 疾病的名称对应的拼音字段
//	public static final String INDEXSPELL = "indexSpell";
//	// 疾病描述
//	public static final String DESCRIPTION = "description";
	public static final String CREATEDSTAMP = "created_stamp";

}
