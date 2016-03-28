package com.patient.ui.patientUi.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.patient.commonent.CommonDialog;
import com.patient.commonent.TitleBar;
import com.patient.constant.CommonConstant;
import com.patient.ui.patientUi.activity.common.ReleaseOnFinishListener;

public class BaseFragment extends Fragment   implements ReleaseOnFinishListener {
    
    private TitleBar titleBar = null;
    
    private MyBroadCast instance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        instance = new MyBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(CommonConstant.ACTION_SIGNAL_LOGIN);
		filter.addAction(CommonConstant.Action_PUSH_MSG);
		getActivity().registerReceiver(instance, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (instance != null) {
            getActivity().unregisterReceiver(instance);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
       
    }
    

    public TitleBar getTitleBar() {
        if (titleBar == null) {
            titleBar = new TitleBar(getActivity(), getView());
        }
        return titleBar;
    }

	
	private class MyBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (CommonConstant.ACTION_SIGNAL_LOGIN.equals(action)) {
				tipLogin("您的账号已在异地登陆，您已经被迫下线");
			}else if (CommonConstant.Action_PUSH_MSG.equals(action)) {
				String type = intent.getStringExtra(CommonConstant.KEY_RESLUT);
				refreshUI(type);
			}
		}
	}

	/**
	 * @author: lihs
	 * @Title: tipLogin
	 * @Description: 推出登录
	 * @date: 2013-9-11 下午6:25:31
	 */

	private CommonDialog dialog;

	private void tipLogin(String text_context) {

		dialog = new CommonDialog(getActivity());
		// 屏蔽返回键
		dialog.setCancelable(false);
		dialog.setTitle("提醒");
		dialog.setMessage(text_context);
		dialog.setPositiveButton(new CommonDialog.BtnClickedListener() {
			@Override
			public void onBtnClicked() {
				// 发广播强制登陆广播
				dialog.dismiss();

			}
		}, "确定");

		if (dialog != null && dialog.isShowing()) {

		} else {
				dialog.showDialog();
		}
	}

	@Override
	public boolean isBackable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setReleaseOnFinish(boolean isRelease) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void releasePreActivity() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refreshUI(Object pushMsg) {
		// TODO Auto-generated method stub
		
	}
}
