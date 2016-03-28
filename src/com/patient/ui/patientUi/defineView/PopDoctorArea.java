package com.patient.ui.patientUi.defineView;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
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
import com.patient.ui.patientUi.entity.extendsTable.ProvinceBean;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

//我的赞助里面选择地区
public class PopDoctorArea {
	
	public PopupWindow getPopupWindow() {
		return popupWindow;
	}
	private Context context;
	private String defaultValue[] = new String[2];
	private PopupWindow popupWindow;
	
	private LeftAdapter leftAdapter;
	private RightAdapter rightAdapter;
	
	// 省
	private List<ProvinceBean> provience;
	// 市
	private List<ProvinceBean> city;
	
	private int leftPos = 0;
	private int rightPos = -1;
	private View view;
	int paddLR = 0;
	public PopDoctorArea(Context context,View view,String defaultValue1, List<ProvinceBean> provence) {
		super();
		this.view = view;
		
		this.provience = provence;
		if (!TextUtils.isEmpty(defaultValue1)) {
			defaultValue = defaultValue1.split(" ");
			for (int i = 0; i < provence.size(); i++) {
				if (provence.get(i).geoName.equals(defaultValue[0])) {
					leftPos = i;
					break;
				}
			}
		}
		
		this.context = context;
		paddLR = context.getResources().getDimensionPixelSize(R.dimen.padding_15);
		
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
				
				 city = null;
				 rightAdapter.notifyDataSetChanged();
				 
				 leftPos = pos;
				 leftAdapter.notifyDataSetChanged();
				 getRightData(leftPos);
				
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
						listener.doRefresh(provience.get(leftPos), city.get(rightPos));
					}
					popupWindow.dismiss();
					popupWindow = null;
				
			}
		});
		
		int[] location = new int[2];
		v.getLocationOnScreen(location);
		
	    popupWindow = new PopupWindow(layout,v.getWidth(), 500);
	    
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setAnimationStyle(R.style.sex_animal);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		
		popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1] + v.getHeight()+1);

		getRightData(leftPos);
	}

	private PopDepartListeners listener;
	public interface PopDepartListeners{
		 
		public void doRefresh(ProvinceBean deptType,ProvinceBean depart );
	}
	
	public void setPopDepartListeners(PopDepartListeners listener){
		this.listener = listener;
	}
	
	private class LeftAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return provience == null ? 0 : provience.size();
		}

		@Override
		public Object getItem(int position) {
			return provience == null ? 0 : provience.get(position);
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
			leftContent.setText(provience.get(position).geoName);
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
			return city == null ? 0 : city.size();
		}

		@Override
		public Object getItem(int position) {
			return city == null ? 0 : city.get(position);
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
			leftContent.setText(city.get(position).geoName);
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
	
	
	// 获取文章
	private void getRightData(int position) {

		RequestParams params = new RequestParams();
		HttpUtils http = new HttpUtils();
		//countPage=0  是获取省；1下面的市； 2 是获取医院
		params.addQueryStringParameter("geoId", provience.get(position).geoId);
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.AJAX_GET_CITY, params,
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
							    city = JSONConverter.convertToArray(json.optString("cityList"), new TypeToken<List<ProvinceBean>>(){});
							    if (city != null && city.size() > 0 && defaultValue != null && defaultValue.length == 2) {
									for (ProvinceBean cityBen : city) {
										if (cityBen.geoName.equals(defaultValue[1])) {
											rightPos = city.indexOf(cityBen);
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
					}
				});
	}
	
}
