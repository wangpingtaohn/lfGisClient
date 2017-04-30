package com.gis.client.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
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
import android.os.Environment;
import android.test.AndroidTestCase;
import android.util.Log;

public class ContentToFile extends AndroidTestCase{

	public void testToFile() {
		String path = Environment.getExternalStorageDirectory().toString()
				+ "/wpt_testFile/line2.txt";
		File file = new File(path);
		File parentFile = file.getParentFile();
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Log.i("wpt", "file=" + file.isFile());
		Map<String, String> params = new HashMap<String, String>();
		InputStream inputStream = conn2(
				"http://10.9.1.168:8080/lfGIS/getLines.action", params);
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file);
			byte[] bytes = new byte[2 * 1024];
			int len = 0;
			while ((len = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, len);
			}
			outputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			InputStream is = new FileInputStream(file);
			JSONObject object = getJsonData(is);
			Log.i("wpt", "object=" + object.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		return jsonObject;
	}
	private InputStream conn2(String url, Map<String, String> map) {

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
				CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);// 连接超时
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);// 等待超时
		try {
			HttpResponse response = client.execute(post);

			if (response.getStatusLine().getStatusCode() == 200) {
				InputStream inputStream = response.getEntity().getContent();
				return inputStream;
			}
		} catch (Exception e) {
			e.printStackTrace();
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
}
