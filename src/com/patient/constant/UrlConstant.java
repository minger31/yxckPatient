package com.patient.constant;

// 医学参考报接口请求URL的参数请求
public class UrlConstant {

	/**开发环境通用接口的前缀**/
	public static String COMMON_URL = "http://182.92.185.73:80/sysCommon/control/restful/";
//	public static  String COMMON_URL = "http://182.92.185.73:80/yxck/control/";
	/**测试环境的Url**/
//	public static  String COMMON_URL = "http://123.57.134.108:80/yxck/control/";
//	public static  String  COMMON_URL = "http://192.168.1.153:80/yxck/control/";
//	public static  String  COMMON_URL = "http://192.168.1.154:80/yxck/control/";

	/**用户登陆**/
	public static  String LOGIN_SERVICE = COMMON_URL+"ajaxLoginPhone";	
	/**用户登陆**/
//	public static  String LOGIN_SERVICE = COMMON_URL+"ajaxLogin";	
	/**获取验证码**/
	public static  String GET_VERIFICATION_CODE = COMMON_URL+"ajaxGetVerificationCode";	
	/**注册**/
	public static  String CREATE_OUTSIDE_EXPERTS = COMMON_URL+"createOutsideExperts";	
	/**修改密码**/
	public static  String AJAX_UPDATE_PASSWORD = COMMON_URL+"ajaxUpdatePassword";	
	/**上传头像的接口**/
	public static  String UPLOAD_ICON = COMMON_URL+"ajaxImportPicUrl";	
	/**修改个人信息**/
	public static  String UPDATE_OUTSIDE_EXPERTS = COMMON_URL+"updateOutsideExperts";	
	/**扫描二维码**/
	public static  String UPDATEQRCODEPUTARTICLE = COMMON_URL+"updateQRCodePutArticle";
	 /**省获取下面的市**/
	public static  String AJAX_GET_CITY = COMMON_URL+"ajaxGetCity";
   /**获取学术币历史**/
    public static  String GETACADEMICCOINHISTORY = COMMON_URL+"getAcademicCoinHistory";
	/**获取shenfen**/
	public static  String GET_PROVICE = COMMON_URL+"ajaxGetProvice";	
	/**病友圈**/
	public static  String GET_PATIENT_EDU = COMMON_URL+"getPatientEdu";	
	/**生命线**/
	public static  String GET_TREATMENT = COMMON_URL+"getTreatment";
	/**赞  评论**/
	public static  String CREATE_PATIENT_EDUFORUM = COMMON_URL+"createPatientEduForum";
	/**评论列表**/
	public static  String GET_PATIENT_EDU_FORUM = COMMON_URL+"getPatientEduForum";
	/**取消 赞  评论**/
	public static  String DELETE_PATIENT_EDU_FORUM = COMMON_URL+"deletePatientEduForum";
	/**收藏**/
	public static  String CREATE_PARTY_COLLECTION = COMMON_URL+"createPartyCollection";
	/**取消收藏**/
	public static  String DELETE_COLLECTION = COMMON_URL+"ajaxDeleteCollection";
	
	/**获取患者端问诊列表**/
	public static  String GET_INTERROGATION = COMMON_URL+"getInterrogation";
	/**开放日程**/
	public static  String getOpenScheduleData = COMMON_URL+"getOpenScheduleData";
	/**根据科室类别获取下面的科室**/
	public static  String AJAX_DEPT_DATA = COMMON_URL+"ajaxDeptData";	
	/**添加问诊**/
	public static  String CREATE_INTERROGATION = COMMON_URL+"createInterrogation";	
	/**完善资料准备数据**/
	public static  String GET_CER_DOCTOR_DATA = COMMON_URL+"getCerDoctorData";	
	/**开放日程医生详情页（患者端）**/
	public static  String GET_SCHEDULE_INDEX = COMMON_URL+"getScheduleIndex";	
	/** 获取医生下个病人数 **/
	public static  String GET_DISEASE_NUM = COMMON_URL+"getDiseaseNum";	
	
	/**患者搜索医生添加就诊**/
	public static  String GET_OUTSIDEEXPERTS = COMMON_URL+"getOutsideExperts";	
	/**创建就诊**/
	public static  String CREATE_TREATMENT = COMMON_URL+"createTreatment";	
	/**完善/修改就诊信息**/
	public static  String UPDATE_TREATMENT = COMMON_URL+"updateTreatment";	
	/**获取疾病的种子数据**/
	public static  String GET_DOCTORDISEASE = COMMON_URL+"getDoctorDisease";	
	/**获取所有的疾病**/
	public static  String GET_DISEASE = COMMON_URL+"getDisease";	
	/**患者端随访列表  微访**/
	public static  String GET_INITIATE_FOLLOWUP = COMMON_URL+"getInitiateFollowup";	
	/**填写随访**/
	public static  String FILL_IN_RETURN_VISIT = COMMON_URL+"FillInReturnVisit";	
	/** 发送聊天 （旧）**/
	public static  String SAVE_FOLLOWUP_RECORD = COMMON_URL+"saveFollowupRecord";	
	/**发送聊天**/
	public static  String ADD_TINY_FOLLOW_UP_MESSAGE = COMMON_URL+"addTinyFollowUpMessage";
	/** 微随访记录即聊天列表 */
	public static  String GET_TINY_FOLLOW_UP_MESSAGE = COMMON_URL+"getTinyFollowUpMessage";
	
	/*********************健康数据 start************************************/
	/** 新增指标数据 */
	public static  String CREATE_FITNESS_VALUE = COMMON_URL+"createFitnessValue";
	/** 查询指标值数据 */
	public static  String GET_FITNESS_VALUE = COMMON_URL+"getFitnessValue";
	/** 送鲜花/锦旗 */
	public static  String SEND_FLOWER_BANNER = COMMON_URL+"sendFlowerBanner";
	
	/**添加学术币充值 */
	public static  String ajaxAddAcaCoinHis = COMMON_URL+"ajaxAddAcaCoinHis";
	/**医生个人主页 */
	public static  String GET_DOCTORHOME = COMMON_URL+"getDoctorHome";
	/**购买鲜花/锦旗 */
	public static  String PURCHASE_CITIGROUP = COMMON_URL+"purchaseCitigroup";
	
	public static void asd(){
		LOGIN_SERVICE = COMMON_URL+"ajaxLoginPhone";	
		/**获取验证码**/
		GET_VERIFICATION_CODE = COMMON_URL+"ajaxGetVerificationCode";	
		/**注册**/
		CREATE_OUTSIDE_EXPERTS = COMMON_URL+"createOutsideExperts";	
		/**修改密码**/
		AJAX_UPDATE_PASSWORD = COMMON_URL+"ajaxUpdatePassword";	
		/**上传头像的接口**/
		UPLOAD_ICON = COMMON_URL+"ajaxImportPicUrl";	
		/**修改个人信息**/
		UPDATE_OUTSIDE_EXPERTS = COMMON_URL+"updateOutsideExperts";	
		/**扫描二维码**/
		UPDATEQRCODEPUTARTICLE = COMMON_URL+"updateQRCodePutArticle";
		 /**省获取下面的市**/
		AJAX_GET_CITY = COMMON_URL+"ajaxGetCity";
	   /**获取学术币历史**/
	    GETACADEMICCOINHISTORY = COMMON_URL+"getAcademicCoinHistory";
		/**获取shenfen**/
		GET_PROVICE = COMMON_URL+"ajaxGetProvice";	
		/**病友圈**/
		GET_PATIENT_EDU = COMMON_URL+"getPatientEdu";	
		/**生命线**/
		GET_TREATMENT = COMMON_URL+"getTreatment";
		/**赞  评论**/
		CREATE_PATIENT_EDUFORUM = COMMON_URL+"createPatientEduForum";
		/**评论列表**/
		GET_PATIENT_EDU_FORUM = COMMON_URL+"getPatientEduForum";
		/**取消 赞  评论**/
		DELETE_PATIENT_EDU_FORUM = COMMON_URL+"deletePatientEduForum";
		/**收藏**/
		CREATE_PARTY_COLLECTION = COMMON_URL+"createPartyCollection";
		/**取消收藏**/
		DELETE_COLLECTION = COMMON_URL+"ajaxDeleteCollection";
		
		/**获取患者端问诊列表**/
		GET_INTERROGATION = COMMON_URL+"getInterrogation";
		/**开放日程**/
		getOpenScheduleData = COMMON_URL+"getOpenScheduleData";
		/**根据科室类别获取下面的科室**/
		AJAX_DEPT_DATA = COMMON_URL+"ajaxDeptData";	
		/**添加问诊**/
		CREATE_INTERROGATION = COMMON_URL+"createInterrogation";	
		/**完善资料准备数据**/
		GET_CER_DOCTOR_DATA = COMMON_URL+"getCerDoctorData";	
		/**开放日程医生详情页（患者端）**/
		GET_SCHEDULE_INDEX = COMMON_URL+"getScheduleIndex";	
		/** 获取医生下个病人数 **/
		GET_DISEASE_NUM = COMMON_URL+"getDiseaseNum";	
		
		/**患者搜索医生添加就诊**/
		GET_OUTSIDEEXPERTS = COMMON_URL+"getOutsideExperts";	
		/**创建就诊**/
		CREATE_TREATMENT = COMMON_URL+"createTreatment";	
		/**完善/修改就诊信息**/
		UPDATE_TREATMENT = COMMON_URL+"updateTreatment";	
		/**获取疾病的种子数据**/
		GET_DOCTORDISEASE = COMMON_URL+"getDoctorDisease";	
		/**获取所有的疾病**/
		GET_DISEASE = COMMON_URL+"getDisease";	
		/**患者端随访列表  微访**/
		GET_INITIATE_FOLLOWUP = COMMON_URL+"getInitiateFollowup";	
		/**填写随访**/
		FILL_IN_RETURN_VISIT = COMMON_URL+"FillInReturnVisit";	
		/** 发送聊天 （旧）**/
		SAVE_FOLLOWUP_RECORD = COMMON_URL+"saveFollowupRecord";	
		/**发送聊天**/
		ADD_TINY_FOLLOW_UP_MESSAGE = COMMON_URL+"addTinyFollowUpMessage";
		/** 微随访记录即聊天列表 */
		GET_TINY_FOLLOW_UP_MESSAGE = COMMON_URL+"getTinyFollowUpMessage";
		
		/*********************健康数据 start************************************/
		/** 新增指标数据 */
		CREATE_FITNESS_VALUE = COMMON_URL+"createFitnessValue";
		/** 查询指标值数据 */
		GET_FITNESS_VALUE = COMMON_URL+"getFitnessValue";
		/** 送鲜花/锦旗 */
		SEND_FLOWER_BANNER = COMMON_URL+"sendFlowerBanner";
		
		/**添加学术币充值 */
		ajaxAddAcaCoinHis = COMMON_URL+"ajaxAddAcaCoinHis";
		/**医生个人主页 */
		GET_DOCTORHOME = COMMON_URL+"getDoctorHome";
		/**购买鲜花/锦旗 */
		PURCHASE_CITIGROUP = COMMON_URL+"purchaseCitigroup";
	}
	
}

