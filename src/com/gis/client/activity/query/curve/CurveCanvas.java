package com.gis.client.activity.query.curve;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.gis.client.model.SwitchInfo;
import com.gis.client.util.DateUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CurveCanvas extends SurfaceView implements Runnable,
		SurfaceHolder.Callback {

	private SurfaceHolder sh;
	private List<SwitchInfo> mSwitchInfoList = new ArrayList<SwitchInfo>();
	private Map<String, String> mMap;
	private String mStartTime;
	private String mEndTime;
	private int mMaxValue;
	private int mMinValue;
	private float mQiuZheng;
	private int mHeight;
	private int mWidth;
	private float mOriginalHeight;
	private int mOriginalWidth = 50;
	// 纵向间隔
	private int mPortaitSpace = 35;
	// 横向间隔
	private int mLanSpace = 40;
	// 类型：0--电流 ,1--电压
	private String mType;
	// 宽比例
	private float mWScale;
	// 高比例
	private float mHScale;
	//横轴世界类型：0--1分钟之内;1--一小时之内;2--一天之内;
	private int mLanTimeType;

	public CurveCanvas(Context context, List<SwitchInfo> switchInfoList,
			Map<String, String> map) {
		super(context);
		this.mSwitchInfoList = switchInfoList;
		this.mMap = map;
		if (mMap != null) {
			this.mType = mMap.get("type");
			this.mStartTime = mMap.get("startTime");
			this.mEndTime = mMap.get("endTime");
			if (null != mMap.get("minValue")) {
				this.mMinValue = Integer.parseInt(mMap.get("minValue"));
			}
			if (null != mMap.get("maxValue")) {
				this.mMaxValue = Integer.parseInt(mMap.get("maxValue"));
			}
		}
		sh = getHolder();
		sh.addCallback(this);

	}

	public void drawCoordinate() {

		mHeight = this.getHeight();// 243*480 293*480 438*800 341*800
		mWidth = this.getWidth();
		mHScale = mHeight / 243.0f;
		mWScale = mWidth / 480.0f;
		mQiuZheng = ((mMaxValue - mMinValue) / 4.0f) == 0 ? 1
				: ((mMaxValue - mMinValue) / 4.0f);
		mLanSpace *= mWScale;
		mPortaitSpace *= mHScale;
		mOriginalHeight = mHeight - 60 * mHScale;

		Canvas canvas = sh.lockCanvas();
		canvas.drawColor(Color.WHITE);
		Paint paint = new Paint();
		paint.setColor(Color.RED);

		// 画纵向线
		for (int i = 0; i < 11; i++) {
			paint.setColor(Color.GRAY);
			canvas.drawLine(mOriginalWidth + mLanSpace * i, mOriginalHeight,
					mOriginalWidth + mLanSpace * i, 20 * mHScale, paint);
		}
		// 画横向线
		for (int i = 0; i < 5; i++) {
			canvas.drawLine(mOriginalWidth,
					mOriginalHeight - mPortaitSpace * i, mWidth - 50 * mWScale,
					mOriginalHeight - mPortaitSpace * i, paint);
		}

		// 画纵向坐标点
		paint.setTextSize(16);
		for (int i = 0; i < 5; i++) {
			paint.setStyle(Style.FILL);
			paint.setStrokeWidth(4);
			paint.setColor(Color.BLUE);
			canvas.drawPoint(50, mOriginalHeight - mPortaitSpace * i, paint);
			canvas.drawText(mMinValue + i * mQiuZheng + "", 20, mOriginalHeight
					- mPortaitSpace * i, paint);
		}
		// 画横向坐标点
		for (int i = 0; i < 11; i++) {
			paint.setColor(Color.BLUE);
			paint.setTextSize(16);
			canvas.drawPoint(mOriginalWidth + i * mLanSpace, mOriginalHeight,
					paint);
			paint.setTextSize(12);
			canvas.drawText(getLanValue(i), mOriginalWidth + i * mLanSpace - 20,
					mHeight - 40 * mHScale, paint);
//			canvas.drawText(10 * i + "", mOriginalWidth + i * mLanSpace,
//					mHeight - 40 * mHScale, paint);
		}

		drawSwitchInfo(canvas);

		sh.unlockCanvasAndPost(canvas);
		this.getWidth();

	}
	/*获取横轴上的间断值*/
	private String getLanValue(int index) {
		String value = "";
		long startLTime = DateUtils.timeStrToLong(mStartTime);
		long endLTime = DateUtils.timeStrToLong(mEndTime);
		long interval = (endLTime - startLTime) / 1000;//单位-秒
		if (1 <= interval && interval <= 60) {//一分钟
			value = 6 * index + "秒";
			mLanTimeType = 0;
		} else if (60 < interval && interval <= 60 * 60) {//一小时
			value = 6 * index + "分";
			mLanTimeType = 1;
		} else if (60 * 60 < interval && interval <= 24 * 60 * 60) {//一天
			long minute = interval / 60;
			int lanValue = (int) ((minute / 10) * index);
			int hourIndex = mStartTime.indexOf(" ");
//			Log.i("wpt", "minute=" + minute);
//			Log.i("wpt", "lanValue=" + lanValue);
			String hourStr = mStartTime.substring(hourIndex + 1, hourIndex + 3);
			String minuteStr = mStartTime.substring(hourIndex + 4, hourIndex + 6);
			int hour = Integer.parseInt(hourStr);
			int outHour = 0;
			int resMinute = Integer.parseInt(minuteStr) + lanValue;
			if (resMinute > 60) {
				outHour = (int) (resMinute / 60);
				resMinute = (int) (resMinute % 60);
			}
			int temp = hour + outHour;
			if (temp >= 24) {//第二天
				temp = temp - 24;
			}
			value = temp + "时" + resMinute + "分";
			mLanTimeType = 2;
		}
		
		return value;
	}

	private void drawSwitchInfo(Canvas canvas) {
		Paint paint = new Paint();
		paint.setStyle(Style.FILL);
		paint.setStrokeWidth(2);
		int count = mSwitchInfoList.size();
		if (mSwitchInfoList != null && count > 0) {
			for (int i = 0; i < count; i++) {
				SwitchInfo startInfo = mSwitchInfoList.get(i);
				String startTime = startInfo.getTime();
//				long startLTime = DateUtils.timeStrToLong(startTime);// 把标准时间转化成long型,单位毫秒
//				String startMinute = DateUtils.mSecondsToMinute(startLTime);// 把毫秒转化成分钟
//				float startX = (float) (mOriginalWidth + ((Integer
//						.parseInt(startMinute) / 10.0) * mLanSpace));
				float startX = getLanInfoX(startTime);
				SwitchInfo endInfo = null;
				float endX = 0;
				if (i < count - 1) {
					endInfo = mSwitchInfoList.get(i + 1);
					String endTime = endInfo.getTime();
//					long endLTime = DateUtils.timeStrToLong(endTime);
//					String endMinute = DateUtils.mSecondsToMinute(endLTime);
//					endX = (float) (mOriginalWidth + ((Integer
//							.parseInt(endMinute) / 10.0) * mLanSpace));
					endX = getLanInfoX(endTime);
				}
				if ("0".equals(mType)) {
					paint.setColor(Color.RED);
					int startAl = -1;
					int endAl = -1;
					int startBl = -1;
					int endBl = -1;
					int startCl = -1;
					int endCl = -1;
					if ("true".equals(mMap.get("al"))) {
						startAl = startInfo.getaL();
						float startAlY = mOriginalHeight
								- ((startAl - mMinValue) / mQiuZheng)
								* mPortaitSpace;
						paint.setColor(Color.BLACK);
						paint.setStrokeWidth(5);
						canvas.drawPoint(startX, startAlY, paint);
						if (endInfo != null) {
							endAl = endInfo.getaL();
							float endAlY = mOriginalHeight
									- ((endAl - mMinValue) / mQiuZheng)
									* mPortaitSpace;
							canvas.drawPoint(endX, endAlY, paint);
							paint.setColor(Color.RED);
							paint.setStrokeWidth(3);
							canvas.drawLine(startX, startAlY, endX, endAlY,
									paint);
						}
					}
					if ("true".equals(mMap.get("bl"))) {
						startBl = startInfo.getbL();
						float startBlY = mOriginalHeight
								- ((startBl - mMinValue) / mQiuZheng)
								* mPortaitSpace;
						paint.setColor(Color.BLACK);
						paint.setStrokeWidth(5);
						canvas.drawPoint(startX, startBlY, paint);
						if (endInfo != null) {
							endBl = endInfo.getbL();
							float endBlY = mOriginalHeight
									- ((endBl - mMinValue) / mQiuZheng)
									* mPortaitSpace;
							canvas.drawPoint(endX, endBlY, paint);
							paint.setColor(Color.BLUE);
							paint.setStrokeWidth(3);
							canvas.drawLine(startX, startBlY, endX, endBlY,
									paint);
						}
					}
					if ("true".equals(mMap.get("cl"))) {
						startCl = startInfo.getcL();
						float startClY = mOriginalHeight
								- ((startCl - mMinValue) / mQiuZheng)
								* mPortaitSpace;
						paint.setColor(Color.BLACK);
						paint.setStrokeWidth(5);
						canvas.drawPoint(startX, startClY, paint);
						if (endInfo != null) {
							endCl = endInfo.getcL();
							float endClY = mOriginalHeight
									- ((endCl - mMinValue) / mQiuZheng)
									* mPortaitSpace;
							canvas.drawPoint(endX, endClY, paint);
							paint.setColor(Color.YELLOW);
							paint.setStrokeWidth(3);
							canvas.drawLine(startX, startClY, endX, endClY,
									paint);
						}
					}
				} else {

					int startVoltage = startInfo.getVoltage();
					float startY = mOriginalHeight
							- ((startVoltage - mMinValue) / mQiuZheng)
							* mPortaitSpace;
					paint.setColor(Color.BLACK);
					paint.setStrokeWidth(5);
					canvas.drawPoint(startX, startY, paint);
					if (i < count - 1) {
						int endVoltage = endInfo.getVoltage();
						float endY = mOriginalHeight
								- ((endVoltage - mMinValue) / mQiuZheng)
								* mPortaitSpace;
						canvas.drawPoint(endX, endY, paint);
						paint.setColor(Color.GREEN);
						paint.setStrokeWidth(3);
						canvas.drawLine(startX, startY, endX, endY, paint);
					}
				}
			}
		}
	}

	/*获取开关在横轴上的值*/
	private float getLanInfoX(String Time) {
		float infoX = 0;
		long lTime;
		switch (mLanTimeType) {
		case 0:
			lTime = DateUtils.timeStrToLong(Time);// 把标准时间转化成long型,单位毫秒
			String minute = DateUtils.mSecondsToseconds(lTime);// 把毫秒转化成分钟
			infoX = (float) (mOriginalWidth + ((Integer
					.parseInt(minute) / 6.0) * mLanSpace));
			break;
		case 1:
			lTime = DateUtils.timeStrToLong(Time);// 把标准时间转化成long型,单位毫秒
			String seconds = DateUtils.mSecondsToMinute(lTime);// 把毫秒转化成秒
			infoX = (float) (mOriginalWidth + ((Integer
					.parseInt(seconds) / 6.0) * mLanSpace));
			break;
		case 2:
			lTime = DateUtils.timeStrToLong(Time) - DateUtils.timeStrToLong(mStartTime);// 把标准时间转化成long型,单位毫秒
			long startLTime = DateUtils.timeStrToLong(mStartTime);
			long endLTime = DateUtils.timeStrToLong(mEndTime);
			long interval = (endLTime - startLTime) / 1000;//单位-秒
			long minutes = interval / 60;
			float lanValue = minutes / 10;
			infoX = mOriginalWidth + (((lTime / 1000) / 60) / lanValue) * mLanSpace;
			break;

		default:
			break;
		}
		
		return infoX;
	}
	@Override
	public void run() {
		drawCoordinate();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		new Thread(this).start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

}
