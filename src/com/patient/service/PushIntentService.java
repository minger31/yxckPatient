package com.patient.service;

import java.util.Map;

import org.android.agoo.client.BaseConstants;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.patient.constant.CommonConstant;
import com.patient.db.dao.PushMessageDaoImpl;
import com.patient.library.jsonConvert.JSONConverter;
import com.patient.preference.LoginPreference;
import com.patient.ui.patientUi.activity.common.ActLogin;
import com.patient.ui.patientUi.entity.extendsTable.RedDotBean;
import com.patient.ui.patientUi.entity.extendsTable.UmengMessageBean;
import com.patient.util.CommonUtil;
import com.patient.util.NotifacationUtil;
import com.umeng.message.UmengBaseIntentService;

/**
 * @author lihs
 * 
 *  umeng 消息推送处理
 *  
 * 集成UMENG消息推送。
        场景：第一种是，编委发起项目或者是文章立项时选择联合发起人,联合发起人同意不同意
        第二种是，申请编辑主要是针对编委这个角色。
        第三种是，新建频道二次审核是否同意成为编委
        第四种是，医友圈添加好友时推送通知栏
        第五种，患者端送锦旗和鲜花
        
 */


public class PushIntentService extends UmengBaseIntentService{
	
//	Intent i = new Intent();
//	i.putExtra(CommonConstant.KEY_RESLUT, "我是中国人一切从简单");
//	i.setAction(CommonConstant.Action_PUSH_MSG);
//	sendBroadcast(i);

//	NotifacationUtil.sendNotifacation("ssasa", "sasasasa", null);
   
    public static final String sponsor_success = "messagePushType_8";//赞助成功
    public static final String new_inteoration = "messagePushType_9";//新问诊
    public static final String new_treatment = "messagePushType_10";//新就诊
    public static final String new_followup = "messagePushType_11";//新随访
 
    public static final String open_schedule = "messagePushType_16";//医生开放日程
    public static final String doctor_circle = "messagePushType_17";//医友圈
    public static final String patient_circle = "messagePushType_18";//病友圈 患者教育
   

	public static final String TAG = PushIntentService.class.getName();
	
	/**
	 * 发通知栏显示通知，如果当前界面处于消息列表界面，否则
	 */

	@Override
	protected void onMessage(Context context, Intent intent) {
		super.onMessage(context, intent);
		try {
			String message = intent.getStringExtra(BaseConstants.MESSAGE_BODY);
//			UMessage msg = new UMessage(new JSONObject(message));
//			 "body":{"text":"消息内容","title":"标题","ticker":"Android customizedcast ticker","after_open":"go_app","play_vibrate":"true","play_lights":"true","play_sound":"true"},
//				"extra":{"map":"partyGv","messageType":"joinMan"}}
			// 自定义推送消息内容解析并处理完成这个逻辑,当前栈顶界面是刷新界面则发送广播处理，如果不是广播那就推送通知栏，点击拉起界面
			 UmengMessageBean info = JSONConverter.convertToObject(message, UmengMessageBean.class);
			 if (info != null) {
				handlerPushMsg(context,info);
			 }
			 
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 0) {
				Intent i = new Intent();
				i.setClass(getBaseContext(), ActLogin.class);
				startActivity(i);
			} 
		}
	};
	
	private void handlerPushMsg(Context context,UmengMessageBean msg){
		
		Intent i = new Intent();
		String phone = LoginPreference.getUserInfo().partyId;
		String password = LoginPreference.getUserInfo().password;
		if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
			handler.sendEmptyMessage(0);
			return;
		}
		
		String newsType = msg.extra.messageType;
		String title = msg.body.title;
		String text = msg.body.text;
		
	    if (open_schedule.equals(newsType) || new_treatment.equals(newsType) || new_followup.equals(newsType) || patient_circle.equals(newsType)) {
			 // 显示小红点界面 ，这个存在数据库中和注册url 通知 界面刷新  通知小红点然然后把小红点删除，最终进行红点的显示处理逻辑   // 新问诊，新就诊，新随访
	    	
	    	String map = msg.extra.map;
			@SuppressWarnings("unchecked")
			Map<String, String> keyValue = JSONConverter.convertToMap(map);
			RedDotBean bean = new RedDotBean();
			bean.messageId = keyValue.get("messageId");
			bean.messageType = newsType;
			bean.readStatus = "1";
			bean.id = CommonUtil.getUUID();
			saveMesg(bean, context);
		} 
		NotifacationUtil.sendNotifacation(title, text, i);
	}
	
	private  void saveMesg(final RedDotBean bean,final Context context){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				boolean isSuccsee = new PushMessageDaoImpl(context).insertMessage(bean);
				if (isSuccsee) {
					Intent i = new Intent();
					i.setAction(CommonConstant.Action_PUSH_MSG);
					i.putExtra(CommonConstant.KEY_RESLUT, bean.messageType);
					sendBroadcast(i);
				}
			}
		}).start();
	}
}
