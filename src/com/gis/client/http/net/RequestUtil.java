package com.gis.client.http.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import com.gis.client.R;
import com.gis.client.common.Constants;
import com.gis.client.common.Result;
import com.gis.client.http.HttpPostConn;
import com.gis.client.model.Line;
import com.gis.client.model.Switch;
import com.gis.client.model.SwitchInfo;
import com.gis.client.model.Transformer;
import com.gis.client.util.FileManager;

/**
 * @author wpt 解析返回的json数据
 */
public class RequestUtil {

	// private static String SERVER_HOST = "http://192.168.0.108:8080/lfGIS/";

	public static String SERVER_HOST;

	private String getLoginUrl;

	private String getLineListUrl;

	private String getTransformerListUrl;

	private String getSwitchListUrl;

	private String getAlarmListUrl;

	private String getWwitchInfoListUrl;

	private String getCurSwitchInfoListUrl;

	private String getHistoryListUrl;

	private String getLineStatusListUrl;

	private String getEventNoticeListUrl;
	
	private String getOpenAndCloseSwitchUrl;
	
	private Context mContext;

	public RequestUtil(Context context) {
		mContext = context;
		if (SERVER_HOST == null || "".equals(SERVER_HOST)) {
			SERVER_HOST = FileManager.getSharedPreValue(context,
					Constants.SP_CONFIG, Constants.HOST_IP);
		}
		getLoginUrl = SERVER_HOST + "login.action";

		getLineListUrl = SERVER_HOST + "getLines.action";

		getTransformerListUrl = SERVER_HOST + "transformers.action";

		getSwitchListUrl = SERVER_HOST + "switch.action";

		getAlarmListUrl = SERVER_HOST + "ftuycztalarms.action";

		getWwitchInfoListUrl = SERVER_HOST + "switchInfo.action";

		getCurSwitchInfoListUrl = SERVER_HOST + "switchInfoByCur.action";

		getHistoryListUrl = SERVER_HOST + "ftuyaokongs.action";

		getLineStatusListUrl = SERVER_HOST + "getLinesStatus.action";

		getEventNoticeListUrl = SERVER_HOST + "eventNotice.action";
		
		getOpenAndCloseSwitchUrl = SERVER_HOST + "yKstat.action";
		
	}

	/**
	 * 获取登录信息
	 * 
	 */
	public String getLogin(String userName, String password) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("userName", userName);
		params.put("password", password);

		HttpPostConn hConn = new HttpPostConn();
		JSONObject jsonObject = hConn.conn(getLoginUrl, params);

		if (jsonObject != null) {
			try {
				if (jsonObject.has("errorMsg")) {
					String errorMsg = jsonObject.getString("errorMsg");
					if (errorMsg != null && !"".equals(errorMsg)) {
						if (errorMsg.contains("timed out")) {
							errorMsg = mContext.getResources().getString(
									R.string.str_net_work_time_out);
						} else {
							errorMsg = mContext.getResources().getString(
									R.string.str_net_work_server_error);
						}
						return errorMsg;
					}
				}
				String result = jsonObject.getString("info");
				return result;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 获取线路列表
	 */
	public Result<List<Line>> getlineList() {
		Map<String, String> params = new HashMap<String, String>();

		HttpPostConn hConn = new HttpPostConn();
		JSONObject jsonObject = hConn.conn(getLineListUrl, params);
		if (jsonObject != null) {
			try {
				Result<List<Line>> result = new Result<List<Line>>(
						new ArrayList<Line>());
				if (jsonObject.has("errorMsg")) {
					String errorMsg = jsonObject.getString("errorMsg");
					if (errorMsg != null && !"".equals(errorMsg)) {
						if (errorMsg.contains("timed out")) {
							errorMsg = mContext.getResources().getString(
									R.string.str_net_work_time_out);
						} else {
							errorMsg = mContext.getResources().getString(
									R.string.str_net_work_server_error);
						}
						result.setErrorCode(errorMsg);
						return result;
					}
				}
				int total = jsonObject.getInt("total");
				result.setTotal(total);
				JSONArray resultJsonArray = jsonObject.getJSONArray("results");

				if (resultJsonArray != null) {
					if (resultJsonArray.length() > 0) {
						for (int i = 0; i < resultJsonArray.length(); i++) {
							Line line = new Line();
							JSONObject resultJson = resultJsonArray
									.getJSONObject(i);
							line.setLineNumber(resultJson.getInt("fId"));
							line.setName(resultJson.getString("name"));
							line.setNumber(resultJson.getInt("code"));
							line.setSuperId(resultJson.getInt("mainline"));
							line.setIsPower(resultJson.getString("power"));
							String pois = resultJson.getString("pois");
							getPoint(line, pois);

							result.getResult().add(line);

						}
					}
					return result;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 获取线路状态
	 */
	public Result<List<HashMap<String, String>>> getlineStatusList() {
		Map<String, String> params = new HashMap<String, String>();

		HttpPostConn hConn = new HttpPostConn();
		JSONObject jsonObject = hConn.conn(getLineStatusListUrl,
				params);
		if (jsonObject != null) {
			try {
				Result<List<HashMap<String, String>>> result = new Result<List<HashMap<String, String>>>(
						new ArrayList<HashMap<String, String>>());
				if (jsonObject.has("errorMsg")) {
					String errorMsg = jsonObject.getString("errorMsg");
					if (errorMsg != null && !"".equals(errorMsg)) {
						if (errorMsg.contains("timed out")) {
							errorMsg = mContext.getResources().getString(
									R.string.str_net_work_time_out);
						} else {
							errorMsg = mContext.getResources().getString(
									R.string.str_net_work_server_error);
						}
						result.setErrorCode(errorMsg);
						return result;
					}
				}
				int total = jsonObject.getInt("total");
				result.setTotal(total);
				JSONArray resultJsonArray = jsonObject.getJSONArray("results");

				if (resultJsonArray != null) {
					if (resultJsonArray.length() > 0) {
						for (int i = 0; i < resultJsonArray.length(); i++) {
							HashMap<String, String> map = new HashMap<String, String>();
							JSONObject resultJson = resultJsonArray
									.getJSONObject(i);
							map.put("fId", resultJson.getString("fId"));
							map.put("power", resultJson.getString("power"));
							result.getResult().add(map);

						}
					}
					return result;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 获取线路状态
	 */
	public Result<HashMap<String, String>> getlineStatusMap() {
		Map<String, String> params = new HashMap<String, String>();

		HttpPostConn hConn = new HttpPostConn();
		JSONObject jsonObject = hConn.conn(getLineStatusListUrl,
				params);
		if (jsonObject != null) {
			try {
				Result<HashMap<String, String>> result = new Result<HashMap<String, String>>(
						new HashMap<String, String>());
				if (jsonObject.has("errorMsg")) {
					String errorMsg = jsonObject.getString("errorMsg");
					if (errorMsg != null && !"".equals(errorMsg)) {
						if (errorMsg.contains("timed out")) {
							errorMsg = mContext.getResources().getString(
									R.string.str_net_work_time_out);
						} else {
							errorMsg = mContext.getResources().getString(
									R.string.str_net_work_server_error);
						}
						result.setErrorCode(errorMsg);
						return result;
					}
				}
				int total = jsonObject.getInt("total");
				result.setTotal(total);
				JSONArray resultJsonArray = jsonObject.getJSONArray("results");

				if (resultJsonArray != null) {
					if (resultJsonArray.length() > 0) {
						for (int i = 0; i < resultJsonArray.length(); i++) {
							JSONObject resultJson = resultJsonArray
									.getJSONObject(i);
							result.getResult().put(resultJson.getString("fId"),
									resultJson.getString("power"));

						}
					}
					return result;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 拆分线路返回的经纬度信息
	 * 
	 */
	private void getPoint(Line line, String pois) {
		String[] points = pois.split(" ");
		List<Double> lon = new ArrayList<Double>();
		List<Double> lat = new ArrayList<Double>();
		for (int i = 0; i < points.length; i++) {
			String[] temp = points[i].split(",");
			if (temp != null && temp.length > 1) {
				String ln = temp[0];
				String la = temp[1];
				lon.add(Double.parseDouble(ln));
				lat.add(Double.parseDouble(la));
			}
		}
		line.setLongitudeList(lon);
		line.setLatitudeList(lat);
	}

	/**
	 * 获取变压列表
	 */
	public Result<List<Transformer>> getTransformerList() {
		Map<String, String> params = new HashMap<String, String>();

		HttpPostConn hConn = new HttpPostConn();
		JSONObject jsonObject = hConn.conn(getTransformerListUrl,
				params);
		if (jsonObject != null) {
			try {
				Result<List<Transformer>> result = new Result<List<Transformer>>(
						new ArrayList<Transformer>());
				if (jsonObject.has("errorMsg")) {
					String errorMsg = jsonObject.getString("errorMsg");
					if (errorMsg != null && !"".equals(errorMsg)) {
						if (errorMsg.contains("timed out")) {
							errorMsg = mContext.getResources().getString(
									R.string.str_net_work_time_out);
						} else {
							errorMsg = mContext.getResources().getString(
									R.string.str_net_work_server_error);
						}
						result.setErrorCode(errorMsg);
						return result;
					}
				}
				int total = jsonObject.getInt("total");
				result.setTotal(total);
				JSONArray resultJsonArray = jsonObject.getJSONArray("results");

				if (resultJsonArray != null) {
					if (resultJsonArray.length() > 0) {
						for (int i = 0; i < resultJsonArray.length(); i++) {
							Transformer transformer = new Transformer();
							JSONObject resultJson = resultJsonArray
									.getJSONObject(i);
							transformer.setId(resultJson.getInt("id"));
							transformer.setName(resultJson.getString("name"));
							transformer.setNumber(resultJson.getInt("order"));
							transformer.setDesc(resultJson
									.getString("describe"));
							transformer.setModelNumber(resultJson
									.getInt("model"));
							transformer.setManufacturers(resultJson
									.getString("factory"));
							transformer.setRatedCapacitance(resultJson
									.getInt("ronglv"));
							transformer.setBoard(resultJson.getInt("broad"));
							transformer.setRelayNumber(resultJson
									.getInt("relayId"));
							transformer.setSuperId(resultJson
									.getInt("ownerLineId"));
							transformer.setLongitude(resultJson
									.getDouble("lon"));
							transformer
									.setLatitude(resultJson.getDouble("lat"));

							result.getResult().add(transformer);
						}
					}
					return result;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 获取开关列表
	 */
	public Result<List<Switch>> getSwitchList() {
		Map<String, String> params = new HashMap<String, String>();

		HttpPostConn hConn = new HttpPostConn();
		JSONObject jsonObject = hConn.conn(getSwitchListUrl, params);
		if (jsonObject != null) {
			try {
				Result<List<Switch>> result = new Result<List<Switch>>(
						new ArrayList<Switch>());
				if (jsonObject.has("errorMsg")) {
					String errorMsg = jsonObject.getString("errorMsg");
					if (errorMsg != null && !"".equals(errorMsg)) {
						if (errorMsg.contains("timed out")) {
							errorMsg = mContext.getResources().getString(
									R.string.str_net_work_time_out);
						} else {
							errorMsg = mContext.getResources().getString(
									R.string.str_net_work_server_error);
						}
						result.setErrorCode(errorMsg);
						return result;
					}
				}
				int total = jsonObject.getInt("total");
				result.setTotal(total);
				JSONArray resultJsonArray = jsonObject.getJSONArray("results");

				if (resultJsonArray != null) {
					if (resultJsonArray.length() > 0) {
						for (int i = 0; i < resultJsonArray.length(); i++) {
							Switch switchObjec = new Switch();
							JSONObject resultJson = resultJsonArray
									.getJSONObject(i);
							switchObjec.setId(resultJson.getInt("fid"));
							switchObjec.setName(resultJson.getString("name"));
							switchObjec.setNumber(resultJson.getInt("order"));
							switchObjec.setDesc(resultJson
									.getString("describe"));
							switchObjec.setModelNumber(resultJson
									.getInt("model"));
							switchObjec.setManufacturers(resultJson
									.getString("factory"));
							switchObjec.setMadeDate(resultJson
									.getString("data"));
							switchObjec.setOrganizationNumber(resultJson
									.getString("machineType"));
							switchObjec.setFtuNumber(resultJson
									.getString("ftutype"));
							switchObjec.setMadeNumber(resultJson
									.getInt("leaveFactoryOrder"));
							switchObjec.setRatedVatige(resultJson
									.getInt("voltage"));
							switchObjec.setElectricityValue(resultJson
									.getInt("gldata"));
							switchObjec.setSpeedValue(resultJson
									.getInt("sldata"));
							switchObjec.setIntTime(resultJson.getInt("zdtime"));
							switchObjec.setBoard(resultJson.getInt("broad"));
							switchObjec.setRelayNumber(resultJson
									.getInt("relayId"));
							switchObjec.setSuperId(resultJson
									.getInt("ownerLineId"));
							switchObjec.setAngle(resultJson.getInt("angle"));
							switchObjec.setLongitude(resultJson
									.getDouble("lon"));
							switchObjec
									.setLatitude(resultJson.getDouble("lat"));

							result.getResult().add(switchObjec);
						}
					}
					return result;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 获取报警列表
	 */
	public Result<List<Map<String, String>>> getAlarmInfoList(int boardId,
			String type, String startTime, String endTime, int count, int page) {
		Map<String, String> params = new HashMap<String, String>();
		if (-1 != boardId) {
			params.put("boardId", boardId + "");
		}
		if (type != null && !"-1".equals(type)) {
			params.put("type", type);
		}
		if (startTime != null && !"".equals(startTime)) {
			params.put("startTime", startTime);
		}
		if (endTime != null && !"".equals(endTime)) {
			params.put("endTime", endTime);
		}
		if (count != -1) {
			params.put("count", count + "");
		}
		if (page != -1) {
			params.put("page", page + "");
		}

		HttpPostConn hConn = new HttpPostConn();
		JSONObject jsonObject = hConn.conn(getAlarmListUrl, params);
		if (jsonObject != null) {
			try {
				Result<List<Map<String, String>>> result = new Result<List<Map<String, String>>>(
						new ArrayList<Map<String, String>>());
				if (jsonObject.has("errorMsg")) {
					String errorMsg = jsonObject.getString("errorMsg");
					if (errorMsg != null && !"".equals(errorMsg)) {
						if (errorMsg.contains("timed out")) {
							errorMsg = mContext.getResources().getString(
									R.string.str_net_work_time_out);
						} else {
							errorMsg = mContext.getResources().getString(
									R.string.str_net_work_server_error);
						}
						result.setErrorCode(errorMsg);
						return result;
					}
				}
				int total = jsonObject.getInt("total");
				result.setTotal(total);
				JSONArray resultJsonArray = jsonObject.getJSONArray("results");

				if (resultJsonArray != null) {
					if (resultJsonArray.length() > 0) {
						for (int i = 0; i < resultJsonArray.length(); i++) {
							Map<String, String> resultMap = new HashMap<String, String>();
							JSONObject resultJson = resultJsonArray
									.getJSONObject(i);
							resultMap.put("boardId",
									resultJson.getString("boardId"));
							resultMap.put("relayId",
									resultJson.getString("relayId"));
							resultMap.put("time", resultJson.getString("time"));
							resultMap.put("alarmType",
									resultJson.getString("alarmType"));
							resultMap.put("info", resultJson.getString("info"));
							resultMap.put("xlname",
									resultJson.getString("xlname"));
							result.getResult().add(resultMap);

						}
					}
					return result;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 获取所有开关信息列表
	 */
	public Result<List<SwitchInfo>> getSwitchInfoList(String boardId,
			String startTime, String endTime, int interval,int page) {
		Map<String, String> params = new HashMap<String, String>();
		if (interval != -1) {
			params.put("interval", interval + "");
		}
		if (boardId != null && !"".equals(boardId)) {
			params.put("broadId", boardId);
		}
		if (startTime != null && !"".equals(startTime)) {
			params.put("startTime", startTime);
		}
		if (endTime != null && !"".equals(endTime)) {
			params.put("endTime", endTime);
		}
		if (-1 != page) {
			params.put("page", page + "");
		}

		HttpPostConn hConn = new HttpPostConn();
		JSONObject jsonObject = hConn.conn(getWwitchInfoListUrl,
				params);
		if (jsonObject != null) {
			try {
				Result<List<SwitchInfo>> result = new Result<List<SwitchInfo>>(
						new ArrayList<SwitchInfo>());
				if (jsonObject.has("errorMsg")) {
					String errorMsg = jsonObject.getString("errorMsg");
					if (errorMsg != null && !"".equals(errorMsg)) {
						if (errorMsg.contains("timed out")) {
							errorMsg = mContext.getResources().getString(
									R.string.str_net_work_time_out);
						} else {
							errorMsg = mContext.getResources().getString(
									R.string.str_net_work_server_error);
						}
						result.setErrorCode(errorMsg);
						return result;
					}
				}
				int total = jsonObject.getInt("total");
				result.setTotal(total);
				JSONArray resultJsonArray = jsonObject.getJSONArray("results");
//				Log.i("wpt", "resultJsonArray.length()=" + resultJsonArray.length());

				if (resultJsonArray != null) {
					if (resultJsonArray.length() > 0) {
						for (int i = 0; i < resultJsonArray.length(); i++) {
							SwitchInfo resultInfo = new SwitchInfo();
							JSONObject resultJson = resultJsonArray
									.getJSONObject(i);
							resultInfo.setId(resultJson.getInt("id"));
							resultInfo.setBoardNumber(resultJson
									.getInt("broadId"));
							resultInfo.setSwitchStatus(resultJson
									.getInt("upOrDown"));
							resultInfo
									.setIsSave(resultJson.getInt("ableSkill"));
							resultInfo
									.setIsFlash(resultJson.getInt("isBicker"));
							resultInfo.setaL(resultJson.getInt("aliu"));
							resultInfo.setbL(resultJson.getInt("bliu"));
							resultInfo.setcL(resultJson.getInt("cliu"));
							resultInfo.setoL(resultJson.getInt("zoreLiu"));
							resultInfo.setVoltage(resultJson.getInt("dianYa"));
							resultInfo.setTime(resultJson.getString("time"));
//							Log.i("wpt", "Time=" + resultJson.getString("time"));
							result.getResult().add(resultInfo);
						}
					}
					return result;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 获取所有开关信息列表
	 */
	public Result<Map<Integer, SwitchInfo>> getSwitchInfoMap() {
		Map<String, String> params = new HashMap<String, String>();

		HttpPostConn hConn = new HttpPostConn();
		JSONObject jsonObject = hConn.conn(getCurSwitchInfoListUrl,
				params);
		if (jsonObject != null) {
			try {
				Result<Map<Integer, SwitchInfo>> result = new Result<Map<Integer, SwitchInfo>>(
						new HashMap<Integer, SwitchInfo>());
				if (jsonObject.has("errorMsg")) {
					String errorMsg = jsonObject.getString("errorMsg");
					if (errorMsg != null && !"".equals(errorMsg)) {
						if (errorMsg.contains("timed out")) {
							errorMsg = mContext.getResources().getString(
									R.string.str_net_work_time_out);
						} else {
							errorMsg = mContext.getResources().getString(
									R.string.str_net_work_server_error);
						}
						result.setErrorCode(errorMsg);
						return result;
					}
				}
				int total = jsonObject.getInt("total");
				result.setTotal(total);
				JSONArray resultJsonArray = jsonObject.getJSONArray("results");

				if (resultJsonArray != null) {
					if (resultJsonArray.length() > 0) {
						for (int i = 0; i < resultJsonArray.length(); i++) {
							SwitchInfo resultInfo = new SwitchInfo();
							JSONObject resultJson = resultJsonArray
									.getJSONObject(i);
							resultInfo.setId(resultJson.getInt("id"));
							resultInfo.setBoardNumber(resultJson
									.getInt("broadId"));
							resultInfo.setSwitchStatus(resultJson
									.getInt("upOrDown"));
							resultInfo
									.setIsSave(resultJson.getInt("ableSkill"));
							resultInfo
									.setIsFlash(resultJson.getInt("isBicker"));
							resultInfo.setaL(resultJson.getInt("aliu"));
							resultInfo.setbL(resultJson.getInt("bliu"));
							resultInfo.setcL(resultJson.getInt("cliu"));
							resultInfo.setoL(resultJson.getInt("zoreLiu"));
							resultInfo.setVoltage(resultJson.getInt("dianYa"));
							resultInfo.setTime(resultJson.getString("time"));
							result.getResult().put(
									resultJson.getInt("broadId"), resultInfo);
						}
					}
					return result;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 获取历史操作事件列表
	 */
	public Result<List<Map<String, String>>> getHistoryList(String name,
			String type, String startTime, String endTime, int count, int page) {
		Map<String, String> params = new HashMap<String, String>();
		if (name != null && !"".equals(name)) {
			params.put("name", name);
		}
		if (type != null && !"".equals(type)) {
			params.put("type", type);
		}
		if (startTime != null && !"".equals(startTime)) {
			params.put("startTime", startTime);
		}
		if (endTime != null && !"".equals(endTime)) {
			params.put("endTime", endTime);
		}
		if (count != -1) {
			params.put("count", count + "");
		}
		if (page != -1) {
			params.put("page", page + "");
		}

		HttpPostConn hConn = new HttpPostConn();
		JSONObject jsonObject = hConn.conn(getHistoryListUrl, params);
		if (jsonObject != null) {
			try {
				Result<List<Map<String, String>>> result = new Result<List<Map<String, String>>>(
						new ArrayList<Map<String, String>>());
				if (jsonObject.has("errorMsg")) {
					String errorMsg = jsonObject.getString("errorMsg");
					if (errorMsg != null && !"".equals(errorMsg)) {
						if (errorMsg.contains("timed out")) {
							errorMsg = mContext.getResources().getString(
									R.string.str_net_work_time_out);
						} else {
							errorMsg = mContext.getResources().getString(
									R.string.str_net_work_server_error);
						}
						result.setErrorCode(errorMsg);
						return result;
					}
				}
				int total = jsonObject.getInt("total");
				result.setTotal(total);
				JSONArray resultJsonArray = jsonObject.getJSONArray("results");

				if (resultJsonArray != null) {
					if (resultJsonArray.length() > 0) {
						for (int i = 0; i < resultJsonArray.length(); i++) {
							Map<String, String> resultMap = new HashMap<String, String>();
							JSONObject resultJson = resultJsonArray
									.getJSONObject(i);
							resultMap.put("boardId",
									resultJson.getString("boardId"));
							resultMap.put("relayId",
									resultJson.getString("relayId"));
							resultMap.put("time", resultJson.getString("time"));
							resultMap.put("type", resultJson.getString("type"));
							resultMap.put("name", resultJson.getString("name"));
							resultMap.put("data1",
									resultJson.getString("data1"));
							resultMap.put("data2",
									resultJson.getString("data2"));
							result.getResult().add(resultMap);

						}
					}
					return result;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 获取事件通知表
	 */
	public Result<List<HashMap<String, String>>> getEventNotice(String userName) {
		Map<String, String> params = new HashMap<String, String>();

		params.put("userName", userName);

		HttpPostConn hConn = new HttpPostConn();
		JSONObject jsonObject = hConn.conn(getEventNoticeListUrl,
				params);
		if (jsonObject != null) {
			try {
				Result<List<HashMap<String, String>>> result = new Result<List<HashMap<String, String>>>(
						new ArrayList<HashMap<String, String>>());
				if (jsonObject.has("errorMsg")) {
					String errorMsg = jsonObject.getString("errorMsg");
					if (errorMsg != null && !"".equals(errorMsg)) {
						if (errorMsg.contains("timed out")) {
							errorMsg = mContext.getResources().getString(
									R.string.str_net_work_time_out);
						} else {
							errorMsg = mContext.getResources().getString(
									R.string.str_net_work_server_error);
						}
						result.setErrorCode(errorMsg);
						return result;
					}
				}
				JSONArray resultJsonArray = jsonObject.getJSONArray("results");

				if (resultJsonArray != null) {
					if (resultJsonArray.length() > 0) {
						for (int i = 0; i < resultJsonArray.length(); i++) {
							HashMap<String, String> map = new HashMap<String, String>();
							JSONObject resultJson = resultJsonArray
									.getJSONObject(i);
							map.put("eveMake", resultJson.getString("eveMake"));
							map.put("eveStatus",
									resultJson.getString("eveStatus"));
							result.getResult().add(map);

						}
					}
					return result;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}
	public Result<List<Map<String, String>>> getOpenSwitchList(int boardId,
			String type, String startTime, String endTime, int count, int page) {
		Map<String, String> params = new HashMap<String, String>();
		if (-1 != boardId) {
			params.put("code", boardId + "");
		}
		if (type != null && !"-1".equals(type)) {
			params.put("type", type);
		}
		if (startTime != null && !"".equals(startTime)) {
			params.put("startTime", startTime);
		}
		if (endTime != null && !"".equals(endTime)) {
			params.put("endTime", endTime);
		}
		if (count != -1) {
			params.put("count", count + "");
		}
		if (page != -1) {
			params.put("page", page + "");
		}

		HttpPostConn hConn = new HttpPostConn();
		JSONObject jsonObject = hConn.conn(getOpenAndCloseSwitchUrl, params);
		if (jsonObject != null) {
			try {
				Result<List<Map<String, String>>> result = new Result<List<Map<String, String>>>(
						new ArrayList<Map<String, String>>());
				if (jsonObject.has("errorMsg")) {
					String errorMsg = jsonObject.getString("errorMsg");
					if (errorMsg != null && !"".equals(errorMsg)) {
						if (errorMsg.contains("timed out")) {
							errorMsg = mContext.getResources().getString(
									R.string.str_net_work_time_out);
						} else {
							errorMsg = mContext.getResources().getString(
									R.string.str_net_work_server_error);
						}
						result.setErrorCode(errorMsg);
						return result;
					}
				}
				int total = jsonObject.getInt("total");
				result.setTotal(total);
				JSONArray resultJsonArray = jsonObject.getJSONArray("results");

				if (resultJsonArray != null) {
					if (resultJsonArray.length() > 0) {
						for (int i = 0; i < resultJsonArray.length(); i++) {
							Map<String, String> resultMap = new HashMap<String, String>();
							JSONObject resultJson = resultJsonArray
									.getJSONObject(i);
							resultMap.put("boardId",
									resultJson.getString("boardId"));
							resultMap.put("cmdname",
									resultJson.getString("cmdname"));
							resultMap.put("cmdtime", resultJson.getString("cmdtime"));
							resultMap.put("cmdtype",
									resultJson.getString("cmdtype"));
							resultMap.put("dataInfo", resultJson.getString("dataInfo"));
							resultMap.put("isSuccess",
									resultJson.getString("isSuccess"));
							result.getResult().add(resultMap);

						}
					}
					return result;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}
}
