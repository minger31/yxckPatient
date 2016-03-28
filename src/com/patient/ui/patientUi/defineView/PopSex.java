package com.patient.ui.patientUi.defineView;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yxck.patient.R;

// 通用搜索界面
public class PopSex {
	
	private Context context;
	// 0 男  ；1 女
	private String data[] = null;
	private String defaultValue;
	private PopupWindow popupWindow;
	public PopupWindow getPopupWindow() {
		return popupWindow;
	}

	private SearchAdapter searchAdapter = null;
	
	int paddLR = 0;
	public PopSex(Context context,View view,String defaultValue,boolean isSub) {
		
		super();
		this.defaultValue = defaultValue;
		this.context = context;
		
		if (isSub) {
			paddLR = context.getResources().getDimensionPixelSize(R.dimen.padding_10);
		} 
		data = context.getResources().getStringArray(R.array.sex);
		
		init(view);
	}
	
	private void init(View v){
		
		View layout = LayoutInflater.from(context).inflate(R.layout.common_search, null);
		
		ListView listView = (ListView)layout.findViewById(R.id.common_search);
		searchAdapter = new SearchAdapter();
		listView.setAdapter(searchAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				
				searchAdapter.notifyDataSetChanged();
				if (listener != null) {
					listener.doRefresh(pos);
					popupWindow.dismiss();
					popupWindow = null;
					searchAdapter = null;
				}
			}
		});
		
		int[] location = new int[2];
		v.getLocationOnScreen(location);
		
	    popupWindow = new PopupWindow(layout,v.getWidth() - 2 * paddLR, LinearLayout.LayoutParams.WRAP_CONTENT);
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setAnimationStyle(R.style.sex_animal);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		
		popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0] + paddLR , location[1] + v.getHeight()+1);
	}
	
	private class SearchAdapter extends  BaseAdapter{

		@Override
		public int getCount() {
			return data.length;
		}

		@Override
		public Object getItem(int position) {
			return data[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			
			if (view == null) {
				view = LayoutInflater.from(context).inflate(R.layout.row_signal_chose, null);
			}
			
			TextView tvSearch = (TextView)view.findViewById(R.id.searchItem);
			tvSearch.setText(data[position]);
			
			CheckBox cb = (CheckBox)view.findViewById(R.id.cb);
			
			if (data[position].equals(defaultValue)) {
				cb.setChecked(true);
				tvSearch.setTextColor(context.getResources().getColor(R.color.color_6cbef8));
			}else{
				cb.setChecked(false);
				tvSearch.setTextColor(context.getResources().getColor(R.color.white));
			}
			 
			return view;
		}
	}

	private SexRefreshSearch listener;
	public interface SexRefreshSearch{
		public void doRefresh(int choseSearch);
	}
	
	public void setRefreshListener(SexRefreshSearch listener){
		this.listener = listener;
	}
}
