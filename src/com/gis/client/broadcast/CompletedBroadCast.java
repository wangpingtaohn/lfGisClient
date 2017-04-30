package com.gis.client.broadcast;

import com.gis.client.common.Constants;
import com.gis.client.service.MyService;
import com.gis.client.util.FileManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CompletedBroadCast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Log.i("wpt", "CompletedBroadCast");
			String userName = FileManager.getSharedPreValue(context,
					Constants.SP_CONFIG, Constants.USER_NAME);
			String password = FileManager.getSharedPreValue(context,
					Constants.SP_CONFIG, Constants.PASSWORD);
			if (userName != null && !"".equals(userName) && password != null && !"".equals(password) ) {
				Intent i = new Intent(context, MyService.class);
				context.startService(i);
			}
		}

	}

}
