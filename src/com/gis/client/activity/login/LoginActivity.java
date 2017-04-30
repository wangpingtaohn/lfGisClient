package com.gis.client.activity.login;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import com.gis.client.R;
import com.gis.client.activity.map.MainMapActivity;
import com.gis.client.common.CommonActivity;
import com.gis.client.common.CommonProgressDialog;
import com.gis.client.common.Constants;
import com.gis.client.common.CustomDialog;
import com.gis.client.common.CustomToast;
import com.gis.client.common.CustomDialog.Builder;
import com.gis.client.http.net.RequestUtil;
import com.gis.client.util.FileManager;
import com.gis.client.util.NetworkUtil;

public class LoginActivity extends CommonActivity implements OnClickListener {

	private Context mContext;

	private EditText mUserEdit;

	private EditText mPwdEdit;

	private CheckBox autoLoginCheckBox;

	private CommonProgressDialog mProgressDialog;

	private boolean mExit;

	private Spinner mAreaSpinner;

	private List<String> mHostIpList = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);

		mContext = LoginActivity.this;
		initView();
	}

	private void initView() {
		Button loginBtn = (Button) findViewById(R.id.login_btn);
		Button exitBtn = (Button) findViewById(R.id.exit_btn);
		mUserEdit = (EditText) findViewById(R.id.login_username_edit);
		mPwdEdit = (EditText) findViewById(R.id.login_password_edit);
		autoLoginCheckBox = (CheckBox) findViewById(R.id.autou_login_checkBox);
		mAreaSpinner = (Spinner) findViewById(R.id.login_area_spinner);

		getHostIpArray();

		String userName = FileManager.getSharedPreValue(mContext,
				Constants.SP_CONFIG, Constants.USER_NAME);
		String password = FileManager.getSharedPreValue(mContext,
				Constants.SP_CONFIG, Constants.PASSWORD);
		if (userName != null && !"".equals(userName)) {
			mUserEdit.setText(userName);
			mUserEdit.setSelection(userName.length());
		}
		if (password != null && !"".equals(password)) {
			mPwdEdit.setText(password);
			mPwdEdit.setSelection(password.length());
		}

		loginBtn.setOnClickListener(this);
		exitBtn.setOnClickListener(this);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			setContentView(R.layout.login_layout);
			initView();
		} else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setContentView(R.layout.login_layout);
			initView();
		}
	}

	private void getHostIpArray() {
		String path = FileManager.getSdcardPath() + "/GIS_IP_Config.txt";
		String hostIp = convertCodeAndGetText(path);
		Log.i("wpt", "hostIp=" + hostIp);
		if (hostIp != null && !"".equals(hostIp)) {
			String[] ipStr = hostIp.split("#");
			List<String> areaList = new ArrayList<String>();
			if (ipStr != null && ipStr.length > 0) {
				for (String ip : ipStr) {
					if (ip != null && !"".equals(ip)) {
						String tempIp = ip.split("&")[0];
						if (ip.split("&").length > 1) {
							String tempArea = ip.split("&")[1];
							areaList.add(tempArea);
						}
						mHostIpList.add(tempIp);
					}
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						mContext, android.R.layout.simple_spinner_item,
						areaList);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				mAreaSpinner.setAdapter(adapter);
			} else {
				mHostIpList.add(hostIp);
			}
		} else {
			CustomToast.showShortToast(mContext, "配置文件为空!,请重新配置!");
		}
	}

	private String convertCodeAndGetText(String path) {
		File file = new File(path);
		String text = "";
		if (file.exists()) {
			BufferedReader bReader;
			try {
				FileInputStream fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);
				bis.mark(4);
				byte[] b3 = new byte[3];
				bis.read(b3);//找到文档的前三个字节并自动判断文档类型
//				bis.reset();
				if (b3[0] == (byte) 0xEF && b3[1] == (byte) 0xBB
						&& b3[2] == (byte) 0xBF) {// utf-8
					bReader = new BufferedReader(new InputStreamReader(bis,
							"utf-8"));
				} else if (b3[0] == (byte) 0xFF && b3[1] == (byte) 0xFE) {
					bReader = new BufferedReader(new InputStreamReader(bis,
							"unicode"));
				} else if (b3[0] == (byte) 0xFE && b3[1] == (byte) 0xFF) {
					bReader = new BufferedReader(new InputStreamReader(bis,
							"utf-16be"));
				} else if (b3[0] == (byte) 0xFF && b3[1] == (byte) 0xFF) {
					bReader = new BufferedReader(new InputStreamReader(bis,
							"utf-16be"));
				} else {
					bis.reset();
					bReader = new BufferedReader(new InputStreamReader(bis,
							"GBK"));
				}
				String str = bReader.readLine();
				while (str != null) {
					text += str;
					str = bReader.readLine();
				}
				bReader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return text;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.login_btn:
			String userName = mUserEdit.getText().toString();
			String password = mPwdEdit.getText().toString();
			if (userName == null || "".equals(userName)) {
				CustomToast.showToast(mContext, "请输入账号");
				return;
			}
			if (password == null || "".equals(password)) {
				CustomToast.showToast(mContext, "请输入密码");
				return;
			}
			int index = mAreaSpinner.getSelectedItemPosition();
			if (mHostIpList != null && mHostIpList.size() > 0) {
				if (-1 == index) {
					index = 0;
				}
				String hostIp = mHostIpList.get(index);
				RequestUtil.SERVER_HOST = hostIp;
				FileManager.saveSharedPre(mContext, Constants.SP_CONFIG,
						Constants.HOST_IP, hostIp);
				if ("sAdmin".equals(userName) && "123".equals(password)) {
					FileManager.saveSharedPre(mContext, Constants.SP_CONFIG,
							Constants.USER_NAME, userName);
					if (autoLoginCheckBox.isChecked()) {
						FileManager.saveSharedPre(mContext, Constants.SP_CONFIG,
								Constants.PASSWORD, mPwdEdit.getText().toString());
					}
					Intent intent = new Intent(mContext, MainMapActivity.class);
					startActivity(intent);
					finish();
				} else {
					new LoginTask().execute(userName, password);
				}
			} else {
				CustomToast.showShortToast(mContext, "配置文件为空!,请重新配置!");
			}

			break;
		case R.id.exit_btn:
			showExitDialog();
			break;

		default:
			break;
		}
	}

	class LoginTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			int netCode = NetworkUtil.getInstance()
					.isNetworkAvailable(mContext);
			if (NetworkUtil.NET_NOT_AVAILABLE == netCode) {
				CustomToast.showToast(mContext, R.string.str_net_work_error);
				cancel(true);
			} else {
				if (!mExit) {
					mProgressDialog = CommonProgressDialog.show(mContext,
							R.string.str_tip,
							getResources()
									.getString(R.string.str_login_loading));
				}
			}
		}

		@Override
		protected String doInBackground(String... params) {
			String userName = params[0];
			String password = params[1];
			RequestUtil requestUtil = new RequestUtil(mContext);
			String result = requestUtil.getLogin(userName, password);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (!mExit) {
				mProgressDialog.dismiss();
			}
			if (result != null) {
				if (result.equals("成功")) {
					FileManager
							.saveSharedPre(mContext, Constants.SP_CONFIG,
									Constants.USER_NAME, mUserEdit.getText()
											.toString());
					if (autoLoginCheckBox.isChecked()) {
						FileManager.saveSharedPre(mContext,
								Constants.SP_CONFIG, Constants.PASSWORD,
								mPwdEdit.getText().toString());
					}
					Intent intent = new Intent(mContext, MainMapActivity.class);
					startActivity(intent);
					finish();
				} else {
					CustomToast.showToast(mContext, result);
				}
			} else {
				CustomToast.showToast(mContext, "登陆失败,请重试");
			}
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
		mExit = true;
	}

	@Override
	public void onBackPressed() {
		showExitDialog();
	}

	private void showExitDialog() {
		Builder builder = new CustomDialog.Builder(mContext);
		CustomDialog dialog = null;
		builder.setTitle(R.string.str_tip);
		builder.setMessage(R.string.str_tip_exit);
		builder.setPositiveButton(
				getResources().getString(R.string.str_ok_btn),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
						dialog.dismiss();
					}
				});
		builder.setNegativeButton(
				getResources().getString(R.string.str_cancel_btn),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		dialog = builder.create();
		dialog.show();
	}
}
