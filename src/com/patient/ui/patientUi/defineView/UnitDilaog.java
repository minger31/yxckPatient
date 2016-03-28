package com.patient.ui.patientUi.defineView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pinyin4j.PinyinHelper;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.patient.commonent.CommonWaitDialog;
import com.patient.commonent.SideBar;
import com.patient.constant.CommonConstant;
import com.patient.constant.UrlConstant;
import com.patient.library.http.HttpRequest.HttpMethod;
import com.patient.library.http.HttpUtils;
import com.patient.library.http.RequestCallBack;
import com.patient.library.http.RequestParams;
import com.patient.library.jsonConvert.JSONConverter;
import com.patient.ui.patientUi.entity.baseTable.ProvinceBean;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

/**
 * <dl>
 * <dt>UnitDilaog.java</dt>
 * <dd>Description:单位三级选择界面</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company: </dd>
 * <dd>CreateDate: 2014-11-28 下午5:27:42</dd>
 * </dl>
 * 
 * @author lihs
 */
public class UnitDilaog  implements Comparator<ProvinceBean>{

	private Dialog dialog;
	private Context context;
    private View contentView;
    
    private ListView list;
    
    private List<ProvinceBean> currentData;
    private int provienceId = 0;
    private int cityId = 0;
    private UnitAdapter unitAdapter;
    // 0 是省 1 是 市 2 医院
    private int countPage = 0;
    
    private TextView titleTxt;
    private Map<Integer, List<ProvinceBean>> allDataMap = new HashMap<Integer, List<ProvinceBean>>();
    // side bar
	
 	private String max = String.valueOf((char)(255)); // 常用字符最大ASCII
 	private Map<String, Integer> positonMap = new HashMap<String, Integer>();
 	private String[] sections;
 	private String[] letters = {"A","B", "C","D", "E","F", "G","H", "I","J", "K","L", "M","N", "O","P", "Q","R", "S","T", "U","V", "W","X", "Y","Z"};
 	private int lastCount = 0;
 	
    // 快速定位条
 	private SideBar sideBar;
 	// 快速定位条，选中提示
 	private TextView mDialogText;
 	private View dialogView;
 	
 	private ListView listView;
    
	public UnitDilaog(Context context,List<ProvinceBean> provinceLists,String proId) {
		
		super();
		this.context = context;
		this.currentData = provinceLists;
		if (!TextUtils.isEmpty(proId)) {
			for (int i = 0; i < currentData.size(); i++) {
				if (currentData.get(i).geoId.equals(proId)) {
					proPosition = i;
				}
			}
		}
		allDataMap.put(countPage, currentData);
		
		dialog = new Dialog(context, R.style.CustomProgressDialog);
	    contentView = LayoutInflater.from(context).inflate(R.layout.unit, null);
	    LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
	    params.height = (int) (CommonUtil.getDeviceSize(context).y * 0.75); // 高度设置为屏幕的0.6
	    params.width = (int) (CommonUtil.getDeviceSize(context).x * 0.9); // 高度设置为屏幕的0.6
	    dialog.addContentView(contentView, params);
	    dialog.setCanceledOnTouchOutside(false);
	    dialog.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_BACK));
	    dialog.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_BACK));
	    
	    init();
	    
	    LinearLayout    leftView = (LinearLayout) contentView.findViewById(R.id.title_left);
	    LinearLayout    rightView = (LinearLayout) contentView.findViewById(R.id.title_right);
	    titleTxt = (TextView) contentView.findViewById(R.id.title_txt);
	    leftView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				countPage--;
				if (countPage < 0) {
					dialog.dismiss();
					dialog = null;
				}else{
					currentData = allDataMap.get(countPage);
					if (currentData == null) {
						dialog.dismiss();
						dialog = null;
					}
					refreshData();
					unitAdapter.notifyDataSetChanged();
				}
			}
		});
	}
	
	private int proPosition = 0;
	private void init(){
		
		listView = (ListView)contentView.findViewById(R.id.lv_multi_select_linkman);
		
		unitAdapter = new UnitAdapter();
		listView.setAdapter(unitAdapter);
		initSideBar();
		refreshData();
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,final int pos, long id) {
				if (countPage == 1) {
					// 把医院的信息带过去既可以
					if (listener != null) {
						listener.doRefresh(allDataMap.get(0).get(proPosition),allDataMap.get(1).get(pos));
					}
					dialog.dismiss();
					return;
				}
				proPosition = pos;
				getCityData(pos);
			}
		});
	}
	
	
	private String currentSortKey = "";
	private Map<String, Integer> subMap = new HashMap<String, Integer>();
	private int illegalDataCount = 0; // 异常数据个数
	
	private void sortListData() {
		
		if (currentData == null) {
			return;
		}
		// 不包含在定位条内的非法数据
		List<ProvinceBean> tempList = new ArrayList<ProvinceBean>();
		// 比最大常用ASCII大的异常数据
		List<ProvinceBean> tempList2 = new ArrayList<ProvinceBean>();
		int size = currentData.size();
		String sortKey = "";
		for (int count = size - 1; count >= 0; count--) {
			sortKey = currentData.get(count).sortKey[0].substring(0, currentData.get(count).sortKey[0].length() -1);
			LogUtil.d("sortKey1="+sortKey);
			if (TextUtils.isEmpty(sortKey)) {
				tempList.add(0, currentData.get(count)); // 倒叙遍历，小的添加到最底部
				currentData.remove(count);
				if (!TextUtils.isEmpty(currentSortKey)) {
					// 记录当前的位置需要被减一
					if (subMap.get(currentSortKey) != null) {
						subMap.put(currentSortKey,
								subMap.get(currentSortKey) + 1);
					} else {
						subMap.put(currentSortKey, 1);
					}
				}
				continue;
			} else {
				sortKey = sortKey.substring(0, 1);
				sortKey = sortKey.toUpperCase();
				if (sortKey.compareTo(max) > 0) {// 比最大常用ASCII大的异常数据
					tempList2.add(0, currentData.get(count));
					currentData.remove(count);
					if (!TextUtils.isEmpty(currentSortKey)) {
						// 记录当前的位置需要被减一
						if (subMap.get(currentSortKey) != null) {
							subMap.put(currentSortKey,
									subMap.get(currentSortKey) + 1);
						} else {
							subMap.put(currentSortKey, 1);
						}
					}
					continue;
				} else if (!CommonConstant.letter.contains(sortKey)) {
					// 不包含在定位条内的非法数据
					tempList.add(0, currentData.get(count));
					currentData.remove(count);
					if (!TextUtils.isEmpty(currentSortKey)) {
						// 记录当前的位置需要被减一
						if (subMap.get(currentSortKey) != null) {
							subMap.put(currentSortKey, subMap.get(currentSortKey) + 1);
						} else {
							subMap.put(currentSortKey, 1);
						}
					}
					continue;
				}
			}
			if (positonMap.get(sortKey) == null) {
				currentSortKey = sortKey; // 当前sortkey
				positonMap.put(sortKey, count);
			}
		}
		if (tempList2 != null && tempList2.size() > 0) {
			illegalDataCount = illegalDataCount + tempList2.size();
			currentData.addAll(0, tempList2);
		}
		if (tempList != null && tempList.size() > 0) {
			illegalDataCount = illegalDataCount + tempList.size();
			currentData.addAll(0, tempList);
		}
	}

	
	private void initSideBar() {

		sideBar = (SideBar) contentView.findViewById(R.id.linkman_sideBar);
		// 在屏幕中央添加一个TextView，用来提示快速定位条选中的字母
		dialogView = (View) LayoutInflater.from(context).inflate(R.layout.linkman_list_position, null);
		mDialogText = (TextView) dialogView.findViewById(R.id.position_text);
		sideBar.setTextView(mDialogText);
		sideBar.setPopView(dialogView);
		sideBar.setListView(listView);
	}

	
	public void show(){
		if (dialog != null) {
			dialog.show();
		}
	}
	
	
	private void refreshData(){
		
		if (currentData == null || currentData.size() == 0) {
			unitAdapter.notifyDataSetChanged();
		} else {
			hanzi2pingyin();
			Collections.sort(currentData,this);
			sortListData();
			unitAdapter.setSection(positonMap, illegalDataCount, subMap);
			unitAdapter.notifyDataSetChanged();
		}
	}
	
	private PopCityListener listener;
	public interface PopCityListener{
		 
		public void doRefresh(ProvinceBean pro,ProvinceBean city);
	}
	
	public void setPopCityListener(PopCityListener listener){
		this.listener = listener;
	}
	
	private class UnitAdapter extends BaseAdapter implements SectionIndexer{
		

		@Override
		public int getCount() {
			return currentData == null ? 0 : currentData.size();
		}

		@Override
		public Object getItem(int position) {
			return currentData == null ? 0 : currentData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("ResourceAsColor")
		@Override
		public View getView(int position, View view, ViewGroup parent) {

			if (view == null) {
				view = LayoutInflater.from(context).inflate(R.layout.row_signal_chose, null);
			}
			
			view.findViewById(R.id.cb).setVisibility(View.GONE);
			TextView leftContent = (TextView) view.findViewById(R.id.searchItem);
			leftContent.setText(currentData.get(position).geoName);
			if (proPosition == position) {
				leftContent.setTextColor(context.getResources().getColor(R.color.color_267093));
			} else {
				leftContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				leftContent.setTextColor(context.getResources().getColor(R.color.black));
			}
			return view;
		}

		@Override
		public Object[] getSections() {
			return sections;
		}

		@Override
		public int getSectionForPosition(int position) {
			return 1;
		}

		@Override
		public int getPositionForSection(int section) {
			return positonMap.size() !=0 ? positonMap.get(sections[section]):0;
		}
		 
		public void setSection(Map<String, Integer> posMap, int illegalDataCount, Map<String, Integer> subMap){
		   
			positonMap = posMap; 
			Set<String> sectionletters = positonMap.keySet();
			int count = 0;
			lastCount = 0;
				int size = letters.length;
				for(int index = 0; index < size; index ++){
					if(subMap.get(letters[index]) != null && subMap.get(letters[index]) > 0){
					// 累加的原因：是subMap记录的个数只是快速定位点开始到其前面
					count = count + subMap.get(letters[index]); 
					}
					if(positonMap.get(letters[index]) != null){
					    // 加1原因：初始定位的位置为每个快速定位点的最后一条，+1 即为其后面一条的第一个位置
						int currentCount = positonMap.get(letters[index]) - count + 1;
						positonMap.put(letters[index], lastCount + illegalDataCount);
						lastCount = currentCount;
					}
				}
			if (illegalDataCount > 0) {
				positonMap.put("#", 0);
				sectionletters = positonMap.keySet();
			}
			ArrayList<String> sectionList = new ArrayList<String>(sectionletters);
			Collections.sort(sectionList);
			sections = new String[sectionletters.size()];
			sectionList.toArray(sections);
		}
	}
	

	// 获取 
	private void getCityData(int position) {
		final CommonWaitDialog wg = new CommonWaitDialog(context, "加载中");
		RequestParams params = new RequestParams();
		HttpUtils http = new HttpUtils();
		//countPage=0  是获取省；1下面的市； 
		params.addQueryStringParameter("geoId", currentData.get(position).geoId);
		String url = null;
		 
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.AJAX_GET_CITY, params,
				new RequestCallBack<Object>() {

					@Override
					public void onStart() {
					}

					@Override
					public void onSuccess(Object result) {
						wg.clearAnimation();
						if (result != null) {

							LogUtil.d2File(result.toString());
							JSONObject json;
							try {
								
								 json = new JSONObject(result.toString());
								 if (countPage == 0) {
									currentData = JSONConverter.convertToArray(json.optString("cityList"), new TypeToken<List<ProvinceBean>>(){});
								 }
								 countPage++;
								 allDataMap.put(countPage, currentData);
								 refreshData();
								 unitAdapter.notifyDataSetChanged();
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onFailure(Throwable error, String msg) {
						wg.clearAnimation();
						LogUtil.d2File(msg);
					}
				});
	}
	
	@Override
	public int compare(ProvinceBean o1, ProvinceBean o2) {
		 
		return o1.sortKey[0].compareTo(o2.sortKey[0]);
 
	}

	
	private void hanzi2pingyin(){
		if (currentData == null) {
			return;
		}
		for (ProvinceBean bean :currentData) {
			String value = bean.geoName;
			
			bean.sortKey = PinyinHelper.toHanyuPinyinStringArray(value.charAt(0));
			LogUtil.d("sortKey="+bean.sortKey);
		}
	}
}
