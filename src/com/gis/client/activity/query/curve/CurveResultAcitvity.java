package com.gis.client.activity.query.curve;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gis.client.R;
import com.gis.client.model.SwitchInfo;

public class CurveResultAcitvity extends Activity {

	private Context mContext;

	private LinearLayout mCavansLayout;

	private TextView mMaxTView;

	private TextView mMinTView;

	private TextView mAveTView;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.curve_result_layout);

		mContext = CurveResultAcitvity.this;
		Intent intent = getIntent();
		List<SwitchInfo> switchInfoList = (List<SwitchInfo>) intent
				.getSerializableExtra("switchInfoList");
		Map<String, String> map = new HashMap<String, String>();
		map = (Map<String, String>) intent.getSerializableExtra("map");
		initView(map);
		CurveCanvas canvas = new CurveCanvas(mContext, switchInfoList, map);
		mCavansLayout.addView(canvas);

	}

	private void initView(Map<String, String> map) {
		mCavansLayout = (LinearLayout) findViewById(R.id.curve_result_canvas_layout);
		TextView nameTView = (TextView) findViewById(R.id.curve_result_name);
		TextView titleTView = (TextView) findViewById(R.id.curve_result_title);
		mMaxTView = (TextView) findViewById(R.id.curve_result_max_value);
		mMinTView = (TextView) findViewById(R.id.curve_result_min_value);
		mAveTView = (TextView) findViewById(R.id.curve_result_ave_value);

		TextView redTView = (TextView) findViewById(R.id.curve_line_red);
		TextView blueTView = (TextView) findViewById(R.id.curve_line_blue);
		TextView yellowTView = (TextView) findViewById(R.id.curve_line_yellow);

		if (map != null) {
			nameTView.setText(map.get("name"));
			mMaxTView.setText(getResources().getString(
					R.string.str_curve_query_max_value_text)
					+ map.get("maxValue"));
			mMinTView.setText(getResources().getString(
					R.string.str_curve_query_min_value_text)
					+ map.get("minValue"));
			mAveTView.setText(getResources().getString(
					R.string.str_curve_query_ave_value_text)
					+ map.get("aveValue"));
			if ("0".equals(map.get("type"))) {
				titleTView.setText(R.string.str_curve_query_dianliu_curve_text);
				if ("true".equals(map.get("al"))) {
					redTView.setVisibility(View.VISIBLE);
				}
				if ("true".equals(map.get("bl"))) {
					blueTView.setVisibility(View.VISIBLE);
				}
				if ("true".equals(map.get("cl"))) {
					yellowTView.setVisibility(View.VISIBLE);
				}
			} else if ("1".equals(map.get("type"))) {
				titleTView.setText(R.string.str_curve_query_dianya_curve_text);
			}
		}
	}

}
