package com.gis.client.activity.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MKOfflineMap;
import com.baidu.mapapi.map.MKOfflineMapListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.gis.client.R;
import com.gis.client.activity.query.alarm.AlarmInfoActivity;
import com.gis.client.activity.query.curve.CurveActivity;
import com.gis.client.activity.query.history.HistoryEventActivity;
import com.gis.client.activity.query.show.ShowInfoActivity;
import com.gis.client.activity.query.statistics.defend.StatisticsDefendActivity;
import com.gis.client.activity.query.statistics.gate.StatisticsGateActivity;
import com.gis.client.activity.query.tree.TreeLineActivity;
import com.gis.client.asyncTask.GetAlarmListTask;
import com.gis.client.asyncTask.GetEventNoticeTask;
import com.gis.client.asyncTask.GetLineListTask;
import com.gis.client.asyncTask.GetLineStatusMapTask;
import com.gis.client.asyncTask.GetSwitchInfoMapTask;
import com.gis.client.asyncTask.GetSwitchListTask;
import com.gis.client.asyncTask.GetTransformerListTask;
import com.gis.client.asyncTask.GetSwitchInfoMapTask.CallBackListener;
import com.gis.client.common.CommonActivity;
import com.gis.client.common.CommonProgressDialog;
import com.gis.client.common.Constants;
import com.gis.client.common.CustomToast;
import com.gis.client.common.MyApplication;
import com.gis.client.model.Line;
import com.gis.client.model.Switch;
import com.gis.client.model.SwitchInfo;
import com.gis.client.model.Transformer;
import com.gis.client.notification.NotificationUtil;
import com.gis.client.service.MyService;
import com.gis.client.util.NetworkUtil;

public class MainMapActivity extends CommonActivity implements
		LocationListener, MKOfflineMapListener {

	private MapView mMapView;

	private Context mContext;

	private List<Line> mLineList = new ArrayList<Line>();

	private List<Transformer> mTransformerList = new ArrayList<Transformer>();

	private List<Switch> mSwitchList = new ArrayList<Switch>();

	private List<SwitchInfo> mSwitchInfoList = new ArrayList<SwitchInfo>();

	private HashMap<String, String> mLineStatusMap = new HashMap<String, String>();

	private Map<Integer, SwitchInfo> mSwitchInfoMap = new HashMap<Integer, SwitchInfo>();

	private CommonProgressDialog mProgressDialog;

	private MapController mController;

	private static long exitTime;

	private static final int REFRESH = 0;

	private static final int REGETINFO = 1;

	private MKOfflineMap mOfflineMap;

	private List<Map<String, String>> mAlarmList = new ArrayList<Map<String, String>>();

	private boolean mExit;

	private boolean mIsFirst = true;

	private float mCurLevel = 12;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_map);

		mContext = MainMapActivity.this;
		initMapView();
		getLineList();

	}

	private void getLineList() {
		int netCode = NetworkUtil.getInstance().isNetworkAvailable(mContext);
		if (NetworkUtil.NET_NOT_AVAILABLE == netCode && mIsFirst) {
			CustomToast.showToast(mContext, R.string.str_net_work_error);
			return;
		} else {
			if (!mExit) {
				mProgressDialog = CommonProgressDialog.show(
						mContext,
						R.string.str_tip,
						mContext.getResources().getString(
								R.string.str_msg_loadding));
			}
		}
		new GetLineListTask(mContext, new GetLineListTask.CallBackListener() {

			@Override
			public void succeed(List<Line> result) {
				if (!mExit) {
					mProgressDialog.dismiss();
				}
				mLineList = result;
				getTransformerList();
				Log.i("wpt", "GetLineListTask_succeed");
			}

			@Override
			public void failed() {
				getTransformerList();
				Log.i("wpt", "GetLineListTask_failed");
				if (!mExit) {
					mProgressDialog.dismiss();
				}
			}

		}).execute();
	}

	private void getSwitchList() {
		new GetSwitchListTask(mContext,
				new GetSwitchListTask.CallBackListener() {

					@Override
					public void succeed(List<Switch> result) {
						Log.i("wpt", "getSwitchList_succeed");
						if (mIsFirst) {
							setCenterPoint(result);
						}
						mSwitchList = result;
						getSwitchInfoMap();
					}

					@Override
					public void failed() {
						getSwitchInfoMap();
						Log.i("wpt", "getSwitchList_failed");
					}
				}).execute();
	}

	private void getSwitchInfoMap() {
		new GetSwitchInfoMapTask(mContext, new CallBackListener() {

			@Override
			public void succeed(Map<Integer, SwitchInfo> result) {
				Log.i("wpt", "getSwitchInfoMap_succeed");

				if (mSwitchInfoList != null && mSwitchInfoList.size() > 0) {
					mSwitchInfoList.clear();
				}
				mSwitchInfoMap = result;
				if (mMapView == null) {
					initMapView();
				}
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					if (!mExit) {
						mProgressDialog.dismiss();
					}
				}
				if (!mExit) {
					if (mMapView.getOverlays() != null) {
						mMapView.getOverlays().clear();
					}
					new MyDrawCanvas(mContext, mLineList, mTransformerList,
							mSwitchList, mSwitchInfoMap, mLineStatusMap,
							mMapView);
				}
				mHandler.sendEmptyMessageDelayed(REFRESH,
						Constants.REFRESH_TIME);
			}

			@Override
			public void failed() {
				Log.i("wpt", "getSwitchInfoMap_failed");
				if (mSwitchInfoList != null && mSwitchInfoList.size() > 0) {
					mSwitchInfoList.clear();
				}
				if (mMapView == null) {
					initMapView();
				}
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					if (!mExit) {
						mProgressDialog.dismiss();
					}
				}
				if (mMapView.getOverlays() != null) {
					mMapView.getOverlays().clear();
				}
				new MyDrawCanvas(mContext, mLineList, mTransformerList,
						mSwitchList, mSwitchInfoMap, mLineStatusMap, mMapView);
				mHandler.sendEmptyMessageDelayed(REFRESH,
						Constants.REFRESH_TIME);
			}
		}).execute();
	}

	private void getTransformerList() {
		new GetTransformerListTask(mContext,
				new GetTransformerListTask.CallBackListener() {

					@Override
					public void succeed(List<Transformer> result) {
						Log.i("wpt", "getTransformerList_succeed");
						mTransformerList = result;
						getSwitchList();
					}

					@Override
					public void failed() {
						Log.i("wpt", "getTransformerList_failed");
						getSwitchList();
					}
				}).execute();
	}

	private void getEventNoticeInfo() {
		new GetEventNoticeTask(mContext,
				new GetEventNoticeTask.CallBackListener() {

					@Override
					public void succeed(List<HashMap<String, String>> result) {
						Log.i("wpt", "getEventNoticeInfo_succeed");
						for (int i = 0; i < result.size(); i++) {
							Map<String, String> map = result.get(i);
							String eveMake = map.get("eveMake");
							String eveStatus = map.get("eveStatus");
							if ("0".equals(eveStatus)) {
								if ("0".equals(eveMake)) {
									getLineStatusList();
								} else if ("1".equals(eveMake)) {
									getAlarmInfoList();
								}
							}
						}
					}

					@Override
					public void failed() {
						Log.i("wpt", "getEventNoticeInfo_failed");
						if (mProgressDialog != null
								&& mProgressDialog.isShowing()) {
							if (!mExit) {
								mProgressDialog.dismiss();
							}
						}
					}
				}).execute();
	}

	private void getLineStatusList() {
		new GetLineStatusMapTask(mContext,
				new GetLineStatusMapTask.CallBackListener() {

					@Override
					public void succeed(HashMap<String, String> result) {
						Log.i("wpt", "GetLineStatusListTask_succeed");
						mLineStatusMap = result;
						if (mMapView == null) {
							initMapView();
						}
						new MyDrawCanvas(mContext, mLineList, mTransformerList,
								mSwitchList, mSwitchInfoMap, mLineStatusMap,
								mMapView);
						mHandler.sendEmptyMessageDelayed(REFRESH,
								Constants.REFRESH_TIME);
					}

					@Override
					public void failed() {
						mHandler.sendEmptyMessageDelayed(REFRESH,
								Constants.REFRESH_TIME);
						Log.i("wpt", "GetLineStatusListTask_failed");
						if (mProgressDialog != null
								&& mProgressDialog.isShowing()) {
							if (!mExit) {
								mProgressDialog.dismiss();
							}
						}
					}
				}).execute();
	}

	private void getAlarmInfoList() {
		new GetAlarmListTask(mContext, new GetAlarmListTask.CallBackListener() {

			@Override
			public void succeed(List<Map<String, String>> result) {
				Log.i("wpt", "getAlarmInfoList_succeed");
				mAlarmList.addAll(result);
				if (mAlarmList != null && mAlarmList.size() > 0) {
					for (int i = 0; i < mAlarmList.size(); i++) {
						if ("3".equals(result.get(i).get("alarmType"))) {
							mAlarmList.remove(i);
						}
					}
					if (mAlarmList.size() > 0) {
						NotificationUtil.showNotification(mContext, 1, null,
								"请注意,有报警信息！！！", GetSwitchListTask.class);// 随便给一个class，所以不用跳转
					}
				}
				mHandler.sendEmptyMessageDelayed(REFRESH,
						Constants.REFRESH_TIME);
			}

			@Override
			public void failed() {
				Log.i("wpt", "getAlarmInfoList_failed");
				mHandler.sendEmptyMessageDelayed(REFRESH,
						Constants.REFRESH_TIME);
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					if (!mExit) {
						mProgressDialog.dismiss();
					}
				}
			}
		}).execute();
	}

	private void initMapView() {

		mMapView = (MapView) findViewById(R.id.map_bmapsView);
		// 设置启用内置的缩放控件
		mMapView.setBuiltInZoomControls(true);
		mMapView.setDoubleClickZooming(true);
		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		mController = mMapView.getController();

		GeoPoint point = new GeoPoint((int) (39.55 * 1E6), (int) (116.24 * 1E6));
		// 用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
		mController.setCenter(point);// 设置地图中心点
		mController.setZoom(mCurLevel);// 设置地图zoom级别
		// 修改定位数据后刷新图层生效
		mMapView.refresh();
		MyApplication app = (MyApplication) this.getApplication();
		if (app.getmBMapManager() == null) {
			app.setmBMapManager(new BMapManager(this));
			app.getmBMapManager().init(MyApplication.MAP_KEY, null);
		}
		mMapView.regMapViewListener(app.getmBMapManager(), mapViewListener);

		mOfflineMap = new MKOfflineMap();
		mOfflineMap.init(mController, this);
		int num = mOfflineMap.scan();
		Log.i("wpt", "num=" + num);

	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		MyApplication app = MyApplication.getInstance();
		if (app.getmBMapManager() != null) {
			app.getmBMapManager().start();
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		MyApplication app = MyApplication.getInstance();
		if (app.getmBMapManager() != null) {
			app.getmBMapManager().stop();
		}

		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i("wpt", "*****onStop******");
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		boolean isScreenOn = pm.isScreenOn();
		Log.i("wpt", "isScreenOn=" + isScreenOn);
		Log.i("wpt", "hasMessages=" + mHandler.hasMessages(REFRESH));
		if (isScreenOn) {
			mHandler.removeMessages(REFRESH);
		}
		Intent i = new Intent(mContext, MyService.class);
		i.putExtra("other", true);
		startService(i);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (mMapView == null) {
			initMapView();
		}
		if (mMapView.getOverlays() != null) {
			mMapView.getOverlays().clear();
		}
		new MyDrawCanvas(mContext, mLineList, mTransformerList, mSwitchList,
				mSwitchInfoMap, mLineStatusMap, mMapView);
		if (!mHandler.hasMessages(REFRESH)) {
			mHandler.sendEmptyMessageDelayed(REFRESH, Constants.REFRESH_TIME);
		}
	}

	@Override
	protected void onDestroy() {
		mExit = true;
		MyApplication app = MyApplication.getInstance();
		if (app.getmBMapManager() != null) {
			app.getmBMapManager().destroy();
			app.setmBMapManager(null);
		}
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
		if (mMapView.getOverlays() != null) {
			mMapView.getOverlays().clear();
		}
		MyDrawCanvas.mIsSeleted = false;
		Intent i = new Intent(mContext, MyService.class);
		startService(i);
		System.exit(0);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		final MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.menu_tree_item:
			intent = new Intent(mContext, TreeLineActivity.class);
			intent.putExtra("lineList", (Serializable) mLineList);
			intent.putExtra("transformerList", (Serializable) mTransformerList);
			break;
		case R.id.menu_alarm_item:
			intent = new Intent(mContext, AlarmInfoActivity.class);
			intent.putExtra("lineList", (Serializable) mLineList);
			intent.putExtra("transformerList", (Serializable) mTransformerList);
			break;
		case R.id.menu_curve_item:
			intent = new Intent(mContext, CurveActivity.class);
			intent.putExtra("lineList", (Serializable) mLineList);
			intent.putExtra("transformerList", (Serializable) mTransformerList);
			intent.putExtra("switchInfoList", (Serializable) mSwitchInfoList);
			break;
		case R.id.menu_info_item:
			intent = new Intent(mContext, ShowInfoActivity.class);
			break;
		case R.id.menu_history_item:
			intent = new Intent(mContext, HistoryEventActivity.class);
			intent.putExtra("lineList", (Serializable) mLineList);
			intent.putExtra("transformerList", (Serializable) mTransformerList);
			break;
		case R.id.menu_item_statistics_open_close_switch:
			intent = new Intent(mContext, StatisticsGateActivity.class);
			intent.putExtra("lineList", (Serializable) mLineList);
			intent.putExtra("transformerList", (Serializable) mTransformerList);
			intent.putExtra("switchInfoList", (Serializable) mSwitchInfoList);
			break;
		case R.id.menu_item_statistics_defend:
			intent = new Intent(mContext, StatisticsDefendActivity.class);
			intent.putExtra("lineList", (Serializable) mLineList);
			intent.putExtra("transformerList", (Serializable) mTransformerList);
			intent.putExtra("switchInfoList", (Serializable) mSwitchInfoList);
			break;
		default:
			break;
		}
		if (intent != null) {
			intent.putExtra("switchList", (Serializable) mSwitchList);
			startActivity(intent);
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// if (arg0 != null && mIsFirst) {
		// // 将当前位置转换成地理坐标点
		// final GeoPoint pt = new GeoPoint((int) (arg0.getLatitude() * 1E6),
		// (int) (arg0.getLongitude() * 1E6));
		// // 将当前位置设置为地图的中心
		// // mController.setCenter(pt);
		// mIsFirst = false;
		// }
	}

	@Override
	public void onBackPressed() {
		if (System.currentTimeMillis() - exitTime > 5000) {
			CustomToast.showShortToast(mContext,
					R.string.str_double_click_exit_tip);
			exitTime = System.currentTimeMillis();
		} else {
			finish();
		}
	}

	public void onSelectMode(View view) {
		Button button = (Button) view;
		if (!MyDrawCanvas.mIsSeleted) {
			MyDrawCanvas.mIsSeleted = true;
			button.setBackgroundResource(R.drawable.main_sel_btn_pressed);
		} else {
			MyDrawCanvas.mIsSeleted = false;
			button.setBackgroundResource(R.drawable.main_sel_btn_normol);
		}
	}

	/**
	 * 手动刷新
	 */
	public void onRefreshLineStatus(View view) {
		if (mMapView == null) {
			initMapView();
		}
		mHandler.removeMessages(REFRESH);
		getLineList();
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REFRESH:
				getEventNoticeInfo();
				getSwitchInfoMap();
				break;
			case REGETINFO:
				getLineList();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onProviderDisabled(String arg0) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onGetOfflineMapState(int type, int state) {

	}

	MKMapViewListener mapViewListener = new MKMapViewListener() {

		@Override
		public void onMapMoveFinish() {
			Log.i("wpt", "onMapMoveFinish");
			float level = mMapView.getZoomLevel();
			if (mCurLevel != level) {
				if (mMapView.getOverlays() != null) {
					mMapView.getOverlays().clear();
				}
				new MyDrawCanvas(mContext, mLineList, mTransformerList,
						mSwitchList, mSwitchInfoMap, mLineStatusMap, mMapView);
				mCurLevel = level;
			}
		}

		@Override
		public void onMapLoadFinish() {
		}

		@Override
		public void onMapAnimationFinish() {
			if (mMapView.getOverlays() != null) {
				mMapView.getOverlays().clear();
			}
			new MyDrawCanvas(mContext, mLineList, mTransformerList,
					mSwitchList, mSwitchInfoMap, mLineStatusMap, mMapView);
		}

		@Override
		public void onGetCurrentMap(Bitmap arg0) {

		}

		@Override
		public void onClickMapPoi(MapPoi arg0) {
		}
	};

	private void setCenterPoint(List<Switch> result) {
		double lonTotal = 0;
		double latTotal = 0;
		if (result != null && result.size() > 0) {
			mIsFirst = false;
			int size = result.size();
			for (int i = 0; i < size; i++) {
				Switch switch1 = result.get(i);
				double lon = switch1.getLongitude();
				double lat = switch1.getLatitude();
				lonTotal += lon;
				latTotal += lat;
			}
			GeoPoint geoPoint = new GeoPoint((int) (latTotal / size),
					(int) (lonTotal / size));
			mController.setCenter(geoPoint);
		}
	}

}
