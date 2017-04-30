package com.gis.client.asyncTask;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.os.AsyncTask;
import com.gis.client.R;
import com.gis.client.common.CustomToast;
import com.gis.client.common.Result;
import com.gis.client.http.net.RequestUtil;
import com.gis.client.model.Switch;

public class GetSwitchListTask extends
		AsyncTask<Void, Void, Result<List<Switch>>> {


	private Context mContext;

	private List<Switch> mSwitchList = new ArrayList<Switch>();

	private CallBackListener mCallBackListener;

	public GetSwitchListTask(Context context, CallBackListener callBackListener) {
		this.mContext = context;
		this.mCallBackListener = callBackListener;
	}

	@Override
	protected Result<List<Switch>> doInBackground(Void... params) {
		RequestUtil requestUtil = new RequestUtil(mContext);
		return requestUtil.getSwitchList();
	}

	@Override
	protected void onPostExecute(Result<List<Switch>> result) {
		super.onPostExecute(result);
		if (result != null) {
			if (result.getErrorCode() != null
					&& !"".equals(result.getErrorCode())) {
				mCallBackListener.failed();
				CustomToast.showToast(mContext, result.getErrorCode());
			} else if (result.getResult() != null) {
				mSwitchList = result.getResult();
				mCallBackListener.succeed(mSwitchList);
			}
		} else {
			mCallBackListener.failed();
			CustomToast.showToast(mContext, R.string.str_net_work_server_error);
		}
	}

	public interface CallBackListener {
		void succeed(List<Switch> result);
		void failed();
	}

}
