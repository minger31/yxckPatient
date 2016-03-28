package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class ArticleBean implements Serializable {

	/**
	 * 文章
	 */
	private static final long serialVersionUID = 6116758259116901881L;
	public String articleId;// ID
	public String magazineId;// 期刊ID
	public String magazineName;// 期刊名称
	public String createId;// 创建人ID
	public String createName;// 创建人名称
	public String reviewerId;// 审核者ID
	public String reviewerName;// 审核者姓名
	public String title;// 文章标题
	public String channelId;// 频道ID
	public String channelName;// 频道名称
	public String originalUrl;// 原文地址
	public String articleName;// 原文标题
	public String author;// 原文作者
	public String content;// 内容
	public String reportPeopleId;// 报道人Id
	public String reportPeople;// 报道人
	public String reportDate;// 报道日期
	public String link;// 链接
	public String reviewNum;// 评论数
	public String forwardNum;// 转发数
	public String collectionNum;// 收藏数
	public String turnProjectEnum;// 是否转为项目
	public String artStatusEnum;// 状态
	public String sendBackEnum;// 是否退回（1、未退回2、已退回）
	public String refuseReason;// 拒绝原因
}
