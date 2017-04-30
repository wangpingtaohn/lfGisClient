package com.gis.client.notification;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gis.client.R;

/**
 * @author liujun Notification扩展类
 * 
 * @Description: Notification扩展类
 * @File: NotificationUtil.java
 */
public class NotificationUtil {

	// 显示Notification
	@SuppressWarnings("deprecation")
	public static void showNotification(Context context, int notifyId,
			List<Map<String, String>> result, String content, Class activity) {

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.app_logo,
				context.getResources().getString(R.string.app_name),
				System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.defaults = Notification.DEFAULT_ALL;
		Intent intent = new Intent(context, activity);
		intent.putExtra("notification", "notification");
		intent.putExtra("alarmResult", (Serializable)result);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(context,
				notifyId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(context, context.getResources()
				.getString(R.string.app_name), content, contentIntent);
		notificationManager.notify(notifyId, notification);
		Log.i("wpt", "***showNotification****");
	}

}
