package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class ProjectBean implements Serializable {

	/**
	 * 项目
	 */
	private static final long serialVersionUID = 2764557720355913098L;
	public String projectId;// ID
	public String sponsorId;// 发起人ID
	public String sponsorName;// 发起人姓名
	public String blameId;// 负责人ID
	public String blameName;// 负责人姓名
	public String projectName;// 项目名称
	public String content;// 项目内容
	public String projectCourseId;// 科室ID
	public String projectCourseName;// 科室名称
	public String releaseDate;// 发布日期
	public String collectionNum;// 收藏数
	public String reviewNumStu;// 学员评论数
	public String reviewNumEdi;// 编委评论数
	public String creditNum;// 学分数
	public String StudentNum;// 学员数
	public String ResultNum;// 成果数
}
