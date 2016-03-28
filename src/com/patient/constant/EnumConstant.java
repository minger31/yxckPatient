package com.patient.constant;

import java.util.HashMap;
import java.util.Map;

public class EnumConstant {

	public static Map<String, String> enumMap = null;
	static {
		if (enumMap == null) {
			enumMap = new HashMap<String, String>();
		}
		
		// 文章状态,项目评审的状态  编委 评审的评论是待审核的，给了学分以后就是不通过的
		enumMap.put("artStatusEnum_1", "通过");
		enumMap.put("artStatusEnum_2", "未通过");
		enumMap.put("artStatusEnum_3", "待审核");
		enumMap.put("artStatusEnum_4", "新创建");
		// 是否退回
		enumMap.put("sendBackEnum_1", "是");
		enumMap.put("sendBackEnum_2", "否");
		// 问诊状态
		enumMap.put("itgtStatus_1", "开启");
		enumMap.put("itgtStatus_2", "关闭");
		// 是否删除
		enumMap.put("deleteEnum_1", "是");
		enumMap.put("deleteEnum_2", "否");
		// 是否发放
		enumMap.put("provideEnum_1", "是");
		enumMap.put("provideEnum_2", "否");
		// 项目评论类型
		enumMap.put("commentsTypeEnum_1", "文字");
		enumMap.put("commentsTypeEnum_2", "语音");
		enumMap.put("commentsTypeEnum_3", "其他");
		// 项目由谁评论的类型
		enumMap.put("typeEnum_1", "学员评论");
		enumMap.put("typeEnum_2", "编委评论");
		// 是否同意
		enumMap.put("isAgreeEnum_1", "是");
		enumMap.put("isAgreeEnum_2", "否");
		// 性别
		enumMap.put("sexEnum_0", "男");
		enumMap.put("sexEnum_1", "女");
		// 级别
		enumMap.put("levelEnum_0", "注册用户");
		enumMap.put("levelEnum_1", "赞助者");
		enumMap.put("levelEnum_2", "普通医生");
		enumMap.put("levelEnum_3", "编辑");
		enumMap.put("levelEnum_4", "编委");
		enumMap.put("levelEnum_5", "工作人员");
		// 报社职称
		enumMap.put("editorialEnum_0", "主编");
		enumMap.put("editorialEnum_1", "副主编");
		enumMap.put("editorialEnum_2", "名誉主编");
		enumMap.put("editorialEnum_3", "顾问");
		enumMap.put("editorialEnum_4", "常务编委");
		enumMap.put("editorialEnum_5", "编委");
		enumMap.put("editorialEnum_6", "外部专家");
		enumMap.put("editorialEnum_7", "院士");
		// 医院专业职位
		enumMap.put("ediLevelEnum_1", "住院医师");
		enumMap.put("ediLevelEnum_2", "主治医师");
		enumMap.put("ediLevelEnum_3", "副主任医师");
		enumMap.put("ediLevelEnum_4", "主任医师");
		// 医院行政职务
		enumMap.put("executiveEnum_1", "院长");
		enumMap.put("executiveEnum_2", "副院长");
		// 科室职务
		enumMap.put("deptTitleEnum_1", "主任");
		enumMap.put("deptTitleEnum_2", "副主任");
		// 学术界职称
		enumMap.put("bachelorEnum_0", "教授");
		enumMap.put("bachelorEnum_1", "副教授");
		enumMap.put("bachelorEnum_2", "讲师");
		// 业务类型
		enumMap.put("serviceTypeEnum_0", "期刊");
		enumMap.put("serviceTypeEnum_1", "文章");
		enumMap.put("serviceTypeEnum_2", "项目");
		enumMap.put("serviceTypeEnum_3", "课件");
		enumMap.put("serviceTypeEnum_4", "会议");
		enumMap.put("serviceTypeEnum_5", "调研");
		// 申请类型
		enumMap.put("diplomaTypeEnum_0", "自助申请");
		enumMap.put("diplomaTypeEnum_1", "申请赞助");
		// 申请状态
		enumMap.put("diplomaStatusEnum_0", "申请中");
		enumMap.put("diplomaStatusEnum_1", "已结束");
		enumMap.put("diplomaStatusEnum_2", "已申请");
		// 学术币历史的状态
		enumMap.put("BiStatus_0", "充值");
		enumMap.put("BiStatus_1", "花费");
		// appType
		enumMap.put("appTypeEnum_0", "医生端");
		enumMap.put("appTypeEnum_1", "患者端");
		// 最近的类型
		enumMap.put("lastType_0", "文字");
		enumMap.put("lastType_1", "语音");
		enumMap.put("lastType_2", "图片");
		enumMap.put("lastType_3", "其他");
		// 类型
		enumMap.put("type_0", "文字");
		enumMap.put("type_1", "语音");
		enumMap.put("type_2", "图片");
		enumMap.put("type_3", "其他");
		// 日类型
		enumMap.put("dayType_1", "上午");
		enumMap.put("dayType_2", "下午");
		enumMap.put("dayType_3", "全天");
		// 疑似诊断
		enumMap.put("susDiagnoEnum_0", "感冒");
		enumMap.put("susDiagnoEnum_1", "发烧");
		enumMap.put("susDiagnoEnum_2", "神经过敏");
		// 设备选择
		enumMap.put("equipmentSeleEnum_0", "设备一");
		enumMap.put("equipmentSeleEnum_1", "设备二");
		enumMap.put("equipmentSeleEnum_2", "设备三");
		// 就诊状态
		enumMap.put("TreatmStatusEnum_0", "患者填");
		enumMap.put("TreatmStatusEnum_1", "医生改");
		// 随访状态
		enumMap.put("FollowupStatusEnum_0", "医生填");
		enumMap.put("FollowupStatusEnum_1", "患者改");
		// 指标类型
		enumMap.put("TargetType_0", "疾病指标");
		enumMap.put("TargetType_1", "健康数据指标");
		// 疾病状态
		enumMap.put("diseaseStatus_0", "私有");
		enumMap.put("diseaseStatus_1", "公有");
		
		// 申请编辑或者申请医生的状态
		enumMap.put("auditStatus_1", "待审核");
		enumMap.put("auditStatus_2", "已通过");
		enumMap.put("auditStatus_3", "未通过");
	}
	
	/**消息**/
//	enumMap.put("newsTypeEnum_1", "联合发起人消息");
//	enumMap.put("newsTypeEnum_2", "申请特约编辑消息");
//	enumMap.put("newsTypeEnum_3", "申请立项");
	public static final String NEWSTYPEENUM_1 = "newsTypeEnum_1";
	public static final String NEWSTYPEENUM_2 = "newsTypeEnum_2";
	public static final String NEWSTYPEENUM_3 = "newsTypeEnum_3";
	
	/**注册用户**/
	public static final String ROLE_REGISTER = "levelEnum_0";
	/**赞助者**/
	public static final String ROLE_APPROVER = "levelEnum_1";
	/**普通医生**/
	public static final String ROLE_NORMAL_DOCTOR = "levelEnum_2";
	/**编辑者**/
	public static final String ROLE_EDITER = "levelEnum_3";
	/**编委**/
	public static final String ROLE_EDITERS = "levelEnum_4";

}
