package com.patient.util;

import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import com.patient.PatientApplication;
import com.yxck.patient.R;

public class NotifacationUtil {
	
	/**普通医生申请编辑**/
	public  static int NOTIFY_APPLY_EDITOR_ID = 1002;
	/**编辑通讯录添加编辑**/
	public  static int NOTIFY_ADD_EDITOR_ID = 1003;
	private static int NOTIFY_ID = 1001;
	
	@SuppressWarnings("deprecation")
	public static void sendNotifacation(String titleString, String content, Intent intent){
		
		
		Random random = new Random(1000);
		NOTIFY_ID = random.nextInt(1000)+1001;
		// 1001 到 1999 之间的随机数
		LogUtil.d("NotifacationUtil", NOTIFY_ID+"");
		//在通知新消息前，要把先前的通知移除
		cancelNotifacation();
		NotificationManager mNotificationManager = (NotificationManager) PatientApplication.getContext().getSystemService(PatientApplication.getContext().NOTIFICATION_SERVICE);
		Notification n = new Notification(R.drawable.app_icon, titleString, System.currentTimeMillis());
		n.defaults = Notification.DEFAULT_VIBRATE;
		n.flags = Notification.FLAG_AUTO_CANCEL;
        if(intent!=null){
        	PendingIntent pIntent = PendingIntent.getActivity(PatientApplication.getContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            n.setLatestEventInfo(PatientApplication.getContext(), titleString, content, pIntent);
        }else{
        	n.setLatestEventInfo(PatientApplication.getContext(), titleString, content, null);
        }
        mNotificationManager.notify(NOTIFY_ID, n);		
	}
	
	public static void cancelNotifacation(){
		
		NotificationManager mNotificationManager =
		        (NotificationManager) PatientApplication.getContext().getSystemService(PatientApplication.getContext().NOTIFICATION_SERVICE);
		mNotificationManager.cancel(NOTIFY_ID);
	}
	
	public static void sendNoSpaceNotifacation(){
		NotificationManager mNotificationManager =
		        (NotificationManager) PatientApplication.getContext().getSystemService(PatientApplication.getContext().NOTIFICATION_SERVICE);
		PendingIntent pIntent = PendingIntent.getActivity(PatientApplication.getContext(), 0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
        Notification n = new Notification(R.drawable.app_icon, "空间不足提示", System.currentTimeMillis());
        n.flags = Notification.FLAG_AUTO_CANCEL;
        n.setLatestEventInfo(PatientApplication.getContext(), "空间不足提示", "存储空间不足，可能影响部分功能使用，请注意清理空间！",
        		pIntent);
        mNotificationManager.notify(NOTIFY_ID, n);		

	}
	
	public static void cancelNotifacationById(int notifyid){
		
		NotificationManager mNotificationManager = (NotificationManager) PatientApplication.getContext().getSystemService(PatientApplication.getContext().NOTIFICATION_SERVICE);
		mNotificationManager.cancel(notifyid);
	}
}
