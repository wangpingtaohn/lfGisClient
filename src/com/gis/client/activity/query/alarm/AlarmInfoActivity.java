package com.gis.client.activity.query.alarm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import com.gis.client.R;
import com.gis.client.activity.query.tree.TreeLineActivity;
import com.gis.client.common.CommonProgressDialog;
import com.gis.client.common.Constants;
import com.gis.client.common.CustomToast;
import com.gis.client.common.Result;
import com.gis.client.http.net.RequestUtil;
import com.gis.client.model.Line;
import com.gis.client.model.Switch;
import com.gis.client.model.Transformer;
import com.gis.client.util.NetworkUtil;

public class AlarmInfoActivity extends Activity implements OnClickListener {

	private Context mContext;

	private EditText mDevicesEt;

	private int mBoardId = -1;

	private CommonProgressDialog mProgressDialog;

	private List<Map<String, String>> mResultList = new ArrayList<Map<String, String>>();

	private ListView mAlarmListView;

	private AlarmInfoAdapter mListAdapter;

	private TextView mFooterView;

	private int mPageCount = 1;

	private static final int mCount = 20;

	private int mTotal;

	private String[] mSpinnerStr;

	private List<Line> mLineList = new ArrayList<Line>();

	private List<Transformer> mTransformerList = new ArrayList<Transformer>();

	private List<Switch> mSwitchList = new ArrayList<Switch>();

	private Spinner mAlarmSpinner;
	
	private Spinner mDevicesSpinner;
	
	private int mSpDevices;
	
	private String[] mDevicesSpinnerStr;

	private boolean mExit;

	private boolean mIsGetAll;

	private boolean isAddFoot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_info_layout);

		mContext = AlarmInfoActivity.this;

		getTreeInfo();

		initView();
	}

	private void initView() {
		mDevicesEt = (EditText) findViewById(R.id.alarm_info_query_devices_edit);
		Button selDevicesBtn = (Button) findViewById(R.id.alarm_info_query_devices_select);
		Button queryBtn = (Button) findViewById(R.id.alarm_info_query_btn);
		Button queryAllBtn = (Button) findViewById(R.id.alarm_info_query_all_btn);

		mAlarmListView = (ListView) findViewById(R.id.alarm_info_listview);

		mAlarmSpinner = (Spinner) findViewById(R.id.alarm_info_query_alarm_type_spinner);
		mDevicesSpinner = (Spinner) findViewById(R.id.alarm_info_query_devices_select_spinner);
		mSpinnerStr = getResources().getStringArray(
				R.array.alarm_query_type_array);
		mDevicesSpinnerStr = getResources().getStringArray(
				R.array.statistics_gate_sel_devices_array);
		ArrayAdapter<String> spDevicesAdapter = new ArrayAdapter<String>(
				mContext, android.R.layout.simple_spinner_item,
				mDevicesSpinnerStr);
		spDevicesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mDevicesSpinner.setAdapter(spDevicesAdapter);
		
		mSpDevices = mDevicesSpinner.getSelectedItemPosition();
		
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
				mContext, android.R.layout.simple_spinner_item, mSpinnerStr);
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mAlarmSpinner.setAdapter(spinnerAdapter);
		selDevicesBtn.setOnClickListener(this);
		mDevicesEt.setOnClickListener(this);
		queryBtn.setOnClickListener(this);
		queryAllBtn.setOnClickListener(this);
		
		mDevicesSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				mSpDevices = arg2;
				if (1 == arg2) {
					mBoardId = -1;
					mDevicesEt.setText("点击选择设备");
				} else {
					mBoardId = -1;
					mDevicesEt.setText("");
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

	}

	@SuppressWarnings("unchecked")
	private void getTreeInfo() {
		Intent intent = getIntent();
		if (intent != null) {
			mLineList = (List<Line>) intent.getSerializableExtra("lineList");
			mTransformerList = (List<Transformer>) intent
					.getSerializableExtra("transformerList");
			mSwitchList = (List<Switch>) intent
					.getSerializableExtra("switchList");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.alarm_info_query_devices_edit:
		if (1 == mSpDevices) {
			Intent intent = new Intent(mContext, TreeLineActivity.class);
			intent.putExtra("selectFlg", true);
			intent.putExtra("lineList", (Serializable) mLineList);
			intent.putExtra("transformerList",
					(Serializable) mTransformerList);
			intent.putExtra("switchList", (Serializable) mSwitchList);
			startActivityForResult(intent,
					Constants.ACTIVITY_REQUEST_CODE_ALARM);
		}
		break;
		case R.id.alarm_info_query_devices_select:
			Intent intent = new Intent(mContext, TreeLineActivity.class);
			intent.putExtra("selectFlg", true);
			intent.putExtra("lineList", (Serializable) mLineList);
			intent.putExtra("transformerList", (Serializable) mTransformerList);
			intent.putExtra("switchList", (Serializable) mSwitchList);
			startActivityForResult(intent,
					Constants.ACTIVITY_REQUEST_CODE_ALARM);
			break;
		case R.id.alarm_info_query_btn:
			if (1 == mSpDevices && -1 == mBoardId) {
				CustomToast.showToast(mContext,
						R.string.str_history_event_tip_input_name);
			} else {
				isAddFoot = false;
				mIsGetAll = false;
				mPageCount = 1;
				mResultList.clear();
				removeListFooterView();
				if (mListAdapter != null) {
					mListAdapter.notifyDataSetChanged();
				}
				int type = mAlarmSpinner.getSelectedItemPosition();
				type = type >= 3 ? type + 1 : type;
				new getAlarmInfoTask().execute(mCount, mPageCount, type);
			}
			break;
		case R.id.alarm_info_query_all_btn:
			mPageCount = 1;
			isAddFoot = false;
			mIsGetAll = true;
			mResultList.clear();
			mBoardId = -1;
			removeListFooterView();
			if (mListAdapter != null) {
				mListAdapter.notifyDataSetChanged();
			}
			new getAlarmInfoTask().execute(mCount, mPageCount, -1);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK && data != null) {
			String devicesName = data.getStringExtra("devicesName");
			mBoardId = data.getIntExtra("boardId", -1);
			mDevicesEt.setText(devicesName);
		}
	}

	private class getAlarmInfoTask extends
			AsyncTask<Integer, Void, Result<List<Map<String, String>>>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			int netCode = NetworkUtil.getInstance()
					.isNetworkAvailable(mContext);
			if (NetworkUtil.NET_NOT_AVAILABLE == netCode) {
				CustomToast.showToast(mContext, R.string.str_net_work_error);
				cancel(true);
			} else {
				if (!mExit) {
					mProgressDialog = CommonProgressDialog.show(mContext,
							R.string.str_tip,
							getResources()
									.getString(R.string.str_msg_query_ing));
				}
			}

		}

		@Override
		protected Result<List<Map<String, String>>> doInBackground(
				Integer... params) {
			int page = -1;
			int count = -1;
			int type = -1;
			if (params != null) {
				count = params[0];
				page = params[1];
				type = params[2];
			}
			RequestUtil requestUtil = new RequestUtil(mContext);
			return requestUtil.getAlarmInfoList(mBoardId, type + "", "", "",
					count, page);
		}

		@Override
		protected void onPostExecute(Result<List<Map<String, String>>> result) {
			super.onPostExecute(result);
			if (!mExit) {
				mProgressDialog.dismiss();
			}
			if (result != null) {
				if (result.getErrorCode() != null
						&& !"".equals(result.getErrorCode())) {
					CustomToast.showToast(mContext, result.getErrorCode());
				} else if (result.getResult() != null && result.getResult().size() > 0) {
					mTotal = result.getTotal();
					// mResultList = result.getResult();
					mResultList.addAll(result.getResult());
					if (mResultList.size() < mTotal) {
						if (!isAddFoot) {
							addListFooterView();
						}
					} else {
						removeListFooterView();
					}
					if (mListAdapter == null) {
						mListAdapter = new AlarmInfoAdapter(mContext, mResultList,
								mLineList, mTransformerList, mSwitchList,
								mDevicesEt.getText().toString());
						mAlarmListView.setAdapter(mListAdapter);
					} else {
						mListAdapter.notifyDataSetChanged();
					}
					mPageCount++;
				} else {
					CustomToast.showToast(mContext, R.string.str_not_result);
					if (mListAdapter != null) {
						mListAdapter.notifyDataSetChanged();
					}
				}
			} else {
				CustomToast.showToast(mContext, R.string.str_not_result);
				if (mListAdapter != null) {
					mListAdapter.notifyDataSetChanged();
				}
			}
		}

	}

	private void addListFooterView() {
		if (mFooterView == null) {
			isAddFoot = true;
			mFooterView = new TextView(mContext);
			mFooterView.setTextSize(18);
			mFooterView.setTextColor(Color.RED);
			mFooterView.setText("点击加载更多");
			mFooterView.setGravity(Gravity.CENTER);
			mFooterView.setPadding(0, 10, 0, 10);
			mAlarmListView.addFooterView(mFooterView, null, false);
			mFooterView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mResultList.size() < mTotal) {
						int type = mIsGetAll ? -1 : mAlarmSpinner
								.getSelectedItemPosition();
						type = type >= 3 ? type + 1 : type;
						new getAlarmInfoTask()
								.execute(mCount, mPageCount, type);
					}
				}
			});
		}
	}

	private void removeListFooterView() {
		if (mFooterView != null && mResultList.size() > 0) {
			mAlarmListView.removeFooterView(mFooterView);
			isAddFoot = false;
			mFooterView = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mExit = true;
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}
}
