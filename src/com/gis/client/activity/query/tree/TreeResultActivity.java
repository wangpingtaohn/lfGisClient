package com.gis.client.activity.query.tree;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.gis.client.R;
import com.gis.client.activity.query.tree.adapter.TreeLineResultAdapter;
import com.gis.client.activity.query.tree.adapter.TreeSwitchResultAdapter;
import com.gis.client.activity.query.tree.adapter.TreeTransformerResultAdapter;
import com.gis.client.common.CommonActivity;
import com.gis.client.common.CustomDialog;
import com.gis.client.model.CommonObject;
import com.gis.client.model.Line;
import com.gis.client.model.Switch;
import com.gis.client.model.Transformer;

public class TreeResultActivity extends CommonActivity {

	private Context mContext;
	
	private ListView mResultList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tree_result_list_layout);
		Log.i("wpt", "****onCreate******");
		mContext = TreeResultActivity.this;
				
		initView();
		fillData();
	}
	
	private void initView() {
		Button backBtn = (Button)findViewById(R.id.header_back_button);
		mResultList = (ListView)findViewById(R.id.tree_line_result_list);
//		BaseAdapter adapter = new TreeTransformerResultAdapter(mContext, null);
		
//		mResultList.setAdapter(adapter);
		
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
	}
	
	private void fillData() {
		
		Intent intent = getIntent();
//		CommonObject object = (CommonObject) intent.getExtras().get("result");
		CommonObject object = (CommonObject) intent.getSerializableExtra("result");
		
		BaseAdapter adapter = null;
		
		if (object instanceof Line) {
			Line line = (Line) object;
			adapter = new TreeLineResultAdapter(mContext, line);
		} else if (object instanceof Transformer) {
			Transformer voltage = (Transformer) object;
			adapter = new TreeTransformerResultAdapter(mContext, voltage);
		} else if (object instanceof Switch) {
			Switch switch1 = (Switch) object;
			adapter = new TreeSwitchResultAdapter(mContext, switch1);
		}
		CustomDialog dialog = new CustomDialog(mContext, R.style.mydialog);
		dialog.setContentView(R.layout.tree_result_list_layout);
		
		if (adapter != null) {
			mResultList.setAdapter(adapter);
		}
	}
	
	@Override
	protected void onDestroy() {
		Log.i("wpt", "****onDestroy******");
		super.onDestroy();
	}
}

