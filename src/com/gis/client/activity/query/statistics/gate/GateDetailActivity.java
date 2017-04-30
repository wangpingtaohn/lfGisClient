package com.gis.client.activity.query.statistics.gate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import com.gis.client.R;

public class GateDetailActivity extends Activity {

	private Context mContext;

	private ListView mShowListView;

	private GateDetailAdapter mAdapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistics_gate_detail_layout);
		mContext = GateDetailActivity.this;

		initView();
		getData();
	}

	private void initView() {

		Button backBtn = (Button) findViewById(R.id.header_back_button);

		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mShowListView = (ListView) findViewById(R.id.statistics_gate_detail_listview);

	}


	@SuppressWarnings("unchecked")
	private void getData() {
		Intent intent = getIntent();
		String boardId = intent.getStringExtra("boardId");
		String name = intent.getStringExtra("name");
		List<Map<String, String>> resultList = (List<Map<String, String>>) intent
				.getSerializableExtra("resultList");
		mAdapter = new GateDetailAdapter(mContext, name, managerResult(resultList, boardId));
		mShowListView.setAdapter(mAdapter);
	}

	private List<Map<String, String>> managerResult(
			List<Map<String, String>> resultList, String boardId) {
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		if (resultList != null && resultList.size() > 0) {
			for (Map<String, String> map : resultList) {
				String bId = map.get("boardId");
				if (boardId.equals(bId)) {
					list.add(map);
				}
			}
		}
		return list;
	}
}
