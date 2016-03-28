package com.patient.ui.patientUi.adapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.patient.constant.CommonConstant;
import com.patient.constant.UrlConstant;
import com.patient.library.http.HttpRequest.HttpMethod;
import com.patient.library.http.HttpUtils;
import com.patient.library.http.RequestCallBack;
import com.patient.library.http.RequestParams;
import com.patient.preference.LoginPreference;
import com.patient.ui.patientUi.activity.common.ActSendComment;
import com.patient.ui.patientUi.activity.common.PreviewActivity;
import com.patient.ui.patientUi.activity.patientsCircle.ActCommentList;
import com.patient.ui.patientUi.activity.personal.ActPersonalMainPage;
import com.patient.ui.patientUi.entity.baseTable.PatientEduBean;
import com.patient.ui.patientUi.entity.baseTable.TreatmentBean;
import com.patient.util.CommonUtil;
import com.patient.util.DateUtil;
import com.patient.util.LoaderImage;
import com.patient.util.LogUtil;
import com.yxck.patient.R;
 
/**
 * 暂时不用
 * */
public class PatientsCircleAdapter extends BaseAdapter {

	private static final String TAG = PatientsCircleAdapter.class.getName();
	private List<PatientEduBean> list;
	private Context context;

	public void setList(List<PatientEduBean> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public PatientsCircleAdapter(Context context) {
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
	
	private int clickPosition = -1;

	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View view, ViewGroup parent) {

		ViewHolder holder = null;

		if (view == null) {

			view = LayoutInflater.from(context).inflate(R.layout.row_patients_circle, null);
			holder = new ViewHolder();

			holder.ivHead = (ImageView) view.findViewById(R.id.iv_head);// 头像
			holder.tvName = (TextView) view.findViewById(R.id.tv_name);//医生姓名
			holder.tvHospitalTitle = (TextView) view.findViewById(R.id.tv_hospital_title);//医院专业职称
			holder.tvHospital = (TextView) view.findViewById(R.id.tv_hospital);//医院
			holder.tvDepartment = (TextView) view.findViewById(R.id.tv_department);//科室
			holder.tvReleaseTime = (TextView) view.findViewById(R.id.tv_release_time);//日期
			holder.tvContent = (TextView) view.findViewById(R.id.tv_content);//内容
			holder.gridView = (GridView) view.findViewById(R.id.grid_view);//
			holder.tvCollect = (TextView) view.findViewById(R.id.tv_collect);//收藏
			holder.tvComment = (TextView) view.findViewById(R.id.tv_comment);//评论
			holder.tvPraise = (TextView) view.findViewById(R.id.tv_praise);//赞

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		final PatientEduBean bean = list.get(position);
		if (bean.partyGv != null) {
			LoaderImage.getInstance(R.drawable.ic_heads_doc).ImageLoaders(bean.partyGv.partyheadUrl, holder.ivHead);
			holder.tvName.setText(bean.partyGv.partyName);//医生姓名
			holder.tvHospitalTitle.setText(bean.partyGv.hospitalTitle);//医院专业职称
			holder.tvHospital.setText(bean.partyGv.hospitalName);//医院
			holder.tvDepartment.setText(bean.partyGv.departmentName);//科室
		}
		final TreatmentBean bean2 = new TreatmentBean();
		bean2.doctorGv = bean.partyGv;
		holder.ivHead.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ActPersonalMainPage.class);
				intent.putExtra(CommonConstant.KEY_RESLUT, bean2);
				context.startActivity(intent);
			}
		});
		
		String releaseTime = DateUtil.getNewFormatDateString(bean.releaseTime, DateUtil.FORMAT_YYYY_MM_DD_HHMMSS_WORD_ZH, DateUtil.FORMAT_YYYY_MM_DD);
		holder.tvReleaseTime.setText(releaseTime);//日期
		
		holder.tvContent.setText(bean.content);//内容+
		
		// 有分享图片和没有分享图片处理逻辑
		if (!TextUtils.isEmpty(bean.imagePath) || !TextUtils.isEmpty(bean.videoPath)) {
			holder.gridView.setVisibility(View.VISIBLE);
			initCommentsPic(holder.gridView, bean);
		} else {
			holder.gridView.setVisibility(View.GONE);
		}
		
		/**根据是否赞来显示不同的图标*/
		Drawable drawable= context.getResources().getDrawable(R.drawable.ic_praises);
		Drawable drawable1= context.getResources().getDrawable(R.drawable.bg_praise);
		/// 这一步必须要做,否则不会显示.
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
		if (!TextUtils.isEmpty(bean.whetherPraise)) {//没有赞
			holder.tvPraise.setCompoundDrawables(drawable, null, null, null);
		}else {//有赞
			holder.tvPraise.setCompoundDrawables(drawable1, null, null, null);
		}//end
		/**根据是否赞来显示不同的图标*/
		Drawable drawable2= context.getResources().getDrawable(R.drawable.ic_collect);
		Drawable drawable3= context.getResources().getDrawable(R.drawable.bg_collect);
		/// 这一步必须要做,否则不会显示.
		drawable2.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		drawable3.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
		if (!TextUtils.isEmpty(bean.whetherCollection)) {//没有收藏
			holder.tvCollect.setCompoundDrawables(drawable2, null, null, null);
		}else {//有收藏
			holder.tvCollect.setCompoundDrawables(drawable3, null, null, null);
		}//end
		
		if (!CommonConstant.VALUE.equals(bean.collectionCount)) {
			holder.tvCollect.setText(bean.collectionCount);//收藏
		}else {
			holder.tvCollect.setText("");//收藏
		}
		if (!CommonConstant.VALUE.equals(bean.reviewCount)) {
			holder.tvComment.setText(bean.reviewCount);//评论
		}else {
			holder.tvComment.setText("");//评论
		}
		if (!CommonConstant.VALUE.equals(bean.praiseCount)) {
			holder.tvPraise.setText(bean.praiseCount);//赞
		}else {
			holder.tvPraise.setText("");//赞
		}
		 
		
		view.findViewById(R.id.ll_collect).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(bean.whetherCollection)) {//已收藏
					if (isCollect) {
						deleteCollection(bean);
					}
				}else if(TextUtils.isEmpty(bean.whetherCollection)){//没收藏
					if (isCollect) {
						createPartyCollection(bean);
					}
				}
			}
		});
		view.findViewById(R.id.ll_comment).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ActCommentList.class);
				intent.putExtra(CommonConstant.KEY_RESLUT, bean);
				((Activity)context).startActivityForResult(intent,CommonConstant.SUCCESS);
			}
		});
		
		view.findViewById(R.id.ll_praise).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(bean.whetherPraise)) {//已赞
					if (isPraise) {
						deletePatientEduForum(bean);
					}
				}else {//没赞
					if (isPraise) {
						clickPosition = -1;
						createPatientEduforum(bean,position);
					}
				}
			}
		});
		if (clickPosition == position) {
			scalePraise(view.findViewById(R.id.tv_praise));
			clickPosition = -1;
		}
		

		return view;
	}
	
	// 初始化图片信息
	private void initCommentsPic(final GridView gv, final PatientEduBean bean) {
		ImageAdapter imageAdapter = new ImageAdapter(context, gv,bean);
		gv.setAdapter(imageAdapter);
	}
	
	private boolean isCollect = true;//
	private boolean isPraise = true;//

	private class ViewHolder {
		ImageView ivHead;// 头像
		TextView tvName;// 医生姓名
		TextView tvHospitalTitle;// 医院专业职称
		TextView tvHospital;// 医院
		TextView tvDepartment;// 科室
		TextView tvReleaseTime;// 日期
		TextView tvContent;// 内容
		GridView gridView;
		TextView tvCollect;// 收藏
		TextView tvComment;// 评论
		TextView tvPraise;// 赞
	}
	
	private void scalePraise(View view){
	    Animation scaleAnimation = AnimationUtils.loadAnimation(context,R.anim.img_selected_cnt_anim);
	    view.startAnimation(scaleAnimation);
	}
	 
	/** 赞 */
	private void createPatientEduforum(final PatientEduBean bean,final int posi) {
		isPraise = false;
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("commentsId", LoginPreference.getUserInfo().partyId);
		params.addQueryStringParameter("patientEduId",bean.patientEduId);
		params.addQueryStringParameter("reviewTypeEnum","reviewTypeEnum_2");
		 
		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.CREATE_PATIENT_EDUFORUM, params, new RequestCallBack<Object>() {

					@Override
					public void onStart() {

					}
					
					@Override
					public void onSuccess(Object result) {

						if (result != null) {

							LogUtil.d2File(result.toString());

							JSONObject json;
							try {
								json = new JSONObject(result.toString());
								String code = json.optString("responseMessage");
								String forumId = json.optString("forumId");
							
								if (CommonConstant.STATUS_SUCCESS.equals(code)) {
									clickPosition = posi;
									bean.whetherPraise = forumId;
									isPraise = true;
									bean.praiseCount = String.valueOf(Integer.parseInt(bean.praiseCount)+1);
									notifyDataSetChanged();
								}else {
									isPraise = true;
								}

							} catch (JSONException e) {
								isPraise = true;
								e.printStackTrace();
							}
						}
					}
					
					@Override
					public void onFailure(Throwable error, String msg) {
						isPraise = true;
						LogUtil.d2File(msg);
						CommonUtil.showError(error, msg);
					}
		});
	}

	/** 取消赞 */
	private void deletePatientEduForum(final PatientEduBean bean) {
		isPraise = false;
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("forumId", bean.whetherPraise);

		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.DELETE_PATIENT_EDU_FORUM, params,
				new RequestCallBack<Object>() {

					@Override
					public void onStart() {

					}

					@Override
					public void onSuccess(Object result) {

						if (result != null) {

							LogUtil.d2File(result.toString());

							JSONObject json;
							try {
								json = new JSONObject(result.toString());
								String code = json.optString("responseMessage");

								if (CommonConstant.STATUS_SUCCESS.equals(code)) {
									bean.whetherPraise = "";
									isPraise = true;
									bean.praiseCount = String.valueOf(Integer.parseInt(bean.praiseCount) - 1);
									notifyDataSetChanged();
								}else {
									isPraise = true;
								}
							} catch (JSONException e) {
								isPraise = true;
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onFailure(Throwable error, String msg) {
						LogUtil.d2File(msg);
						isPraise = true;
						CommonUtil.showError(error, msg);
					}
				});
	}
		
	/** 收藏 */
	private void createPartyCollection(final PatientEduBean bean) {
		
		isCollect = false;
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("partyId", LoginPreference.getUserInfo().partyId);
		params.addQueryStringParameter("collectionId",bean.patientEduId);
		params.addQueryStringParameter("collectType","serviceTypeEnum_6");//收藏类型
		 
		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.CREATE_PARTY_COLLECTION, params, new RequestCallBack<Object>() {

					@Override
					public void onStart() {

					}
					
					@Override
					public void onSuccess(Object result) {

					 
						if (result != null) {

							LogUtil.d2File(result.toString());

							JSONObject json;
							try {
								json = new JSONObject(result.toString());
								String code = json.optString("responseMessage");
								String partyCollId = json.optString("partyCollId");
							
								if (CommonConstant.STATUS_SUCCESS.equals(code)) {
//									clickPosition = posi;
									isCollect = true;
									bean.whetherCollection = partyCollId;
									bean.collectionCount = String.valueOf(Integer.parseInt(bean.collectionCount)+1);
									notifyDataSetChanged();
								}else {
									isCollect = true;
								}

							} catch (JSONException e) {
								e.printStackTrace();
								isCollect = true;
							}
						}
					}
					
					@Override
					public void onFailure(Throwable error, String msg) {
						LogUtil.d2File(msg);
						isCollect = true;
						CommonUtil.showError(error, msg);
					}
		});
	}

	/** 取消收藏 */
	private void deleteCollection(final PatientEduBean bean) {
		
		isCollect = false;
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("partyId", LoginPreference.getUserInfo().partyId);
		params.addQueryStringParameter("collectionId",bean.patientEduId);//这个是患教id，不是收藏id
		params.addQueryStringParameter("collectType","serviceTypeEnum_6");//收藏类型
		 
		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.DELETE_COLLECTION, params, new RequestCallBack<Object>() {

					@Override
					public void onStart() {

					}
					
					@Override
					public void onSuccess(Object result) {

					 
						if (result != null) {

							LogUtil.d2File(result.toString());

							JSONObject json;
							try {
								json = new JSONObject(result.toString());
								int code = json.optInt("status");

								if (CommonConstant.SUCCESS == code) {
									bean.whetherCollection = "";
									isCollect = true;
									bean.collectionCount = String.valueOf(Integer.parseInt(bean.collectionCount)-1);
									notifyDataSetChanged();
								}else {
									isCollect = true;
								}

							} catch (JSONException e) {
								isCollect = true;
								e.printStackTrace();
							}
						}
					}
					
					@Override
					public void onFailure(Throwable error, String msg) {
						isCollect = true;
						LogUtil.d2File(msg);
						CommonUtil.showError(error, msg);
					}
		});
	}

	
	// 图片adapter
	private class ImageAdapter extends BaseAdapter {

		// 显示4个期刊，如果期刊数>=8 那就点击更多期刊

		Context context;
		GridView gv;
		int channelWidth = 0;
		int mImageThumbSpacing;

		String pic[];
		ArrayList<ImageVideo> reslut;
			
			
		public ImageAdapter(Context context, GridView gv, PatientEduBean bean) {
			super();
			this.context = context;
			this.gv = gv;

			ImageVideo imageVideo = null;
			reslut = new ArrayList<ImageVideo>();
			if (!TextUtils.isEmpty(bean.imagePath)) {
				pic = bean.imagePath.split(",");
				for (int i = 0; i < pic.length; i++) {
					imageVideo = new ImageVideo();
					imageVideo.imageUrl = pic[i];
					imageVideo.isVideo = false;
					reslut.add(imageVideo);
				}
			}
				
			if (!TextUtils.isEmpty(bean.videoPath)) {
				imageVideo = new ImageVideo();
				imageVideo.imageUrl = bean.videoImagePath;
				imageVideo.videoImagePath = bean.videoPath;
				imageVideo.isVideo = true;
				reslut.add(imageVideo);
			}
				
			int screenwidth = CommonUtil.getDeviceSize(context).x;
			mImageThumbSpacing = (int) context.getResources().getDimensionPixelSize(R.dimen.padd_wh);
			int padd = (int) context.getResources().getDimensionPixelSize(R.dimen.padding_20);
			int totalWidth = (screenwidth - padd) * 3 / 4;
			int count = reslut.size();
			if (count >= 3) {
				count = 3;
			}
			if (count == 1) {
				count = 2;
			}
			channelWidth = (totalWidth - mImageThumbSpacing * (count - 1)) / count;
			gv.setNumColumns(count);
			RelativeLayout.LayoutParams imgGridLp = (RelativeLayout.LayoutParams) gv.getLayoutParams();
			gv.setColumnWidth(channelWidth);
			imgGridLp.width = totalWidth + mImageThumbSpacing * (count - 1);
			int yushu = reslut.size() % 3;
			if (yushu != 0) {
				imgGridLp.height = channelWidth*(reslut.size()/3 +1) + mImageThumbSpacing*reslut.size()/3;
			} else {
				imgGridLp.height = channelWidth * (reslut.size() / 3) + mImageThumbSpacing * (reslut.size() / 3 - 1);
			}
			// imgGridLp.gravity = Gravity.CENTER_VERTICAL|Gravity.LEFT;
			gv.setLayoutParams(imgGridLp);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return reslut == null ? 0 : reslut.size();
		}

		@Override
		public Object getItem(int position) {
			return reslut == null ? 0 : reslut.get(position);
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
				view = LayoutInflater.from(context).inflate(R.layout.share_img_item_pic, null);
				holder.tvChannel = (ImageView) view.findViewById(R.id.image_target);
				holder.video = (ImageView) view.findViewById(R.id.video_icon);
				holder.prBar = (ProgressBar)view.findViewById(R.id.video_progressbar);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
				
			holder.video.setVisibility(View.GONE);
			if (reslut.get(position).isVideo) {
				holder.video.setVisibility(View.VISIBLE);
				holder.video.setBackgroundResource(R.drawable.ic_video_flag);
			}
			RelativeLayout.LayoutParams magazine = (RelativeLayout.LayoutParams) holder.tvChannel.getLayoutParams();
			magazine.width = channelWidth;
			magazine.height = channelWidth;
			holder.tvChannel.setLayoutParams(magazine);
			holder.tvChannel.setBackgroundColor(context.getResources().getColor(R.color.color_e5e5e5));
			final ImageView iv = holder.tvChannel;
			ImageVideo url = reslut.get(position);
			if (!TextUtils.isEmpty(url.imageUrl) && (url.imageUrl).startsWith("http")) {
				LogUtil.d("Url=" + url);
				LoaderImage.getInstance(-1).ImageLoaders(url.imageUrl, iv);
			}
			final ProgressBar bar = holder.prBar;
			final ImageView video = holder.video;
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (reslut.get(position).isVideo) {
//						Intent i = new Intent(context, ActPlayVideo.class);
//						i.putExtra("url", reslut.get(position).videoImagePath);
//						i.putExtra("cache", Environment.getExternalStorageDirectory().getAbsolutePath() + CommonConstant.CASHE_VIDEO + System.currentTimeMillis() + ".mp4");
//
//						context.startActivity(i);
						downloadVedio(reslut.get(position).videoImagePath, bar,video);
						return;
					}
					// 点击图片或视频，进入图片/视频预览界面（可移除图片，即不选择）。
					ArrayList<String> tempData = new ArrayList<String>();
					for (ImageVideo my : reslut) {
						if (!my.isVideo) {
							tempData.add(my.imageUrl);
						}
					}
					Bundle bundle = new Bundle();
					bundle.putStringArrayList(Intent.EXTRA_STREAM, tempData);
					bundle.putBoolean(ActSendComment.KEY_ACTION_CUSTOM, true);
					bundle.putInt(ActSendComment.KEY_FOR_SHARE_TYPE, ActSendComment.SHARE_TYPE_PIC);
					bundle.putString(PreviewActivity.ACTIVITY_FLAG, PreviewActivity.AVITVITY_START_FOR_RESULT);
					bundle.putInt(PreviewActivity.SELECTED_IMG_INDEX, 0);
					Intent intent = new Intent(context, PreviewActivity.class);
					intent.putExtras(bundle);
					context.startActivity(intent);
					}
				});

			return view;
		}

		class ViewHolder {
			ImageView tvChannel;
			ImageView video;
			ProgressBar prBar;
		}

		class ImageVideo {
			String imageUrl;
			String videoImagePath;
			boolean isVideo;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void downloadVedio(final String remoteUrl,final ProgressBar bar,final ImageView video){
		
	
		if (bar.getVisibility() == View.VISIBLE) {
			CommonUtil.showToast("正在下载中");
			return;
		}
		bar.setVisibility(View.VISIBLE);
		
		@SuppressWarnings("rawtypes")
		AsyncTask  downLoadTsks = new AsyncTask<Object, Object, String>() {
			
			
			@Override
			protected String doInBackground(final Object... params) {
				
				ProgressBar bar = (ProgressBar)params[1];
				String remoteUrls = (String)params[0];
				FileOutputStream out = null;
				InputStream is = null;
				try {
					URL url = new URL(remoteUrls);
					HttpURLConnection httpConnection = (HttpURLConnection) url
							.openConnection();
				    String localUrl = null;
					if (localUrl == null) {
						localUrl = Environment.getExternalStorageDirectory().getPath()+ "/VideoCache/"+ System.currentTimeMillis() + ".mp4";
						CommonUtil.scanFileAsync(context, localUrl);
					}
					System.out.println("localUrl: " + localUrl);
					File cacheFile = new File(localUrl);
					if (!cacheFile.exists()) {
						cacheFile.getParentFile().mkdirs();
						cacheFile.createNewFile();
					}
					long readSize = cacheFile.length();
					out = new FileOutputStream(cacheFile, true);

					httpConnection.setRequestProperty("User-Agent", "NetFox");
					httpConnection.setRequestProperty("RANGE", "bytes="+readSize + "-");
					is = httpConnection.getInputStream();
					long mediaLength = httpConnection.getContentLength();
					mediaLength += readSize;
					byte buf[] = new byte[1024];
					int size = 0;
					while ((size = is.read(buf)) != -1) {
						try {
							out.write(buf, 0, size);
							readSize += size;
						} catch (Exception e) {
							e.printStackTrace();
						}
//						Thread.sleep(1000);
						publishProgress(readSize,mediaLength,bar);
					}
					return localUrl;
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							//
						}
					}
					if (is != null) {
						try {
							is.close();
						} catch (IOException e) {
						}
					}
				}
				return null;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				LogUtil.d(TAG, "onPreExecute "+ remoteUrl);
//				video.setVisibility(View.INVISIBLE);
				
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				bar.setVisibility(View.GONE);
				if (!TextUtils.isEmpty(result)) {
					CommonUtil.playVideo(context, result);
				}else{
					video.setVisibility(View.VISIBLE);
				}
			}

			@Override
			protected void onProgressUpdate(Object... values) {
				super.onProgressUpdate(values);
				long readSize = (Long)values[0];
				long max = (Long)values[1];
				ProgressBar bar = (ProgressBar)values[2];
				double cachepercent = (readSize * 100.00 / max * 1.0);
				bar.setProgress((int)cachepercent);
			}
		};
		downLoadTsks.execute(remoteUrl,bar);
	}
}