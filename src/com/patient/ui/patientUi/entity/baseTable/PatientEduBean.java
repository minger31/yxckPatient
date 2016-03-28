package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class PatientEduBean implements Serializable {

	/**
	 * 患教
	 */
	private static final long serialVersionUID = 3716948083485198792L;
	public String patientEduId;// ID
	public String theme;// 标题
	public String content;// 发布内容
	public String imagePath;// 发布图片路径
	public String videoPath;// 视频路径
	public String videoImagePath;// 视频路径第一帧
	public String releaseTime;// 发布时间
	public String releaseId;// 发布人ID
	public String reviewCount;// 评论数
	public String praiseCount;// 赞
	public String flowerNum;//鲜花数
	public String collectionCount;// 收藏数

	public String whetherCollection;// 是否收藏  ""为没有收藏，收藏过后为partyCollId 
	public String whetherPraise;// 是否点赞   ""为没有赞，赞过后为forumId 

	public PartyBean partyGv;
}
