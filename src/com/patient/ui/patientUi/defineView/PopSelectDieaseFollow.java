package com.patient.ui.patientUi.defineView;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.PaintDrawable;
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

import com.patient.preference.LoginPreference;
import com.patient.ui.patientUi.entity.baseTable.PartyBean;
import com.patient.ui.patientUi.entity.extendsTable.DiseaseBean;
import com.yxck.patient.R;

// 通用搜索界面
public class PopSelectDieaseFollow {
	
	private Context context;
	private String defaultValue[] = new String[2];
	private PopupWindow popupWindow;
	
	public PopupWindow getPopupWindow() {
		return popupWindow;
	}
	private LeftAdapter leftAdapter;
	private List<DiseaseBean> allData;
	private List<DiseaseBean> temp;
	private int leftPos = 0;
	
	public PopSelectDieaseFollow(Context context,View view,List<DiseaseBean> disData) {
		
		super();
		this.context = context;
		this.allData = disData;
		temp = new ArrayList<DiseaseBean>();
		this.v = view;
		init(view);
	}
	
	
	public void setFiltrData(String filtrs){
		if (allData != null) {
			for (DiseaseBean bean :allData) {
				if (bean.index_Name.contains(filtrs)) {
					temp.add(bean);
				}
			}
			leftAdapter.notifyDataSetChanged();
		}
	}
	
	int[] location = new int[2];

	private View v;
	private void init(View v){
		
		View layout = LayoutInflater.from(context).inflate(R.layout.diease_follow, null);
		ListView listView = (ListView)layout.findViewById(R.id.lv_multi_select_linkman);
		leftAdapter = new LeftAdapter();
		listView.setAdapter(leftAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,final int pos, long id) {
				
				 leftPos = pos;
				 if (listener != null) {
					listener.doRefresh(temp.get(pos));
					popupWindow.dismiss();
				}
			}
		});
		
		int[] location = new int[2];
		v.getLocationOnScreen(location);
		popupWindow = new PopupWindow(layout,v.getWidth(), 500);
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setAnimationStyle(R.style.sex_animal);
		popupWindow.setBackgroundDrawable(new PaintDrawable());
	 
	}

	
	public void show(){
		if (!popupWindow.isShowing()) {
			popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1] + v.getHeight()+1);
		}
	}
	private PopDepartListener listener;
	public interface PopDepartListener{
		 
		public void doRefresh(DiseaseBean bean );
	}
	
	public void setPopDepartListener(PopDepartListener listener){
		this.listener = listener;
	}
	
	
	private class LeftAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return temp == null ? 0 : temp.size();
		}

		@Override
		public Object getItem(int position) {
			return temp == null ? 0 : temp.get(position);
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
			leftContent.setText(temp.get(position).index_Name);
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
}
