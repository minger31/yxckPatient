package com.patient.ui.patientUi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;
import com.patient.ui.patientUi.activity.common.ActPreViewIcon;
import com.patient.util.CommonUtil;
import com.patient.util.LoaderImage;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

public class CheckImageAdapter extends BaseAdapter {
	
	private static final String TAG = BasicImageAdapter.class.getName();

	// 分享文件本地路径
	private List<String> mbasicPhotoPath = null;

	public List<String> getmListPhotoPath() {
		return mbasicPhotoPath;
	}

	private LayoutInflater layoutInflater = null;

	private ImgViewHolder viewHolder = null;
	private Context context;
	private int imgSize = 0;
	private GridView gv;
	
	private int gvHeight = 0;

	private TextPaint mTextPaint;
	private int wordSize = 0;
	private boolean isCommitVisit;
	public CheckImageAdapter(Context context,GridView gv, boolean isCommitVisit) {

		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.gv = gv;
		this.isCommitVisit = isCommitVisit;
		wordSize = context.getResources().getDimensionPixelSize(
				R.dimen.word_size_14sp);
		mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setTextSize(wordSize);
	}
	
	public void setData(List<String> mListPhotoPaths){
		
		this.mbasicPhotoPath = mListPhotoPaths;
		gvHeight = gv.getHeight();
		LogUtil.d(TAG, "gvHeight="+gvHeight);
		StringBuffer sb = new StringBuffer();
		
		if (isCommitVisit) {
			sb.append("自行车自");
			int totalWidth = (int) mTextPaint.measureText(sb.toString());//字体的宽度
			int mImagePadding = context.getResources().getDimensionPixelSize(R.dimen.padding_15);
			int paddingVh = context.getResources().getDimensionPixelSize(R.dimen.padding_5);
			
			if (mbasicPhotoPath != null) {
				int count = mbasicPhotoPath.size();
				int heightCount = count % 3;
				if (heightCount != 0) {
					heightCount = count/3 + 1;
				}else{
					heightCount = count/3;
				}
				//一个图片的宽度
				imgSize = (CommonUtil.getDeviceSize(context).x - 2 * paddingVh - 2 * mImagePadding - totalWidth - paddingVh) / 3;
				LinearLayout.LayoutParams params = (LayoutParams) gv.getLayoutParams();
				params.height = heightCount * imgSize + (heightCount - 1) * paddingVh;
				params.width = CommonUtil.getDeviceSize(context).x - 2 * mImagePadding - totalWidth - paddingVh;//有一个5dp的间距
				gv.setLayoutParams(params);
			}
		}else {
			sb.append("自行车自:");
			int totalWidth = (int) mTextPaint.measureText(sb.toString());//字体的宽度
			int mImagePadding = context.getResources().getDimensionPixelSize(R.dimen.padding_20);
			int paddingVh = context.getResources().getDimensionPixelSize(R.dimen.padding_5);
			
			if (mbasicPhotoPath != null) {
				int count = mbasicPhotoPath.size();
				int heightCount = count % 3;
				if (heightCount != 0) {
					heightCount = count/3 + 1;
				}else{
					heightCount = count/3;
				}
				//一个图片的宽度
				imgSize = (CommonUtil.getDeviceSize(context).x - 2*paddingVh -mImagePadding - totalWidth)/3;
				LinearLayout.LayoutParams params = (LayoutParams) gv.getLayoutParams();
				params.height = heightCount*imgSize+(heightCount - 1)*paddingVh;
				params.width = CommonUtil.getDeviceSize(context).x - mImagePadding - totalWidth;
				gv.setLayoutParams(params);
			}
		}
		gv.setNumColumns(3);
		notifyDataSetChanged();
	}


	@Override
	public int getCount() {
		if (mbasicPhotoPath == null) {
			return 0;
		} else {
			return mbasicPhotoPath.size();
		}
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View curView = convertView;

		if (convertView == null) {
			curView = (View) layoutInflater.inflate(
					R.layout.share_img_item_pic, parent, false);
			viewHolder = new ImgViewHolder();
			viewHolder.imageTarget = (ImageView) curView
					.findViewById(R.id.image_target);
			viewHolder.videoIcon = (ImageView) curView
					.findViewById(R.id.video_icon);
			curView.setTag(viewHolder);
		} else {
			viewHolder = (ImgViewHolder) curView.getTag();
		}
		// 显示图片
		RelativeLayout.LayoutParams imgLp = (RelativeLayout.LayoutParams) viewHolder.imageTarget
				.getLayoutParams();
		imgLp.width = imgSize;
		imgLp.height = imgSize;
		viewHolder.imageTarget.setLayoutParams(imgLp);

		viewHolder.imageTarget.setTag(position);
		viewHolder.videoIcon.setVisibility(View.GONE);
		 
		viewHolder.imageTarget.setBackgroundColor(context.getResources().getColor(R.color.color_E5E5E5));
		if (!TextUtils.isEmpty(mbasicPhotoPath.get(position))) {
			if (mbasicPhotoPath.get(position).startsWith("http")) {
				LoaderImage.getInstance(-1).ImageLoaders(mbasicPhotoPath.get(position), viewHolder.imageTarget);
			}else {
				String url = Scheme.FILE.wrap(mbasicPhotoPath.get(position));
				LoaderImage.getInstance(-1).ImageLoaders(url, viewHolder.imageTarget);
			}
		}
		viewHolder.imageTarget.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (CommonUtil.isFastDoubleClick()) {
					return;
				}
				
				ArrayList<String> reslut;
				reslut = new ArrayList<String>();
				for (int i = 0; i < mbasicPhotoPath.size(); i++) {
					if (mbasicPhotoPath.get(i).startsWith("http")) {
						reslut.add(mbasicPhotoPath.get(i));
					}else {
						reslut.add("file://" + mbasicPhotoPath.get(i));
					}
				}
				// 预览
				Bundle bundle = new Bundle();
				bundle.putStringArrayList(ActPreViewIcon.KEY_ALL_ICON,reslut);
				bundle.putInt(ActPreViewIcon.KEY_CURRENT_ICON, position);
				Intent intent = new Intent(context, ActPreViewIcon.class);
				intent.putExtras(bundle);
				context.startActivity(intent);
				
//				// 预览
//				if (isCommitVisit) {//代表是ActPerfectMedicalInfo界面过来的
//					((ActPerfectMedicalInfo)context).previewIcon(mbasicPhotoPath,position);
//				}else {
//					((ActTreatmentDetail)context).previewIcon(mbasicPhotoPath,position);
//				}
			};
		});
		return curView;
	}

	private static class ImgViewHolder {
		ImageView imageTarget;
		ImageView videoIcon;
	}
	
	private RefreshCheckInfo listener;
	public interface RefreshCheckInfo{
		 
		public void doDel(String url);
	}
	
	public void setRefreshCheckInfo(RefreshCheckInfo listener){
		this.listener = listener;
	}

}
