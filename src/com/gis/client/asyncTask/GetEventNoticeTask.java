package com.gis.client.asyncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.gis.client.common.Constants;
import com.gis.client.common.Result;
import com.gis.client.http.net.RequestUtil;

public class GetEventNoticeTask extends
		AsyncTask<Void, Void, Result<List<HashMap<String, String>>>> {

	private Context mContext;

	private CallBackListener mCallBackListener;

	public GetEventNoticeTask(Context context, CallBackListener callBackListener) {
		this.mContext = context;
		this.mCallBackListener = callBackListener;
	}

	@Override
	protected Result<List<HashMap<String, String>>> doInBackground(
			Void... params) {
		SharedPreferences sPreferences = mContext.getSharedPreferences(
				Constants.SP_CONFIG, Context.MODE_PRIVATE);
		String userName = sPreferences.getString(Constants.USER_NAME, null);
		RequestUtil requestUtil = new RequestUtil(mContext);
		Log.i("wpt", "userName=" + userName);
		if (userName != null) {
			return requestUtil.getEventNotice(userName);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Result<List<HashMap<String, String>>> result) {
		super.onPostExecute(result);
		if (result != null) {
			if (result.getErrorCode() != null
					&& !"".equals(result.getErrorCode())) {
				mCallBackListener.failed();
				return;
			} else if (result.getResult() != null) {
				List<HashMap<String, String>> eventResultList = new ArrayList<HashMap<String, String>>();
				eventResultList = result.getResult();
				mCallBackListener.succeed(eventResultList);
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
