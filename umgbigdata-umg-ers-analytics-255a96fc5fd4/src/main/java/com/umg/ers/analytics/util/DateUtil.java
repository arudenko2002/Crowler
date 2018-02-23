package com.umg.ers.analytics.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtil {

	public static String getDateFormate(){
		String DATE_FORMAT = "yyyyMMdd";
	    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	    Calendar c1 = Calendar.getInstance(); // today
	    String dailyDate = sdf.format(c1.getTime());
		return dailyDate;
	}
	
}
