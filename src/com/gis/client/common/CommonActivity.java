package com.gis.client.common;

import android.app.Activity;
import android.os.Bundle;

public class CommonActivity extends Activity {

	private MyApplication mApp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mApp = (MyApplication) getApplication();
		mApp.addActivity(this);
	}

	@Override
	protected void onDestroy() {
		mApp.removeActivity(this);
		super.onDestroy();
	}

}
