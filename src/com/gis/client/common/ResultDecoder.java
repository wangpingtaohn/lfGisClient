package com.gis.client.common;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * @author wpt
 */
public class ResultDecoder {

	/**
	 * 获取错误码
	 * @param json
	 * @return
	 */
	public static int getResponseCode(String json)
	{
		try {
			JSONObject jsonObject = new JSONObject(json);
			return jsonObject.getJSONObject("result").getInt("code");
		} catch (JSONException e) {
			Log.e("ResultDecoder", "json format error", e);
			return Result.CLIENT_ERROR_JSON_ERROR;
		}
		
	}
	
	/**
	 * 获取json数据对象
	 * @param json
	 * @return
	 */
	public static JSONObject getResponseData(String json)
	{
		try {
			JSONObject jsonObject = new JSONObject(json);
			return jsonObject.getJSONObject("data");
		} catch (JSONException e) {
			Log.e("ResultDecoder", "json format error", e);
			return null;
		}
	}
}
