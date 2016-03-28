package com.patient.ui.patientUi.activity.healthData;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.patient.commonent.TitleBar;
import com.patient.ui.patientUi.fragment.BaseFragment;
import com.yxck.patient.R;

/**
 * 健康数据
 */
public class FragHealthData extends BaseFragment implements OnClickListener {

	private View view = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		view = LayoutInflater.from(getActivity()).inflate(R.layout.health_data_fragment, null);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		TitleBar bar = getTitleBar();
		bar.setTitle("健康数据", R.color.white);
		init();
	}

	private void init() {
		view.findViewById(R.id.rl_personal_info).setOnClickListener(this);// 个人信息
		view.findViewById(R.id.rl_animal_heat).setOnClickListener(this);// 体温
		view.findViewById(R.id.rl_breathe).setOnClickListener(this);// 呼吸
		view.findViewById(R.id.rl_heart_rate).setOnClickListener(this);// 心率
		view.findViewById(R.id.rl_oxygen_content).setOnClickListener(this);// 含氧量
		view.findViewById(R.id.rl_blood_pressure).setOnClickListener(this);// 血压
		view.findViewById(R.id.rl_blood_glucose).setOnClickListener(this);// 血糖
		view.findViewById(R.id.no_click).setOnClickListener(null);//  
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_personal_info:// 个人信息
			startActivity(new Intent(getActivity(), ActPersonalInfo.class));
			break;
		case R.id.rl_animal_heat:// 体温
			startActivity(new Intent(getActivity(), ActAnimalHeat.class));
			break;
		case R.id.rl_breathe:// 呼吸
			startActivity(new Intent(getActivity(), ActBreathe.class));
			break;
		case R.id.rl_heart_rate:// 心率
			startActivity(new Intent(getActivity(), ActHeartRate.class));
			break;
		case R.id.rl_oxygen_content:// 含氧量
			startActivity(new Intent(getActivity(), ActOxygenContent.class));
			break;
		case R.id.rl_blood_pressure:// 血压
			startActivity(new Intent(getActivity(), ActBloodPressure.class));
			break;
		case R.id.rl_blood_glucose:// 血糖
			startActivity(new Intent(getActivity(), ActBloodGlucose.class));
			break;
		default:
			break;
		}
	}
}
