package com.statistiquescovid.scheduler.utils;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {

	public static Date getEpochDate() {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(1970, 1, 1, 0, 0, 59);
		return calendar.getTime();
	}

	public static Date getBeginingDateOfMonth() {
		Calendar calendar = getCalendarForNow();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		setTimeToBeginningOfDay(calendar);
		Date begining = calendar.getTime();

		return begining;
	}

	public static Date getEndDateOfMonth() {
		Calendar calendar = getCalendarForNow();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		setTimeToEndofDay(calendar);
		Date end = calendar.getTime();

		return end;
	}

	public static Calendar getCalendarForNow() {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(new Date());
		return calendar;
	}

	public static void setTimeToBeginningOfDay(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}

	public static void setTimeToEndofDay(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
	}

	public static String getStringDate(Date date) {
		Format simpleFormat = new SimpleDateFormat("dd/MM/yyyy");
		return date != null ? simpleFormat.format(date) : null;
	}

	public static String formatDate(Date date) {
		Format simpleFormat = new SimpleDateFormat("dd/MM/yyyy");
		return date != null ? simpleFormat.format(date) : "";
	}

	public static String formatDateHeure(Date date) {
		Format simpleFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return date != null ? simpleFormat.format(date) : "";
	}

	public static String getStringDateHeureOrNull(Date date) {
		Format simpleFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return date != null ? simpleFormat.format(date) : null;
	}

	public static Date getFormattedFromDateTime(Date date) {
		if(date == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}

	public static Date getFormattedToDateTime(Date date) {
		if(date == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return cal.getTime();
	}

}
