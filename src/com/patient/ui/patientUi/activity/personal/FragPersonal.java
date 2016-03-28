package com.patient.ui.patientUi.activity.personal;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.patient.commonent.TitleBar;
import com.patient.library.zxing.view.MipcaActivityCapture;
import com.patient.preference.LoginPreference;
import com.patient.ui.patientUi.activity.common.ActMyQr;
import com.patient.ui.patientUi.activity.common.SpaceImageDetailActivity;
import com.patient.ui.patientUi.entity.baseTable.PartyBean;
import com.patient.ui.patientUi.fragment.BaseFragment;
import com.patient.util.LoaderImage;
import com.yxck.patient.R;

/**
 * 患者端我界面
 * <dl>
 * <dt>PersonalFragment.java</dt>
 * <dd>Description:TODO</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company: 医学参考报社</dd>
 * <dd>CreateDate: 2014-11-18 下午2:37:52</dd>
 * </dl>
 * 
 * @author dell
 */
public class FragPersonal extends BaseFragment implements OnClickListener {

	private View view = null;
	
	private PartyBean partyBean ; 
	private ImageView ivHead = null;//头像
	private TextView tvName = null;//姓名
	private TextView tvLevel = null;//级别
	private ArrayList<String> tempData;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = LayoutInflater.from(getActivity()).inflate(R.layout.patient_personal_fragment, null);
		init();
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		partyBean = LoginPreference.getUserInfo();
		
		TitleBar bar = getTitleBar();
		bar.setTitle("我", R.color.white);
		bar.enableRightBtn("", -1, null);
	}

	private void init() {
		ivHead = (ImageView)view.findViewById(R.id.iv_head);//头像
		ivHead.setOnClickListener(this);
		tvName = (TextView)view.findViewById(R.id.tv_name);//姓名
		tvLevel = (TextView)view.findViewById(R.id.tv_level);//级别

		view.findViewById(R.id.personalInfo).setOnClickListener(this);//个人信息
		view.findViewById(R.id.rl_rich_scan).setOnClickListener(this);//扫一扫
		view.findViewById(R.id.rl_setting).setOnClickListener(this);//设置
		view.findViewById(R.id.ll1).setOnClickListener(this);//设置
		view.findViewById(R.id.rl_academic_currency).setOnClickListener(this);//学术币
		view.findViewById(R.id.no_click).setOnClickListener(null);//不可点击
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.personalInfo://个人信息
			intent.setClass(getActivity(), ActPersonalInfo.class);
			startActivity(intent);
			break;
		case R.id.rl_rich_scan://扫一扫
			startActivity(new Intent(getActivity(), MipcaActivityCapture.class));
			break;
		case R.id.ll1://二维码图片
			startActivity(new Intent(getActivity(), ActMyQr.class));
		    getActivity().overridePendingTransition(R.anim.my_qr_right_top, 0);
			break;
		case R.id.rl_academic_currency://学术币
			startActivity(new Intent(getActivity(), ActAcademicCurrency.class));
			break;
		case R.id.rl_setting://设置
			startActivity(new Intent(getActivity(), ActSetting.class));
			break;
		case R.id.iv_head://设置
			Intent intent1 = new Intent(getActivity(), SpaceImageDetailActivity.class);
			intent1.putExtra("images", (ArrayList<String>) tempData);
			intent1.putExtra("position", 0);
			int[] location = new int[2];
			ivHead.getLocationOnScreen(location);
			intent1.putExtra("locationX", location[0]);
			intent1.putExtra("locationY", location[1]);
			intent1.putExtra("width", ivHead.getWidth());
			intent1.putExtra("height", ivHead.getHeight());
			startActivityForResult(intent1,1);
			getActivity().overridePendingTransition(0, 0);
			break;
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (!TextUtils.isEmpty(partyBean.partyName)) {
			tvName.setText(partyBean.partyName);//姓名
		}else {
			tvName.setText(partyBean.phone1);//姓名
		}
		
		String level = partyBean.levelEnum;
		if ("levelEnum_0".equals(level)) {
			tvLevel.setText("注册用户");
		}else if ("levelEnum_1".equals(level)) {
			tvLevel.setText("赞助者");
		}else if ("levelEnum_2".equals(level)) {
			tvLevel.setText("普通医生");
		}else if ("levelEnum_3".equals(level)) {
			tvLevel.setText("编辑");
		}else if ("levelEnum_4".equals(level)) {
			tvLevel.setText("编委");
		}else if ("levelEnum_5".equals(level)) {
			tvLevel.setText("工作人员");
		}
		 
		if (null == tempData) {
			tempData = new ArrayList<String>();
		}
		tempData.clear();
		tempData.add(partyBean.partyheadUrl);
		LoaderImage.getInstance(R.drawable.ic_heads).ImageLoaders(partyBean.partyheadUrl, ivHead);
	}
	
}
