package com.patient.ui.patientUi.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.patient.constant.CommonConstant;
import com.patient.ui.patientUi.activity.common.ActPreViewIcon;
import com.patient.ui.patientUi.activity.lifeLine.ActDoctorSchdule;
import com.patient.ui.patientUi.activity.personal.ActPersonalMainPage;
import com.patient.ui.patientUi.entity.baseTable.PartyBean;
import com.patient.ui.patientUi.entity.baseTable.TreatmentBean;
import com.patient.ui.patientUi.entity.extendsTable.PatientIntegerationBean;
import com.patient.util.CommonUtil;
import com.patient.util.DateUtil;
import com.patient.util.LoaderImage;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

/**
 * 
 * <dl>
 * <dt>DoctorChildMeetingListAdapter.java</dt>
 * <dd>Description:问诊列表的数据</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company: 医学参考</dd>
 * <dd>CreateDate: 2014-10-29 下午4:33:38</dd>
 * </dl>
 * 
 */
public class AddPatientIntegAdapter extends BaseAdapter {

	private static final String TAG = AddPatientIntegAdapter.class.getName();

	// 患者问诊集合
	private List<PartyBean> doctorUser = null;
	private Context context;
	private ViewHolder holder = null;
	
	public AddPatientIntegAdapter(Context context) {
		super();
		this.context = context;
	}

	public void setList(List<PatientIntegerationBean> patientUser) {
		
		if (doctorUser == null) {
			doctorUser = new ArrayList<PartyBean>();
		}else{
			doctorUser.clear();
		}
		if (patientUser != null && patientUser.size() > 0) {
			PartyBean userInfo = null;
			List<PartyBean> temp = null;
			
			for (PatientIntegerationBean inteerbean : patientUser) {
				 temp = inteerbean.getOpenScheduleList();
				 if (temp.size() == 0) {
					 userInfo = new PartyBean();
					 userInfo.interrogationId = inteerbean.interrogationId;
					 userInfo.description = inteerbean.description;
					 userInfo.createTime = inteerbean.createTime;
					 userInfo.basicImage = inteerbean.basicImage;
					 doctorUser.add(userInfo);
				}else {
					// 显示所有的数据
					for (PartyBean user : temp) {
						user.interrogationId = inteerbean.interrogationId;
						user.description = inteerbean.description;
						user.createTime = inteerbean.createTime;
						user.basicImage = inteerbean.basicImage;
						doctorUser.add(user);
					}
				}
			}
		}
		
		LogUtil.d(TAG, "问诊的个数："+doctorUser.size());
		// 把数据全部转换成 partyBean 进行处理
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {

		return doctorUser == null ? 0 : doctorUser.size();
	}

	@Override
	public Object getItem(int position) {
		return doctorUser == null ? -1 : doctorUser.get(position);
	}
 

	@Override
	public long getItemId(int position) {
		return position;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Override
	public View getView(final int position, View view, ViewGroup parent) {

		if (view != null) {
			 holder = (ViewHolder) view.getTag();
		} else {

			holder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.row_ask_interration, null);
			holder.ltAsk = (LinearLayout)view.findViewById(R.id.askInfoLt);
			holder.tvDate = (TextView)view.findViewById(R.id.time_ask);// 问诊时间
			holder.askDescription = (TextView)view.findViewById(R.id.tv_ask_description);
			holder.userRl = (RelativeLayout)view.findViewById(R.id.userRl);
			
			holder.ivHead = (ImageView) view.findViewById(R.id.partyheadUrl);// 头像
			holder.doctorDuty = (TextView) view.findViewById(R.id.tv_duty);// 所患疾病
			holder.tvDoctorName = (TextView) view.findViewById(R.id.tv_party_name);// 医生姓名
			holder.depart = (TextView) view.findViewById(R.id.tvCertificationBook);// 科室
			holder.tvHospital = (TextView) view.findViewById(R.id.tv_worker_unit);// 医院
			holder.lookSchdule = (TextView) view.findViewById(R.id.lookSchdule);//查看日程
			holder.gridView = (GridView) view.findViewById(R.id.grid_view);

			view.setTag(holder);
		
		}
		 
		final PartyBean bean = doctorUser.get(position);
		holder.tvDate.setVisibility(View.GONE);
		
		holder.ltAsk.setVisibility(View.VISIBLE);
		
		if (isShowAskDetail(position)) {
			holder.tvDate.setVisibility(View.VISIBLE);
			holder.tvDate.setText(getTime(position));
		}
		
		holder.gridView.setVisibility(View.GONE);
		holder.askDescription.setVisibility(View.GONE);
		
		if (isShowAskContent(position)) {
			if (!TextUtils.isEmpty(bean.basicImage)) {
				holder.gridView.setVisibility(View.VISIBLE);
				initCommentsPic(holder.gridView, bean);
			}
			holder.askDescription.setVisibility(View.VISIBLE);
			holder.askDescription.setText(bean.description);
		} 
		String doctorId = bean.partyId;
		holder.userRl.setVisibility(View.VISIBLE);
		if (!TextUtils.isEmpty(doctorId)) {
			// 如果医生信息不为空那就显示医生的信息
			// 显示用户的信息
			holder.ivHead.setBackgroundColor(context.getResources().getColor(R.color.color_e5e5e5));
			if (!TextUtils.isEmpty(bean.partyheadUrl) && (bean.partyheadUrl).startsWith("http")) {
				LogUtil.d("发起人Url=" + bean.partyheadUrl);
				LoaderImage.getInstance(-1).ImageLoaders(bean.partyheadUrl, holder.ivHead);
			}
			holder.tvDoctorName.setText(getName(bean));// 医生姓名
			holder.tvHospital.setText(bean.hospitalName);// 医院
			holder.depart.setText(bean.departmentName);
			holder.doctorDuty.setText(bean.hospitalTitle);
			
			final TreatmentBean bean2 = new TreatmentBean();
			bean2.doctorGv = bean;// 医生个人主页需要TreatmentBean，需把PartyBean转化为TreatmentBean
			holder.ivHead.setOnClickListener(new OnClickListener() {
					
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context,ActPersonalMainPage.class);
					intent.putExtra(CommonConstant.KEY_RESLUT, bean2);
					context.startActivity(intent);
				}
			});
				
			holder.lookSchdule.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Date dateFrom = DateUtil.formatString2Date( bean.createdStamp, DateUtil.FORMAT_YYYY_MM_DD_HHMMSS_WORD_ZH);
					int days = DateUtil.realDateIntervalDay(dateFrom, new Date());
					if (days > 14) {
						// 大于两周的时间过期（患者端目前可以观看的时间是半个月）
						CommonUtil.showToast(getName(bean) + "给你开放的日程已过期 ");
					} else {
						// 查看日程,查看医生的日程，患者端看到的日期分
						// 自开放日程的时间开始算，14天为有效期，过了14天 就是日程开放过期处理
						Intent i = new Intent(context, ActDoctorSchdule.class);
						i.putExtra(CommonConstant.KEY_RESLUT, (14 - days) + "");
						i.putExtra(CommonConstant.KEY_ID, bean.partyId);
						((Activity) context).startActivity(i);
					}
				}
			});

		} else{
			holder.userRl.setVisibility(View.GONE);
		}
		 
		 if (position == getCount() -1) {
			view.findViewById(R.id.line).setVisibility(View.INVISIBLE);
		}
		return view;
	}
	
	// 初始化图片信息
	private void initCommentsPic(final GridView gv, final PartyBean bean) {
		ImageAdapter imageAdapter = new ImageAdapter(context, gv, bean.basicImage);
		gv.setAdapter(imageAdapter);
	}
	
	private boolean isShowAskDetail(int position){
		if (position == 0) {
			return true;
		}else if (position < doctorUser.size()) {
			if (!doctorUser.get(position).interrogationId.equals(doctorUser.get(position -1).interrogationId)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isShowAskContent(int position){
		int next = position + 1;
		if (next < doctorUser.size()) {
			if (!doctorUser.get(position).interrogationId.equals(doctorUser.get(next).interrogationId)) {
				return true;
			}
		}else{
			return true;
		}
		return false;
	}
	
	
	private String getTime(int position){
		 String time = doctorUser.get(position).createTime;
		 if (!TextUtils.isEmpty(time)) {
			 time = time.substring(5,11);
			 time ="-"+time;
		}
		return time;
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
 
	// 问诊数据
	public class ViewHolder {
		
		RelativeLayout userRl;//用户信息
		LinearLayout ltAsk;
		TextView tvDate;// 问诊时间
		TextView askDescription;// 问诊描述
		
		// 开放日程的医生
		ImageView ivHead;// 头像
		TextView doctorDuty;// 医生职责
		TextView tvDoctorName;// 医生姓名
		TextView depart;// 科室
		TextView tvHospital;// 医院
		TextView lookSchdule;//查看日程
		GridView gridView;
	}
	
	// 图片adapter
	private class ImageAdapter extends BaseAdapter {

		Context context;
		int channelWidth = 0;
		int mImageThumbSpacing;
		String pic[];
		ArrayList<String> reslut;

		public ImageAdapter(Context context, GridView gv, String pics) {
			super();
			this.context = context;

			if (!TextUtils.isEmpty(pics)) {
				pic = pics.split(",");
				reslut = new ArrayList<String>();
				for (int i = 0; i < pic.length; i++) {
					String string = pic[i];
					if (!TextUtils.isEmpty(string)) {
						reslut.add(string);
					}
				}
			}

			int screenwidth = CommonUtil.getDeviceSize(context).x;
			mImageThumbSpacing = (int) context.getResources().getDimensionPixelSize(R.dimen.padd_wh);
			int padd = (int) context.getResources().getDimensionPixelSize(R.dimen.padding_20);
			int count = reslut.size();
			if (reslut.size() % 2 == 0) {
				count = reslut.size() / 2;
			}
			if (count >= 2) {
				count = 4;
			} else {
				count = 2;
			}
			channelWidth = (screenwidth - mImageThumbSpacing * (count - 1) - padd) / count;
			LogUtil.d(TAG, "channelWidth = " + channelWidth);
			gv.setNumColumns(count);
			LinearLayout.LayoutParams imgGridLp = (LinearLayout.LayoutParams) gv.getLayoutParams();
			gv.setColumnWidth(channelWidth);
			imgGridLp.width = screenwidth - padd;
			imgGridLp.gravity = Gravity.LEFT;
			int heightCount = reslut.size() % count;
			if (heightCount != 0) {
				imgGridLp.height = channelWidth * (reslut.size() / count + 1)
						+ mImageThumbSpacing * (reslut.size() / count);
			} else {
				imgGridLp.height = channelWidth * (reslut.size() / count)
						+ mImageThumbSpacing * (reslut.size() / count - 1);
			}
			gv.setLayoutParams(imgGridLp);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return pic == null ? 0 : pic.length;
		}

		@Override
		public Object getItem(int position) {
			return pic == null ? 0 : pic[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {

			ViewHolder holder = null;
			if (view == null) { 
				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(R.layout.row_magazine_layout, null);
				holder.tvChannel = (ImageView) view.findViewById(R.id.magazine);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			LinearLayout.LayoutParams magazine = (LinearLayout.LayoutParams) holder.tvChannel
					.getLayoutParams();
			magazine.width = channelWidth;
			magazine.height = channelWidth;
			holder.tvChannel.setLayoutParams(magazine);
			holder.tvChannel.setBackgroundColor(context.getResources().getColor(R.color.color_e5e5e5));
			final ImageView iv = holder.tvChannel;
			String url = pic[position];
			if (!TextUtils.isEmpty(url) && (url).startsWith("http")) {
				LogUtil.d("Url=" + url);

				LoaderImage.getInstance(0).ImageLoaders(url, iv);
			}

			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Bundle bundle = new Bundle();
					bundle.putStringArrayList(ActPreViewIcon.KEY_ALL_ICON, reslut);
					bundle.putInt(ActPreViewIcon.KEY_CURRENT_ICON, position);
					Intent intent = new Intent(context, ActPreViewIcon.class);
					intent.putExtras(bundle);
					((Activity)context).startActivityForResult(intent, 2);
				}
			});

			return view;
		}

		class ViewHolder {
			ImageView tvChannel;
		}
	}
}
