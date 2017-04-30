package com.gis.client.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * @Title: HttpPost.java
 * @Description: 连接服务器
 * @author wpt
 * @since 2012-9-21 10:21:59
 * @version V1.0
 */
public class HttpPostConn {

	public HttpPostConn() {

	}

	/**
	 * @Description:
	 * @author wpt
	 * @since 2012-9-21 10:41:22
	 * @version V1.0
	 * @param url
	 *            url
	 * @param map
	 * @return
	 */
	public JSONObject conn(String url, Map<String, String> map) {

		HttpPost post = new HttpPost(url);

		List<NameValuePair> list = new ArrayList<NameValuePair>();
		
		String jsonStr = makeJson(map);
		list.add(new BasicNameValuePair("postStr", jsonStr));
		UrlEncodedFormEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(list, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		post.setEntity(entity);
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);//连接超时
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);//等待超时
		try {
			// client.execute(post);
			HttpResponse response = client.execute(post);
			
			if (response.getStatusLine().getStatusCode() == 200) {
				InputStream inputStream = response.getEntity().getContent();
				return getJsonData(inputStream);
			} else {
				String errorMsg = "服务器异常";
				JSONObject object = new JSONObject();
					object.put("errorMsg", errorMsg);
					return object;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("wpt", "e=" + e.getMessage());
			String errorMsg = e.getMessage();
			JSONObject object = new JSONObject();
			try {
				object.put("errorMsg", errorMsg);
				return object;
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}

	private static String makeJson(Map<String, String> map) {
		if (map != null) {
			JSONObject jsonObject = new JSONObject();
			for (String key : map.keySet()) {
				try {
					jsonObject.put(key, map.get(key));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return jsonObject.toString();
		}
		return null;
	}

	private static JSONObject getJsonData(InputStream inputStream)
			throws JSONException, IOException {
		StringBuilder builder = new StringBuilder();
		InputStreamReader reader = new InputStreamReader(inputStream);
		BufferedReader bReader = new BufferedReader(reader);
		for (String s = bReader.readLine(); s != null; s = bReader.readLine()) {
			builder.append(s);
		}
		JSONObject jsonObject = new JSONObject(builder.toString());
		Log.i("wpt", "result=" + jsonObject);
		return jsonObject;
	}
}
