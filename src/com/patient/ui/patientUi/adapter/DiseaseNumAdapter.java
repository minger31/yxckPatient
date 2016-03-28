package com.patient.ui.patientUi.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.patient.ui.patientUi.entity.baseTable.TreatmentBean;
import com.yxck.patient.R;

/**
 * 
 * <dl>
 * <dt>DoctorChildMeetingListAdapter.java</dt>
 * <dd>Description:获取医生下各病人数</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company: 医学参考</dd>
 * <dd>CreateDate: 2014-10-29 下午4:33:38</dd>
 * </dl>
 * 
 */
public class DiseaseNumAdapter extends BaseAdapter {

	
	private List<TreatmentBean> list = null;
	private Context context;
	private ViewHolder holder = null;
	
	public void setList(List<TreatmentBean> list) {
		this.list = list;
		notifyDataSetChanged();
	}
		
	public DiseaseNumAdapter(Context context) {
		super();
		this.context = context;
	}
	
	
	@Override
	public int getCount() {

		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return list == null ? -1 : list.get(position);
	}
 

	@Override
	public long getItemId(int position) {
		return position;
	}

	 
	@Override
	public View getView(final int position, View view, ViewGroup parent) {

		if (view != null) {
			 holder = (ViewHolder) view.getTag();
		} else {

			holder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.row_disease_num, null);
			holder.tvPatientDisease = (TextView) view.findViewById(R.id.tv_patient_disease); 
			holder.tvTreatmentNum = (TextView) view.findViewById(R.id.tv_treatment_num); 
			view.setTag(holder);
		}

		final TreatmentBean bean = list.get(position);
		holder.tvPatientDisease.setText(bean.patientDisease);
		holder.tvTreatmentNum.setText(bean.treatmentId + "例");//treatmentId在这里为病例数
		return view;
	}
	
	public class ViewHolder {
		TextView tvPatientDisease;//疾病
		TextView tvTreatmentNum;//例数
	}
}
