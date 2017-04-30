package com.gis.client.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @Desc: 网络连接管理
 * @author wpt
 * @since 2013-7-1 下午4:19:06
 */
public class NetworkUtil {

	private static NetworkUtil networkUtil;

	private Context mContext;

	public static int NET_AVAILABLE = 0;

	public static int NET_NOT_AVAILABLE = 1;

	private NetworkUtil() {
	}

	public static NetworkUtil getInstance() {
		synchronized (NetworkUtil.class) {
			if (networkUtil == null) {
				networkUtil = new NetworkUtil();
			}
		}

		return networkUtil;
	}

	public int isNetworkAvailable(Context context) {
		this.mContext = context;
		ConnectivityManager connectivity = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return NET_NOT_AVAILABLE;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return NET_AVAILABLE;
					}
				}
			}
		}
		return NET_NOT_AVAILABLE;
	}
}
