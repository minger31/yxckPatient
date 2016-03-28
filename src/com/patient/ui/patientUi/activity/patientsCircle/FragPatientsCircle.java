package com.patient.ui.patientUi.activity.patientsCircle;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
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
import com.patient.ui.patientUi.activity.PatientMainFragment;
import com.patient.ui.patientUi.adapter.PatientCircleAdapter;
import com.patient.ui.patientUi.adapter.PatientCircleAdapter.Refresh;
import com.patient.ui.patientUi.defineView.flowerAnimal.SnowView;
import com.patient.ui.patientUi.entity.baseTable.PatientEduBean;
import com.patient.ui.patientUi.fragment.BaseFragment;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;
import com.patient.util.NetWorkUtil;
import com.yxck.patient.R;

/**
 * 病友圈主界面
 */
public class FragPatientsCircle extends BaseFragment {

	private final static String TAG = FragPatientsCircle.class.getName();
	private View view = null;
	private PullToRefreshListView pullToRefreshListView;
	private static PatientCircleAdapter adapter;
	private static List<PatientEduBean> list;
	private SnowView snowView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = LayoutInflater.from(getActivity()).inflate(R.layout.patients_circle_fragment, null);
		snowView = (SnowView) view.findViewById(R.id.snow);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		TitleBar bar = getTitleBar();
		bar.setTitle("病友圈", R.color.white);

		init();

		getPatientEdu(true, true);
	}

	private void init() {
		
		pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pullToRefreshListView);
		adapter = new PatientCircleAdapter(getActivity());
		adapter.setRefresh(new Refresh() {

			@Override
			public void refresh() {
				// 天女散花 3秒后自动消失

				CommonUtil.showToast("天女散花");
				snowView.LoadSnowImage();
				DisplayMetrics dm = new DisplayMetrics();
				getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
				snowView.SetView(dm.heightPixels, dm.widthPixels);
				update();

			}
		});
		
		list = new ArrayList<PatientEduBean>();
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
				getPatientEdu(false, true);
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
				getPatientEdu(false, false);
			}
		});
	}
	
 
	private RefreshHandler mRedrawHandler = new RefreshHandler();
	private class RefreshHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			//snow.addRandomSnow();
			if (msg.what == 1) {
				snowView.setVisibility(View.GONE);
			}else{
				snowView.invalidate();
				sleep(100);
			}
		}
		public void sleep(long delayMillis) {
			
			this.removeMessages(0);
			mRedrawHandler.sendEmptyMessageDelayed(0, delayMillis);
			
		}
	};

	/**
	 * Handles the basic update loop, checking to see if we are in the running
	 * state, determining if a move should be made, updating the snake's
	 * location.
	 */
	public void update() {
		snowView.setVisibility(View.VISIBLE);
		snowView.addRandomSnow();
		mRedrawHandler.sleep(200);
		mRedrawHandler.sendEmptyMessageDelayed(1, 3*1000);
	}

	private CommonWaitDialog wdg;
	private int currentDoctorPage = 1;
	private int totalPage = 1;

	/** 就诊列表 */
	private void getPatientEdu(final boolean isWait, final boolean isRefresh) {
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
		params.addQueryStringParameter("page", String.valueOf(currentDoctorPage));
		params.addQueryStringParameter("partyId", LoginPreference.getUserInfo().partyId);

		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.GET_PATIENT_EDU, params, new RequestCallBack<Object>() {

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
								List<PatientEduBean> temp = JSONConverter.convertToArray(json.optString("outputList"),new TypeToken<List<PatientEduBean>>() {});
								notifitionData(temp, isRefresh);
							} else {
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

	private void notifitionData(List<PatientEduBean> temp, boolean isRefresh) {

		if (isRefresh) {
			list.clear();
		}
		list.addAll(temp);
		adapter.setList(list);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		list = null;
		adapter = null;
	}

	public static void refresh(int requestCode, int resultCode, Intent data) {
		LogUtil.d(TAG, "----------refresh");
		if (resultCode == Activity.RESULT_OK && requestCode == CommonConstant.SUCCESS) {
			if (null == data) {
				return;
			}
			PatientEduBean bean = (PatientEduBean) data
					.getSerializableExtra(CommonConstant.KEY_RESLUT);
			if (bean != null) {
				if (list != null) {
					for (PatientEduBean peB : list) {
						if (peB.patientEduId.equals(bean.patientEduId)) {
							peB.reviewCount = bean.reviewCount;
							break;
						}
					}
				}
			}
			adapter.setList(list);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		LogUtil.d(TAG, " onResume");
		if (PatientMainFragment.TAG_PATIENTSCIRCLE.equals(PatientMainFragment.getInstance().getCurrentTag())) {

		}
	}
}
