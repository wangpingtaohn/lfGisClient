package com.gis.client.activity.query.tree.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.gis.client.R;
import com.gis.client.model.Line;

public class TreeLineResultAdapter extends BaseAdapter {
	private Context mContext;

	private Line mResult;

	private LayoutInflater mInflator;

	private String[] nameStrs;

	public TreeLineResultAdapter(Context context, Line result) {
		this.mContext = context;
		this.mResult = result;
		this.mInflator = LayoutInflater.from(mContext);
		nameStrs = mContext.getResources().getStringArray(
				R.array.line_value_array);
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
			view.setText(mResult.getLineNumber() + "");
			break;
		case 1:
			view.setText(mResult.getName());
			break;
		case 2:
			view.setText(mResult.getNumber() + "");
			break;
		case 3:
			view.setText(mResult.getSuperId() + "");
			break;
		case 4:
			view.setText(mResult.getIsPower());
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
