package com.gis.client.activity.query.curve;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.gis.client.model.SwitchInfo;
import com.gis.client.model.Transformer;
import com.gis.client.util.DateAndTimeUtil;
import com.gis.client.util.DateUtils;
import com.gis.client.util.DateAndTimeUtil.CallBackLisneter;

public class CurveActivity extends Activity implements OnClickListener {

	private Context mContext;

	private String[] mSpinnerStr;

	private String mType;

	private int mBoardId;

	private EditText mDevicesEt;

	private CommonProgressDialog mProgressDialog;

	private TextView mStartDateEt;

	private TextView mStartTimeEt;

	private TextView mEndDateEt;

	private TextView mEndTimeEt;

	private Spinner mCarveSpinner;

	private CheckBox mAcheckBox;

	private CheckBox mBcheckBox;

	private CheckBox mCcheckBox;

	private List<Line> mLineList = new ArrayList<Line>();

	private List<Transformer> mTransformerList = new ArrayList<Transformer>();

	private List<Switch> mSwitchList = new ArrayList<Switch>();

	private List<SwitchInfo> mSwitchInfoList = new ArrayList<SwitchInfo>();

	private int mMax = -1;

	private int mMin = -1;

	private static final int START_TIME = 0;

	private static final int END_TIME = 1;

	private int mPage = 1;
	
	private boolean mExit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.curve_main_layout);
		mContext = CurveActivity.this;

		getTreeInfo();
		initView();

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		//从canvas页返回时清空上次查询的结果
		if (mSwitchInfoList != null && mSwitchInfoList.size() > 0) {
			mSwitchInfoList.clear();
			mPage = 1;
		}
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
			// mSwitchInfoList = (List<SwitchInfo>) intent
			// .getSerializableExtra("switchInfoList");
		}
	}

	private void initView() {

		mStartDateEt = (TextView) findViewById(R.id.curve_start_time_date_edit);
		mStartTimeEt = (TextView) findViewById(R.id.curve_start_time_time_edit);
		mEndDateEt = (TextView) findViewById(R.id.curve_end_time_date_edit);
		mEndTimeEt = (TextView) findViewById(R.id.curve_end_time_time_edit);

		mAcheckBox = (CheckBox) findViewById(R.id.curve_query_al_checkbox);
		mBcheckBox = (CheckBox) findViewById(R.id.curve_query_bl_checkbox);
		mCcheckBox = (CheckBox) findViewById(R.id.curve_query_cl_checkbox);

		Button queryBtn = (Button) findViewById(R.id.curve_query_btn);
		mDevicesEt = (EditText) findViewById(R.id.curve_query_devices_edit);
		Button devicesSelBtn = (Button) findViewById(R.id.curve_query_devices_select);

		mStartDateEt.setText(DateAndTimeUtil.getDate());
		mEndDateEt.setText(DateAndTimeUtil.getDate());
		mStartTimeEt.setText(DateAndTimeUtil.getTime());
		mEndTimeEt.setText(DateAndTimeUtil.getTime());

		queryBtn.setOnClickListener(this);
		devicesSelBtn.setOnClickListener(this);
		mStartDateEt.setOnClickListener(this);
		mStartTimeEt.setOnClickListener(this);
		mEndDateEt.setOnClickListener(this);
		mEndTimeEt.setOnClickListener(this);

		mCarveSpinner = (Spinner) findViewById(R.id.curve_query_type_spinner);

		mSpinnerStr = getResources().getStringArray(
				R.array.curve_query_type_array);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
				mContext, android.R.layout.simple_spinner_item, mSpinnerStr);
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mCarveSpinner.setAdapter(spinnerAdapter);
		mType = mCarveSpinner.getSelectedItem().toString();
		Log.i("wpt", "默认的类型是:" + mType);

		mCarveSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (arg2 == 0) {
					mAcheckBox.setVisibility(View.VISIBLE);
					mBcheckBox.setVisibility(View.VISIBLE);
					mCcheckBox.setVisibility(View.VISIBLE);
				} else {
					mAcheckBox.setVisibility(View.INVISIBLE);
					mBcheckBox.setVisibility(View.INVISIBLE);
					mCcheckBox.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK && data != null) {
			String devicesName = data.getStringExtra("devicesName");
			mBoardId = data.getIntExtra("boardId", 0);
			mDevicesEt.setText(devicesName);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.curve_query_devices_select:
			Intent intent = new Intent(mContext, TreeLineActivity.class);
			intent.putExtra("lineList", (Serializable) mLineList);
			intent.putExtra("transformerList", (Serializable) mTransformerList);
			intent.putExtra("switchList", (Serializable) mSwitchList);
			intent.putExtra("selectFlg", true);
			startActivityForResult(intent,
					Constants.ACTIVITY_REQUEST_CODE_CURVE);
			break;
		case R.id.curve_query_btn:
			mMax = -1;
			mMin = -1;
			if (mDevicesEt.getText() == null
					|| "".equals(mDevicesEt.getText().toString())) {
				CustomToast.showToast(mContext, "请选择设备");
			} else {
				if (mCarveSpinner.getSelectedItemPosition() == 0) {
					if (!mAcheckBox.isChecked() && !mBcheckBox.isChecked()
							&& !mCcheckBox.isChecked()) {
						CustomToast.showToast(mContext, "请选择电流类型");
						return;
					}
				}
				String startTime = mStartDateEt.getText().toString() + " "
						+ mStartTimeEt.getText().toString();
				String endTime = mEndDateEt.getText().toString() + " "
						+ mEndTimeEt.getText().toString();
				long startLTime = DateUtils.timeStrToLong(startTime);
				long endLTime = DateUtils.timeStrToLong(endTime);
				int startMonth = DateUtils.longToMonth(startTime);
				int endMonth = DateUtils.longToMonth(endTime);
				if (endLTime <= startLTime) {
					CustomToast.showToast(mContext, "结束时间不能小于开始时间");
				} else if (endLTime - startLTime > 24 * 60 * 60 * 1000) {
					CustomToast.showToast(mContext, "查询时间最大不能超过24小时");
				} else if (startMonth != endMonth) {
					CustomToast.showToast(mContext, "不能跨月份查询");
				} else {
					new GetCanvasInfoTask().execute();
				}
			}
			break;
		case R.id.curve_start_time_date_edit:
			new DateAndTimeUtil(mContext).showDateDialog(mStartDateEt,
					new CallBackLisneter() {

						@Override
						public void setFinish(String time) {
							// String startTime = mStartTimeEt.getText()
							// .toString();
							// if (startTime != null && !"".equals(startTime)) {
							// controlTime(time + " " + startTime, START_TIME);
							// }
						}
					});
			break;
		case R.id.curve_start_time_time_edit:
			new DateAndTimeUtil(mContext).showTimeDialog(mStartTimeEt,
					new CallBackLisneter() {

						@Override
						public void setFinish(String time) {
							// String startDate = mStartDateEt.getText()
							// .toString();
							// if (startDate != null && !"".equals(startDate)) {
							// controlTime(startDate + " " + time, START_TIME);
							// }
						}
					});
			break;
		case R.id.curve_end_time_date_edit:
			new DateAndTimeUtil(mContext).showDateDialog(mEndDateEt,
					new CallBackLisneter() {

						@Override
						public void setFinish(String time) {
							// String endTime = mEndTimeEt.getText().toString();
							// if (endTime != null && !"".equals(endTime)) {
							// controlTime(time + " " + endTime, END_TIME);
							// }
						}
					});
			break;
		case R.id.curve_end_time_time_edit:
			new DateAndTimeUtil(mContext).showTimeDialog(mEndTimeEt,
					new CallBackLisneter() {

						@Override
						public void setFinish(String time) {
							// String endDate = mEndDateEt.getText().toString();
							// if (endDate != null && !"".equals(endDate)) {
							// controlTime(endDate + " " + time, END_TIME);
							// }
						}
					});
			break;
		default:
			break;
		}
	}

	private void controlTime(String time, int type) {
		long timeL = DateUtils.timeStrToLong(time);
		String timeStr = null;
		switch (type) {
		case START_TIME:
			timeStr = DateUtils.getStandardTime(timeL + 3600 * 1000);
			if (timeStr != null && !"".equals(timeStr)) {
				int index = timeStr.indexOf(" ");
				mEndDateEt.setText(timeStr.substring(0, index));
				mEndTimeEt.setText(timeStr.substring(index + 1));
			}
			break;
		case END_TIME:
			String startTime = mStartTimeEt.getText().toString();
			String startDate = mStartDateEt.getText().toString();
			if (startTime == null || "".equals(startTime) || startDate == null
					|| "".equals(startDate)) {
				timeStr = DateUtils.getStandardTime(timeL - 3600 * 1000);
				if (timeStr != null && !"".equals(timeStr)) {
					int index = timeStr.indexOf(" ");
					mStartDateEt.setText(timeStr.substring(0, index));
					mStartTimeEt.setText(timeStr.substring(index + 1));
				}
			} else {
				long lTime = DateUtils.timeStrToLong(startDate + " "
						+ startTime);
				if (timeL - lTime > 3600 * 1000) {
					controlTime(startDate + " " + startTime, START_TIME);
				}
			}
			break;
		default:
			break;
		}
	}

	private class GetCanvasInfoTask extends
			AsyncTask<Object, Void, List<SwitchInfo>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (1 == mPage && !mExit) {
				mProgressDialog = CommonProgressDialog.show(mContext,
						R.string.str_tip,
						getResources().getString(R.string.str_msg_query_ing));
			}
		}

		@Override
		protected List<SwitchInfo> doInBackground(Object... params) {
			try {
				return getFilterSwitchInfos();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<SwitchInfo> result) {
			super.onPostExecute(result);
			Log.i("wpt", "result.size()=" + result.size());
			if (result != null && result.size() > 0) {
				if (!mExit) {
					mProgressDialog.dismiss();
				}
				Intent i = new Intent();
				Map<String, String> map = new HashMap<String, String>();
				map.put("minValue", mMin + "");
				map.put("maxValue", mMax + "");
				map.put("name", mDevicesEt.getText().toString());
				map.put("startTime", mStartDateEt.getText().toString() + " "
						+ mStartTimeEt.getText().toString());
				map.put("endTime", mEndDateEt.getText().toString() + " "
						+ mEndTimeEt.getText().toString());
				map.put("type", mCarveSpinner.getSelectedItemPosition() + "");
				map.put("al", mAcheckBox.isChecked() + "");
				map.put("bl", mBcheckBox.isChecked() + "");
				map.put("cl", mCcheckBox.isChecked() + "");
				map.put("aveValue", ((mMax + mMin) / 2.0) + "");
				i.setClass(mContext, CurveResultAcitvity.class);
				i.putExtra("switchInfoList", (Serializable) result);
				i.putExtra("map", (Serializable) map);
				startActivity(i);
			} else if (1 < mPage) {
				new GetCanvasInfoTask().execute();
			}else {
				mProgressDialog.dismiss();
				CustomToast.showToast(mContext, "查不到该设备在该时间段内的信息...");
			}

		}

	}

	private List<SwitchInfo> getFilterSwitchInfos() throws ParseException {
		List<SwitchInfo> reSwitchInfos = new ArrayList<SwitchInfo>();
		String startTime = mStartDateEt.getText().toString() + " "
				+ mStartTimeEt.getText().toString();
		String endTime = mEndDateEt.getText().toString() + " "
				+ mEndTimeEt.getText().toString();
		RequestUtil requestUtil = new RequestUtil(mContext);
		Result<List<SwitchInfo>> result = requestUtil.getSwitchInfoList(
				String.valueOf(mBoardId), startTime, endTime, 10, mPage);
		if (result != null) {
			// if (mSwitchInfoList != null && mSwitchInfoList.size() > 0) {
			// mSwitchInfoList.clear();
			// }
			int total = result.getTotal();
			// mSwitchInfoList = result.getResult();
			mSwitchInfoList.addAll(result.getResult());
			if (mSwitchInfoList.size() < total) {
				mPage++;
			} else {
				String aL = mAcheckBox.isChecked() + "";
				String bL = mBcheckBox.isChecked() + "";
				String cL = mCcheckBox.isChecked() + "";
				String type = mCarveSpinner.getSelectedItemPosition() + "";
				if (mSwitchInfoList != null && mSwitchInfoList.size() > 0) {
					int count = mSwitchInfoList.size();
					int i;
					for (i = 0; i < count; i++) {
						SwitchInfo resultInfo = new SwitchInfo();
						SwitchInfo switchInfo = mSwitchInfoList.get(i);
						String switchTime = switchInfo.getTime();
						resultInfo.setTime(switchTime);
						if ("0".equals(type)) {
							if ("true".equals(aL)) {
								int a = switchInfo.getaL();
								if (0 == i) {
									mMin = a;
								}
								if (a < mMin) {
									mMin = a;
								}
								if (a > mMax) {
									mMax = a;
								}
								resultInfo.setaL(a);
							}
							if ("true".equals(bL)) {
								int b = switchInfo.getbL();
								if (0 == i && mMin == -1) {
									mMin = b;
								}
								if (b < mMin) {
									mMin = b;
								}
								if (b > mMax) {
									mMax = b;
								}
								resultInfo.setbL(b);
							}
							if ("true".equals(cL)) {
								int c = switchInfo.getcL();
								if (0 == i && mMin == -1) {
									mMin = c;
								}
								if (c < mMin) {
									mMin = c;
								}
								if (c > mMax) {
									mMax = c;
								}
								resultInfo.setcL(c);
							}
						} else if ("1".equals(type)) {
							int voltage = switchInfo.getVoltage();
							if (0 == i && mMin == -1) {
								mMin = voltage;
							}
							if (voltage < mMin) {
								mMin = voltage;
							}
							if (voltage > mMax) {
								mMax = voltage;
							}
							resultInfo.setVoltage(voltage);
						}
						reSwitchInfos.add(resultInfo);
					}
				}
			}
		}
		return reSwitchInfos;

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
