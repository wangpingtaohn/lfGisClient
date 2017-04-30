package com.gis.client.activity.query.statistics.defend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.gis.client.R;

public class DefendDetaileAdapter extends BaseAdapter {
	private Context mContext;
	private List<Map<String, String>> mDatas;
	private LayoutInflater mInflator;
	private String mDevicesName;
	private String[] mSpinnerStr;

	public DefendDetaileAdapter(Context context, String switchName,
			List<Map<String, String>> list) {
		this.mContext = context;
		this.mInflator = LayoutInflater.from(mContext);
		this.mDevicesName = switchName;
		this.mDatas = list;
		mSpinnerStr = mContext.getResources().getStringArray(
				R.array.alarm_query_type_array);
	}

	@Override
	public int getCount() {
		if (mDatas != null && mDatas.size() > 0) {
			mDatas = removeNormal(mDatas);
			return mDatas.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null || convertView.getTag() == null) {
			holder = new ViewHolder();
			convertView = mInflator.inflate(
					R.layout.statistics_defend_detail_list_item, null);
			holder.timeTv = (TextView) convertView
					.findViewById(R.id.statistics_defend_detail_list_item_time_text);
			holder.timeTv.setWidth((getWidthScreen() / 10) * 3);
			holder.nameTv = (TextView) convertView
					.findViewById(R.id.statistics_defend_detail_list_item_name_text);
			holder.nameTv.setWidth(getWidthScreen() / 5);
			holder.typeTv = (TextView) convertView
					.findViewById(R.id.statistics_defend_detail_list_item_type_text);
			holder.typeTv.setWidth(getWidthScreen() / 5);
			holder.infoTv = (TextView) convertView
					.findViewById(R.id.statistics_defend_detail_list_item_info_text);
			holder.infoTv.setWidth((getWidthScreen() / 10) * 3);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Map<String, String> map = mDatas.get(position);
		if (map.get("alarmType") != null && !"".equals(map.get("alarmType"))) {
			int type = Integer.parseInt(map.get("alarmType"));
			if (2 == type) {//零序保护则信息列 显示成  “零序电流：A流值”
				String info = map.get("info");//"A流：7，B流：2，C流：5"
				int startIndex = info.indexOf("流");
				int endIndex = info.indexOf("，");
				String alStr = info.substring(startIndex + 2, endIndex);
				holder.infoTv.setText("零序电流:" + alStr);
			} else {
				holder.infoTv.setText(map.get("info"));
			}
			type = type > 3 ? type - 1 : type;
			holder.typeTv.setText(mSpinnerStr[type] + "");
		}
		holder.nameTv.setText(mDevicesName);
		holder.timeTv.setText(map.get("time"));
		return convertView;
	}

	private class ViewHolder {
		TextView timeTv;
		TextView nameTv;
		TextView typeTv;
		TextView infoTv;
	}

	private int getWidthScreen() {
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		int width = dm.widthPixels;
		return width;
	}

	// 去掉正常类型的不显示
	private List<Map<String, String>> removeNormal(
			List<Map<String, String>> list) {
		int count = list.size();
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		for (int i = 0; i < count; i++) {
			Map<String, String> map = list.get(i);
			if (!"3".equals(map.get("alarmType"))) {
				resultList.add(map);
			}
		}
		return resultList;
	}
}
