package com.gis.client.activity.query.show;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import com.gis.client.R;
import com.gis.client.common.CommonProgressDialog;
import com.gis.client.common.Constants;
import com.gis.client.common.CustomToast;
import com.gis.client.common.Result;
import com.gis.client.http.net.RequestUtil;
import com.gis.client.model.Switch;
import com.gis.client.model.SwitchInfo;
import com.gis.client.util.NetworkUtil;

public class ShowInfoActivity extends Activity {

	private Context mContext;

	private CommonProgressDialog mProgressDialog;

	private ListView mShowListView;

	private ShowInfoAdapter mAdapter;

	private static final int REFRESH = 0;

	// 给提供根据boadid查询名称用
	private List<Switch> mSwitchList = new ArrayList<Switch>();

	private Map<Integer, SwitchInfo> mSwitchInfoMap;
	
	private boolean mExit;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_info_layout);
		mContext = ShowInfoActivity.this;

		mSwitchList = (List<Switch>) getIntent().getSerializableExtra(
				"switchList");

		initView();
	}

	private void initView() {

		Button backBtn = (Button) findViewById(R.id.header_back_button);

		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mShowListView = (ListView) findViewById(R.id.show_info_listview);

		new getAllInfoMapTask().execute();

	}


	private class getAllInfoMapTask extends
			AsyncTask<Integer, Void, Result<Map<Integer, SwitchInfo>>> {

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
		protected Result<Map<Integer, SwitchInfo>> doInBackground(
				Integer... params) {
			RequestUtil requestUtil = new RequestUtil(mContext);
			return requestUtil.getSwitchInfoMap();
		}

		@Override
		protected void onPostExecute(Result<Map<Integer, SwitchInfo>> result) {
			super.onPostExecute(result);
			if (!mExit) {
				mProgressDialog.dismiss();
			}
			if (result != null) {
				if (result.getErrorCode() != null
						&& !"".equals(result.getErrorCode())) {
					CustomToast.showToast(mContext, result.getErrorCode());
				} else if (result.getResult() != null) {
					if (mSwitchInfoMap != null && mSwitchInfoMap.size() > 0) {
						mSwitchInfoMap.clear();
					}
					mSwitchInfoMap = result.getResult();
					if (mAdapter != null) {
						mAdapter = null;
					}
					mAdapter = new ShowInfoAdapter(mContext, mSwitchInfoMap,
							mSwitchList);
					mShowListView.setAdapter(mAdapter);
				}
			}
			mHandler.sendEmptyMessageDelayed(REFRESH, Constants.REFRESH_TIME);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mExit = true;
		if (mHandler.hasMessages(REFRESH)) {
			mHandler.removeMessages(REFRESH);
		}
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			if (msg.what == REFRESH) {
				new getAllInfoMapTask().execute();
			}
		}
	};
}
