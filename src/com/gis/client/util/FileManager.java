package com.gis.client.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;

public class FileManager {

	public static String getSdcardPath() {
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		return path;
	}

	public static void saveSharedPre(Context context, String name, String key,
			String value) {
		SharedPreferences sp = context.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getSharedPreValue(Context context, String name,
			String key) {
		SharedPreferences settingInfo = context.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		String value = settingInfo.getString(key, null);
		return value;
	}
}
