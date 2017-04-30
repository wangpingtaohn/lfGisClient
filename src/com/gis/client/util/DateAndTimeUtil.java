package com.gis.client.util;

import java.util.Calendar;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public class DateAndTimeUtil {

	private Context mContext;
	
	private CallBackLisneter mCallBackLisneter;
	
	public interface CallBackLisneter {
		void setFinish(String time);
	}

	public DateAndTimeUtil(Context context) {
		this.mContext = context;
	}

	public void showDateDialog(TextView textView) {

		Calendar calendar = Calendar.getInstance();

		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		if (textView.getText() != null
				&& !"".equals(textView.getText().toString())) {
			String yearStr = textView.getText().toString().substring(0, 4);
			String monthStr = textView.getText().toString().substring(5, 7);
			String dayStr = textView.getText().toString().substring(8);
			if (yearStr != null && !"".equals(yearStr)) {
				year = Integer.parseInt(yearStr);
			}
			if (monthStr != null && !"".equals(monthStr)) {
				month = Integer.parseInt(monthStr) - 1;
			}
			if (dayStr != null && !"".equals(dayStr)) {
				day = Integer.parseInt(dayStr);
			}
		}
		DatePickerDialog dateDialog = new DatePickerDialog(mContext,
				new SetDateDialog(textView), year, month, day);
		if (null != dateDialog) {
			dateDialog.show();
		}
	}
	public void showDateDialog(TextView textView, CallBackLisneter callBackLisneter) {
		mCallBackLisneter = callBackLisneter;
		Calendar calendar = Calendar.getInstance();
		
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		if (textView.getText() != null
				&& !"".equals(textView.getText().toString())) {
			String yearStr = textView.getText().toString().substring(0, 4);
			String monthStr = textView.getText().toString().substring(5, 7);
			String dayStr = textView.getText().toString().substring(8);
			if (yearStr != null && !"".equals(yearStr)) {
				year = Integer.parseInt(yearStr);
			}
			if (monthStr != null && !"".equals(monthStr)) {
				month = Integer.parseInt(monthStr) - 1;
			}
			if (dayStr != null && !"".equals(dayStr)) {
				day = Integer.parseInt(dayStr);
			}
		}
		DatePickerDialog dateDialog = new DatePickerDialog(mContext,
				new SetDateDialog(textView), year, month, day);
		if (null != dateDialog) {
			dateDialog.show();
		}
	}

	public void showTimeDialog(TextView textView, CallBackLisneter callBackLisneter) {
		mCallBackLisneter = callBackLisneter;
		Calendar calendar = Calendar.getInstance();

		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		
		if (textView.getText() != null
				&& !"".equals(textView.getText().toString())) {
			String hourStr = textView.getText().toString().substring(0, 2);
			String minuteStr = textView.getText().toString().substring(3, 5);
			if (hourStr != null && !"".equals(hourStr)) {
				hour = Integer.parseInt(hourStr);
			}
			if (minuteStr != null && !"".equals(minuteStr)) {
				minute = Integer.parseInt(minuteStr);
			}
		}
		
		TimePickerDialog dialog = new TimePickerDialog(mContext,
				new SetTimeDialog(textView), hour, minute, true);
		if (null != dialog) {
			dialog.show();
		}
	}
	public void showTimeDialog(TextView textView) {
		
		Calendar calendar = Calendar.getInstance();
		
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		
		if (textView.getText() != null
				&& !"".equals(textView.getText().toString())) {
			String hourStr = textView.getText().toString().substring(0, 2);
			String minuteStr = textView.getText().toString().substring(3, 5);
			if (hourStr != null && !"".equals(hourStr)) {
				hour = Integer.parseInt(hourStr);
			}
			if (minuteStr != null && !"".equals(minuteStr)) {
				minute = Integer.parseInt(minuteStr);
			}
		}
		
		TimePickerDialog dialog = new TimePickerDialog(mContext,
				new SetTimeDialog(textView), hour, minute, true);
		if (null != dialog) {
			dialog.show();
		}
	}

	private class SetDateDialog implements OnDateSetListener {
		private TextView textView;

		/**
		 * 构造方法.
		 * 
		 * @param view
		 *            显示的控件
		 */
		public SetDateDialog(TextView view) {
			this.textView = view;
		}

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			final String yearStr = String.valueOf(year);
			String monthStr = String.valueOf(monthOfYear + 1);
			String dayStr = String.valueOf(dayOfMonth);
			if (monthStr.length() < 2) {
				monthStr = "0" + monthStr;
			}
			if (dayStr.length() < 2) {
				dayStr = "0" + dayStr;
			}
			final String date = yearStr + "-" + monthStr + "-" + dayStr;
			textView.setText(date);
			if (mCallBackLisneter != null) {
				mCallBackLisneter.setFinish(date);
			}

		}

	}

	class SetTimeDialog implements OnTimeSetListener {

		private TextView textView;

		/**
		 * 构造方法.
		 * 
		 * @param view
		 *            显示的控件
		 */
		public SetTimeDialog(TextView view) {
			this.textView = view;
		}

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Calendar calendar = Calendar.getInstance();
			String hours = String.valueOf(hourOfDay);
			String mMinute = String.valueOf(minute);
			String second = String.valueOf(calendar.get(Calendar.SECOND));
			if (hours.length() < 2) {
				hours = "0" + hours;
			}
			if (mMinute.length() < 2) {
				mMinute = "0" + mMinute;
			}
			if (second.length() < 2) {
				second = "0" + second;
			}
			final String time = hours + ":" + mMinute + ":" + second;
			textView.setText(time);
			if (mCallBackLisneter != null) {
				mCallBackLisneter.setFinish(time);
			}
		}
	}
	
	public static String getDate() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		String monthStr = String.valueOf(month);
		if (month <= 9) {
			monthStr = "0" + monthStr;
		}
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		String dayStr = String.valueOf(day);
		if (day <= 9) {
			dayStr = "0" + dayStr;
		}
		return year + "-" + monthStr + "-" + dayStr;
	}
	public static String getTime() {
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		String hourStr = String.valueOf(hour);
		if (hour <= 9) {
			hourStr = "0" + hourStr;
		}
		String minuteStr = String.valueOf(minute);
		if (minute <= 9) {
			minuteStr = "0" + minuteStr;
		}
		String secondStr = String.valueOf(second);
		if (second <= 9) {
			secondStr = "0" + secondStr;
		}
		return hourStr + ":" + minuteStr + ":" + secondStr;
	}
}
