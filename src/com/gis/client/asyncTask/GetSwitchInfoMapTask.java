package com.gis.client.asyncTask;

import java.util.Map;
import android.content.Context;
import android.os.AsyncTask;
import com.gis.client.R;
import com.gis.client.common.CustomToast;
import com.gis.client.common.Result;
import com.gis.client.http.net.RequestUtil;
import com.gis.client.model.SwitchInfo;

public class GetSwitchInfoMapTask extends
		AsyncTask<Void, Void, Result<Map<Integer, SwitchInfo>>> {


	private Context mContext;

	private Map<Integer, SwitchInfo> mSwitchInfoMap;

	private CallBackListener mCallBackListener;

	public GetSwitchInfoMapTask(Context context, CallBackListener callBackListener) {
		this.mContext = context;
		this.mCallBackListener = callBackListener;
	}

	@Override
	protected Result<Map<Integer, SwitchInfo>> doInBackground(Void... params) {
		RequestUtil requestUtil = new RequestUtil(mContext);
		return requestUtil.getSwitchInfoMap();
	}

	@Override
	protected void onPostExecute(Result<Map<Integer, SwitchInfo>> result) {
		super.onPostExecute(result);
		if (result != null) {
			if (result.getErrorCode() != null
					&& !"".equals(result.getErrorCode())) {
				mCallBackListener.failed();
				CustomToast.showToast(mContext, result.getErrorCode());
			} else if (result.getResult() != null) {
				mSwitchInfoMap = result.getResult();
				mCallBackListener.succeed(mSwitchInfoMap);
			}
		} else {
			mCallBackListener.failed();
			CustomToast.showToast(mContext,
					R.string.str_net_work_server_error);
		}
	}

	public interface CallBackListener {
		void succeed(Map<Integer, SwitchInfo> result);
		void failed();
	}

}
