package com.gis.client.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gis.client.activity.map.MainMapActivity;
import com.gis.client.asyncTask.GetAlarmListTask;
import com.gis.client.asyncTask.GetEventNoticeTask;
import com.gis.client.asyncTask.GetSwitchInfoListTask;
import com.gis.client.common.Constants;
import com.gis.client.notification.NotificationUtil;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class MyService extends Service {

	private final static int GET_EVENT = 0;

	private final static int GET_ALARM = 1;
	
	private boolean mOther;

	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("wpt", "***onCreate_MyService****");
		// getEventNoticeInfo();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.i("wpt", "***onStart_MyService****");
		if (intent != null) {
			mOther = intent.getBooleanExtra("other", false);
			Log.i("wpt", "mOther=" + mOther);
			getEventNoticeInfo();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void getEventNoticeInfo() {
		new GetEventNoticeTask(MyService.this,
				new GetEventNoticeTask.CallBackListener() {

					@Override
					public void succeed(List<HashMap<String, String>> result) {
						Log.i("wpt", "MyService_getEventNoticeInfo_succeed");
						for (int i = 0; i < result.size(); i++) {
							Map<String, String> map = result.get(i);
							String eveMake = map.get("eveMake");
							String eveStatus = map.get("eveStatus");
							if ("1".equals(eveMake) && "0".equals(eveStatus)) {
								getAlarmInfoList();
							}
						}
						mHandler.sendEmptyMessageDelayed(GET_EVENT, Constants.REFRESH_TIME);
					}

					@Override
					public void failed() {
						mHandler.sendEmptyMessageDelayed(GET_EVENT, Constants.REFRESH_TIME);
					}
				}).execute();
	}

	private void getAlarmInfoList() {
		new GetAlarmListTask(MyService.this,
				new GetAlarmListTask.CallBackListener() {

					@Override
					public void succeed(List<Map<String, String>> result) {
						if (result != null && result.size() > 0) {
							for (int i = 0; i < result.size(); i++) {
								if ("3".equals(result.get(i).get("alarmType"))) {
									result.remove(i);
								}
							}
							if (result.size() > 0) {
								Class c = null;
								if (mOther) {
									c = GetSwitchInfoListTask.class;
								} else {
									c = MainMapActivity.class;
								}
								NotificationUtil.showNotification(
										MyService.this, 1, result,
										"请注意,有报警信息！！！", c);
								stopSelf();
							}
						}
					}

					@Override
					public void failed() {
						mHandler.sendEmptyMessageDelayed(GET_ALARM, Constants.REFRESH_TIME);
					}
				}).execute();
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_EVENT:
				getEventNoticeInfo();
				break;
			case GET_ALARM:
				getAlarmInfoList();
				break;
			default:
				break;
			}
		}
	};
}
