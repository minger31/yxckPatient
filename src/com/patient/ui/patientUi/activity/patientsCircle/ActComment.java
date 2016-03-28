package com.patient.ui.patientUi.activity.patientsCircle;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.patient.commonent.CommonWaitDialog;
import com.patient.commonent.TitleBar;
import com.patient.constant.CommonConstant;
import com.patient.constant.UrlConstant;
import com.patient.library.http.HttpRequest.HttpMethod;
import com.patient.library.http.HttpUtils;
import com.patient.library.http.RequestCallBack;
import com.patient.library.http.RequestParams;
import com.patient.preference.LoginPreference;
import com.patient.ui.patientUi.activity.common.BaseActivity;
import com.patient.ui.patientUi.entity.baseTable.PatientEduForumBean;
import com.patient.util.CommonUtil;
import com.patient.util.DateUtil;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

 
/**
 *  病友圈 评论
 */
public class ActComment extends BaseActivity {
	
	private String patientEduId;//文章id
	private EditText etContent;
	private String contentStr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_comment);
	 
		patientEduId = getIntent().getStringExtra(CommonConstant.KEY_RESLUT);
		
		TitleBar bar = getTitleBar();
		bar.setBack("", null, R.drawable.ic_back);
		bar.setTitle("发表评论", R.color.white);
		bar.enableRightBtn("评论", -1, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				contentStr = etContent.getText().toString();
				if (!TextUtils.isEmpty(contentStr)) {
					createPatientEduforum();
				}else {
					CommonUtil.showToast("请输入评论内容");
				}

			}
		});
		etContent = (EditText)findViewById(R.id.et_content);
	}
	 
	/** 评论 */
	private void createPatientEduforum() {
		 
		final CommonWaitDialog wdg = new CommonWaitDialog(ActComment.this, "加载数据中");
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("commentsId", LoginPreference.getUserInfo().partyId);
		params.addQueryStringParameter("content", contentStr);
		params.addQueryStringParameter("patientEduId", patientEduId);
		params.addQueryStringParameter("reviewTypeEnum", "reviewTypeEnum_1");
		 
		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.CREATE_PATIENT_EDUFORUM, params, new RequestCallBack<Object>() {

					@Override
					public void onStart() {

					}
					
					@Override
					public void onSuccess(Object result) {

						wdg.clearAnimation();
						if (result != null) {

							LogUtil.d2File(result.toString());

							JSONObject json;
							try {
								json = new JSONObject(result.toString());
								String code = json.optString("responseMessage");
							
								if (CommonConstant.STATUS_SUCCESS.equals(code)) {
									CommonUtil.showToast("评论成功");
									PatientEduForumBean bean = new PatientEduForumBean();
									String formId = json.optString("forumId");
									bean.forumId = formId;
									bean.partyGv = LoginPreference.getUserInfo();
									bean.content = contentStr;
									bean.commentsTime  = DateUtil.getCurrentTimeSpecifyFormat(DateUtil.FORMAT_YYYY_MM_DD_HHMMSS_WORD_ZH);
									Intent i = new Intent();
									i.putExtra(CommonConstant.KEY_RESLUT, bean);
									setResult(Activity.RESULT_OK, i);
									finish();
								} 
							} catch (JSONException e) {
								wdg.clearAnimation();
								e.printStackTrace();
							}
					 }
					 }
					
					@Override
					public void onFailure(Throwable error, String msg) {
						wdg.clearAnimation();
						LogUtil.d2File(msg);
						CommonUtil.showError(error, msg);
					}
		});
 
	}
}
