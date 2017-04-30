package com.gis.client.activity.query.history;

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

public class HistoryEventAdapter extends BaseAdapter {
	private Context mContext;
	private List<Map<String, String>> mDatas;
	private LayoutInflater mInflator;
	private List<Switch> mSwitchList;
	private String[] mSpinnerStr;
	private List<Line> mLineList;

	public HistoryEventAdapter(Context context, List<Map<String, String>> list,
			List<Switch> switchList, List<Line> lineList) {
		this.mContext = context;
		this.mDatas = list;
		this.mInflator = LayoutInflater.from(mContext);
		this.mSwitchList = switchList;
		this.mSpinnerStr = mContext.getResources().getStringArray(
				R.array.history_query_type_array);
		this.mLineList = lineList;
	}

	@Override
	public int getCount() {
		if (mDatas != null && mDatas.size() > 0) {
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
			convertView = mInflator.inflate(R.layout.history_event_list_item,
					null);
			holder.timeTv = (TextView) convertView
					.findViewById(R.id.history_event_list_item_time_text);
			holder.timeTv.setWidth(getWidthScreen() / 3);
			holder.nameTv = (TextView) convertView
					.findViewById(R.id.history_event_list_item_name_text);
			holder.nameTv.setWidth(getWidthScreen() / 6);
			holder.controlTv = (TextView) convertView
					.findViewById(R.id.history_event_list_item_control_text);
			holder.controlTv.setWidth(getWidthScreen() / 6);
			holder.eventTv = (TextView) convertView
					.findViewById(R.id.history_event_list_item_event_text);
			holder.eventTv.setWidth(getWidthScreen() / 3);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Map<String, String> map = mDatas.get(position);
		if (!map.containsKey("alarmType")) {//事件
			holder.nameTv.setText(getSwitchNameByBoardId(Integer.parseInt(map
					.get("boardId"))));
			holder.timeTv.setText(map.get("time"));
			holder.controlTv.setText(map.get("name"));
			String currentStatus = map.get("data1");
			String sendCommand = map.get("data2");
			String type = map.get("type");
			String event = getType(type) + ",当前:"
					+ getSendCommandOrCurrentStatus(currentStatus) + ",发送:"
					+ getSendCommandOrCurrentStatus(sendCommand);
			holder.eventTv.setText(event);
		} else {//报警
			holder.nameTv.setText(getSwitchNameByBoardId(Integer.parseInt(map
					.get("boardId"))));
			holder.timeTv.setText(map.get("time"));
			String info = map.get("info");//"A流：7，B流：2，C流：5"
			if (map.get("alarmType") != null && !"".equals(map.get("alarmType"))) {
				int type = Integer.parseInt(map.get("alarmType"));
				holder.controlTv.setText(mSpinnerStr[type] + "");
				if (4 == type || 2 == type) {//零序异常 、零序保护则信息列 显示成  “零序电流：A流值”
					int startIndex = info.indexOf("流");
					int endIndex = info.indexOf("，");
					String alStr = info.substring(startIndex + 2, endIndex);
					info = "零序电流:" + alStr;
				}
			}
			int boardId = Integer.parseInt(map.get("boardId"));
			int superId = 0;
			superId = getSwitchMainIdByBoardId(boardId);
			String lineName = getNameByBoardId(superId);
			holder.eventTv.setText(lineName + "," + info);
		}
		return convertView;
	}

	private class ViewHolder {
		TextView timeTv;
		TextView nameTv;
		TextView controlTv;
		TextView eventTv;
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

	/**
	 * 获取发送命令和当前状态
	 * 
	 */
	private String getSendCommandOrCurrentStatus(String msg) {
		String event = "";
		if (msg != null && !"".equals(msg)) {

			switch (Integer.parseInt(msg)) {
			case 1:
				event = mContext.getResources().getString(
						R.string.str_history_event_open);
				break;
			case 2:
				event = mContext.getResources().getString(
						R.string.str_history_event_close);
				break;
			case 3:
				event = mContext.getResources().getString(
						R.string.str_history_event_control_close);
				break;
			case 4:
				event = mContext.getResources().getString(
						R.string.str_history_event_control_open);
				break;
			case 6:
				event = mContext.getResources().getString(
						R.string.str_history_event_0_defend_open);
				break;
			case 7:
				event = mContext.getResources().getString(
						R.string.str_history_event_0_defend_close);
				break;
			case 8:
				event = mContext.getResources().getString(
						R.string.str_history_event_data_clear);
				break;
			case 9:
				event = mContext.getResources().getString(
						R.string.str_history_event_software_reSet);
				break;

			default:
				break;
			}
		}

		return event;
	}

	/**
	 * 获取类型
	 * 
	 */
	private String getType(String msg) {
		String type = "";
		if (msg != null && !"".equals(msg)) {

			switch (Integer.parseInt(msg)) {
			case 0:
				type = mContext.getResources().getString(
						R.string.str_history_event_send_control_command);
				break;
			case 1:
				type = mContext.getResources().getString(
						R.string.str_history_event_receive_control_command);
				break;
			case 2:
				type = mContext.getResources().getString(
						R.string.str_history_event_send_time_out);
				break;
			default:
				break;
			}
		}

		return type;
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
	 * 根据编号获取线路名称
	 */
	private String getNameByBoardId(int number) {
		if (mLineList != null && mLineList.size() > 1) {
			for (int i = 0; i < mLineList.size(); i++) {
				Line line = mLineList.get(i);
				if (number == line.getNumber()) {
					String name = line.getName();
					int surperId = line.getSuperId();
//					if (surperId != -1) {
//						return name + "/" + getNameByBoardId(surperId);
//					}
					return name;
				}
			}
		}

		return null;
	}

}
