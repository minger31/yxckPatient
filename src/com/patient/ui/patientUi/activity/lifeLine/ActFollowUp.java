package com.patient.ui.patientUi.activity.lifeLine;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

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
import com.patient.ui.patientUi.activity.common.BaseActivity;
import com.patient.ui.patientUi.adapter.FollowUpAdapter;
import com.patient.ui.patientUi.entity.baseTable.InitiateFollowupBean;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;
import com.patient.util.NetWorkUtil;
import com.yxck.patient.R;

/**
 * 患者端  随访
 * <dl>
 * <dt>ActFollowUp.java</dt>
 * <dd>Description:TODO</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company: 医学参考报社</dd>
 * <dd>CreateDate: 2014-12-24 下午5:42:37</dd>
 * </dl>
 * 
 * @author dell
 */
public class ActFollowUp extends BaseActivity{
	
	private PullToRefreshListView pullToRefreshListView;
	private FollowUpAdapter adapter;
	private List<InitiateFollowupBean> list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_follow_up);
		
		TitleBar bar = getTitleBar();
		bar.setBack("", null, R.drawable.ic_back);
		bar.setTitle("随访记录", R.color.white);
		
		init();
	}

	private void init() {
		pullToRefreshListView = (PullToRefreshListView)findViewById(R.id.pullToRefreshListView);
		adapter = new FollowUpAdapter(ActFollowUp.this);
		list = new ArrayList<InitiateFollowupBean>();
		pullToRefreshListView.setAdapter(adapter);
		
		// 下拉加载更多数据
		pullToRefreshListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {

				if (!NetWorkUtil.isNetworkConnected(ActFollowUp.this)) {
					CommonUtil.showToast("请检查网络连接");
					pullToRefreshListView.onRefreshComplete();
					return;
				}
				getInitiateFollowup(false, true);
			}
		});
		// 上拉加载更多数据
		pullToRefreshListView.setOnLoadListener(new OnLoadListener() {
			@Override
			public void onLoad() {

				if (!NetWorkUtil
						.isNetworkConnected(ActFollowUp.this)) {
					CommonUtil.showToast("请检查网络连接");
					// 显示footer
					pullToRefreshListView.onLoadComplete();
					return;
				}
				getInitiateFollowup(false, false);
			}

		});
		getInitiateFollowup(true, true);
	}
	
	private CommonWaitDialog wdg;
	private int currentDoctorPage = 1;
	private int totalPage = 1;

	// 获取文章评论列表
	private void getInitiateFollowup(final boolean isWait, final boolean isRefresh) {

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
		http.send(HttpMethod.POST, UrlConstant.GET_INITIATE_FOLLOWUP,
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
									List<InitiateFollowupBean> temp = JSONConverter.convertToArray(json.optString("outputList"), new TypeToken<List<InitiateFollowupBean>>() {});
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

	private void refreshData(List<InitiateFollowupBean> temp, boolean isRefresh) {
		if (isRefresh) {
			list.clear();
		}
		list.addAll(temp);
		adapter.setList(list);
	}
	
}
