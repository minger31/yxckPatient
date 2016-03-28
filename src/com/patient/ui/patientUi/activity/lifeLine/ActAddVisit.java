package com.patient.ui.patientUi.activity.lifeLine;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.patient.commonent.CommonWaitDialog;
import com.patient.commonent.PullToRefreshListView;
import com.patient.commonent.PullToRefreshListView.OnLoadListener;
import com.patient.commonent.PullToRefreshListView.OnRefreshListener;
import com.patient.commonent.TitleBar;
import com.patient.constant.CommonConstant;
import com.patient.constant.UrlConstant;
import com.patient.library.http.HttpRequest.HttpMethod;
import com.patient.library.http.HttpUtils;
import com.patient.library.http.RequestCallBack;
import com.patient.library.http.RequestParams;
import com.patient.library.jsonConvert.JSONConverter;
import com.patient.library.zxing.view.MipcaActivityCapture;
import com.patient.preference.LoginPreference;
import com.patient.ui.patientUi.activity.common.BaseActivity;
import com.patient.ui.patientUi.adapter.SeeDoctorsAdapter;
import com.patient.ui.patientUi.entity.baseTable.PartyBean;
import com.patient.ui.patientUi.entity.baseTable.TreatmentBean;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;
import com.patient.util.NetWorkUtil;
import com.yxck.patient.R;

/**
 * 生命线-就诊-添加就诊 （搜索医生就行添加就诊）
 * <dl>
 * <dt>ActPatientInterrogation.java</dt>
 * <dd>Description:添加就诊
 * 1. 搜索医生添加就诊
 * 2. 扫描医生的二维码添加就诊</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company:</dd>
 * <dd>CreateDate: 2014-12-26 下午3:27:19</dd>
 * </dl>
 * 
 * @author lihs
 */
public class ActAddVisit extends BaseActivity implements OnClickListener{

	private EditText etSearch = null;// 输入医生名
	private LinearLayout ll1 = null;// 输入医生名
	private LinearLayout llSearch = null;// 输入医生名
	private Animation anim;

	private PullToRefreshListView pullToRefreshListView;
	private SeeDoctorsAdapter adapters;
	private List<TreatmentBean> lists;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_see_doctor);
		
		TitleBar bar = getTitleBar();
		bar.setBack("", null, R.drawable.ic_back);
		bar.setTitle("就诊", R.color.white);
		bar.enableRightBtn("", R.drawable.ic_qr_code, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ActAddVisit.this, MipcaActivityCapture.class));
			}
		});
		
		init();
	}
	 
	private void init() {
		
		lists = new ArrayList<TreatmentBean>();
		etSearch = (EditText) findViewById(R.id.et_search);// 输入医生名
		ll1 = (LinearLayout) findViewById(R.id.ll1);
		llSearch = (LinearLayout)findViewById(R.id.ll_search);
	 
		etSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
					// 先隐藏键盘
					((InputMethodManager) etSearch.getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(ActAddVisit.this .getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					// 搜索医生确认就诊
					if (!TextUtils.isEmpty(etSearch.getText().toString())) {
						getDoctor(true, true);
					} else {
						CommonUtil.showToast("请输入医生的姓名");
					}
					return true;
				}
				return false;
			}
		});
		pullToRefreshListView = (PullToRefreshListView)findViewById(R.id.pullToRefreshListView);
		adapters = new SeeDoctorsAdapter(ActAddVisit.this);
		pullToRefreshListView.setAdapter(adapters);
		// 下拉加载更多数据
		pullToRefreshListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {

				if (!NetWorkUtil.isNetworkConnected(ActAddVisit.this)) {
					CommonUtil.showToast("请检查网络连接");
					pullToRefreshListView.onRefreshComplete();
					return;
				}
				if (!TextUtils.isEmpty(etSearch.getText().toString())) {
					getDoctor(false, true);
				} else {
					getDoctors(false, true);
				}
			}
		});
		// 上拉加载更多数据
		pullToRefreshListView.setOnLoadListener(new OnLoadListener() {
			@Override
			public void onLoad() {

				if (!NetWorkUtil.isNetworkConnected(ActAddVisit.this)) {
					CommonUtil.showToast("请检查网络连接");
					// 显示footer
					pullToRefreshListView.onLoadComplete();
					return;
				}
				if (!TextUtils.isEmpty(etSearch.getText().toString())) {
					getDoctor(false, false);
				} else {
					getDoctors(false, false);
				}
			}
		});
		getDoctors(true, true);
	}
	
	private int currentDoctorPage = 1;
	private int totalPage = 1;
//	private CommonWaitDialog wdg;
	/** 搜索医生*/
	private void getDoctor(final boolean isWait,final boolean isRefresh) {
		if (isWait) {
//			wdg = new CommonWaitDialog(ActAddVisit.this, "加载中");
		}
		if (isRefresh) {
			currentDoctorPage = 1;
			totalPage = 1;
		} else {
			currentDoctorPage++;
		}
		adapters.setSearch(true);
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("flag", "jzlbss");
		params.addQueryStringParameter("name", etSearch.getText().toString());
		params.addQueryStringParameter("page", String.valueOf(currentDoctorPage));

		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.GET_OUTSIDEEXPERTS, params, new RequestCallBack<Object>() {

					@Override
					public void onStart() {

					}
					
					@Override
					public void onSuccess(Object result) {
						if (isWait) {
//							wdg.clearAnimation();
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
									List<PartyBean> temp = JSONConverter.convertToArray(json.optString("outputList"),new TypeToken<List<PartyBean>>() {});
									TreatmentBean treatmentBean = null;
									List<TreatmentBean> list = new ArrayList<TreatmentBean>();
									for (PartyBean partyBean : temp) {
										treatmentBean = new TreatmentBean();
										treatmentBean.doctorGv = partyBean;
										list.add(treatmentBean);
									}
									notifitionDatas(list, isRefresh);
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
						if (isWait) {
//							wdg.clearAnimation();
						}
						pullToRefreshListView.onRefreshComplete();
						pullToRefreshListView.onLoadComplete();
						LogUtil.d2File(msg);
						CommonUtil.showError(error, msg);
					}
		});
	}

	 
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		 
		case R.id.ll1:
			anim = new TranslateAnimation(0, 200, 0, 0);
			anim.setFillAfter(true);
			anim.setDuration(200);
			etSearch.startAnimation(anim);
			etSearch.clearAnimation();
			ll1.setGravity(Gravity.CENTER);
			ll1.setEnabled(false);
			llSearch.setEnabled(false);
			break;
		case R.id.et_search:
			anim = new TranslateAnimation(0, 200, 0, 0);
			anim.setFillAfter(true);
			anim.setDuration(200);
			etSearch.startAnimation(anim);
			etSearch.clearAnimation();
			ll1.setGravity(Gravity.CENTER);
			ll1.setEnabled(false);
			llSearch.setEnabled(false);
			break;

		default:
			break;
		}
	}
	
	CommonWaitDialog wdgs;
	/** 就诊列表*/
	private void getDoctors(final boolean isWait,final boolean isRefresh) {
		if (isWait) {
			wdgs = new CommonWaitDialog(ActAddVisit.this, "加载中");
		}
		if (isRefresh) {
			currentDoctorPage = 1;
			totalPage = 1;
		} else {
			currentDoctorPage++;
		}
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("patientId", LoginPreference.getUserInfo().partyId);
		params.addQueryStringParameter("page", String.valueOf(currentDoctorPage));

		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.GET_TREATMENT, params, new RequestCallBack<Object>() {

					@Override
					public void onStart() {

					}
					
					@Override
					public void onSuccess(Object result) {
						if (isWait) {
							wdgs.clearAnimation();
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
									List<TreatmentBean> temp = JSONConverter.convertToArray(json.optString("outputList"),new TypeToken<List<TreatmentBean>>() {});
									notifitionDatas(temp, isRefresh);
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
							wdgs.clearAnimation();
						}
						LogUtil.d2File(msg);
						CommonUtil.showError(error, msg);
					}
		});
	}

	private void notifitionDatas(List<TreatmentBean> temp, boolean isRefresh) {

		if (isRefresh) {
			lists.clear();
		}
		lists.addAll(temp);
		adapters.setList(lists);
	}
}
