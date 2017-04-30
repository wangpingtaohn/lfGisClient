package com.gis.client.asyncTask;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.os.AsyncTask;

import com.gis.client.common.CustomToast;
import com.gis.client.common.Result;
import com.gis.client.http.net.RequestUtil;
import com.gis.client.model.Transformer;

public class GetTransformerListTask extends
		AsyncTask<Void, Void, Result<List<Transformer>>> {

	private Context mContext;

	private List<Transformer> mTransformerList = new ArrayList<Transformer>();

	private CallBackListener mCallBackListener;

	public GetTransformerListTask(Context context, CallBackListener callBackListener) {
		this.mContext = context;
		this.mCallBackListener = callBackListener;
	}

	@Override
	protected Result<List<Transformer>> doInBackground(Void... params) {
		RequestUtil requestUtil = new RequestUtil(mContext);
		return requestUtil.getTransformerList();
	}

	@Override
	protected void onPostExecute(Result<List<Transformer>> result) {
		super.onPostExecute(result);
		if (result != null) {
			if (result.getErrorCode() != null
					&& !"".equals(result.getErrorCode())) {
				CustomToast.showToast(mContext, result.getErrorCode());
				mCallBackListener.failed();
			} else if (result.getResult() != null) {
				mTransformerList = result.getResult();
				mCallBackListener.succeed(mTransformerList);
			}
		} else {
			mCallBackListener.failed();
		}
	}

	public interface CallBackListener {
		void succeed(List<Transformer> result);
		void failed();
	}

}
