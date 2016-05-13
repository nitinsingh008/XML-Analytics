package com.concept.crew.util;


import java.util.Comparator;
import java.util.Date;


public final class DateTimeComparator implements Comparator<Date> {

	@Override
	public int compare(final Date date1, final Date date2) {
		Long time1 = DateUtil.toUTCTime(date1).getTime();
		Long time2 = DateUtil.toUTCTime(date2).getTime();
		return time1.compareTo(time2);
	}

}
