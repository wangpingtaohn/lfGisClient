package com.gis.client.activity.query.alarm;

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
import com.gis.client.model.Line;
import com.gis.client.model.Switch;
import com.gis.client.model.Transformer;

public class AlarmInfoAdapter extends BaseAdapter {
	private Context mContext;
	private List<Map<String, String>> mDatas;
	private LayoutInflater mInflator;
	private List<Line> mLineList;
	private List<Transformer> mVoltegeList;
	private List<Switch> mSwitchList;
	private String mDevicesName;
	private String[] mSpinnerStr;
	private List<Map<String, String>> mResultList;//adapter.notifaDataSetChange();

	public AlarmInfoAdapter(Context context, List<Map<String, String>> list,
			List<Line> lineList, List<Transformer> voltageList,
			List<Switch> switchList, String devicesName) {
		this.mContext = context;
		this.mDatas = list;
		this.mInflator = LayoutInflater.from(mContext);
		this.mLineList = lineList;
		this.mVoltegeList = voltageList;
		this.mSwitchList = switchList;
		this.mDevicesName = devicesName;
		mSpinnerStr = mContext.getResources().getStringArray(
				R.array.alarm_query_type_array);
	}

	@Override
	public int getCount() {
		if (mDatas != null && mDatas.size() > 0) {
			mResultList = removeNormal(mDatas);
			if (mResultList != null && mResultList.size() > 0) {
			return mResultList.size();
			}
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
			convertView = mInflator
					.inflate(R.layout.alarm_info_list_item, null);
			holder.timeTv = (TextView) convertView
					.findViewById(R.id.alarm_info_list_item_time_text);
			holder.timeTv.setWidth(getWidthScreen() / 4);
			holder.nameTv = (TextView) convertView
					.findViewById(R.id.alarm_info_list_item_name_text);
			holder.nameTv.setWidth(getWidthScreen() / 8);
			holder.typeTv = (TextView) convertView
					.findViewById(R.id.alarm_info_list_item_type_text);
			holder.typeTv.setWidth(getWidthScreen() / 8);
			holder.lineTv = (TextView) convertView
					.findViewById(R.id.alarm_info_list_item_line_text);
			holder.lineTv.setWidth(getWidthScreen() / 4);
			holder.infoTv = (TextView) convertView
					.findViewById(R.id.alarm_info_list_item_info_text);
			holder.infoTv.setWidth(getWidthScreen() / 4);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Map<String, String> map = mResultList.get(position);
		// holder.nameTv.setText(map.get("xlname"));
		if (mDevicesName != null && !"".equals(mDevicesName)) {
			holder.nameTv.setText(mDevicesName);
		} else {
			holder.nameTv.setText(getSwitchNameByBoardId(Integer.parseInt(map
					.get("boardId"))));
		}
		if (map.get("alarmType") != null && !"".equals(map.get("alarmType"))) {
			int type = Integer.parseInt(map.get("alarmType"));
			if (4 == type || 2 == type) {//零序异常 、零序保护则信息列 显示成  “零序电流：A流值”
				String info = map.get("info");//"A流：7，B流：2，C流：5"
				int startIndex = info.indexOf("流");
				int endIndex = info.indexOf("，");
				String alStr = info.substring(startIndex + 2, endIndex);
				holder.infoTv.setText("零序电流:" + alStr);
			} else {
				holder.infoTv.setText(map.get("info"));
			}
			type = type > 3?type - 1:type;
			holder.typeTv.setText(mSpinnerStr[type] + "");
		}
		holder.timeTv.setText(map.get("time"));
		int boardId = Integer.parseInt(map.get("boardId"));
		int superId = 0;
		superId = getSwitchMainIdByBoardId(boardId);
		String lineName = getNameByBoardId(superId);
		holder.lineTv.setText(lineName);
		return convertView;
	}

	private class ViewHolder {
		TextView timeTv;
		TextView nameTv;
		TextView typeTv;
		TextView lineTv;
		TextView infoTv;
	}

	private int getWidthScreen() {
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		int width = dm.widthPixels;
		return width;
	}

	/**
	 * 根据编号获取线路名称
	 */
	private String getNameByBoardId(int number) {
		if (mLineList != null && mLineList.size() > 1) {
			for (int i = 0; i < mLineList.size(); i++) {
				Line line = mLineList.get(i);
				if (number == line.getNumber()) {
					String name = line.getName();
					int surperId = line.getSuperId();
					// if (surperId != -1) {
					// return name + "/" + getNameByBoardId(surperId);
					// }
					return name;
				}
			}
		}

		return null;
	}

	/**
	 * 根据板号获取主线板号
	 */
	private int getSwitchMainIdByBoardId(int boardId) {
		if (mSwitchList != null && mSwitchList.size() > 0) {
			for (int i = 0; i < mSwitchList.size(); i++) {
				Switch switch1 = mSwitchList.get(i);
				if (boardId == switch1.getBoard()) {
					return switch1.getSuperId();
				}
			}
		}
		return 0;
	}

	/**
	 * 根据板号获取开关名称
	 */
	private String getSwitchNameByBoardId(int boardId) {
		if (mSwitchList != null && mSwitchList.size() > 0) {
			for (int i = 0; i < mSwitchList.size(); i++) {
				Switch switch1 = mSwitchList.get(i);
				if (boardId == switch1.getBoard()) {
					return switch1.getName();
				}
			}
		}
		return "";
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
