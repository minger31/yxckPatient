package com.patient.ui.patientUi.adapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.patient.constant.CommonConstant;
import com.patient.library.bitmap.ImageFetcher;
import com.patient.library.bitmap.ImageWorker.OnBitmapListener;
import com.patient.library.bitmap.ImageWorker.OnImgLoadAfterListener;
import com.patient.ui.patientUi.activity.common.ActPlayVideo;
import com.patient.ui.patientUi.activity.common.ActPreViewIcon;
import com.patient.ui.patientUi.activity.common.ActSendComment;
import com.patient.ui.patientUi.activity.common.PreviewActivity;
import com.patient.ui.patientUi.entity.baseTable.PartyBean;
import com.patient.ui.patientUi.entity.baseTable.PatientEduBean;
import com.patient.ui.patientUi.entity.baseTable.PatientEduForumBean;
import com.patient.util.BitmapManager;
import com.patient.util.CommonUtil;
import com.patient.util.DateUtil;
import com.patient.util.LoaderImage;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

/**
 * 
 * <dl>
 * <dt>DoctorChildMeetingListAdapter.java</dt>
 * <dd>Description:评论列表</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company: 医学参考</dd>
 * <dd>CreateDate: 2014-10-29 下午4:33:38</dd>
 * </dl>
 * 
 */
public class CommentAdapter extends BaseAdapter {

	private static final String TAG = CommentAdapter.class.getName();
	
	private PatientEduBean patientEduBean;
	public void setPatientEduBean(PatientEduBean patientEduBean) {
		this.patientEduBean = patientEduBean;
	}

	private List<PatientEduForumBean> treatmentList = null;
	// 生命线头
	private static final int LIFE_LINE_TOP = 0;
	// 生命线数据
	static final int LIFE_LINE_DATA = 1;

	private Context context;
	private ViewHolder holder = null;
	
	public CommentAdapter(Context context) {
		super();
		this.context = context;
	}


	public void setList(List<PatientEduForumBean> treatmentList) {
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
				view = LayoutInflater.from(context).inflate(R.layout.row_comment_top, null);
//				view.setVisibility(View.GONE);
				view.setTag(LIFE_LINE_TOP);
				initProjectDetail(view);

			} else if (type == LIFE_LINE_DATA) {

				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(R.layout.row_comment_data, null);

				holder.ivHead = (ImageView) view.findViewById(R.id.iv_head);// 头像
				holder.tvName = (TextView)view.findViewById(R.id.tv_name);//姓名
				holder.tvSex = (TextView)view.findViewById(R.id.tv_sex);//性别
				holder.tvAge = (TextView)view.findViewById(R.id.tv_age);//年龄
				holder.tvAddress = (TextView)view.findViewById(R.id.tv_address);//地区
				holder.tvReleaseTime = (TextView)view.findViewById(R.id.tv_release_time);//发表时间
				holder.tvContent = (TextView)view.findViewById(R.id.tv_content);//评论内容
				
				view.setTag(holder);
			}
		}

		if (type == LIFE_LINE_DATA && holder != null && holder instanceof ViewHolder) {

			final PatientEduForumBean bean = treatmentList.get(position - 1);
			final PartyBean partyBean = bean.partyGv;
			holder.ivHead.setImageResource(R.drawable.ic_heads);
			if (!TextUtils.isEmpty(partyBean.partyheadUrl)&& (partyBean.partyheadUrl).startsWith("http")) {
				LogUtil.d("发起人Url=" + partyBean.partyheadUrl);
				LoaderImage.getInstance(R.drawable.ic_heads).ImageLoaders(partyBean.partyheadUrl, holder.ivHead);
			}
			holder.tvName.setText(partyBean.partyName);//姓名
//			String value = "男";
			Drawable drawable = null;
			if ("sexEnum_0".equals(partyBean.sexEnum)) {
				drawable= context.getResources().getDrawable(R.drawable.ic_man);
//				value = "男";
				
			}else if ("sexEnum_1".equals(partyBean.sexEnum)) {
				drawable= context.getResources().getDrawable(R.drawable.ic_women);
//				value = "女";
			}
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
			/// 这一步必须要做,否则不会显示.
			holder.tvSex.setCompoundDrawables(drawable,null,null,null);
//			holder.tvSex.setText(value);//性别
			holder.tvAge.setText(TextUtils.isEmpty(partyBean.age)?"":(partyBean.age + "岁"));//年龄
			if (!TextUtils.isEmpty(partyBean.provinceName)) {
				holder.tvAddress.setText(partyBean.provinceName + "省 " + partyBean.cityName);//地区
			}
			
			String releaseTime = DateUtil.getNewFormatDateString(bean.commentsTime, DateUtil.FORMAT_YYYY_MM_DD_HHMMSS_WORD_ZH, DateUtil.FORMAT_YYYY_MM_DD);
			holder.tvReleaseTime.setText(releaseTime);//发表时间
			holder.tvContent.setText(bean.content);//评论内容
		}
		return view;
	}
 
	// 话题对应实体
	public class ViewHolder {
		ImageView ivHead;// 头像
		TextView tvName;//姓名
		TextView tvSex;//性别
		TextView tvAge;//年龄
		TextView tvAddress;//地区
		TextView tvReleaseTime;//发表时间
		TextView tvContent;//评论内容
	}

	private void initProjectDetail(View v) {
		if (patientEduBean == null) {
			v.setVisibility(View.GONE);
			return;
		}else{
			v.setVisibility(View.VISIBLE);
		}
		GridView gridView = (GridView)v.findViewById(R.id.grid_view);
		((TextView) v.findViewById(R.id.tv_name)).setText(patientEduBean.partyGv.partyName);//姓名
		((TextView) v.findViewById(R.id.tv_hospital_title)).setText(patientEduBean.partyGv.hospitalTitle);//主治医生
		((TextView) v.findViewById(R.id.tv_hospital)).setText(patientEduBean.partyGv.hospitalName);//医院
		((TextView) v.findViewById(R.id.tv_department)).setText(patientEduBean.partyGv.departmentName);//科室
		
		String releaseTime = DateUtil.getNewFormatDateString(patientEduBean.releaseTime, DateUtil.FORMAT_YYYY_MM_DD_HHMMSS_WORD_ZH, DateUtil.FORMAT_YYYY_MM_DD);
		((TextView) v.findViewById(R.id.tv_release_time)).setText(releaseTime);//发布时间
		((TextView) v.findViewById(R.id.tv_content)).setText(patientEduBean.content);//内容
		
		ImageView ivHead = (ImageView) v.findViewById(R.id.iv_head);
		LoaderImage.getInstance(R.drawable.ic_heads_doc).ImageLoaders(patientEduBean.partyGv.partyheadUrl, ivHead);
		// 有分享图片和没有分享图片处理逻辑
		if (!TextUtils.isEmpty(patientEduBean.imagePath) || !TextUtils.isEmpty(patientEduBean.videoPath)) {
			gridView.setVisibility(View.VISIBLE);
			v.findViewById(R.id.tv1).setVisibility(View.VISIBLE);//只做距离用，撑开一段白色距离
			initCommentsPic(gridView, patientEduBean);
		} else {
			gridView.setVisibility(View.GONE);
			v.findViewById(R.id.tv1).setVisibility(View.GONE);
		}
	}

	// 初始化图片信息
	private void initCommentsPic(final GridView gv, final PatientEduBean bean) {
		ImageAdapter imageAdapter = new ImageAdapter(context, gv, bean);
		gv.setAdapter(imageAdapter);
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
					
					// 预览
					Bundle bundle = new Bundle();
					bundle.putStringArrayList(ActPreViewIcon.KEY_ALL_ICON,tempData);
					bundle.putInt(ActPreViewIcon.KEY_CURRENT_ICON, position);
					Intent intent = new Intent(context, ActPreViewIcon.class);
					intent.putExtras(bundle);
					context.startActivity(intent);
					
//					Bundle bundle = new Bundle();
//					bundle.putStringArrayList(Intent.EXTRA_STREAM, tempData);
//					bundle.putBoolean(ActSendComment.KEY_ACTION_CUSTOM, true);
//					bundle.putInt(ActSendComment.KEY_FOR_SHARE_TYPE, ActSendComment.SHARE_TYPE_PIC);
//					bundle.putString(PreviewActivity.ACTIVITY_FLAG, PreviewActivity.AVITVITY_START_FOR_RESULT);
//					bundle.putInt(PreviewActivity.SELECTED_IMG_INDEX, 0);
//					Intent intent = new Intent(context, PreviewActivity.class);
//					intent.putExtras(bundle);
//					context.startActivity(intent);
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
	
	
	//视频
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
