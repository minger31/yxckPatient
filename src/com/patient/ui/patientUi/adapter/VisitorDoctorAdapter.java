package com.patient.ui.patientUi.adapter;
 
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.patient.constant.CommonConstant;
import com.patient.library.bitmap.ImageFetcher;
import com.patient.library.bitmap.ImageWorker.OnImgLoadAfterListener;
import com.patient.ui.patientUi.activity.lifeLine.ActVisitedHistoryDetail;
import com.patient.ui.patientUi.activity.personal.ActPersonalMainPage;
import com.patient.ui.patientUi.entity.baseTable.TreatmentBean;
import com.patient.util.LoaderImage;
import com.yxck.patient.R;

public class VisitorDoctorAdapter extends BaseAdapter {

	private List<TreatmentBean> list;
	private Context context;

	public void setList(List<TreatmentBean> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public VisitorDoctorAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return list == null ? -1 : list.size();
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

		ViewHolder holder = null;

		if (view == null) {

			view = LayoutInflater.from(context).inflate(R.layout.row_see_doctor, null);
			holder = new ViewHolder();

			holder.ivHead = (ImageView) view.findViewById(R.id.iv_head);// 头像
			holder.tvName = (TextView) view.findViewById(R.id.tv_name);//医生姓名
			holder.tvHospitalTitle = (TextView) view.findViewById(R.id.tv_hospital_title);//医院专业职称
			holder.tvHospital = (TextView) view.findViewById(R.id.tv_hospital);//医院
			holder.tvDepartment = (TextView) view.findViewById(R.id.tv_department);//科室

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		final TreatmentBean bean = list.get(position);

		LoaderImage.getInstance(R.drawable.ic_heads_doc).ImageLoaders(bean.doctorGv.partyheadUrl, holder.ivHead);

		holder.tvName.setText(bean.doctorGv.partyName);//医生姓名
		holder.tvHospitalTitle.setText(bean.doctorGv.hospitalTitle);//医院专业职称
		holder.tvHospital.setText(bean.doctorGv.hospitalName);//医院
		holder.tvDepartment.setText(bean.doctorGv.departmentName);//科室

		// 历史详情
		view.findViewById(R.id.tv_see_doctor).setBackgroundResource(R.drawable.btn_ask_history_detail);
		view.findViewById(R.id.tv_see_doctor).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 就诊的历史详情
				 Intent i = new Intent(context, ActVisitedHistoryDetail.class);
				 i.putExtra(CommonConstant.KEY_RESLUT,bean.doctorId);
				 ((Activity)context).startActivity(i);
			}
		});
		 
		
		holder.ivHead.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 这个是医生的个人主页
				 Intent i = new Intent(context, ActPersonalMainPage.class);
				 i.putExtra(CommonConstant.KEY_RESLUT,bean.doctorId);
				 ((Activity)context).startActivity(i);
			}
		});
		
		return view;
	}

	private class ViewHolder {
		ImageView ivHead;// 头像
		TextView tvName;// 医生姓名
		TextView tvHospitalTitle;// 医院专业职称
		TextView tvHospital;// 医院
		TextView tvDepartment;// 科室
	}
}
