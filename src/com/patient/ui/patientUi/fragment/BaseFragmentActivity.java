package com.patient.ui.patientUi.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup;

import com.patient.commonent.CommonDialog;
import com.patient.commonent.TitleBar;

public class BaseFragmentActivity extends FragmentActivity {

    private CommonDialog dialog;

    private TitleBar titleBar = null;

    // 是否异地登陆弹出对话框 true弹出；false 不弹出
    private boolean isForceLogin = true;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

    }

    public TitleBar getTitleBar() {
        if (titleBar == null) {
            titleBar = new TitleBar(this,
                    ((ViewGroup) findViewById(android.R.id.content))
                            .getChildAt(0));
        }
        return titleBar;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
   
}
