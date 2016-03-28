package com.patient.ui.patientUi.activity.lifeLine;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.patient.preference.LoginPreference;
import com.patient.ui.patientUi.adapter.LifeLineAdapter;
import com.patient.ui.patientUi.entity.baseTable.PartyBean;
import com.patient.ui.patientUi.entity.baseTable.TreatmentBean;
import com.patient.ui.patientUi.fragment.BaseFragment;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;
import com.patient.util.NetWorkUtil;
import com.yxck.patient.R;

/**
 * 生命线
 */
public class FragLifeLine extends BaseFragment{
	
	private static final String TAG = FragLifeLine.class.getName();
	private PartyBean bean;
	private View view = null;
	private PullToRefreshListView pullToRefreshListView;
	private static List<TreatmentBean> list;
	private static LifeLineAdapter adapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = LayoutInflater.from(getActivity()).inflate(R.layout.life_line_fragment, null);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		TitleBar bar = getTitleBar();
		bar.setTitle("生命线", R.color.white);
		init();
	}
	
	private void init() {
		bean = LoginPreference.getUserInfo();
		list = new ArrayList<TreatmentBean>();
		
		pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pullToRefreshListView);
		adapter = new LifeLineAdapter(getActivity());
		pullToRefreshListView.setAdapter(adapter);
		// 下拉加载更多数据
		pullToRefreshListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {

				if (!NetWorkUtil.isNetworkConnected(getActivity())) {
					CommonUtil.showToast("请检查网络连接");
					pullToRefreshListView.onRefreshComplete();
					return;
				}
				getTreatment(false, true);
			}
		});
		// 上拉加载更多数据
		pullToRefreshListView.setOnLoadListener(new OnLoadListener() {
			@Override
			public void onLoad() {

				if (!NetWorkUtil.isNetworkConnected(getActivity())) {
					CommonUtil.showToast("请检查网络连接");
					// 显示footer
					pullToRefreshListView.onLoadComplete();
					return;
				}
				 getTreatment(false, false);
			}
		});
		getTreatment(true, true);
	}
	 
	@Override
	public void onResume() {
		super.onResume();
		LogUtil.d(TAG, " onResume");
//		if (PatientMainFragment.TAG_LIFELINE.equals(PatientMainFragment.getInstance().getCurrentTag())) {
//			getTreatment(true, true);
//		}
	}

	private CommonWaitDialog wdg;
	private int currentDoctorPage = 1;
	private int totalPage = 1;
//	public String interrogationCount;//问诊数
//	public String treatmentCount;//就诊数
//	public String followupCount;//随访数

	private void getTreatment(final boolean isWait,final boolean isRefresh) {
		if (isRefresh) {
			currentDoctorPage = 1;
			totalPage = 1;
		} else {
			currentDoctorPage++;
		}
		if (isWait) {
			wdg = new CommonWaitDialog(getActivity(), "加载数据中");
		}
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("patientId", bean.partyId);
		params.addQueryStringParameter("flag", "sysmx");
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
							
//								interrogationCount = json.optString("interrogationCount");// 问诊数
//								treatmentCount = json.optString("treatmentCount");// 就诊数
//								followupCount = json.optString("followupCount");// 随访数

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
									notifitionData(temp, isRefresh);
								} else{
//									adapter.setList(null, interrogationCount, treatmentCount, followupCount);
									adapter.setList(null);
								}
								
							} catch (JSONException e) {
								pullToRefreshListView.onRefreshComplete();
								pullToRefreshListView.onLoadComplete();
								if (isWait) {
									wdg.clearAnimation();
								}
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
						if (!isRefresh) {
							currentDoctorPage--;
						}
						LogUtil.d2File(msg);
						CommonUtil.showError(error, msg);
					}
		});
	}

	private void notifitionData(List<TreatmentBean> temp, boolean isRefresh) {
		if (isRefresh) {
			list.clear();
		}
		list.addAll(temp);
//		adapter.setList(list, interrogationCount, treatmentCount, followupCount);
		adapter.setList(list);
	}
	
	
	public static void refresh(int requestCode, int resultCode, Intent data){
		LogUtil.d(TAG, "----------refresh");
		if (resultCode == Activity.RESULT_OK && requestCode == CommonConstant.SUCCESS) {
			if (null == data) {
				return;
			}
			
			TreatmentBean treatmentBean = (TreatmentBean) data.getExtras().getSerializable(CommonConstant.KEY_RESLUT);

			if (treatmentBean != null) {
				if (list != null) { 
					for (TreatmentBean treatmentBean2 : list) {
						if (treatmentBean.treatmentId.equals(treatmentBean2.treatmentId)) {
							treatmentBean2.status = treatmentBean.status;
							adapter.setList(list);
							return;
						}
					}
				}
			}
		}
	}
 
	@Override
	public void onDestroy() {
		super.onDestroy();
		list = null;
		adapter = null;
	}
}
