package com.patient.ui.patientUi.adapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.patient.ui.patientUi.entity.baseTable.AcademicCoinHistoryBean;
import com.yxck.patient.R;


/**
 * <dl>
 * <dt>UserSalesAdapter.java</dt>
 * <dd>Description:个人的消费记录（赞助者，医生，编辑）</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company: </dd>
 * <dd>CreateDate: 2014-12-11 下午2:10:56</dd>
 * </dl>
 * 
 * @author lihs
 */
public class UserSalesAdapter extends BaseAdapter implements Comparator<AcademicCoinHistoryBean>{
	
	private static final String TAG = UserSalesAdapter.class.getName();
    private Context context;
    
    private List<AcademicCoinHistoryBean> saleRecords;
 
    
	public UserSalesAdapter(Context context) {
		super();
		this.context = context;
	}
	
	public void setData(List<AcademicCoinHistoryBean> saleRecords){
		 
		 this.saleRecords = saleRecords;
		 Collections.sort(saleRecords, this);
		 notifyDataSetChanged();
	}
	

	@Override
	public int getCount() {
		return saleRecords == null?0:saleRecords.size();
	}

	@Override
	public Object getItem(int position) {
		return saleRecords == null?0:saleRecords.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		
		ViewHolder holder = null;
		if (view == null) {
			
			holder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.row_sales_record, null);
			holder.year = (TextView)view.findViewById(R.id.tvCircles);
			holder.day = (TextView)view.findViewById(R.id.day);
			holder.money = (TextView)view.findViewById(R.id.tvSales);
			holder.line = (TextView)view.findViewById(R.id.lineSales);
			view.setTag(holder);
			
		}else{
			holder = (ViewHolder)view.getTag();
		}
		
		//ffb66f 
		AcademicCoinHistoryBean bean = saleRecords.get(position);
		holder.year.setVisibility(View.INVISIBLE);
		if (isShowYear(position)) {
			holder.year.setVisibility(View.VISIBLE);
			holder.year.setText(getTime(position));
		}
		holder.day.setText(getDay(position));
		if ("BiStatus_1".equals(bean.serviceType)) {
			// 消费
			holder.line.setBackgroundResource(R.drawable.ic_sales_line);
			holder.money.setText("消耗-"+bean.coin + "元");
			holder.money.setTextColor(context.getResources().getColor(R.color.color_ffb66f));
		}else{
			holder.line.setBackgroundResource(R.drawable.bg_blue);
			holder.money.setText("充值+"+bean.money + "元");
			holder.money.setTextColor(context.getResources().getColor(R.color.color_267093));
		}
		return view;
	}
	
	private String getDay(int position){
		String time = "";
		time = saleRecords.get(position).changeTime;
		time = time.substring(5, 10);
		time = time.replace("月", "-");
		return time;
	}
	
	private String getTime(int position){
		String time = "";
		time = saleRecords.get(position).changeTime;
		time = time.substring(2, 4);
		return time;
	}
	
	private boolean isShowYear(int position){
		
		if (position == 0) {
			return true;
		}else if(position > 0 && position < getCount()){
			if (!getTime(position).equals(getTime(position - 1))) {
				return true; 
			}
		}
		return false;
	}
	class ViewHolder{
		TextView year;
		TextView day;
		TextView money;
		TextView line;
	}
	@Override
	public int compare(AcademicCoinHistoryBean ob, AcademicCoinHistoryBean ob2) {
		if (ob == null || ob2 == null) {
			return 0;
		}
		return ob2.changeTime.compareTo(ob.changeTime);
	}
}
