package com.patient.ui.patientUi.defineView;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.PaintDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.patient.constant.UrlConstant;
import com.patient.library.http.HttpRequest.HttpMethod;
import com.patient.library.http.HttpUtils;
import com.patient.library.http.RequestCallBack;
import com.patient.library.http.RequestParams;
import com.patient.library.jsonConvert.JSONConverter;
import com.patient.preference.LoginPreference;
import com.patient.ui.patientUi.entity.baseTable.PartyBean;
import com.patient.ui.patientUi.entity.extendsTable.YxckDeptBean;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

// 通用搜索界面
public class PopSelectDepart {
	
	private Context context;
	private String defaultValue[] = new String[2];
	private PopupWindow popupWindow;
	
	public PopupWindow getPopupWindow() {
		return popupWindow;
	}

	private LeftAdapter leftAdapter;
	private RightAdapter rightAdapter;
	
	// 科室类别
	private List<YxckDeptBean> deptType;
	// 具体的科室类别
	private List<YxckDeptBean> deptTypeList;
	private int leftPos = 0;
	private int rightPos = -1;
	int paddLR = 0;
	private PartyBean bean;
	
 
	private boolean isSub = false;
	
	public PopSelectDepart(Context context,View view,String defaultValue1, List<YxckDeptBean> deptType,boolean isSub) {
		
		super();
		if (!TextUtils.isEmpty(defaultValue1)) {
			defaultValue = defaultValue1.split(" ");
		}
		if (isSub) {
			paddLR = context.getResources().getDimensionPixelSize(R.dimen.padding_15);
		} 
		bean = LoginPreference.getUserInfo();
		for (int i = 0; i < deptType.size(); i++) {
			if (deptType.get(i).typeName.equals(bean.deptTypeName)) {
				leftPos = i;
				break;
			}
		}
		
		this.isSub = isSub;
		getRightData(deptType.get(leftPos).deptTypeId);
		
		this.context = context;
		this.deptType = deptType;
		
		init(view);
	}
	
	private void init(View v){
		
		View layout = LayoutInflater.from(context).inflate(R.layout.common_search_depart, null);
		ListView listView = (ListView)layout.findViewById(R.id.left);
		leftAdapter = new LeftAdapter();
		listView.setAdapter(leftAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,final int pos, long id) {
				
				  
				 deptTypeList = null;
				 rightAdapter.notifyDataSetChanged();
				 
				 leftPos = pos;
				 leftAdapter.notifyDataSetChanged();
				 getRightData(deptType.get(leftPos).deptTypeId);
				
			}
		});
		
		ListView listViewright = (ListView)layout.findViewById(R.id.right);
		rightAdapter = new RightAdapter();
		listViewright.setAdapter(rightAdapter);
		listViewright.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,final int pos, long id) {
				
				    rightPos = pos;
				    rightAdapter.notifyDataSetChanged();
					if (listener != null) {
						listener.doRefresh(deptType.get(leftPos), deptTypeList.get(rightPos));
					}
					popupWindow.dismiss();
					popupWindow = null;
				
			}
		});
		
		int[] location = new int[2];
		v.getLocationOnScreen(location);
 
		if (isSub) {
			 popupWindow = new PopupWindow(layout,v.getWidth()- 2 * paddLR, 500);
		}else{
			 popupWindow = new PopupWindow(layout,v.getWidth(), 500);
		}
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setAnimationStyle(R.style.sex_animal);
		popupWindow.setBackgroundDrawable(new PaintDrawable());
		
		popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0]+paddLR, location[1] + v.getHeight()+1);
	 
	}

	private PopDepartListener listener;
	public interface PopDepartListener{
		 
		public void doRefresh(YxckDeptBean deptType,YxckDeptBean depart );
	}
	
	public void setPopDepartListener(PopDepartListener listener){
		this.listener = listener;
	}
	
	
	private class LeftAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return deptType == null ? 0 : deptType.size();
		}

		@Override
		public Object getItem(int position) {
			return deptType == null ? 0 : deptType.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("ResourceAsColor")
		@Override
		public View getView(int position, View view, ViewGroup parent) {

			if (view == null) {
				view = LayoutInflater.from(context).inflate(R.layout.row_provience, null);
			}
			TextView leftContent = (TextView) view.findViewById(R.id.tvContent);
			leftContent.setText(deptType.get(position).typeName);
			leftContent.setTextColor(R.color.color_6a6965);
			if (leftPos == position) {
				leftContent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_point, 0, 0, 0);
				leftContent.setTextColor(context.getResources().getColor(R.color.white));
			} else {
				leftContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				leftContent.setTextColor(context.getResources().getColor(R.color.white));
			}
			return view;
		}
	}
	
	
	private class RightAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return deptTypeList == null ? 0 : deptTypeList.size();
		}

		@Override
		public Object getItem(int position) {
			return deptTypeList == null ? 0 : deptTypeList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("ResourceAsColor")
		@Override
		public View getView(int position, View view, ViewGroup parent) {

			if (view == null) {
				view = LayoutInflater.from(context).inflate(R.layout.row_provience, null);
			}
			TextView leftContent = (TextView) view.findViewById(R.id.tvContent);
			leftContent.setText(deptTypeList.get(position).deptName);
			leftContent.setTextColor(R.color.color_6a6965);
			if (rightPos == position) {
				leftContent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_point, 0, 0, 0);
				leftContent.setTextColor(context.getResources().getColor(R.color.white));
			} else {
				leftContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				leftContent.setTextColor(context.getResources().getColor(R.color.white));
			}
			return view;
		}
	}
	
	
	private void getRightData(String deptTypeId) {

		RequestParams params = new RequestParams();
		params.addQueryStringParameter("deptTypeId", deptTypeId);
		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.AJAX_DEPT_DATA, params,
				new RequestCallBack<Object>() {

					@Override
					public void onStart() {

					}

					@Override
					public void onSuccess(Object result) {

						if (result != null) {

							LogUtil.d2File(result.toString());
							JSONObject json;
							try {
								json = new JSONObject(result.toString());
								deptTypeList = JSONConverter.convertToArray(json.optString("outputList"), new TypeToken<List<YxckDeptBean>>(){});
							    if (deptTypeList != null && deptTypeList.size() > 0) {
								   for (int i = 0; i < deptTypeList.size(); i++) {
									if (deptTypeList.get(i).deptName.equals(bean.departmentName)) {
										rightPos = i;
										break;
									}
								}	
							 }
							 rightAdapter.notifyDataSetChanged();

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onFailure(Throwable error, String msg) {

						LogUtil.d2File(msg);
						CommonUtil.showError(error, msg);
					}
				});
	}
}
