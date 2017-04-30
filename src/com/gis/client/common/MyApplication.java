package com.gis.client.common;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;
import com.baidu.mapapi.BMapManager;
import com.gis.client.R;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Looper;
import android.util.Log;

public class MyApplication extends Application implements
		UncaughtExceptionHandler {

	private static final String TAG = "MyApplication";

	private Context mContext;
	
	private static MyApplication mInstance;

	private Thread.UncaughtExceptionHandler handler;

	private List<Activity> mList;

	private BMapManager mBMapManager;

	public static final String MAP_KEY = "00555a80e7692e9aed70f43b677b87a4";

	@Override
	public void onCreate() {
		super.onCreate();

		mInstance = this;
		
		mBMapManager = new BMapManager(this);
		if (mBMapManager.init(MAP_KEY, null)) {
			
		} else {
			CustomToast.showShortToast(mContext, "Key错误");
		}
		
		mList = new ArrayList<Activity>();

		mContext = getApplicationContext();
		handler = Thread.getDefaultUncaughtExceptionHandler();
//		Thread.setDefaultUncaughtExceptionHandler(this);
	}
	public static MyApplication getInstance() {
		return mInstance;
	}
	public void addActivity(Activity activity) {
		Log.i("wpt", TAG + "***addActivity_start****");
		if (activity != null) {
			mList.add(activity);
		}
		Log.i("wpt", TAG + "list.size()=" + mList.size());
		Log.i("wpt", TAG + "***addActivity_end****");
	}
	public void removeActivity(Activity activity) {
		Log.i("wpt", TAG + "***removeActivity_start****");
		mList.remove(activity);
		Log.i("wpt", TAG + "***removeActivity_end****");
    }
	public void eixtApp() {
		Log.i("wpt", TAG + "***eixtApp_start****");
		for (int i = 0; i < mList.size(); i++) {
			if (mList.get(i) != null) {
				mList.get(i).finish();
			}
		}
		Log.i("wpt", TAG + "***eixtApp_end****");
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handlerExcepition(ex) && handler != null) {
			handler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
//			ex.getCause().toString();
			Log.i("wpt", TAG + "***ex=" + ex.getCause().toString());
			eixtApp();
		}
	}

	private boolean handlerExcepition(Throwable ex) {
		if (ex == null) {
			return false;
		}
		new Thread() {
			public void run() {
				Looper.prepare();
				CustomToast
						.showToast(mContext, R.string.str_app_exception_exit);
				Looper.loop();
			};
		}.start();
		return true;
	}

	public BMapManager getmBMapManager() {
		return mBMapManager;
	}

	public void setmBMapManager(BMapManager mBMapManager) {
		this.mBMapManager = mBMapManager;
	}

}
