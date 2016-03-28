package com.patient.ui.patientUi.activity.lifeLine;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.patient.ui.patientUi.activity.patientsCircle.ActCommentList;
import com.patient.ui.patientUi.activity.personal.ActPersonalInfo;
import com.patient.ui.patientUi.adapter.AddPatientIntegAdapter;
import com.patient.ui.patientUi.entity.baseTable.PartyBean;
import com.patient.ui.patientUi.entity.extendsTable.PatientIntegerationBean;
import com.patient.util.CommonUtil;
import com.patient.util.LoaderImage;
import com.patient.util.LogUtil;
import com.patient.util.NetWorkUtil;
import com.yxck.patient.R;

/**
 * <dl>
 * <dt>ActPatientInterrogation.java</dt>
 * <dd>Description:患者问诊</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company: </dd>
 * <dd>CreateDate: 2014-12-26 下午3:27:19</dd>
 * </dl>
 * 
 * @author lihs
 */
public class ActPatientInterrogation extends BaseActivity {
	
	
	private static final String TAG = ActPatientInterrogation.class.getName();
	private static final int REQUEST_ADD_INTEGERATION = 1;// 
	
	private AddPatientIntegAdapter integerAdapter;
	private PullToRefreshListView pullToRefreshListView;
	// 患者问诊数据
	private List<PatientIntegerationBean> patientsAskData;
	private PartyBean partyBean;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.life_line_fragment);
		LogUtil.d(TAG,"ActPatientInterrogation.oncreate");
		partyBean = LoginPreference.getUserInfo();
		
		TitleBar bar = getTitleBar();
		bar.setBack("", null, R.drawable.ic_back);
		bar.setTitle("问诊", R.color.white);
		bar.setBack("", null,R.drawable.ic_back);
		bar.enableRightBtn("添加问诊", 0, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(partyBean.partyName) || TextUtils.isEmpty(partyBean.sexEnum) || TextUtils.isEmpty(partyBean.age)) {
					final CommonDialog dialog = new CommonDialog(ActPatientInterrogation.this);
					dialog.setTitle("提示");
					dialog.setMessage("你尚未完善资料，是否完善资料");
					dialog.setPositiveButtonOpen(new BtnClickedListener() {
  
						@Override
						public void onBtnClicked() {
							dialog.dismiss();
							startActivity(new Intent(ActPatientInterrogation.this,ActPersonalInfo.class));
						}
					}, getResources().getString(R.string.ok));
					dialog.setCancleButton(null, getResources().getString(R.string.cancle));
					dialog.showDialog();
				} else {
					Intent i = new Intent(ActPatientInterrogation.this,ActAddInterrgeration.class);
					startActivityForResult(i, CommonConstant.SUCCESS);
				}
			}
		});
		
		init();
	}
	
	
	private void init() {
		
		pullToRefreshListView = (PullToRefreshListView)findViewById(R.id.pullToRefreshListView);
		// 下拉加载更多数据
		pullToRefreshListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {

				if (!NetWorkUtil.isNetworkConnected(ActPatientInterrogation.this)) {
					CommonUtil.showToast("请检查网络连接");
					pullToRefreshListView.onRefreshComplete();
					return;
				}
				getIntegerationData(false,true);
			}
		});
		// 上拉加载更多数据
		pullToRefreshListView.setOnLoadListener(new OnLoadListener() {
			@Override
			public void onLoad() {

				if (!NetWorkUtil.isNetworkConnected(ActPatientInterrogation.this)) {
					CommonUtil.showToast("请检查网络连接");
					// 显示footer
					pullToRefreshListView.onLoadComplete();
					return;
				}
				getIntegerationData(false,false);
			}
			
		});
		integerAdapter = new AddPatientIntegAdapter(this);
		pullToRefreshListView.setAdapter(integerAdapter);
		
		getIntegerationData(true, true);
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_ADD_INTEGERATION && resultCode == Activity.RESULT_OK) {
			// TODO 添加新增的数据到列表中处理
			PatientIntegerationBean bean = (PatientIntegerationBean) data.getSerializableExtra(CommonConstant.KEY_RESLUT);
			if (patientsAskData == null) {
				patientsAskData = new ArrayList<PatientIntegerationBean>();
			}
			 patientsAskData.add(0, bean);
			 integerAdapter.setList(patientsAskData);
		}else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
		}
	}
	
	
	
	private CommonWaitDialog wdg;
	private int currentDoctorPage = 1;
	private int totalPage = 1;

	// 获取文章评论列表
	private void getIntegerationData(final boolean isWait, final boolean isRefresh) {

		if (isWait) {
			wdg = new CommonWaitDialog(this, "加载数据中");
		}

		if (isRefresh) {
			currentDoctorPage = 1;
			totalPage = 1;
		} else {
			currentDoctorPage++;
		}

		RequestParams params = new RequestParams();
		params.addQueryStringParameter("patientId", LoginPreference.getUserInfo().partyId);
		params.addQueryStringParameter("page", currentDoctorPage+"");
		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.GET_INTERROGATION,
				params, new RequestCallBack<Object>() {

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
									List<PatientIntegerationBean> temp = JSONConverter.convertToArray(json.optString("outputList"), new TypeToken<List<PatientIntegerationBean>>() {});
									refreshData(temp, isRefresh);
								}
							} catch (JSONException e) {
								e.printStackTrace();
								if (isWait) {
									wdg.clearAnimation();
								}
								if (!isRefresh) {
									currentDoctorPage--;
								}
								pullToRefreshListView.onRefreshComplete();
								pullToRefreshListView.onLoadComplete();
							}
						}
					}

					@Override
					public void onFailure(Throwable error, String msg) {
						if (isWait) {
							wdg.clearAnimation();
						}
						if (!isRefresh) {
							currentDoctorPage--;
						}
						pullToRefreshListView.onRefreshComplete();
						pullToRefreshListView.onLoadComplete();
						LogUtil.d2File(msg);
						CommonUtil.showError(error, msg);
					}
				});
	}

	private void refreshData(List<PatientIntegerationBean> temp, boolean isRefresh) {
		if (patientsAskData == null) {
			patientsAskData = new ArrayList<PatientIntegerationBean>();
		}
		 if (isRefresh) {
			 patientsAskData.clear();
		 }
		 if (temp != null) {
			 patientsAskData.addAll(temp);
		 }
		 integerAdapter.setList(patientsAskData);
	}
	 
}
