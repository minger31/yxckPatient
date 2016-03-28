package com.patient.ui.patientUi.activity.common;

import java.io.Serializable;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.patient.commonent.TitleBar;
import com.patient.constant.CommonConstant;
import com.patient.db.dao.DiseaseDaoImpl;
import com.patient.ui.patientUi.entity.extendsTable.DiseaseBean;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

/**
 * @author 疾病选择的通用界面 确认就诊或者 随访模板使用
 */
public class ActChoseDiease extends BaseActivity {

	private UnitAdapter unitAdapter;
	private ListView listView;
	private List<DiseaseBean> allData;
	private DiseaseDaoImpl dao;
	private EditText dieaseName;
	private boolean isMiddle = true;
	private RelativeLayout ltSearch;
	
	private final String TAG = ActChoseDiease.class.getName();
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			unitAdapter.notifyDataSetChanged();
		}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_chose_disease);
		dao = new DiseaseDaoImpl(this);

		TitleBar bar = getTitleBar();
		bar.setBack("", null, R.drawable.ic_back);
		bar.setTitle("选择疾病", 0);

		ltSearch = (RelativeLayout) findViewById(R.id.searchlt);

		listView = (ListView) findViewById(R.id.lv_multi_select_linkman);
		unitAdapter = new UnitAdapter();
		listView.setAdapter(unitAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int pos, long id) {
				
                  Intent i = new Intent();
                  i.putExtra(CommonConstant.KEY_RESLUT, (Serializable)allData.get(pos));
                  setResult(Activity.RESULT_OK, i);
                  finish();
			}
		});
		dieaseName = (EditText)findViewById(R.id.et_search);
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
		getData();//进来默认显示全部
	}

	private void getData() {
		LogUtil.d(TAG, "------ getData()");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				if (TextUtils.isEmpty(dieaseName.getText().toString())) {
					allData = dao.getAllDisease("all");
				} else {
					allData = dao.getAllDisease(dieaseName.getText().toString());
				}
				handler.sendEmptyMessage(1);
			}
		}).start();
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
				view = LayoutInflater.from(ActChoseDiease.this).inflate(R.layout.row_diease_chose, null);
			}

			view.findViewById(R.id.cb).setVisibility(View.GONE);
			TextView leftContent = (TextView) view.findViewById(R.id.searchItem);
			if (TextUtils.isEmpty(dieaseName.getText().toString())) {
				leftContent.setText(allData.get(position).index_Name);
			} else {
				String name = allData.get(position).index_Name;
				if (name.contains(dieaseName.getText().toString())) {
					name = name.replace(dieaseName.getText().toString(), "<font color='#ff0000'>" + dieaseName.getText().toString() + "</font>");
					leftContent.setText(Html.fromHtml(name));
				}
			}
			leftContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			leftContent.setTextColor(getResources().getColor(R.color.black));
			return view;
		}
	}
}
