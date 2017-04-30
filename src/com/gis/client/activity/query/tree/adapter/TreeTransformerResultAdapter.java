package com.gis.client.activity.query.tree.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.gis.client.R;
import com.gis.client.model.Transformer;

public class TreeTransformerResultAdapter extends BaseAdapter {
	private Context mContext;

	private Transformer mResult;

	private LayoutInflater mInflator;

	private String[] nameStrs;

	public TreeTransformerResultAdapter(Context context, Transformer result) {
		this.mContext = context;
		this.mResult = result;
		this.mInflator = LayoutInflater.from(mContext);
		nameStrs = mContext.getResources().getStringArray(
				R.array.voltage_value_array);
	}

	@Override
	public int getCount() {
		if (nameStrs != null) {
			return nameStrs.length;
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
		ViewHolder viewHost;
		if (convertView == null || convertView.getTag() == null) {
			convertView = mInflator.inflate(R.layout.tree_result_list_item,
					null);
			viewHost = new ViewHolder();
			viewHost.paramsName = (TextView) convertView
					.findViewById(R.id.tree_line_result_params_name);
			viewHost.paramsValue = (TextView) convertView
					.findViewById(R.id.tree_line_result_params_value);
			convertView.setTag(viewHost);

		} else {
			viewHost = (ViewHolder) convertView.getTag();
		}
		viewHost.paramsName.setText(nameStrs[position]);

		if (mResult != null) {
			setValue(viewHost.paramsValue, position);
		}
		return convertView;
	}

	private void setValue(TextView view, int position) {
		switch (position) {
		case 0:
			view.setText(mResult.getId() + "");
			break;
		case 1:
			view.setText(mResult.getName());
			break;
		case 2:
			view.setText(mResult.getNumber() + "");
			break;
		case 3:
			view.setText(mResult.getBoard() + "");
			break;
		case 4:
			view.setText(mResult.getDesc());
			break;
		case 5:
			view.setText(mResult.getModelNumber() + "");
			break;
		case 6:
			view.setText(mResult.getManufacturers());
			break;
		case 7:
			view.setText(mResult.getRatedCapacitance() + "");
			break;
		case 8:
			view.setText(mResult.getRelayNumber() + "");
			break;
		case 9:
			view.setText(mResult.getSuperId() + "");
			break;
		case 10:
			view.setText(mResult.getLongitude() / 1e6 + "");
			break;
		case 11:
			view.setText(mResult.getLatitude()  / 1e6 + "");
			break;
		default:
			break;
		}
	}

	private class ViewHolder {
		TextView paramsName;
		TextView paramsValue;
	}

}
