package com.patient.ui.patientUi.defineView;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.patient.db.dao.DiseaseDaoImpl;
import com.patient.ui.patientUi.activity.lifeLine.ActCommitVisit;
import com.patient.ui.patientUi.entity.extendsTable.DiseaseBean;
import com.patient.util.CommonUtil;
import com.yxck.patient.R;

// 通用搜索界面
public class PopSelectDiease {

	private Dialog dialog;
	private Context context;
    private View contentView;
    private UnitAdapter unitAdapter;
 	private ListView listView;
 	private List<DiseaseBean> allData;
 	private DiseaseDaoImpl dao;
 	private EditText dieaseName;
	public PopSelectDiease(Context context,List<DiseaseBean> allData1,String dieaseId) {
		
		this.context = context;
		this.allData = allData1;
		dao = new DiseaseDaoImpl(context);
		if (!TextUtils.isEmpty(dieaseId)) {
			for (int i = 0; i < allData.size(); i++) {
				if (allData.get(i).disease_Id.equals(dieaseId)) {
					proPosition = i;
					break;
				}
			}
		}
		dialog = new Dialog(context, R.style.CustomProgressDialog);
	    contentView = LayoutInflater.from(context).inflate(R.layout.diease, null);
	    LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
	    params.height = (int) (CommonUtil.getDeviceSize(context).y * 0.75); // 高度设置为屏幕的0.6
	    params.width = (int) (CommonUtil.getDeviceSize(context).x * 0.9); // 高度设置为屏幕的0.6
	    dialog.addContentView(contentView, params);
	    dialog.setCanceledOnTouchOutside(false);
	    dialog.setCancelable(false);
	    dialog.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_BACK));
	    dialog.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_BACK));
	    
	    init();
	    
	    LinearLayout  leftView = (LinearLayout) contentView.findViewById(R.id.title_left);
	    TextView titleTxt = (TextView) contentView.findViewById(R.id.title_txt);
	    titleTxt.setText("选择疾病");
	    leftView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				ActCommitVisit.isCheck = true;
				dialog = null;
			}
		});
	    
	    dieaseName = (EditText)contentView.findViewById(R.id.et_search);
	    dieaseName.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// 获取数据
				getData();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
	}
	
	private  void getData(){
		
		if (TextUtils.isEmpty(dieaseName.getText().toString())) {
			 allData = dao.getAllDisease("all");
		}else{
			 allData = dao.getAllDisease(dieaseName.getText().toString());
		}
	    unitAdapter.notifyDataSetChanged();
	}
	
	private int proPosition = 0;
	private void init(){
		
		listView = (ListView)contentView.findViewById(R.id.lv_multi_select_linkman);
		unitAdapter = new UnitAdapter();
		listView.setAdapter(unitAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,final int pos, long id) {
 
				proPosition = pos;
				if (listener != null) {
					listener.doRefresh(allData.get(proPosition));
				}
				dialog.dismiss();
			}
		});
	}
	
	public void show(){
		if (dialog != null) {
			dialog.show();
		}
	}
 
	
	private PopDieaseListener listener;
	public interface PopDieaseListener{
		 
		public void doRefresh(DiseaseBean bean);
	}
	public void setDieaseListener (PopDieaseListener listener){
		this.listener = listener;
	}
	
	private class UnitAdapter extends BaseAdapter {
		

		@Override
		public int getCount() {
			return allData == null ? 0 : allData.size();
		}

		@Override
		public Object getItem(int position) {
			return allData == null ? 0 : allData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("ResourceAsColor")
		@Override
		public View getView(int position, View view, ViewGroup parent) {

			if (view == null) {
				view = LayoutInflater.from(context).inflate(R.layout.row_signal_chose, null);
			}
			
			view.findViewById(R.id.cb).setVisibility(View.GONE);
			TextView leftContent = (TextView) view.findViewById(R.id.searchItem);
			if (TextUtils.isEmpty(dieaseName.getText().toString())) {
				leftContent.setText(allData.get(position).index_Name);
			}else{ 
				String name = allData.get(position).index_Name;
				if (name.contains(dieaseName.getText().toString())) {
					name = name.replace(dieaseName.getText().toString(), "<font color='#ff0000'>"+dieaseName.getText().toString()+"</font>");
					leftContent.setText(Html.fromHtml(name));
				}
			}
			if (proPosition == position) {
				leftContent.setTextColor(context.getResources().getColor(R.color.color_267093));
			} else {
				leftContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				leftContent.setTextColor(context.getResources().getColor(R.color.black));
			}
			return view;
		}
	}
}
