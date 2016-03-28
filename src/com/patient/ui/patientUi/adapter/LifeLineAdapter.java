package com.patient.ui.patientUi.adapter;

import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.patient.constant.CommonConstant;
import com.patient.db.dao.PushMessageDaoImpl;
import com.patient.preference.LoginPreference;
import com.patient.service.PushIntentService;
import com.patient.ui.patientUi.activity.lifeLine.ActAddVisit;
import com.patient.ui.patientUi.activity.lifeLine.ActFollowUp;
import com.patient.ui.patientUi.activity.lifeLine.ActPatientInterrogation;
import com.patient.ui.patientUi.activity.lifeLine.ActPerfectMedicalInfo;
import com.patient.ui.patientUi.activity.lifeLine.ActTreatmentDetail;
import com.patient.ui.patientUi.activity.personal.ActPersonalMainPage;
import com.patient.ui.patientUi.entity.baseTable.PartyBean;
import com.patient.ui.patientUi.entity.baseTable.TreatmentBean;
import com.patient.util.CommonUtil;
import com.patient.util.LoaderImage;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

/**
 * 
 * <dl>
 * <dt>DoctorChildMeetingListAdapter.java</dt>
 * <dd>Description:生命线</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company: 医学参考</dd>
 * <dd>CreateDate: 2014-10-29 下午4:33:38</dd>
 * </dl>
 * 
 */
public class LifeLineAdapter extends BaseAdapter {

	private static final String TAG = LifeLineAdapter.class.getName();
	
	// 病例话题集合
	private List<TreatmentBean> treatmentList = null;
	// 生命线头
	private static final int LIFE_LINE_TOP = 0;
	// 生命线数据
	static final int LIFE_LINE_DATA = 1;

	private Context context;
	private ViewHolder holder = null;
	
	public LifeLineAdapter(Context context) {
		super();
		this.context = context;
	}

	public void setList(List<TreatmentBean> treatmentList) {
		this.treatmentList = treatmentList;
 
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {

		return treatmentList == null ? 1 : treatmentList.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		return treatmentList == null ? -1 : treatmentList.get(position);
	}

	@Override
	public int getItemViewType(int position) {

		if (position == 0) {
			return LIFE_LINE_TOP;
		}
		return LIFE_LINE_DATA;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Override
	public View getView(final int position, View view, ViewGroup parent) {

		int type = getItemViewType(position);
		LogUtil.d(TAG, "TYPE ==" + type + "position=" + position);
		if (view != null) {
			Object tag = view.getTag();
			if (tag instanceof ViewHolder) {
				holder = (ViewHolder) tag;
			} else if (tag instanceof Integer) {
				int tagType = (Integer) tag;
				switch (tagType) {
				case LIFE_LINE_TOP:
					initProjectDetail(view);
					break;
				default:
					break;
				}
			}
		} else {
			if (LIFE_LINE_TOP == type) {
				view = LayoutInflater.from(context).inflate(R.layout.row_lifeline_top, null);
				view.setTag(LIFE_LINE_TOP);
				initProjectDetail(view);
			} else if (type == LIFE_LINE_DATA) {
				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(R.layout.row_lifeline_data, null);

				holder.ivHead = (ImageView) view.findViewById(R.id.iv_head);// 头像
				holder.tvDate = (TextView) view.findViewById(R.id.tv_date);// 时间
				holder.tvPatientDisease = (TextView) view.findViewById(R.id.tv_patient_disease);// 所患疾病
				holder.tvDoctorName = (TextView) view.findViewById(R.id.tv_doctor_name);// 医生姓名
				holder.tvHospitalTitle = (TextView) view.findViewById(R.id.tv_hospital_title);// 医院专业职称
				holder.tvHospital = (TextView) view.findViewById(R.id.tv_hospital);// 医院

				view.setTag(holder);
			}
		}

		if (type == LIFE_LINE_DATA && holder != null && holder instanceof ViewHolder) {
			final TreatmentBean bean = treatmentList.get(position - 1);
			final PartyBean partyBean = bean.doctorGv;
			holder.ivHead.setImageResource(R.drawable.ic_heads_doc);
			if (!TextUtils.isEmpty(partyBean.partyheadUrl)&& (partyBean.partyheadUrl).startsWith("http")) {
				LogUtil.d("发起人Url=" + partyBean.partyheadUrl);
				LoaderImage.getInstance(R.drawable.ic_heads_doc).ImageLoaders(partyBean.partyheadUrl, holder.ivHead);
			}
			
			// TODO 跳转个人主页
			holder.ivHead.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, ActPersonalMainPage.class);
					intent.putExtra(CommonConstant.KEY_RESLUT, bean);
					context.startActivity(intent);
				}
			});
			
			if (!TextUtils.isEmpty(bean.treatmentTime)) {
				String date = bean.treatmentTime.substring(5, 10);
				holder.tvDate.setText(date);// 时间
			}
			holder.tvPatientDisease.setText(bean.patientDisease);// 所患疾病
			holder.tvDoctorName.setText(getName(partyBean));// 医生姓名
			holder.tvHospitalTitle.setText(partyBean.hospitalTitle);// 医院专业职称
			holder.tvHospital.setText(partyBean.hospitalName);// 医院
			 
			if ("TreatmStatusEnum_2".equals(bean.status)) {//此时显示完善资料，隐藏就诊史和随访史
				view.findViewById(R.id.complete_info).setVisibility(View.VISIBLE);
				view.findViewById(R.id.followup_visits).setVisibility(View.GONE);
			}else {
				view.findViewById(R.id.complete_info).setVisibility(View.GONE);
				view.findViewById(R.id.followup_visits).setVisibility(View.VISIBLE);
			}
			view.findViewById(R.id.tv_complete_info).setOnClickListener(new OnClickListener() {
				//完善就诊信息
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, ActPerfectMedicalInfo.class);
					intent.putExtra(CommonConstant.KEY_RESLUT, bean);
					((Activity)context).startActivityForResult(intent,CommonConstant.SUCCESS);
				}
			});
			view.findViewById(R.id.tv_follow_up).setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							CommonUtil.showToast("随访");
						}
					});
			view.findViewById(R.id.tv_see_doctor).setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(context, ActTreatmentDetail.class);
							intent.putExtra(CommonConstant.KEY_RESLUT, bean.treatmentId);
							context.startActivity(intent);
						}
					});
		}
		return view;
	}

	private String getName(PartyBean bean) {
		String name = bean.partyName;
		if (TextUtils.isEmpty(name)) {
			name = bean.nickName;
			if (TextUtils.isEmpty(name)) {
				name = bean.partyId;
			}
		}
		return name;
	}

	// 话题对应实体
	public class ViewHolder {
		ImageView ivHead;// 头像
		TextView tvDate;// 时间
		TextView tvPatientDisease;// 所患疾病
		TextView tvDoctorName;// 医生姓名R
		TextView tvHospitalTitle;// 医院专业职称
		TextView tvHospital;// 医院
	}

	private void initProjectDetail(View v) {

		final String patientId = LoginPreference.getUserInfo().partyId;
		v.findViewById(R.id.fl_physician_visits).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 问诊
						v.findViewById(R.id.interrogationCount).setVisibility(View.GONE);
						new PushMessageDaoImpl(context).deleteMesgByType(PushIntentService.new_inteoration);
						Intent i = new Intent(context,ActPatientInterrogation.class);
						context.startActivity(i);

					}
				});
		v.findViewById(R.id.fl_see_doctor).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 就诊
						v.findViewById(R.id.treatmentCount).setVisibility(View.GONE);
						new PushMessageDaoImpl(context).deleteMesgByType(PushIntentService.new_treatment);
						Intent i = new Intent(context, ActAddVisit.class);
						i.putExtra(CommonConstant.KEY_RESLUT, patientId);
						context.startActivity(i);
					}
				});
		v.findViewById(R.id.fl_follow_up).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						 // 随访
						v.findViewById(R.id.followupCount).setVisibility(View.GONE);
						new PushMessageDaoImpl(context).deleteMesgByType(PushIntentService.new_followup);
						Intent i = new Intent(context,ActFollowUp.class);
						context.startActivity(i);
					}
				});
		  
		//统计数字
		v.findViewById(R.id.interrogationCount).setVisibility(View.GONE);
		v.findViewById(R.id.treatmentCount).setVisibility(View.GONE);
		v.findViewById(R.id.followupCount).setVisibility(View.GONE);
		
		if (new PushMessageDaoImpl(context).getMessageByType(PushIntentService.new_inteoration)) {
			v.findViewById(R.id.interrogationCount).setVisibility(View.VISIBLE);
		}
		
		if (new PushMessageDaoImpl(context).getMessageByType(PushIntentService.new_treatment)) {
			v.findViewById(R.id.treatmentCount).setVisibility(View.VISIBLE);
		}
		
		if (new PushMessageDaoImpl(context).getMessageByType(PushIntentService.new_followup)) {
			v.findViewById(R.id.followupCount).setVisibility(View.VISIBLE);
		}
	}
}
