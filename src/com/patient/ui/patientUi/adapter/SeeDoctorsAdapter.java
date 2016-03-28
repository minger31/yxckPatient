package com.patient.ui.patientUi.adapter;
 
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.patient.commonent.CommonDialog;
import com.patient.commonent.CommonDialog.BtnClickedListener;
import com.patient.constant.CommonConstant;
import com.patient.library.bitmap.ImageFetcher;
import com.patient.library.bitmap.ImageWorker.OnImgLoadAfterListener;
import com.patient.preference.LoginPreference;
import com.patient.ui.patientUi.activity.lifeLine.ActCommitVisit;
import com.patient.ui.patientUi.activity.lifeLine.ActPerfectMedicalInfo;
import com.patient.ui.patientUi.activity.personal.ActPersonalInfo;
import com.patient.ui.patientUi.activity.personal.ActPersonalMainPage;
import com.patient.ui.patientUi.entity.baseTable.PartyBean;
import com.patient.ui.patientUi.entity.baseTable.TreatmentBean;
import com.patient.util.LoaderImage;
import com.yxck.patient.R;

public class SeeDoctorsAdapter extends BaseAdapter {

	private List<TreatmentBean> list;
	private Context context;
	
	private boolean isSearch = false;
	public void setSearch(boolean isSearch) {
		this.isSearch = isSearch;
	}

	public void setList(List<TreatmentBean> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public SeeDoctorsAdapter(Context context) {
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
		if (!TextUtils.isEmpty(bean.doctorGv.partyheadUrl)) {
			LoaderImage.getInstance(R.drawable.ic_heads_doc).ImageLoaders(bean.doctorGv.partyheadUrl, holder.ivHead);
		}
		holder.tvName.setText(bean.doctorGv.partyName);//医生姓名
		holder.tvHospitalTitle.setText(bean.doctorGv.hospitalTitle);//医院专业职称
		holder.tvHospital.setText(bean.doctorGv.hospitalName);//医院
		holder.tvDepartment.setText(bean.doctorGv.departmentName);//科室
		final PartyBean partyBean = LoginPreference.getUserInfo();
		if (isSearch) {
			view.findViewById(R.id.tv_see_doctor).setBackgroundResource(R.drawable.btn_pressed_see_doctor1);
		}else {
			view.findViewById(R.id.tv_see_doctor).setBackgroundResource(R.drawable.btn_pressed_add_see_doctor);
		}
		view.findViewById(R.id.tv_see_doctor).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 如果个人信息为空，先去完善资料后跳转确认就诊详情界面
				if (TextUtils.isEmpty(partyBean.partyName) || TextUtils.isEmpty(partyBean.sexEnum) || TextUtils.isEmpty(partyBean.age)) {
					final CommonDialog dialog = new CommonDialog(context);
					dialog.setTitle("提示");
					dialog.setMessage("你尚未完善资料，是否完善资料");
					dialog.setPositiveButtonOpen(new BtnClickedListener() {
						
						@Override
						public void onBtnClicked() {
							dialog.dismiss();
							context.startActivity(new Intent(context, ActPersonalInfo.class));
						}
					}, context.getResources().getString(R.string.ok));
					dialog.setCancleButton(null, context.getResources().getString(R.string.cancle));
					dialog.showDialog();
				} else {
					Intent i = new Intent(context, ActCommitVisit.class);
					i.putExtra(CommonConstant.KEY_RESLUT, bean.doctorGv.partyId);
					((Activity) context).startActivity(i);
				}
			}
		});
		
		holder.ivHead.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(context, ActPersonalMainPage.class);
				i.putExtra(CommonConstant.KEY_RESLUT, bean);
				((Activity)context).startActivity(i);
 
			}
		});
		
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ActPerfectMedicalInfo.class);
				intent.putExtra(CommonConstant.KEY_RESLUT, bean);
				context.startActivity(intent);
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
