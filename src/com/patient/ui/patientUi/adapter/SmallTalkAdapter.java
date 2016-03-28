package com.patient.ui.patientUi.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import com.patient.constant.CommonConstant;
import com.patient.preference.LoginPreference;
import com.patient.ui.patientUi.activity.common.ActPreViewIcon;
import com.patient.ui.patientUi.activity.personal.ActPersonalMainPage;
import com.patient.ui.patientUi.entity.baseTable.InitiateFollowupBean;
import com.patient.ui.patientUi.entity.baseTable.PartyBean;
import com.patient.ui.patientUi.entity.baseTable.TinyFollowUpMessageBean;
import com.patient.ui.patientUi.entity.baseTable.TreatmentBean;
import com.patient.util.CommonUtil;
import com.patient.util.DateUtil;
import com.patient.util.LoaderImage;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

/** 医生端聊天 */
public class SmallTalkAdapter extends BaseAdapter implements Comparator<TinyFollowUpMessageBean>{

	private static final String TAG = SmallTalkAdapter.class.getName();
	
	private static final long ZERO = DateUtil.currentTime();
	private static final long TWELVE = 12*60*60*1000 + ZERO;
	private static final long EIGHTEEN = TWELVE + 6*60*60*1000;
	private static final long TWENTYFOUR = ZERO + 24*60*60*1000;
	private static final String MORNING = "早上";
	private static final String AFTERNOON = "下午";
	private static final String NINGHT = "晚上";
	
	
	private InitiateFollowupBean followupBean;
	public void setBean(InitiateFollowupBean initiateFollowupBean) {
		this.followupBean = initiateFollowupBean;
	}

	private List<TinyFollowUpMessageBean> list = null;
	private static final int ITEM_DOCTOR = 0;
	static final int ITEM_PATIENT = 1;

	private Context context;
	private Holder holder = null;

	public SmallTalkAdapter(Context context) {

		super();
		LogUtil.d(TAG, "DoctorCircleAdapter");
		this.context = context;

	}

	public void setList(List<TinyFollowUpMessageBean> list) {

		this.list = list;
		Collections.sort(list, this);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {

		return list == null ? 0: list.size();
	}

	@Override
	public Object getItem(int position) {
		return list == null ? -1 : list.get(position);
	}

	@Override
	public int getItemViewType(int position) {

		TinyFollowUpMessageBean bean = list.get(position);
		if (!LoginPreference.getUserInfo().partyId.equals(bean.launchId)) {
			return ITEM_DOCTOR;//发送者和登陆人一样，说明不患者
		}
		return ITEM_PATIENT;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View view, ViewGroup parent) {

		final int type = getItemViewType(position);
		LogUtil.d(TAG, "TYPE ==" + type + "position=" + position);
		if (view != null) {
			Object tag = view.getTag();
			if (tag instanceof Holder) {
				holder = (Holder) tag;
			}  
		} else {

			if (ITEM_DOCTOR == type) {
				holder = new Holder();
				view = LayoutInflater.from(context).inflate(R.layout.row_small_talk_left, null);
				holder.head = (ImageView) view.findViewById(R.id.iv_head);
				holder.content = (TextView)view.findViewById(R.id.tv_context);
				holder.time = (TextView)view.findViewById(R.id.time);
				holder.gridView = (GridView)view.findViewById(R.id.gridView1);
				view.setTag(holder);
			} else if (type == ITEM_PATIENT) {
				holder = new Holder();
				view = LayoutInflater.from(context).inflate(R.layout.row_small_talk_right, null);
				holder.head = (ImageView) view.findViewById(R.id.iv_head);
				holder.content = (TextView)view.findViewById(R.id.tv_context);
				holder.time = (TextView)view.findViewById(R.id.time);
				holder.gridView = (GridView)view.findViewById(R.id.gridView1);
				view.setTag(holder);
			}
		}
		
		final TinyFollowUpMessageBean bean = list.get(position);
		final PartyBean user = LoginPreference.getUserInfo();
		
		holder.head.setBackground(context.getResources().getDrawable(R.drawable.ic_heads));
		if (ITEM_PATIENT == type) {
			if (!TextUtils.isEmpty(user.partyheadUrl)&& (user.partyheadUrl).startsWith("http")) {
				LogUtil.d("发起人Url=" + user.partyheadUrl);
				LoaderImage.getInstance(R.drawable.ic_heads).ImageLoaders(user.partyheadUrl, holder.head);
			}
		} else if (type == ITEM_DOCTOR) {
			if (!TextUtils.isEmpty(followupBean.doctorGv.partyheadUrl)
					&& (followupBean.doctorGv.partyheadUrl).startsWith("http")) {
				LoaderImage.getInstance(R.drawable.ic_heads_doc).ImageLoaders(followupBean.doctorGv.partyheadUrl, holder.head);
			}
		}
		
		String time = bean.createdStamp;
		String current = DateUtil.getCurrentTimeSpecifyFormat(DateUtil.FORMAT_YYYY_MM_DD_ZH);
		StringBuffer sb = new StringBuffer();
		long currentMs = DateUtil.formatString2Date(time , DateUtil.FORMAT_YYYY_MM_DD_HHMMSS_WORD_ZH).getTime();
		if (!current.substring(0, 11).equals(time.substring(0, 11))) {
			//是不是昨天的
			if (DateUtil.getYesterdaySpecifyFormat(DateUtil.FORMAT_YYYY_MM_DD_ZH).equals(time.substring(0, 11))) {
				sb.append("昨天");
			}else{
				sb.append(time.substring(0, 11));
			}
		}
		if (currentMs > ZERO && currentMs < TWELVE) {
			sb.append(MORNING);
		}else if (currentMs > TWELVE && currentMs < EIGHTEEN) {
			sb.append(AFTERNOON);
		}else if (currentMs > EIGHTEEN && currentMs < TWENTYFOUR) {
			sb.append(NINGHT);
		}
		sb.append(time.substring(11, 18));
		holder.time.setText(sb.toString());
		holder.content.setVisibility(View.GONE);
		if (!TextUtils.isEmpty(bean.content)) {
			holder.content.setVisibility(View.VISIBLE);
			holder.content.setGravity(Gravity.CENTER_VERTICAL);
			holder.content.setText(bean.content);
		}else {
			holder.content.setVisibility(View.GONE);
		}
		
		//有图片  显示gridview
		holder.gridView.setVisibility(View.GONE);
		if (!TextUtils.isEmpty(bean.tinyMsgImage)) {
			holder.gridView.setVisibility(View.VISIBLE);
			GridViewAdapter adapter = new GridViewAdapter(context, holder.gridView, bean.tinyMsgImage);
			holder.gridView.setAdapter(adapter);
		}else {
			holder.gridView.setVisibility(View.GONE);
		}
		
		final TreatmentBean treatmentBean = new TreatmentBean();
		treatmentBean.doctorGv = followupBean.doctorGv;
		holder.head.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ITEM_DOCTOR == type) {
					Intent intent = new Intent(context, ActPersonalMainPage.class);
					intent.putExtra(CommonConstant.KEY_RESLUT, treatmentBean);
					context.startActivity(intent);
				} else if (type == ITEM_PATIENT) {
//					Intent intent = new Intent(context, ActLifeLineActivity.class);
//					intent.putExtra(CommonConstant.KEY_RESLUT, bean.patientGv.partyId);
//					context.startActivity(intent);
				}
			}
		});
		
		return view;
	}

 
	// 话题对应实体
	public class Holder {
		ImageView head;
		TextView content;// xxx:内容
		TextView time;// xxx:内容
		GridView gridView;
	}
	
	@Override
	public int compare(TinyFollowUpMessageBean lhs, TinyFollowUpMessageBean rhs) {
		
		return lhs.createdStamp.compareTo(rhs.createdStamp);
	}
	
	// GridView适配
		private class GridViewAdapter extends BaseAdapter {

			Context context;
			int channelWidth = 0;
			int mImageThumbSpacing;
			String pic[];
			ArrayList<String> reslut;

//			public GridViewAdapter(Context context, GridView gv, String pics) {
//				super();
//				this.context = context;
//
//				if (!TextUtils.isEmpty(pics)) {
//					pic = pics.split(",");
//					reslut = new ArrayList<String>();
//					for (int i = 0; i < pic.length; i++) {
//						String string = pic[i];
//						if (!TextUtils.isEmpty(string)) {
//							reslut.add(string);
//						}
//					}
//				}
//
//				int screenwidth = CommonUtil.getDeviceSize(context).x;
//				mImageThumbSpacing = (int) context.getResources().getDimensionPixelSize(R.dimen.padd_wh);
//				int padd = (int) context.getResources().getDimensionPixelSize(R.dimen.width_160);
//				int count = 4;
//				channelWidth = (screenwidth - mImageThumbSpacing * (count - 1) - padd) / count;
//				LogUtil.d(TAG, "channelWidth = " + channelWidth);
//				gv.setNumColumns(count);
//				LinearLayout.LayoutParams imgGridLp = (LinearLayout.LayoutParams) gv.getLayoutParams();
//				gv.setColumnWidth(channelWidth);
//				imgGridLp.width = screenwidth - padd;
//				imgGridLp.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
////				int heightCount = reslut.size() % count;
////				if (heightCount != 0) {
////					imgGridLp.height = channelWidth * (reslut.size() / count + 1)
////							+ mImageThumbSpacing * (reslut.size() / count);
////					imgGridLp.height = channelWidth;
////				} else {
////					imgGridLp.height = channelWidth * (reslut.size() / count)
////							+ mImageThumbSpacing * (reslut.size() / count - 1);
////				}
//				imgGridLp.height = channelWidth;
//				gv.setLayoutParams(imgGridLp);
//				notifyDataSetChanged();
//			}
			
			public GridViewAdapter(Context context, GridView gv, String pics) {
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
				int padd = (int) context.getResources().getDimensionPixelSize(R.dimen.width_160);
					
				int count = reslut.size();
				channelWidth = (screenwidth - mImageThumbSpacing * 3 - padd) / 4;
				channelWidth = channelWidth * count;
				LogUtil.d(TAG, "channelWidth = " + channelWidth);
				gv.setNumColumns(count);
				LinearLayout.LayoutParams imgGridLp = (LinearLayout.LayoutParams) gv.getLayoutParams();
				gv.setColumnWidth(channelWidth);
				imgGridLp.width = channelWidth;
				imgGridLp.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
				imgGridLp.height = (screenwidth - mImageThumbSpacing * (3) - padd) / 4;//只能发4张，显示一行高度就可
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
				LinearLayout.LayoutParams magazine = (LinearLayout.LayoutParams) holder.tvChannel.getLayoutParams();
				magazine.width = channelWidth;
				magazine.height = channelWidth;
				holder.tvChannel.setLayoutParams(magazine);
				holder.tvChannel.setBackgroundColor(context.getResources().getColor(R.color.color_e5e5e5));
				final ImageView iv = holder.tvChannel;
				String url = pic[position];
				if (!TextUtils.isEmpty(url) && (url).startsWith("http")) {
					LogUtil.d("Url=" + url);
					LoaderImage.getInstance(-1).ImageLoaders(url, iv);
				}

				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						// 预览
						Bundle bundle = new Bundle();
						bundle.putStringArrayList(ActPreViewIcon.KEY_ALL_ICON,reslut);
						bundle.putInt(ActPreViewIcon.KEY_CURRENT_ICON, position);
						Intent intent = new Intent(context, ActPreViewIcon.class);
						intent.putExtras(bundle);
						context.startActivity(intent);
						
//						Bundle bundle = new Bundle();
//						bundle.putStringArrayList(Intent.EXTRA_STREAM, reslut);
//						bundle.putBoolean(ActSendComment.KEY_ACTION_CUSTOM, true);
//						bundle.putInt(ActSendComment.KEY_FOR_SHARE_TYPE,ActSendComment.SHARE_TYPE_PIC);
//						bundle.putString(PreviewActivity.ACTIVITY_FLAG,PreviewActivity.AVITVITY_START_FOR_RESULT);
//						bundle.putInt(PreviewActivity.SELECTED_IMG_INDEX, position);
//						Intent intent = new Intent(context, PreviewActivity.class);
//						intent.putExtras(bundle);
//						context.startActivity(intent);
					}
				});
				return view;
			}

			class ViewHolder {
				ImageView tvChannel;
			}
		}

}
	
