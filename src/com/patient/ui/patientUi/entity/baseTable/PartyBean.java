package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class PartyBean implements Serializable {

	/**
	 * 用户
	 */
	private static final long serialVersionUID = -7382733315285618261L;
	
	public String password;
	public String partyId;// ID
	public String parentId;// 父ID
	public String partyName;// 姓名
	public String phone1;// 手机
	public String provinceId;// 省ID
	public String cityId;// 市ID
	
	public String provinceName="";// 省ID
	public String cityName="";// 市ID
	
	public String hospitalId;// 医院ID
	public String hospitalName;// 医院名称
	public String departmentId;// 科室ID
	public String departmentName;// 科室
	public String departTitle;// 科室职务
	public String hospitalTitleId;// 医院专业职称Id
	public String hospitalTitle;// 医院专业职称名
	public String partyheadUrl;// 头像
	public String editorialEnum;// 报社职称id
	public String levelEnum;// 级别
	public String partyFlag;// 标识
	public String bachelorCommunityEnum;// 学术界职称id
	public String administrationTitle;// 医院行政职称
	public String applyForSponsorship;// 申请赞助状态（1、未申请2、申请中 3、已申请）
	public String auditStatus;// 审核状态（申请医生）
	public String refuseReason;// 拒绝原因
	public String phone2;// 固话
	public String captcha;// 验证码
	public String sexEnum;// 性别
	public String birthDate;// 生日
	public String introduction;// 简介
	public String expertise;// 专长
	public String email;// 邮箱
	public String nickName;// 昵称
	public String qrCodeUrl;// 二维码地址
	public String academicCoin;// 学术币
	public String creditNum;// 学分数
	public String reviewNum;// 评论数
	public String StudentNum;// 学员数
	public String teamNum;// 编辑数
	public String academicEffect;// 学术影响力
	public String societyEffect;// 社会影响力
	public String academicAppeal;// 学术号召力
	public String collectionNum;// 收藏数
	public String businessLicenseAdd;// 医生从业执照
	public String sbusinessLicenseAdd;// 医生从业执照缩略图
	public String age;// 年龄
	public String bloodType;// 血型
	
	public String getInterrogation;// 开放日程的时间
	public String interrogationId;// ID
	public String description;// 描述
	public String createTime;// 创建时间 问诊的时间
	
	public String deptTypeName;
	public String basicImage;// 
	public String createdStamp;// 医生给患者开放日程的时间
	
	public String patientNum;//患者数
	public String followCount;//随访数
	public String patientEduCount;//患教数
	public String flowersNum;//鲜花数
	public String bannerNum;//锦旗数
}
