package com.patient.ui.patientUi.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
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

import com.patient.library.bitmap.ImageFetcher;
import com.patient.library.bitmap.ImageWorker.OnBitmapListener;
import com.patient.ui.patientUi.activity.lifeLine.ActTreatmentDetail;
import com.patient.util.CommonUtil;
import com.patient.util.LoaderImage;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

public class CureImageDetileAdapter extends BaseAdapter {
	
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

	public CureImageDetileAdapter(Context context,GridView gv) {

		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.gv = gv;
	}
	
	public void setData(List<String> mListPhotoPaths){
		
		this.mbasicPhotoPath = mListPhotoPaths;
		gvHeight = gv.getHeight();
		LogUtil.d(TAG, "gvHeight="+gvHeight);
		int mImagePadding = context.getResources().getDimensionPixelSize(R.dimen.padding_20);
		int paddingVh = context.getResources().getDimensionPixelSize(R.dimen.padding_10);
		imgSize = (CommonUtil.getDeviceSize(context).x - 2*paddingVh - mImagePadding)/4;
//		CommonUtil.showToast("快读度："+imgSize);
		if (mbasicPhotoPath != null) {
			int count = mbasicPhotoPath.size();
			int heightCount = count%4;
			if (heightCount != 0) {
				heightCount = count/4 + 1;
			}else{
				heightCount = count/4;
			}
			LinearLayout.LayoutParams params = (LayoutParams) gv.getLayoutParams();
			params.height = heightCount*imgSize+heightCount*paddingVh;
			params.width = LinearLayout.LayoutParams.MATCH_PARENT;
			gv.setLayoutParams(params);
		}
		gv.setNumColumns(4);
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
		if (!TextUtils.isEmpty(mbasicPhotoPath.get(position)) && mbasicPhotoPath.get(position).startsWith("http")) {
			LoaderImage.getInstance(-1).ImageLoaders( mbasicPhotoPath.get(position), viewHolder.imageTarget);
		}
		viewHolder.imageTarget.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (CommonUtil.isFastDoubleClick()) {
					return;
				}
				// 预览
				((ActTreatmentDetail)context).previewIcon(mbasicPhotoPath,position);
			};
		});
		return curView;
	}

	private static class ImgViewHolder {
		ImageView imageTarget;
		ImageView videoIcon;
	}
	
	private RefreshCureInfo listener;
	public interface RefreshCureInfo{
		 
		public void doDel(String url);
	}
	
	public void setRefreshCureInfo(RefreshCureInfo listener){
		this.listener = listener;
	}

}
