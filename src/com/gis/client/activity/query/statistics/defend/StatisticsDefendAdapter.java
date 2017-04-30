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
import com.gis.client.model.Switch;

public class StatisticsDefendAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflator;
	private List<Switch> mSwitchList;
	private List<String> mResList = new ArrayList<String>();
	private Map<String, Integer> mTypeMap;

	public StatisticsDefendAdapter(Context context, List<String> list,
			Map<String, Integer> map, List<Switch> switchList) {
		this.mContext = context;
		this.mResList = list;
		this.mTypeMap = map;
		this.mSwitchList = switchList;
		this.mInflator = LayoutInflater.from(mContext);
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
			convertView = mInflator.inflate(
					R.layout.statistics_defend_list_item, null);
			holder.guoTv = (TextView) convertView
					.findViewById(R.id.statistics_defend_list_item_guo_text);
			holder.guoTv.setWidth(getWidthScreen() / 4);
			holder.nameTv = (TextView) convertView
					.findViewById(R.id.statistics_defend_list_item_name_text);
			holder.nameTv.setWidth(getWidthScreen() / 4);
			holder.suTv = (TextView) convertView
					.findViewById(R.id.statistics_defend_list_item_su_text);
			holder.suTv.setWidth(getWidthScreen() / 4);
			holder.zeroTv = (TextView) convertView
					.findViewById(R.id.statistics_defend_list_item_zero_text);
			holder.zeroTv.setWidth(getWidthScreen() / 4);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String boardId = mResList.get(position);
		if (boardId != null && !"".equals(boardId)) {
			String name = getSwitchNameByBoardId(Integer.parseInt(boardId));
			holder.nameTv.setText(name);
			String gTypeKey = boardId + "-" + "0";
			String sTypeKey = boardId + "-" + "1";
			String zTypeKey = boardId + "-" + "2";
			int guoLCount = 0;
			int suLCount = 0;
			int zeoLCount = 0;
			if (mTypeMap != null) {
				if (mTypeMap.containsKey(gTypeKey)) {
					guoLCount = mTypeMap.get(gTypeKey);
				}
				if (mTypeMap.containsKey(sTypeKey)) {
					suLCount = mTypeMap.get(sTypeKey);
				}
				if (mTypeMap.containsKey(zTypeKey)) {
					zeoLCount = mTypeMap.get(zTypeKey);
				}
			}
			holder.guoTv.setText(guoLCount + "次");
			holder.suTv.setText(suLCount + "次");
			holder.zeroTv.setText(zeoLCount + "次");
		}
		return convertView;
	}

	private class ViewHolder {
		TextView nameTv;
		TextView guoTv;
		TextView suTv;
		TextView zeroTv;
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
