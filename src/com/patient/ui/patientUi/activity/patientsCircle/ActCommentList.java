package com.patient.ui.patientUi.activity.patientsCircle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.gson.reflect.TypeToken;
import com.patient.commonent.CommonDialog;
import com.patient.commonent.CommonWaitDialog;
import com.patient.commonent.PullToRefreshListView;
import com.patient.commonent.CommonDialog.BtnClickedListener;
import com.patient.commonent.PullToRefreshListView.OnLoadListener;
import com.patient.commonent.PullToRefreshListView.OnRefreshListener;
import com.patient.commonent.TitleBar;
import com.patient.constant.CommonConstant;
import com.patient.constant.UrlConstant;
import com.patient.library.bitmap.ImageCache;
import com.patient.library.bitmap.ImageCache.ImageCacheParams;
import com.patient.library.bitmap.ImageFetcher;
import com.patient.library.http.HttpRequest.HttpMethod;
import com.patient.library.http.HttpUtils;
import com.patient.library.http.RequestCallBack;
import com.patient.library.http.RequestParams;
import com.patient.library.jsonConvert.JSONConverter;
import com.patient.preference.LoginPreference;
import com.patient.ui.patientUi.activity.common.BaseActivity;
import com.patient.ui.patientUi.activity.personal.ActPersonalInfo;
import com.patient.ui.patientUi.adapter.CommentAdapter;
import com.patient.ui.patientUi.entity.baseTable.PartyBean;
import com.patient.ui.patientUi.entity.baseTable.PatientEduBean;
import com.patient.ui.patientUi.entity.baseTable.PatientEduForumBean;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;
import com.patient.util.NetWorkUtil;
import com.yxck.patient.R;

 
/**
 *  病友圈 评论列表
 */
public class ActCommentList extends BaseActivity {
	
 	private PullToRefreshListView pullToRefreshListView;
	private CommentAdapter adapter;
	private List<PatientEduForumBean> list;
	private PatientEduBean bean;
	private PartyBean partyBean;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_comment_list);
		
		partyBean = LoginPreference.getUserInfo();
		
		bean = (PatientEduBean) getIntent().getSerializableExtra(CommonConstant.KEY_RESLUT);
		TitleBar bar = getTitleBar();
		bar.setBack("", new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.putExtra(CommonConstant.KEY_RESLUT, bean);
				i.putExtra(CommonConstant.VALUE, list.size());
				setResult(Activity.RESULT_OK, i);
				finish();
			}
		}, R.drawable.ic_back);
		bar.setTitle("评论列表", R.color.white);
		bar.enableRightBtn("评论", -1, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (TextUtils.isEmpty(partyBean.partyName) || TextUtils.isEmpty(partyBean.sexEnum) || TextUtils.isEmpty(partyBean.age)) {
					final CommonDialog dialog = new CommonDialog(ActCommentList.this);
					dialog.setTitle("提示");
					dialog.setMessage("你尚未完善资料，是否完善资料");
					dialog.setPositiveButtonOpen(new BtnClickedListener() {

						@Override
						public void onBtnClicked() {
							dialog.dismiss();
							startActivity(new Intent(ActCommentList.this,ActPersonalInfo.class));
						}
					}, getResources().getString(R.string.ok));
					dialog.setCancleButton(null, getResources().getString(R.string.cancle));
					dialog.showDialog();
				} else {
					Intent intent = new Intent(ActCommentList.this, ActComment.class);
					intent.putExtra(CommonConstant.KEY_RESLUT, bean.patientEduId);
					startActivityForResult(intent, CommonConstant.SUCCESS);
				}
			}
		});
		list = new ArrayList<PatientEduForumBean>();
		
		init();
		
		getPatientEduForum(true, true);
	}
	 
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent i = new Intent();
			i.putExtra(CommonConstant.KEY_RESLUT, bean);
			i.putExtra(CommonConstant.VALUE, list.size());
			setResult(Activity.RESULT_OK, i);
			finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (CommonConstant.SUCCESS == requestCode && resultCode == Activity.RESULT_OK) {
			PatientEduForumBean bean1 = (PatientEduForumBean) data.getSerializableExtra(CommonConstant.KEY_RESLUT);
			if (bean != null) {
				list.add(0, bean1);
				bean.reviewCount = TextUtils.isEmpty(bean.reviewCount)?"1":(Integer.parseInt(bean.reviewCount)+1)+"";
				adapter.setList(list);
			}
		}
	}

	private void init() {
		pullToRefreshListView = (PullToRefreshListView)findViewById(R.id.pullToRefreshListView);
		adapter = new CommentAdapter(ActCommentList.this);
		pullToRefreshListView.setAdapter(adapter);
		// 下拉加载更多数据
		pullToRefreshListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {

				if (!NetWorkUtil.isNetworkConnected(ActCommentList.this)) {
					CommonUtil.showToast("请检查网络连接");
					pullToRefreshListView.onRefreshComplete();
					return;
				}
				getPatientEduForum(false, true);
			}
		});
		// 上拉加载更多数据
		pullToRefreshListView.setOnLoadListener(new OnLoadListener() {
			@Override
			public void onLoad() {

				if (!NetWorkUtil.isNetworkConnected(ActCommentList.this)) {
					CommonUtil.showToast("请检查网络连接");
					// 显示footer
					pullToRefreshListView.onLoadComplete();
					return;
				}
				getPatientEduForum(false, false);
			}
		});
	}
	
	private CommonWaitDialog wdg;
	private int currentDoctorPage = 1;
	private int totalPage = 1;
	/** 评论列表 */
	private void getPatientEduForum(final boolean isWait,final boolean isRefresh) {
		if (isRefresh) {
			currentDoctorPage = 1;
			totalPage = 1;
		} else {
			currentDoctorPage++;
		}
		if (isWait) {
			wdg = new CommonWaitDialog(ActCommentList.this, "加载数据中");
		}
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("page", String.valueOf(currentDoctorPage));
		params.addQueryStringParameter("patientEduId", bean.patientEduId);
		params.addQueryStringParameter("reviewTypeEnum", "reviewTypeEnum_1");
		 
		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.GET_PATIENT_EDU_FORUM, params, new RequestCallBack<Object>() {

					@Override
					public void onStart() {

					}
					
					@Override
					public void onSuccess(Object result) {

						if (isWait) {
							wdg.clearAnimation();
						}
						pullToRefreshListView.onRefreshComplete();
						pullToRefreshListView.onLoadComplete();
						if (result != null) {

							LogUtil.d2File(result.toString());

							JSONObject json;
							try {
								json = new JSONObject(result.toString());
								int code = json.optInt("status");
							
								currentDoctorPage = json.optInt("page");
								totalPage = json.optInt("total");
								if (currentDoctorPage >= totalPage) {
									pullToRefreshListView.setLoadFull(true);
								} else {
									pullToRefreshListView.setLoadFull(false);
								}
								if (code == CommonConstant.SUCCESS) {
									// 解析返回的数据类型
									List<PatientEduForumBean> temp = JSONConverter.convertToArray(json.optString("outputList"),new TypeToken<List<PatientEduForumBean>>() {});
									notifitionData(temp, isRefresh);
								} else{
									adapter.setPatientEduBean(bean);
									adapter.setList(null);
								}
								
							} catch (JSONException e) {
								pullToRefreshListView.onRefreshComplete();
								if (!isRefresh) {
									currentDoctorPage--;
								}
								e.printStackTrace();
							}
					 }
					 }
					
					@Override
					public void onFailure(Throwable error, String msg) {

						pullToRefreshListView.onRefreshComplete();
						pullToRefreshListView.onLoadComplete();
						if (isWait) {
							wdg.clearAnimation();
						}
						LogUtil.d2File(msg);
						CommonUtil.showError(error, msg);
					}
		});
 
	}

	private void notifitionData(List<PatientEduForumBean> temp, boolean isRefresh) {

		if (isRefresh) {
			list.clear();
		}
		list.addAll(temp);
		adapter.setPatientEduBean(bean);
		adapter.setList(list);
		pullToRefreshListView.setSelection(2);
	}
	
}
