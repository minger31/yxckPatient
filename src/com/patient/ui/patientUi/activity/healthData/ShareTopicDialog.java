package com.patient.ui.patientUi.activity.healthData;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.patient.util.CommonUtil;
import com.yxck.patient.R;

public class ShareTopicDialog {

	private String defaultValue;
	private Dialog dialog;

	private String data[] = null;

	public void setDialog(Dialog dialog) {
		this.dialog = dialog;
	}

	private Context context;
	private SearchAdapter searchAdapter = null;

	public ShareTopicDialog(final Context context, String defaultValue) {
		this.context = context;
		this.defaultValue = defaultValue;

		data = context.getResources().getStringArray(R.array.blood_type);

		dialog = new Dialog(context, R.style.CustomProgressDialog);
		View layout = LayoutInflater.from(context).inflate(R.layout.blood_type, null);

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.width = CommonUtil.getDeviceSize(context).x - 40;
		params.height = LayoutParams.WRAP_CONTENT;
		dialog.addContentView(layout, params);
		dialog.setCanceledOnTouchOutside(true);
		dialog.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
		dialog.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));

		ListView listView = (ListView) layout.findViewById(R.id.listView1);
		searchAdapter = new SearchAdapter();
		listView.setAdapter(searchAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

				if (listener != null) {
					listener.doRefresh(data[pos]);
					searchAdapter = null;
					dismiss();
				}
			}
		});
	}

	private TopicListener listener;

	public interface TopicListener {

		public void doRefresh(String value);
	}

	public void setPopDepartListener(TopicListener listener) {
		this.listener = listener;
	}

	public void showDg() {
		if (dialog != null)
			dialog.show();
	}

	public Dialog getDialog() {
		return dialog;
	}

	public void setCancelable(boolean cancelable) {
		if (dialog != null) {
			dialog.setCancelable(cancelable);
		}
	}

	public boolean isShowing() {
		if (dialog != null) {
			return dialog.isShowing();
		} else {
			return false;
		}
	}

	public void dismiss() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	private class SearchAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return data.length;
		}

		@Override
		public Object getItem(int position) {
			return data[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {

			if (view == null) {
				view = LayoutInflater.from(context).inflate(R.layout.row_blood_type, null);
			}

			TextView tvSearch = (TextView) view.findViewById(R.id.searchItem);
			tvSearch.setText(data[position]);
			CheckBox cb = (CheckBox)view.findViewById(R.id.cb);
			if (data[position].equals(defaultValue)) {
				cb.setChecked(true);
				tvSearch.setTextColor(context.getResources().getColor( R.color.color_363636));
			} else {
				cb.setChecked(false);
				tvSearch.setTextColor(context.getResources().getColor( R.color.color_979797));
			}
			return view;
		}
	}
}
