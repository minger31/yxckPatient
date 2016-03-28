package com.patient.ui.patientUi.activity.common;

import java.io.Serializable;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.patient.commonent.TitleBar;
import com.patient.constant.CommonConstant;
import com.patient.db.dao.PchDaoImpl;
import com.patient.ui.patientUi.entity.baseTable.ProvCityBean;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

public class ActChoosePCH extends BaseActivity {
	private UnitAdapter unitAdapter;
	private ListView listView;
	private List<ProvCityBean> allData;
	private PchDaoImpl dao;
	private boolean isEnd = false;//选择完医院要返回
	private final String TAG = ActChoosePCH.class.getName();
	private ProvCityBean bean ;//省份的信息  保存起来要带回去
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
		setContentView(R.layout.act_chose_hospital);
		dao = new PchDaoImpl(this);

		final TitleBar bar = getTitleBar();
		bar.setBack("", null, R.drawable.ic_back);
		bar.setTitle("选择省份", 0);

		listView = (ListView) findViewById(R.id.lv_multi_select_linkman);
		unitAdapter = new UnitAdapter();
		listView.setAdapter(unitAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int pos, long id) {
				
				if (isEnd) {
                  Intent i = new Intent();
                  i.putExtra(CommonConstant.KEY_RESLUT, (Serializable)allData.get(pos));//市
                  i.putExtra(CommonConstant.JSON, bean);//省
                  setResult(Activity.RESULT_OK, i);
                  finish();
				} else {
					isEnd = true;
					bar.setTitle("选择市区", 0);
					bean = new ProvCityBean();
					bean.geo_id = allData.get(pos).geo_id;
					bean.geo_name = allData.get(pos).geo_name;
					getData("'" + allData.get(pos).geo_id + "'");// 格式 'CN-1301'
				}
			}
		});
		getData("all");//进来默认显示全部
	}

	private void getData(final String conditions) {
		LogUtil.d(TAG, "------ getData()");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				allData = dao.getProCityData(conditions);
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
				view = LayoutInflater.from(ActChoosePCH.this).inflate(R.layout.row_hospital_chose, null);
			}

			view.findViewById(R.id.cb).setVisibility(View.GONE);
			TextView leftContent = (TextView) view.findViewById(R.id.searchItem);
			leftContent.setText(allData.get(position).geo_name);
			leftContent.setTextColor(getResources().getColor(R.color.black));
			return view;
		}
	}
}
