package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class MagazineBean implements Serializable {

	/**
	 * 期刊
	 */
	private static final long serialVersionUID = -3155707977435695806L;
	public String magazineId;// 期刊ID
	public String number;// 出版物编号ISSN
	public String magazineName;// 期刊名称
	public String magazineEngName;// 期刊英文名称
	public String introduction;// 简介
	public String coverUrl;// 封面地址
	public String coverName;// 封面名称
	public String influenceIndex;// 影响力指数
	public String collectionNum;// 收藏数
	public String reviewNum;// 评论数
	public String forwardNum;// 转发数
	public String publishDate;// 发布日期
	public String titleNum;// 文章数
	public String createId;// 创建人ID
	public String createName;// 创建人名称
}
