package com.gis.client.activity.query.history;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.gis.client.util.DateAndTimeUtil;
import com.gis.client.util.NetworkUtil;

public class HistoryEventActivity extends Activity implements OnClickListener {

	private Context mContext;

	private int mBoardId;

	private EditText mDevicesEt;

	private TextView mStartDateEt;

	private TextView mStartTimeEt;

	private TextView mEndDateEt;

	private TextView mEndTimeEt;

	private CommonProgressDialog mProgressDialog;

	private List<Map<String, String>> mResultList = new ArrayList<Map<String, String>>();

	private ListView mHistoryListView;

	private HistoryEventAdapter mListAdapter;

	private TextView mFooterView;

	private int mPageCount = 1;

	private static final int mCount = 50;

	private List<Line> mLineList = new ArrayList<Line>();

	private List<Transformer> mTransformerList = new ArrayList<Transformer>();

	private List<Switch> mSwitchList = new ArrayList<Switch>();

	private int mTotal;

	private DateAndTimeUtil dAndTUtil;

	private String mControlName;

	private String mStartTime;

	private String mEndTime;
	
	private boolean mExit;
	
	private Spinner mDevicesSpinner;
	
	private String[] mDevicesSpinnerStr;
	
	private int mSpDevices;
	
	private boolean mIsGetAll;

	private boolean isAddFoot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_event_layout);
		mContext = HistoryEventActivity.this;

		getTreeInfo();
		initView();
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

	private void initView() {

		dAndTUtil = new DateAndTimeUtil(mContext);

		mDevicesEt = (EditText) findViewById(R.id.history_event_query_devices_edit);
		mStartDateEt = (TextView) findViewById(R.id.history_event_start_time_date_edit);
		mStartTimeEt = (TextView) findViewById(R.id.history_event_start_time_time_edit);
		mEndDateEt = (TextView) findViewById(R.id.history_event_end_time_date_edit);
		mEndTimeEt = (TextView) findViewById(R.id.history_event_end_time_time_edit);
		// Button selDevicesBtn = (Button)
		// findViewById(R.id.history_event_query_devices_select);
		Button queryBtn = (Button) findViewById(R.id.history_event_query_btn);
		Button queryAllBtn = (Button) findViewById(R.id.history_event_query_all_btn);
		
		mDevicesSpinner = (Spinner) findViewById(R.id.history_event_query_devices_select_spinner);
		mDevicesSpinnerStr = getResources().getStringArray(
				R.array.statistics_gate_sel_devices_array);
		ArrayAdapter<String> spDevicesAdapter = new ArrayAdapter<String>(
				mContext, android.R.layout.simple_spinner_item,
				mDevicesSpinnerStr);
		spDevicesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mDevicesSpinner.setAdapter(spDevicesAdapter);

		mSpDevices = mDevicesSpinner.getSelectedItemPosition();
		
		mStartDateEt.setText(DateAndTimeUtil.getDate());
		mEndDateEt.setText(DateAndTimeUtil.getDate());
		mStartTimeEt.setText(DateAndTimeUtil.getTime());
		mEndTimeEt.setText(DateAndTimeUtil.getTime());

		mDevicesEt.setOnClickListener(this);
		mStartDateEt.setOnClickListener(this);
		mStartTimeEt.setOnClickListener(this);
		mEndDateEt.setOnClickListener(this);
		mEndTimeEt.setOnClickListener(this);

		mHistoryListView = (ListView) findViewById(R.id.history_event_listview);

		// selDevicesBtn.setOnClickListener(this);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.history_event_query_devices_edit:
			if (1 == mSpDevices) {
				Intent intent = new Intent(mContext, TreeLineActivity.class);
				intent.putExtra("selectFlg", true);
				intent.putExtra("lineList", (Serializable) mLineList);
				intent.putExtra("transformerList",
						(Serializable) mTransformerList);
				intent.putExtra("switchList", (Serializable) mSwitchList);
				startActivityForResult(intent,
						Constants.ACTIVITY_REQUEST_CODE_HISTORY);
			}
			break;
		case R.id.history_event_query_btn:
			String name = mDevicesEt.getText().toString();
			String startTime = "";
			String endTime = "";
			if (1 == mSpDevices && -1 == mBoardId) {
				CustomToast.showToast(mContext,
						R.string.str_history_event_tip_input_name);
			} else if (mStartDateEt.getText() == null
					|| "".equals(mStartDateEt.getText().toString())
					|| mStartTimeEt.getText() == null
					|| "".equals(mStartTimeEt.getText().toString())
					|| mEndDateEt.getText() == null
					|| "".equals(mEndDateEt.getText().toString())
					|| mEndTimeEt.getText() == null
					|| "".equals(mEndTimeEt.getText().toString())) {
				CustomToast.showToast(mContext,
						R.string.str_history_event_tip_select_time);
			} else {
				isAddFoot = false;
				mIsGetAll = false;
				this.mControlName = name;
				startTime = mStartDateEt.getText().toString() + " "
						+ mStartTimeEt.getText().toString();
				endTime = mEndDateEt.getText().toString() + " "
						+ mEndTimeEt.getText().toString();
				this.mStartTime = startTime;
				this.mEndTime = endTime;
				mPageCount = 1;
				mResultList.clear();
				removeListFooterView();
				if (mListAdapter != null) {
					mListAdapter.notifyDataSetChanged();
				}
				mIsGetAll = false;
				new getHistoryTask().execute(mBoardId, "", startTime, endTime,
						mCount, mPageCount);
			}
			break;
		case R.id.history_event_query_all_btn:
			isAddFoot = false;
			mIsGetAll = true;
			mPageCount = 1;
			mResultList.clear();
			removeListFooterView();
			if (mListAdapter != null) {
				mListAdapter.notifyDataSetChanged();
			}
			new getHistoryTask().execute(-1, "", "", "", mCount,
					mPageCount);
			break;
		case R.id.history_event_start_time_date_edit:
			dAndTUtil.showDateDialog(mStartDateEt);
			break;
		case R.id.history_event_start_time_time_edit:
			dAndTUtil.showTimeDialog(mStartTimeEt);
			break;
		case R.id.history_event_end_time_date_edit:
			dAndTUtil.showDateDialog(mEndDateEt);
			break;
		case R.id.history_event_end_time_time_edit:
			dAndTUtil.showTimeDialog(mEndTimeEt);
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
	private class getHistoryTask extends
			AsyncTask<Object, Void, Result<List<Map<String, String>>>> {

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
							getResources().getString(R.string.str_msg_query_ing));
				}
			}
		}

		@Override
		protected Result<List<Map<String, String>>> doInBackground(
				Object... params) {
			int boardId = -1;
			String type = "";
			String startTime = "";
			String endTime = "";
			int page = 1;
			int count = -1;
			if (params != null && params.length >= 6) {
				boardId = (Integer) params[0];
				type = (String) params[1];
				startTime = (String) params[2];
				endTime = (String) params[3];
				count = (Integer) params[4];
				page = (Integer)params[5];
			}
			RequestUtil requestUtil = new RequestUtil(mContext);
			
			Result<List<Map<String, String>>> eventResult = requestUtil
					.getHistoryList(boardId + "", type, startTime, endTime, count, page);
			Result<List<Map<String, String>>> alarmResult = requestUtil
					.getAlarmInfoList(boardId, type, startTime, endTime, count, page);

			List<Map<String, String>> alarmList = null;
			List<Map<String, String>> eventList = null;
			int alamTotal = 0;
			int eventTotal = 0;
			if (alarmResult != null) {
				alarmList = alarmResult.getResult();
				alamTotal = alarmResult.getTotal();
			}
			if (eventResult != null) {
				eventList = eventResult.getResult();
				eventTotal = eventResult.getTotal();
			}
			Result<List<Map<String, String>>> result = new Result<List<Map<String, String>>>(
					sortList(alarmList, eventList));
			result.setTotal(alamTotal + eventTotal);
			return result;
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
					mResultList.addAll(result.getResult());
					mTotal = result.getTotal();
					if (mResultList.size() < mTotal) {
						if (!isAddFoot) {
							addListFooterView();
						}
					} else {
						removeListFooterView();
					}
					if (mListAdapter == null) {
						mListAdapter = new HistoryEventAdapter(mContext,
								mResultList, mSwitchList, mLineList);
						mHistoryListView.setAdapter(mListAdapter);
					} else {
						mListAdapter.notifyDataSetChanged();
					}
					mPageCount++;
				} else {
					CustomToast.showToast(mContext, R.string.str_not_result);
				}
			} else {
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
			mFooterView.setGravity(Gravity.CENTER);
			mFooterView.setText("点击加载更多");
			mFooterView.setGravity(Gravity.CENTER);
			mFooterView.setPadding(0, 10, 0, 10);
			mHistoryListView.addFooterView(mFooterView, null, false);
			mFooterView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mResultList.size() < mTotal) {
						if (mIsGetAll) {
							new getHistoryTask().execute(-1, "", "", "",
									mCount, mPageCount);
						} else {
							String name = mDevicesEt.getText().toString();
							String startTime = mStartDateEt.getText()
									.toString()
									+ " "
									+ mStartTimeEt.getText().toString();
							String endTime = mEndDateEt.getText().toString()
									+ " " + mEndTimeEt.getText().toString();
							if (mControlName != null
									&& name.equals(mControlName)
									&& mStartTime != null
									&& mStartTime.equals(startTime)
									&& mEndTime != null
									&& mEndTime.equals(endTime)) {
								new getHistoryTask().execute(mBoardId, "",
										startTime, endTime, mCount,
										mPageCount);
							}
						}
					}
				}
			});
		}
	}

	private void removeListFooterView() {
		if (mFooterView != null && mResultList.size() > 0) {
			mHistoryListView.removeFooterView(mFooterView);
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

	private List<Map<String, String>> sortList(
			List<Map<String, String>> alarmList,
			List<Map<String, String>> eventList) {
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		List<String> timeList = new ArrayList<String>();
		if (alarmList != null && alarmList.size() > 0) {
			for (int i = 0; i < alarmList.size(); i++) {
				String time = alarmList.get(i).get("time");
				if (time != null && !"".equals(time.trim())) {
					timeList.add(time);
				}
			}
		}
		if (eventList != null && eventList.size() > 0) {
			for (int i = 0; i < eventList.size(); i++) {
				String time = eventList.get(i).get("time");
				if (time != null && !"".equals(time.trim())) {
					timeList.add(time);
				}
			}
		}
		Collections.sort(timeList);
		if (timeList != null && timeList.size() > 0) {
			for (int i = 0; i < timeList.size(); i++) {
				String time = timeList.get(i);
				if (alarmList != null && alarmList.size() > 0) {
					for (int j = 0; j < alarmList.size(); j++) {
						Map<String, String> alarMap = alarmList.get(j);
						String alarmTime = alarMap.get("time");
						if (time.equals(alarmTime)) {
							resultList.add(alarMap);
							break;
						}
					}
				}
				if (eventList != null && eventList.size() > 0) {
					for (int j = 0; j < eventList.size(); j++) {
						Map<String, String> eventMap = eventList.get(j);
						String eventTime = eventMap.get("time");
						if (time.equals(eventTime)) {
							resultList.add(eventMap);
							break;
						}
					}
				}
			}
		}
		return resultList;
	}
}
