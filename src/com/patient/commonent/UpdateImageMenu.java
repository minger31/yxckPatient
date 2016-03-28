package com.patient.commonent;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yxck.patient.R;

/**
 * <dl>
 * <dt>BottomGridMenu.java</dt>
 * <dd>Description:底部按钮菜单</dd>
 * <dd>Copyright: Copyright (C) 2013</dd>
 * <dd>Company:</dd>
 * <dd>CreateDate: 2013-12-18 上午9:59:00</dd>
 * </dl>
 * 
 * @author lihs
 */
public class UpdateImageMenu extends BottomMenuWindow {

	public UpdateImageMenu(Activity activity,String hint) {
		super(activity);
		
		setContentView(R.layout.update_image_menu);
		
		((TextView) getContentView().findViewById(R.id.tv_hint)).setText(hint);
		((Button) getContentView().findViewById(R.id.btn_cancle))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dismiss();
					}
				});

		((LinearLayout) getContentView().findViewById(R.id.ll_camera))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dismiss();
						if (listener != null) {
							listener.takePhoto();
						}
					}
				});
		((LinearLayout) getContentView().findViewById(R.id.ll_imagelib))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dismiss();
						if (listener != null) {
							listener.selecAblum();
						}
					}
				});
	}

	private DoPhtot listener;

	public void setListener(DoPhtot listener) {
		this.listener = listener;
	}

	public interface DoPhtot {
		public void takePhoto();

		public void selecAblum();
	}
}
