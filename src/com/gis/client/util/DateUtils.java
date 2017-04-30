package com.gis.client.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 日期时间格式转换类.
 * 
 * @author King
 * @since 2010.07.02
 */
public final class DateUtils {

	// 时间格式
	public static final int FORMAT_HHMM = 1;
	public static final int FORMAT_HHMMSS = 2;

	/**
	 * 格式为【yyyyMMdd】.
	 */
	public static final String FORMAT_YYYYMMDD = "yyyyMMdd";
	/**
	 * 格式为【yyyy/MM/dd】.
	 */
	public static final String FORMAT_YMD = "yyyy/MM/dd";
	/**
	 * 格式为【yyyy-MM-dd】.
	 */
	public static final String FORMAT_YMD2 = "yyyy-MM-dd";
	/**
	 * 格式为【yyyy年MM月dd日】.
	 */
	public static final String FORMAT_YEAR_MONTH_DATE = "yyyy年MM月dd日";
	/**
	 * 格式为【yyyy年MM月】.
	 */
	public static final String FORMAT_YEAR_MONTH = "yyyy年MM月";
	/**
	 * 格式为【yyyy年MM月dd日(E)】.
	 */
	public static final String FORMAT_YEAR_MONTH_DATE_DAY = "yyyy年MM月dd日(E)";
	/**
	 * 格式为【yyyy】.
	 */
	public static final String FORMAT_YEAR = "yyyy";
	/**
	 * 格式为【MM】.
	 */
	public static final String FORMAT_MONTH = "MM";

	/**
	 * 格式为【yyyy/MM/dd HH:mm:ss】.
	 */
	public static final String FORMAT_YYYYMMDDHHSS = "yyyy/MM/dd HH:mm:ss";
	/**
	 * 格式为【yyyy-MM-dd HH:mm:ss】.
	 */
	public static final String FORMAT_YYYYMMDDHHSS2 = "yyyy-MM-dd HH:mm:ss";
	/**
	 * 格式为【yyyyMMddHHmmss】.
	 */
	public static final String FORMAT_YYYYMMDDHHSS3 = "yyyyMMddHHmmss";
	/**
	 * 格式为【yyyy/MM/dd HH:mm】.
	 */
	public static final String FORMAT_YYYYMMDDHHMM = "yyyy/MM/dd HH:mm";
	/**
	 * 格式为【yyyy/MM/dd/HH/mm】.
	 */
	public static final String FORMAT_YYYYMMDDHHMM2 = "yyyy/MM/dd/HH/mm";
	/**
	 * 格式为【MM.dd HH:mm】.
	 */
	public static final String FORMAT_YYYYMMDDHHMM3 = "MM.dd HH:mm";
	/**
	 * 格式为【HH:mm】.
	 */
	public static final String FORMAT_YYYYMMDDHHMM4 = "MM.dd";
	/**
	 * 格式为【HH:mm:ss】.
	 */
	public static final String FORMAT_HH_MM_SS = "HH:mm:ss";
	/**
	 * 格式为【HH/mm】.
	 */
	public static final String FORMAT_HHMM2 = "HH/mm";
	/**
	 * 格式为【HH/mm】.
	 */
	public static final String FORMAT_HHMM3 = "HH:mm";
	/**
	 * 格式为【yyyy年MM月dd日HH:mm:ss】.
	 */
	public static final String FORMAT_YEAR_MONTH_DATE_TIME = "yyyy年MM月dd日HH:mm:ss";
	/**
	 * 格式为【yyyy年MM月dd日 HH:mm】.
	 */
	public static final String FORMAT_YEAR_MONTH_DATE_TIME2 = "yyyy年MM月dd日 HH:mm";

	/**
	 * 构造函数
	 */
	private DateUtils() {
		super();
	}

	/**
	 * 日期格式化.
	 * 
	 * @param cal
	 *            日期
	 * @param format
	 *            格式
	 * @return 格式化后的日期
	 */
	public static final String makeFormat(Calendar cal, String format) {

		DateFormat df = new SimpleDateFormat(format);
		String date = df.format(cal.getTime());

		return date;
	}

	/**
	 * 当前时间转换成数字.
	 * 
	 * @param format
	 *            格式
	 * @return 转化后的时间
	 */
	public static final long getNowTimeNum(String format) {

		return Long.parseLong(getNowTime(format));

	}

	public static final Date getStringToDate(String dateStr, String formatStr) {
		final DateFormat format = new SimpleDateFormat(formatStr);
		Date date = null;
		try {
			date = format.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return date;
	}

	public static final String getStringToString(String dateStr,
			String formatStr) {
		Date date = getStringToDate(dateStr, FORMAT_YYYYMMDDHHSS2);
		SimpleDateFormat df = new SimpleDateFormat(formatStr);
		return df.format(date);
	}

	/**
	 * 现在时间以格式化后的形式取得.
	 * 
	 * @param format
	 *            格式
	 * @return 格式化后的时间
	 */
	public static final String getNowTime(String format) {

		return makeFormat(GregorianCalendar.getInstance(), format);
	}

	/**
	 * 年月日的格式化.
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param day
	 *            日
	 * @param format
	 *            格式
	 * @return 格式化的数据
	 */
	public static long getFormatDate(int year, int month, int day, String format) {

		final Calendar cal = new GregorianCalendar(year, month - 1, day);

		return Long.parseLong(makeFormat(cal, format));
	}

	/**
	 * 最后一天取得.
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param format
	 *            格式
	 * @return 最后一天
	 */
	public static final long getFinalDay(int year, int month, String format) {

		final Calendar cal = new GregorianCalendar(year, month - 1, 1);

		final int day = cal.getActualMaximum(Calendar.DATE);
		cal.set(Calendar.DATE, day);

		return Long.parseLong(makeFormat(cal, format));
	}

	/**
	 * 第一天的星期取得.
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @return 第一天的星期
	 */
	public static final int getfirstDayOfWeek(int year, int month) {

		Calendar cal1 = new GregorianCalendar(year, month - 1, 1);

		return cal1.get(Calendar.DAY_OF_WEEK) - 1;
	}

	/**
	 * 最后一天的星期的取得.
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @return 最后一天的星期
	 */
	public static final int getLastDayOfWeek(int year, int month) {
		Calendar cal1 = new GregorianCalendar(year, month, 1);
		cal1.add(Calendar.DATE, -1);
		return cal1.get(Calendar.DAY_OF_WEEK) - 1;
	}

	/**
	 * 年月日在本月的第几周，星期几的取得.
	 * 
	 * @param ymd
	 *            年月日
	 * @return 年月日在本月的第几周，星期几
	 */
	public static final int[] getIndexByYmd(int ymd) {

		int[] nRet = null;

		String tmp = String.valueOf(ymd);
		if (tmp.length() >= 8) {
			int year = Integer.parseInt(tmp.substring(0, 4));
			int month = Integer.parseInt(tmp.substring(4, 6));
			int day = Integer.parseInt(tmp.substring(6, 8));

			nRet = getIndexByYmd(year, month, day);
		}

		return nRet;
	}

	/**
	 * 年月日在本月的第几周，星期几的取得.
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param day
	 *            日
	 * @return
	 */
	public static final int[] getIndexByYmd(int year, int month, int day) {

		int[] nRet = new int[2];

		Calendar cal = new GregorianCalendar(year, month - 1, day);
		int firstday = getfirstDayOfWeek(year, month);

		// 月中的第几周
		nRet[0] = cal.get(Calendar.DAY_OF_WEEK_IN_MONTH);

		// 星期几
		nRet[1] = cal.get(Calendar.DAY_OF_WEEK) - 1;

		if (firstday <= nRet[1]) {
			nRet[0] = nRet[0] - 1;
		}

		return nRet;
	}

	/**
	 * 日期以addNum的值以及kind的类型变化.
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param day
	 *            日
	 * @return 变化后的日期
	 */
	public static final int[] add(int year, int month, int day, int kind,
			int addNum) {

		int[] nRet = { -1, -1, -1 };

		if (year != -1) {

			Calendar cal = GregorianCalendar.getInstance();
			// 年
			cal.set(Calendar.YEAR, year);
			// 月
			cal.set(Calendar.MONTH, month - 1);
			// 日
			cal.set(Calendar.DATE, day);

			cal.add(kind, addNum);

			nRet[0] = cal.get(Calendar.YEAR);
			nRet[1] = cal.get(Calendar.MONTH);
			nRet[2] = cal.get(Calendar.DATE);
		}
		return nRet;
	}

	/**
	 * 时间的格式化.
	 * 
	 * @param type
	 *            HHMM/HHMMSS
	 * @param time
	 *            时间
	 * @return 格式化后的时间
	 */
	public static final String getTimeFormat(int type, int time) {

		return getTimeFormat(type, String.valueOf(time));
	}

	/**
	 * 时间的格式化.
	 * 
	 * @param type
	 *            HH:MM/HH:MM:SS
	 * @param hour
	 *            小时
	 * @param min
	 *            分钟
	 * @param sec
	 *            秒
	 * @return 格式化后的时间
	 */
	public static final String getTimeFormat(int type, int hour, int min,
			int sec) {

		DecimalFormat decform = new DecimalFormat("00");

		int time = 0;
		if (type == FORMAT_HHMM) {
			time = Integer.parseInt(decform.format(hour) + decform.format(min));

		} else if (type == FORMAT_HHMMSS) {
			time = Integer.parseInt(decform.format(hour) + decform.format(min)
					+ decform.format(sec));

		}

		return getTimeFormat(type, time);
	}

	/**
	 * 文字形式的时间转换.
	 * 
	 * @param type
	 *            HHMM/HHMMSS
	 * @param time
	 *            时间
	 * @return 转换后的时间
	 */
	public static final String getTimeFormat(int type, String time) {

		String ret = null;
		if (type == FORMAT_HHMM) {
			if (time.length() < 4) {
				DecimalFormat decForm = new DecimalFormat("0000");
				time = decForm.format(Integer.parseInt(time));
			}
			String hh = time.substring(0, 2);
			String mm = time.substring(2, 4);
			ret = hh + ":" + mm;

		} else if (type == FORMAT_HHMMSS) {

			if (time.length() < 6) {
				DecimalFormat decForm = new DecimalFormat("000000");
				time = decForm.format(Integer.parseInt(time));
			}
			String hh = time.substring(0, 2);
			String mm = time.substring(2, 4);
			String ss = time.substring(4, 6);
			ret = hh + ":" + mm + ":" + ss;
		}

		return ret;

	}

	/**
	 * 补全日期后的转换.
	 * 
	 * @param dateValue
	 *            日期
	 * @param format
	 *            格式
	 * @return 转换后的日期
	 */
	public static final String formatString(String dateValue, String format) {
		if (dateValue == null || dateValue.equals("")) {
			return dateValue;
		}

		if (dateValue.length() == 8) {
			// YYYYMMDD 的 000000补全
			dateValue += "000000";
		} else if (dateValue.length() == 12) {
			// YYYYMMDD 的HHMM00补全
			dateValue += "00";
		} else if (dateValue.length() != 14) {
			return dateValue;
		}

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, Integer.parseInt(dateValue.substring(0, 4)));
		cal.set(Calendar.MONTH, Integer.parseInt(dateValue.substring(4, 6)) - 1);
		cal.set(Calendar.DATE, Integer.parseInt(dateValue.substring(6, 8)));

		cal.set(Calendar.HOUR_OF_DAY,
				Integer.parseInt(dateValue.substring(8, 10)));
		cal.set(Calendar.MINUTE, Integer.parseInt(dateValue.substring(10, 12)));
		cal.set(Calendar.SECOND, Integer.parseInt(dateValue.substring(12, 14)));

		String retVal = makeFormat(cal, format);

		return retVal;
	}

	/**
	 * 日期增加addNum天数.
	 * 
	 * @param addNum
	 *            增加天数
	 * @param format
	 *            格式
	 * @return 增加后的日期
	 */
	public static final long add2(int addNum, String format) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, addNum - 1);

		DateFormat df = new SimpleDateFormat(format);
		long date = Long.parseLong(df.format(cal.getTime()));
		return date;
	}

	public static final String dateTimeFormat(String format, Date date) {
		Calendar cal = Calendar.getInstance();
		date.getTime();
		cal.setTime(date);
		String dateFormat = makeFormat(cal, format);
		return dateFormat;
	}

	public static String getTimeSlot(int simeSlot) {
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH) + 1;
		int year = calendar.get(Calendar.YEAR);
		if (7 == simeSlot) {
			if (day < simeSlot) {
				month = month - 1;
				calendar.set(Calendar.MONTH, month);
				day = calendar.getActualMaximum(Calendar.DATE) + 1 + day;
			}
			day = day - simeSlot + 1;
			if (month < 0) {
				year = year - 1;
				month = month + 12;
			}
		} else if (30 == simeSlot) {
			if (day < simeSlot) {
				month = month - 1;
				calendar.set(Calendar.MONTH, month);
				day = calendar.getActualMaximum(Calendar.DATE) + 1 + day;
			}
			day = day - simeSlot + 1;
			if (month < 0) {
				year = year - 1;
				month = month + 12;
			}

		}
		String date = "";
		if (month < 10) {
			if (day < 10) {
				date = year + "-0" + month + "-0" + day;
			} else {
				date = year + "-0" + month + "-" + day;
			}
		} else {
			if (day < 10) {
				date = year + "-" + month + "-0" + day;
			} else {
				date = year + "-" + month + "-" + day;
			}
		}
		return date;
	}
	/*从long型时间获取出秒*/
	public static String mSecondsToseconds(long seconds) {
		seconds = seconds / 1000;
		String second = patchZero((seconds % 3600) % 60);
		return second;
	}
	/*从long型时间获取出分钟*/
	public static String mSecondsToMinute(long seconds) {
		seconds = seconds / 1000;
		String minute = patchZero((seconds % 3600) / 60);
		return minute;
	}
	/*从long型时间获取出小时*/
	public static String mSecondsToHour(long seconds) {
		seconds = seconds / 1000;//把毫秒转化成秒
		String hour = patchZero((seconds / 3600) % 24);
		return hour;
	}
	/*从long型时间获取出天*/
	public static String mSecondsToDay(long seconds) {
		seconds = seconds / 1000;//把毫秒转化成秒
		String day = patchZero((seconds / 3600) / 24);
		return day;
	}

	public static String patchZero(long d) {
		if (d == 0)
			return "00";
		else if (d > 0 && d <= 9) {
			return "0" + d;
		} else {
			return String.valueOf(d);
		}
	}
	
	public static long timeStrToLong(String timeStr) {
		SimpleDateFormat format = new SimpleDateFormat(
				DateUtils.FORMAT_YYYYMMDDHHSS2);
		Date date;
		try {
			date = format.parse(timeStr);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	public static String getStandardTime(long milliSecond) {
		SimpleDateFormat mDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		if (milliSecond != -1) {
			return mDateFormat.format(milliSecond);
		}
		return "";
	}
	
	public static long getLongCurrentTime() {
		long time = System.currentTimeMillis();
		return time;
	}
	
	public static String getStrCurrentTime() {
		long time = System.currentTimeMillis();
		String timeStr = getStandardTime(time);
		return timeStr;
	}
	
	@SuppressWarnings("deprecation")
	public static int longToMonth(String timeStr) {
		int day = 0;
		SimpleDateFormat format = new SimpleDateFormat(
				DateUtils.FORMAT_YYYYMMDDHHSS2);
		Date date;
		try {
			date = format.parse(timeStr);
			day = date.getMonth();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return day;
	}
}
