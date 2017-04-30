package com.gis.client.test;


import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

import com.gis.client.http.HttpPostConn;

import android.test.AndroidTestCase;
import android.util.Log;


public class UserInfoDownloaderTest extends AndroidTestCase {

	public UserInfoDownloaderTest() {
		// TODO Auto-generated constructor stub
	}
	public void testGetCridet() throws JSONException {

		Map<String, String> map = new HashMap<String, String>();
		map.put("userName","admin");
		map.put("password", "123");
//		JSONObject jsonObject = HttpPostConn.conn("http://192.168.0.103:8080/lfGIS/login.action", map);
//		Log.i("wpt", "test_jsonObject=" + jsonObject.toString());
		getLines();

	}
	
	private void getLines() {
		Map<String, String> map = new HashMap<String, String>();
//		map.put("name", "");
//		map.put("type", "");
//		map.put("startTime", "2013-06-01 00:00:01");
//		map.put("endTime", "2013-09-30 23:59:59");
		map.put("page", 2+ "");
		map.put("count", 5+ "");
//		map.put("count", "");
//		map.put("Page", "");
		HttpPostConn http = new HttpPostConn();
		JSONObject jsonObject = http.conn("http://192.168.0.102:8080/lfGIS/ftuyaokongs.action", map);
		Log.i("wpt", "test_jsonObject_line=" + jsonObject.toString());
	}
}
