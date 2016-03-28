package com.patient.ui.patientUi.adapter;

import java.util.List;

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
import com.patient.ui.patientUi.activity.lifeLine.ActFillFollowup;
import com.patient.ui.patientUi.activity.lifeLine.ActSmallTalk;
import com.patient.ui.patientUi.activity.personal.ActPersonalMainPage;
import com.patient.ui.patientUi.entity.baseTable.InitiateFollowupBean;
import com.patient.ui.patientUi.entity.baseTable.TreatmentBean;
import com.patient.util.CommonUtil;
import com.patient.util.DateUtil;
import com.patient.util.LoaderImage;
import com.yxck.patient.R;

/**
 * 	患者端  随访
 * <dl>
 * <dt>WorkerProjectAdapter.java</dt>
 * <dd>Description: </dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company: 医学参考报社</dd>
 * <dd>CreateDate: 2014-11-27 下午2:48:31</dd>
 * </dl>
 * 
 * @author dell
 */
public class FollowUpAdapter extends BaseAdapter {

	private static final String TAG = FollowUpAdapter.class.getName();
	private ViewHolder holder = null;

	private List<InitiateFollowupBean> list = null;
	private Context context = null;

	public FollowUpAdapter(Context context) {
		this.context = context;
	}

	public void setList(List<InitiateFollowupBean> list) {
		this.list = list;
		notifyDataSetChanged();
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

		if (view == null) { 
			holder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.row_follow_up, null);
			
			holder.ivHead = (ImageView)view.findViewById(R.id.iv_head);// 头像
			holder.tvDoctorName = (TextView)view.findViewById(R.id.tv_doctor_name);// 姓名
			holder.tvDate = (TextView)view.findViewById(R.id.tv_date);//日期
			holder.tvContext = (TextView)view.findViewById(R.id.tv_context);//内容
			
			view.setTag(holder);
		} else {
			holder = (ViewHolder)view.getTag();
		}
		
		final InitiateFollowupBean bean = list.get(position);
		
		LoaderImage.getInstance(R.drawable.ic_heads_doc).ImageLoaders(bean.doctorGv.partyheadUrl, holder.ivHead);
 		
		String name = bean.doctorGv.partyName == null?(bean.doctorGv.nickName == null?bean.doctorGv.partyId:bean.doctorGv.nickName):bean.doctorGv.partyName;
 		
		holder.tvDoctorName.setText(name);
 		String time = DateUtil.getNewFormatDateString(bean.createdStamp, DateUtil.FORMAT_YYYY_MM_DD_HHMMSS_WORD_ZH, DateUtil.FORMAT_MM_DD);
		holder.tvDate.setText(time);//日期
		if ("followupType_1".equals(bean.followupType)) {//followupType_1是随访
			holder.tvContext.setText(name + "给您发了一条随访");//
		}else if("followupType_2".equals(bean.followupType)) {//followupType_2是微访
			holder.tvContext.setText(name + "给您发了一条消息"); 
		}
  
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if ("followupType_1".equals(bean.followupType)) {//followupType_1是随访
					Intent intent = new Intent(context,ActFillFollowup.class);
					intent.putExtra(CommonConstant.KEY_RESLUT, bean.initFollowupId);
					context.startActivity(intent);
				}else if("followupType_2".equals(bean.followupType)) {//followupType_2是微访
					Intent intent = new Intent(context,ActSmallTalk.class);
					intent.putExtra(CommonConstant.KEY_RESLUT, bean);
					System.out.println(bean.treatmentId);
					context.startActivity(intent);
				}
			}
		});
		
		final TreatmentBean bean2 = new TreatmentBean();
		bean2.doctorGv = bean.doctorGv;
		holder.ivHead.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 跳转到患者的生命线，生命线里面有 就诊历史和随访历史
				Intent i = new Intent(context,ActPersonalMainPage.class);
				i.putExtra(CommonConstant.KEY_RESLUT, bean2);
				context.startActivity(i);
			}
		});
		
		return view;
	}

	public class ViewHolder {
		ImageView ivHead;// 头像
		TextView tvDoctorName;// 姓名
		TextView tvDate;//日期
		TextView tvContext;//内容
	}
 
}
