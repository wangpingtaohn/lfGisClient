package com.gis.client.asyncTask;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.os.AsyncTask;
import com.gis.client.R;
import com.gis.client.common.CustomToast;
import com.gis.client.common.Result;
import com.gis.client.http.net.RequestUtil;
import com.gis.client.model.Line;

public class GetLineListTask extends AsyncTask<Void, Void, Result<List<Line>>> {

	private Context mContext;
	
	private List<Line> mLineList = new ArrayList<Line>();
	
	private CallBackListener mCallBackListener;
	
	public GetLineListTask(Context context, CallBackListener callBackListener) {
		this.mContext = context;
		this.mCallBackListener = callBackListener;
	}
	
	@Override
	protected Result<List<Line>> doInBackground(Void... params) {
		RequestUtil requestUtil = new RequestUtil(mContext);
		return requestUtil.getlineList();
	}

	@Override
	protected void onPostExecute(Result<List<Line>> result) {
		super.onPostExecute(result);
		if (result != null) {
			if (result.getErrorCode() != null
					&& !"".equals(result.getErrorCode())) {
				CustomToast.showToast(mContext, result.getErrorCode());
				mCallBackListener.failed();
			} else if (result.getResult() != null) {
				mLineList = result.getResult();
				mCallBackListener.succeed(mLineList);
			}
		} else {
			mCallBackListener.failed();
			CustomToast.showToast(mContext, R.string.str_net_work_server_error);
		}
	}
	
	public interface CallBackListener {
		void succeed(List<Line> result);
		void failed();
	}

}
