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

public class GateDetailAdapter extends BaseAdapter {
	private Context mContext;
	private List<Map<String, String>> mDatas;
	private LayoutInflater mInflator;
	private String mDevicesName;
	private String[] mSpinnerStr;

	public GateDetailAdapter(Context context, String switchName,
			List<Map<String, String>> list) {
		this.mContext = context;
		this.mInflator = LayoutInflater.from(mContext);
		this.mDevicesName = switchName;
		this.mDatas = list;
		mSpinnerStr = mContext.getResources().getStringArray(
				R.array.statistics_gate_type_array);
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
			convertView = mInflator.inflate(
					R.layout.statistics_gate_detail_list_item, null);
			holder.name = (TextView) convertView
					.findViewById(R.id.statistics_gate_detail_list_item_name_text);
			holder.name.setWidth(getWidthScreen() / 6);
			holder.timeTv = (TextView) convertView
					.findViewById(R.id.statistics_gate_detail_list_item_time_text);
			holder.timeTv.setWidth(getWidthScreen() / 3);
			holder.typeTv = (TextView) convertView
					.findViewById(R.id.statistics_gate_detail_list_item_type_text);
			holder.typeTv.setWidth(getWidthScreen() / 6);
			holder.controllerTv = (TextView) convertView
					.findViewById(R.id.statistics_gate_detail_list_item_controller_text);
			holder.controllerTv.setWidth(getWidthScreen() / 6);
			holder.isSucceedTv = (TextView) convertView
					.findViewById(R.id.statistics_gate_detail_list_item_is_succeed_text);
			holder.isSucceedTv.setWidth(getWidthScreen() / 6);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Map<String, String> map = mDatas.get(position);
		if (map.get("cmdtype") != null && !"".equals(map.get("cmdtype"))) {
			int type = Integer.parseInt(map.get("cmdtype"));
			holder.typeTv.setText(mSpinnerStr[type] + "");
		}
		holder.name.setText(mDevicesName);
		holder.timeTv.setText(map.get("cmdtime"));
		holder.controllerTv.setText(map.get("cmdname"));
		String isSucceed = map.get("isSuccess");
		if ("0".equals(isSucceed)) {
			isSucceed = "失败";
		} else if ("1".equals(isSucceed)) {
			isSucceed = "成功";
		}
		holder.isSucceedTv.setText(isSucceed);
		return convertView;
	}

	private class ViewHolder {
		TextView name;
		TextView timeTv;
		TextView typeTv;
		TextView controllerTv;
		TextView isSucceedTv;
	}

	private int getWidthScreen() {
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		int width = dm.widthPixels;
		return width;
	}

}
