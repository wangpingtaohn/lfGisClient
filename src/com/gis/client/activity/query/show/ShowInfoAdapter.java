package com.gis.client.activity.query.show;

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
import com.gis.client.model.SwitchInfo;
import com.gis.client.util.DateUtils;

public class ShowInfoAdapter extends BaseAdapter {
	private Context mContext;
	private List<SwitchInfo> mDatas;
	private LayoutInflater mInflator;
	private List<Switch> mSwitchList;
	private Map<Integer, SwitchInfo> mSwitchInfoMap;

	public ShowInfoAdapter(Context context, List<SwitchInfo> list,
			List<Switch> switchList) {
		this.mContext = context;
		this.mDatas = list;
		this.mSwitchList = switchList;
		this.mInflator = LayoutInflater.from(mContext);
	}

	public ShowInfoAdapter(Context context, Map<Integer, SwitchInfo> list,
			List<Switch> switchList) {
		this.mContext = context;
		this.mSwitchInfoMap = list;
		this.mSwitchList = switchList;
		this.mInflator = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		if (mSwitchList != null && mSwitchList.size() > 0) {
			return mSwitchList.size();
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
			convertView = mInflator.inflate(R.layout.show_info_list_item, null);
			holder.name = (TextView) convertView
					.findViewById(R.id.show_info_list_item_address_text);
			holder.name.setWidth(getWidthScreen() / 9);
			holder.switchTv = (TextView) convertView
					.findViewById(R.id.show_info_list_item_switch_status_text);
			holder.switchTv.setWidth(getWidthScreen() / 9);
			holder.energyTv = (TextView) convertView
					.findViewById(R.id.show_info_list_item_energy_status_text);
			holder.energyTv.setWidth(getWidthScreen() / 9);
			holder.voltageTv = (TextView) convertView
					.findViewById(R.id.show_info_list_item_voltage_text);
			holder.voltageTv.setWidth(getWidthScreen() / 9);
			holder.aElectryTv = (TextView) convertView
					.findViewById(R.id.show_info_list_item_a_electricity_text);
			holder.aElectryTv.setWidth(getWidthScreen() / 9);
			holder.bElectryTv = (TextView) convertView
					.findViewById(R.id.show_info_list_item_b_electricity_text);
			holder.bElectryTv.setWidth(getWidthScreen() / 9);
			holder.cElectryTv = (TextView) convertView
					.findViewById(R.id.show_info_list_item_c_electricity_text);
			holder.cElectryTv.setWidth(getWidthScreen() / 9);
			holder.electryTv = (TextView) convertView
					.findViewById(R.id.show_info_list_item_0_electricity_text);
			holder.electryTv.setWidth(getWidthScreen() / 9);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		SwitchInfo switchInfo = getSwitchInfoById2(mSwitchList.get(position)
				.getBoard());
		holder.name.setText(mSwitchList.get(position).getName());
		if (switchInfo != null) {
			String time = switchInfo.getTime();
			long lTime = DateUtils.getLongCurrentTime();
			if (time != null && !"".equals(time)) {
				lTime = DateUtils.timeStrToLong(time);
			}
			long lCurTime = DateUtils.getLongCurrentTime();
			if (lCurTime - lTime <= 10 * 60 * 1000) {
				holder.switchTv.setText(getSwitchStatus(switchInfo
						.getSwitchStatus()));
				holder.energyTv.setText(getStored(switchInfo.getIsSave()));
				holder.voltageTv.setText(switchInfo.getVoltage() + "");
				holder.aElectryTv.setText(switchInfo.getaL() + "");
				holder.bElectryTv.setText(switchInfo.getbL() + "");
				holder.cElectryTv.setText(switchInfo.getcL() + "");
				holder.electryTv.setText(switchInfo.getoL() + "");
			} else {
				holder.switchTv.setText("未知");
				holder.energyTv.setText("未知");
				holder.voltageTv.setText(0 + "");
				holder.aElectryTv.setText(0 + "");
				holder.bElectryTv.setText(0 + "");
				holder.cElectryTv.setText(0 + "");
				holder.electryTv.setText(0 + "");
			}
		} else {
			holder.switchTv.setText("未知");
			holder.energyTv.setText("未知");
			holder.voltageTv.setText(0 + "");
			holder.aElectryTv.setText(0 + "");
			holder.bElectryTv.setText(0 + "");
			holder.cElectryTv.setText(0 + "");
			holder.electryTv.setText(0 + "");
		}
		return convertView;
	}

	private class ViewHolder {
		TextView name;
		TextView switchTv;
		TextView energyTv;
		TextView voltageTv;
		TextView cElectryTv;
		TextView aElectryTv;
		TextView bElectryTv;
		TextView electryTv;
	}

	private int getWidthScreen() {
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		int width = dm.widthPixels;
		return width;
	}

	/**
	 * 根据开关板号获取开关信息
	 */
	private SwitchInfo getSwitchInfoById(int boardId) {
		if (mDatas != null && mDatas.size() > 0) {
			int count = mDatas.size();
			for (int i = count - 1; i >= 0; i--) {
				int infoBoardId = mDatas.get(i).getBoardNumber();
				if (boardId == infoBoardId) {
					return mDatas.get(i);
				}
			}
		}
		return null;

	}

	/**
	 * 根据开关板号获取开关信息
	 */
	private SwitchInfo getSwitchInfoById2(int boardId) {
		if (mSwitchInfoMap != null && mSwitchInfoMap.containsKey(boardId)) {
			return mSwitchInfoMap.get(boardId);
		}
		return null;

	}

	/**
	 * 获取储能状态
	 * 
	 */
	private String getStored(int msg) {
		String type = "";

		switch (msg) {
		case 0:
			type = mContext.getResources().getString(
					R.string.str_info_query_switch_stored);
			break;
		case 1:
			type = mContext.getResources().getString(
					R.string.str_info_query_switch_unStored);
			break;
		case 2:
			type = mContext.getResources().getString(
					R.string.str_info_query_switch_unKnown);
			break;
		default:
			break;
		}

		return type;
	}

	/**
	 * 获取开关状态
	 * 
	 */
	private String getSwitchStatus(int msg) {
		String type = "";

		switch (msg) {
		case 0:
			type = mContext.getResources().getString(
					R.string.str_info_query_switch_close);
			break;
		case 1:
			type = mContext.getResources().getString(
					R.string.str_info_query_switch_open);
			break;
		case 2:
			type = mContext.getResources().getString(
					R.string.str_info_query_switch_unKnown);
			break;
		default:
			break;
		}

		return type;
	}

}
