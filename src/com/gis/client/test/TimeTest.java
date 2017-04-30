package com.gis.client.test;

import android.test.AndroidTestCase;
import android.util.Log;

import com.gis.client.util.DateUtils;

public class TimeTest extends AndroidTestCase {

	public void testStrToLong() {
		String timeStr = "2013-02-28 23:59:59";
		long timeL = DateUtils.timeStrToLong(timeStr);
		String time = DateUtils.getStandardTime(timeL + 3600 * 1000);
		Log.i("wpt", "增加一小时后=" + time);
	}
}
