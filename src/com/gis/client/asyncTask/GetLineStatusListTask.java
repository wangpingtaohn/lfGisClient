package com.gis.client.asyncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.os.AsyncTask;

import com.gis.client.common.CustomToast;
import com.gis.client.common.Result;
import com.gis.client.http.net.RequestUtil;
import com.gis.client.util.NetworkUtil;

public class GetLineStatusListTask extends
		AsyncTask<Void, Void, Result<List<HashMap<String, String>>>> {

	private Context mContext;

	private List<HashMap<String, String>> mLineStatusList = new ArrayList<HashMap<String, String>>();

	private CallBackListener mCallBackListener;

	public GetLineStatusListTask(Context context,
			CallBackListener callBackListener) {
		this.mContext = context;
		this.mCallBackListener = callBackListener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		int netCode = NetworkUtil.getInstance().isNetworkAvailable(mContext);
		if (NetworkUtil.NET_NOT_AVAILABLE == netCode) {
			cancel(true);
		}
	}

	@Override
	protected Result<List<HashMap<String, String>>> doInBackground(
			Void... params) {
		RequestUtil requestUtil = new RequestUtil(mContext);
		return requestUtil.getlineStatusList();
	}

	@Override
	protected void onPostExecute(Result<List<HashMap<String, String>>> result) {
		super.onPostExecute(result);
		if (result != null) {
			if (result.getErrorCode() != null
					&& !"".equals(result.getErrorCode())) {
				CustomToast.showToast(mContext, result.getErrorCode());
				mCallBackListener.failed();
			} else if (result.getResult() != null) {
				mLineStatusList = result.getResult();
				mCallBackListener.succeed(mLineStatusList);
			}
		} else {
			mCallBackListener.failed();
		}
	}

	public interface CallBackListener {
		void succeed(List<HashMap<String, String>> result);

		void failed();
	}

}
