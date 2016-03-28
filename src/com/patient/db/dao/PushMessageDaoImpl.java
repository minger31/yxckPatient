package com.patient.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.patient.constant.ProviderConstant;
import com.patient.db.table.PushMsgTable;
import com.patient.ui.patientUi.entity.extendsTable.RedDotBean;
import com.patient.util.LogUtil;

/**
 * @author dell
 * 消息推送 解决小红点的代码逻辑
 */
public  class PushMessageDaoImpl  implements PushMessageDao{
	
	
    private Context context;
    
	public PushMessageDaoImpl(Context context) {
		
		 super();
		 this.context = context;
	}

	@Override
	public boolean getMessageByType(String pushType) {
		
		Cursor cursor = null;
		try {
			Uri uri = Uri.parse(ProviderConstant.CACHE_URI + "/QUERY_MESSAGE/" + pushType);
			cursor = context.getContentResolver().query(uri, null,null,null,null);
			if (cursor != null && cursor.getCount() > 0) {
				LogUtil.d("查询消息");
				return true;
			}
		} catch (Exception e) {
			return false;
		} finally {
		    if (cursor != null) {
		        cursor.close();
		        cursor = null;
		    }
		}
		return false;
	}

	public boolean insertMessage(RedDotBean bean) {
		
		try {
			 Uri uri = Uri.parse(ProviderConstant.CACHE_URI + "/INSERT_MESSAGE");
			 context.getContentResolver().insert(uri, RedDotBean.parseToValue(bean));
			 return true;
		} catch (Exception e) {
			 LogUtil.d("插入消息异常："+e.getLocalizedMessage());
		}
		return false;
	}

	@Override
	public boolean deleteMesgByType(String pushType) {
		 
		try {
			Uri uri = Uri.parse(ProviderConstant.CACHE_URI + "/DELETE_MESSAGE/" + pushType);
			int count  = context.getContentResolver().delete(uri, PushMsgTable.MESSAGETYPE+"=?", new String[]{pushType});
			if (count > 0) {
				return true;
			}
		} catch (Exception e) {
			LogUtil.d("删除消息异常："+e.getLocalizedMessage());
			return false;
		} finally {
		    
		}
		return false;
	}
}
