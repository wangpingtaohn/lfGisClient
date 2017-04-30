package com.gis.client.asyncTask;

import java.util.HashMap;
import android.content.Context;
import android.os.AsyncTask;

import com.gis.client.common.CustomToast;
import com.gis.client.common.Result;
import com.gis.client.http.net.RequestUtil;
import com.gis.client.util.NetworkUtil;

public class GetLineStatusMapTask extends
		AsyncTask<Void, Void, Result<HashMap<String, String>>> {

	private Context mContext;

	private HashMap<String, String> mLineStatusMap = new HashMap<String, String>();

	private CallBackListener mCallBackListener;

	public GetLineStatusMapTask(Context context,
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
	protected Result<HashMap<String, String>> doInBackground(
			Void... params) {
		RequestUtil requestUtil = new RequestUtil(mContext);
		return requestUtil.getlineStatusMap();
	}

	@Override
	protected void onPostExecute(Result<HashMap<String, String>> result) {
		super.onPostExecute(result);
		if (result != null) {
			if (result.getErrorCode() != null
					&& !"".equals(result.getErrorCode())) {
				CustomToast.showToast(mContext, result.getErrorCode());
				mCallBackListener.failed();
			} else if (result.getResult() != null) {
				mLineStatusMap = result.getResult();
				mCallBackListener.succeed(mLineStatusMap);
			}
		} else {
			mCallBackListener.failed();
		}
	}

	public interface CallBackListener {
		void succeed(HashMap<String, String> result);

		void failed();
	}

}
