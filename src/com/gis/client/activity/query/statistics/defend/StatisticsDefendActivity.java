package com.gis.client.activity.query.statistics.defend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
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

public class StatisticsDefendActivity extends Activity implements
		OnClickListener, OnItemClickListener {

	private Context mContext;

	private EditText mDevicesEt;

	private TextView mStartDateTv;

	private TextView mEndDateTv;

	private int mBoardId = -1;

	private CommonProgressDialog mProgressDialog;

	private List<Map<String, String>> mResultList = new ArrayList<Map<String, String>>();

	private Map<String, Integer> mTypeMap = new HashMap<String, Integer>();;

	private List<String> mBoardList = new ArrayList<String>();

	private ListView mListView;

	private StatisticsDefendAdapter mListAdapter;

	private TextView mFooterView;

	private int mPageCount = 1;

	private static final int mCount = 20;

	private int mTotal;

	private String[] mSpinnerStr;

	private int mDevices;

	private List<Line> mLineList = new ArrayList<Line>();

	private List<Transformer> mTransformerList = new ArrayList<Transformer>();

	private List<Switch> mSwitchList = new ArrayList<Switch>();

	private Spinner mSpinner;

	private boolean mExit;

	private DateAndTimeUtil dAndTUtil;
	
	private boolean isAddFoot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistics_defend_main_layout);

		mContext = StatisticsDefendActivity.this;

		getTreeInfo();

		initView();
	}

	private void initView() {
		dAndTUtil = new DateAndTimeUtil(mContext);
		mDevicesEt = (EditText) findViewById(R.id.statistics_defend_devices_edit);
		Button queryBtn = (Button) findViewById(R.id.statistics_defend_query_btn);
		mStartDateTv = (TextView) findViewById(R.id.statistics_defend_start_time_date_edit);
		mEndDateTv = (TextView) findViewById(R.id.statistics_defend_end_time_date_edit);

		mListView = (ListView) findViewById(R.id.statistics_defend_listview);

		mSpinner = (Spinner) findViewById(R.id.statistics_defend_sel_devices_spinner);
		mSpinnerStr = getResources().getStringArray(
				R.array.statistics_gate_sel_devices_array);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
				mContext, android.R.layout.simple_spinner_item, mSpinnerStr);
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinner.setAdapter(spinnerAdapter);
		mDevices = mSpinner.getSelectedItemPosition();

		queryBtn.setOnClickListener(this);
		mDevicesEt.setOnClickListener(this);
		mStartDateTv.setOnClickListener(this);
		mEndDateTv.setOnClickListener(this);

		mStartDateTv.setText(DateAndTimeUtil.getDate());
		mEndDateTv.setText(DateAndTimeUtil.getDate());

		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				mDevices = arg2;
				if (1 == arg2) {
					mDevicesEt.setText("点击选择设备");
					mBoardId = -1;
				} else {
					mBoardId = -1;
					mDevicesEt.setText("");
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		// mListAdapter = new StatisticsDefendAdapter(mContext, mAboardList,
		// mTypeMap, mSwitchList);
		// mListView.setAdapter(mListAdapter);

		mListView.setOnItemClickListener(this);

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
		case R.id.statistics_defend_devices_edit:
			if (1 == mDevices) {
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
		case R.id.statistics_defend_query_btn:
			resSetData();
			new getAlarmInfoTask().execute(mCount, mPageCount,
					mSpinner.getSelectedItemPosition());
			break;
		case R.id.statistics_defend_start_time_date_edit:
			dAndTUtil.showDateDialog(mStartDateTv);
			break;
		case R.id.statistics_defend_end_time_date_edit:
			dAndTUtil.showDateDialog(mEndDateTv);
			break;
		default:
			break;
		}
	}
	private void resSetData() {
		mPageCount = 1;
		isAddFoot = false;
		mResultList.clear();
		mBoardList.clear();
		mTypeMap.clear();
		removeListFooterView();
		if (mListAdapter != null) {
			mListAdapter.notifyDataSetChanged();
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
				// type = params[2];
			}
			String startTime = null;
			String endTime = null;
			if (mStartDateTv.getText() != null
					&& !"".equals(mStartDateTv.getText())) {
				startTime = mStartDateTv.getText().toString() + " "
						+ "00:00:00";
			}
			if (mEndDateTv.getText() != null
					&& !"".equals(mEndDateTv.getText())) {
				endTime = mEndDateTv.getText().toString() + " " + "23:59:59";
			}
			RequestUtil requestUtil = new RequestUtil(mContext);
			return requestUtil.getAlarmInfoList(mBoardId, type + "", startTime,
					endTime, count, page);
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
					mResultList.addAll(result.getResult());
					if (mResultList.size() < mTotal) {
						if (!isAddFoot) {
							addListFooterView();
						}
					} else {
						removeListFooterView();
					}
					setAdapter(result.getResult());
					mPageCount++;
				} else {
					CustomToast.showToast(mContext, R.string.str_not_result);
				}
			} else {
				CustomToast.showToast(mContext, R.string.str_not_result);
				if (mListAdapter != null) {
					mListAdapter.notifyDataSetChanged();
				}
			}
		}

	}

	private void setAdapter(List<Map<String, String>> result) {
		managerResult(result);
		if (mListAdapter == null) {
			mListAdapter = new StatisticsDefendAdapter(mContext, mBoardList,
					mTypeMap, mSwitchList);
			mListView.setAdapter(mListAdapter);
		} else {
			mListAdapter.notifyDataSetChanged();
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
			mListView.addFooterView(mFooterView, null, false);
			mFooterView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mResultList.size() < mTotal) {
						new getAlarmInfoTask().execute(mCount, mPageCount, -1);
					}
				}
			});
		}
	}

	private void removeListFooterView() {
		if (mFooterView != null && mBoardList.size() > 0) {
			isAddFoot = false;
			mListView.removeFooterView(mFooterView);
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (mBoardList != null && mBoardList.size() > 0) {
			Intent intent = new Intent(mContext, DefendDetailActivity.class);
			String boardId = mBoardList.get(arg2);
			String name = getSwitchNameByBoardId(Integer.parseInt(boardId));
			intent.putExtra("name", name);
			intent.putExtra("boardId", boardId);
			intent.putExtra("resultList", (Serializable) mResultList);
			startActivity(intent);
		}
	}

	/**
	 * 合并重复的id，并统计各流次数
	 */
	private void managerResult(List<Map<String, String>> result) {
		if (result != null && result.size() > 0) {
			for (Map<String, String> map : result) {
				String boardId = map.get("boardId");
				String alarmType = map.get("alarmType");
				if (mBoardList.contains(boardId)) {
					String typtKey = boardId + "-" + alarmType;
					if (mTypeMap.containsKey(typtKey)) {
						int value = mTypeMap.get(typtKey);
						mTypeMap.put(typtKey, ++value);
					} else {
						mTypeMap.put(typtKey, 1);
					}
				} else {
					int type = Integer.parseInt(alarmType);
					if (type < 3) {//只显示 0--过流;1--速断;2--零序保护
						mBoardList.add(boardId);
						String typtKey = boardId + "-" + alarmType;
						mTypeMap.put(typtKey, 1);
					}
				}
			}
		}
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
}
