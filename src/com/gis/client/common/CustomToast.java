package com.gis.client.common;

import android.content.Context;
import android.widget.Toast;

/**
 * @Title: CustomToast.java
 * @Description: 自定义toast
 * @author wpt
 * @since 2013-6-10 下午8:54:19
 * @version V1.0
 */
public class CustomToast {

	public static void showToast(Context context, String msg) {
		Toast toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
		toast.setText(msg);
		toast.show();
	}

	public static void showToast(Context context, int msgId) {
		Toast toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
		toast.setText(msgId);
		toast.show();
	}
	public static void showShortToast(Context context, String msg) {
		Toast toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
		toast.setText(msg);
		toast.show();
	}
	
	public static void showShortToast(Context context, int msgId) {
		Toast toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
		toast.setText(msgId);
		toast.show();
	}
}
