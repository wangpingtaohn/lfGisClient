package com.gis.client.asyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.os.AsyncTask;
import com.gis.client.R;
import com.gis.client.common.CustomToast;
import com.gis.client.common.Result;
import com.gis.client.http.net.RequestUtil;
import com.gis.client.util.NetworkUtil;

public class GetAlarmListTask extends
		AsyncTask<Integer, Void, Result<List<Map<String, String>>>> {

	private Context mContext;

	private CallBackListener mCallBackListener;

	private List<Map<String, String>> mResultList = new ArrayList<Map<String, String>>();

	public GetAlarmListTask(Context context, CallBackListener callBackListener) {
		this.mContext = context;
		this.mCallBackListener = callBackListener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		int netCode = NetworkUtil.getInstance().isNetworkAvailable(mContext);
		if (NetworkUtil.NET_NOT_AVAILABLE == netCode) {
			CustomToast.showToast(mContext, R.string.str_net_work_error);
			cancel(true);
		}

	}

	@Override
	protected Result<List<Map<String, String>>> doInBackground(
			Integer... params) {
		int page = -1;
		int count = 1;
		int type = -1;
		RequestUtil requestUtil = new RequestUtil(mContext);
		return requestUtil.getAlarmInfoList(-1, type + "", "", "", count, page);
	}

	@Override
	protected void onPostExecute(Result<List<Map<String, String>>> result) {
		super.onPostExecute(result);
		if (result != null) {
			if (result.getErrorCode() != null
					&& !"".equals(result.getErrorCode())) {
				CustomToast.showToast(mContext, result.getErrorCode());
			} else if (result.getResult() != null) {
				mResultList = result.getResult();
				mCallBackListener.succeed(mResultList);
			}
		}
	}

	public interface CallBackListener {
		void succeed(List<Map<String, String>> result);

		void failed();
	}
}
