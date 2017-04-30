package com.gis.client.activity.query.statistics.gate;

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
import com.gis.client.model.Switch;

public class StatisticsGateAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflator;
	private List<Switch> mSwitchList;
	private List<Map<String, String>> mResList;
	private Map<String, Integer> mCountMap;
	private Map<String, Integer> mSucceedCountMap;
	private String[] mSpinnerStr;

	public StatisticsGateAdapter(Context context,
			List<Map<String, String>> list, Map<String, Integer> map,
			Map<String, Integer> sMap, List<Switch> switchList) {
		this.mContext = context;
		this.mResList = list;
		this.mCountMap = map;
		this.mSucceedCountMap = sMap;
		this.mSwitchList = switchList;
		this.mInflator = LayoutInflater.from(mContext);
		mSpinnerStr = mContext.getResources().getStringArray(
				R.array.statistics_gate_type_array);
	}

	@Override
	public int getCount() {
		if (mResList != null && mResList.size() > 0) {
			return mResList.size();
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
			convertView = mInflator.inflate(R.layout.statistics_gate_list_item,
					null);
			holder.countTv = (TextView) convertView
					.findViewById(R.id.statistics_gate_list_item_counnt_text);
			holder.countTv.setWidth(getWidthScreen() / 3);
			holder.nameTv = (TextView) convertView
					.findViewById(R.id.statistics_gate_list_item_name_text);
			holder.nameTv.setWidth(getWidthScreen() / 3);
			holder.typeTv = (TextView) convertView
					.findViewById(R.id.statistics_gate_list_item_type_text);
			holder.typeTv.setWidth(getWidthScreen() / 3);
			holder.succeedCount = (TextView) convertView
					.findViewById(R.id.statistics_gate_list_item_succeed_count_text);
			holder.succeedCount.setWidth(getWidthScreen() / 3);
			holder.succeedRateTv = (TextView) convertView
					.findViewById(R.id.statistics_gate_list_item_succeed_rate_text);
			holder.succeedRateTv.setWidth(getWidthScreen() / 3);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Map<String, String> map = mResList.get(position);
		String boardId = map.get("boardId");
		int type = 0;
		if (map.get("cmdtype") != null && !"".equals(map.get("cmdtype"))) {
			type = Integer.parseInt(map.get("cmdtype"));
			holder.typeTv.setText(mSpinnerStr[type] + "");
		}
		if (boardId != null && !"".equals(boardId)) {
			String name = getSwitchNameByBoardId(Integer.parseInt(boardId));
			holder.nameTv.setText(name);
			String countKey = boardId + "-" + type;
			int typeCount = 0;
			if (mCountMap != null) {
				if (mCountMap.containsKey(countKey)) {
					typeCount = mCountMap.get(countKey);
				}
			}
			holder.countTv.setText(typeCount + "次");
			int succeedCount = 0;
			if (mSucceedCountMap != null) {
				if (mSucceedCountMap.containsKey(countKey)) {
					succeedCount = mSucceedCountMap.get(countKey);
				}
			}
			holder.succeedCount.setText(succeedCount + "次");
			float succeedRate = 0.0f;
			if (0 != typeCount) {
				succeedRate = succeedCount / (float)typeCount;
			}
			holder.succeedRateTv.setText(succeedRate * 100 + "%");
		}
		return convertView;
	}

	private class ViewHolder {
		TextView countTv;
		TextView nameTv;
		TextView typeTv;
		TextView succeedCount;
		TextView succeedRateTv;
	}

	private int getWidthScreen() {
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		int width = dm.widthPixels;
		return width;
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

}
