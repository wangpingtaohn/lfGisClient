package com.gis.client.asyncTask;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.os.AsyncTask;
import com.gis.client.R;
import com.gis.client.common.CustomToast;
import com.gis.client.common.Result;
import com.gis.client.http.net.RequestUtil;
import com.gis.client.model.SwitchInfo;
import com.gis.client.util.DateUtils;

public class GetSwitchInfoListTask extends
		AsyncTask<Void, Void, Result<List<SwitchInfo>>> {


	private Context mContext;

	private List<SwitchInfo> mSwitchInfoList = new ArrayList<SwitchInfo>();

	private CallBackListener mCallBackListener;

	public GetSwitchInfoListTask(Context context, CallBackListener callBackListener) {
		this.mContext = context;
		this.mCallBackListener = callBackListener;
	}

	@Override
	protected Result<List<SwitchInfo>> doInBackground(Void... params) {
		RequestUtil requestUtil = new RequestUtil(mContext);
		long startTime = DateUtils.getLongCurrentTime() - 1000*10;
		long endTime = DateUtils.getLongCurrentTime();
		String sStartTime = DateUtils.getStandardTime(startTime);
		String sEndTime = DateUtils.getStandardTime(endTime);
		return requestUtil.getSwitchInfoList(null, sStartTime, sEndTime, 5, -1);
	}

	@Override
	protected void onPostExecute(Result<List<SwitchInfo>> result) {
		super.onPostExecute(result);
		if (result != null) {
			if (result.getErrorCode() != null
					&& !"".equals(result.getErrorCode())) {
				mCallBackListener.failed();
				CustomToast.showToast(mContext, result.getErrorCode());
			} else if (result.getResult() != null) {
				mSwitchInfoList = result.getResult();
				mCallBackListener.succeed(mSwitchInfoList);
			}
		} else {
			mCallBackListener.failed();
			CustomToast.showToast(mContext,
					R.string.str_net_work_server_error);
		}
	}

	public interface CallBackListener {
		void succeed(List<SwitchInfo> result);
		void failed();
	}

}
