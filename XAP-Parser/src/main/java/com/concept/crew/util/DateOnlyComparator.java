package com.concept.crew.util;


import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;


public final class DateOnlyComparator implements Comparator<Date> {

	@Override
	public int compare(final Date date1, final Date date2) {
		Integer days1 = Integer.parseInt(DateUtil.YYYYMMDD.format(date1));
		Integer days2 = Integer.parseInt(DateUtil.YYYYMMDD.format(date2));
		return days1.compareTo(days2);
	}

	public static void main(String[] args) throws ParseException {
		Date today = new Date();
		Date before = DateUtil.MMDDYYYY.parse("01/01/2013");
		Date after = DateUtil.MMDDYYYY.parse("12/31/2099");

		DateOnlyComparator dateOnlyComparator = new DateOnlyComparator();

		System.out.println(dateOnlyComparator.compare(today, before));
		System.out.println(dateOnlyComparator.compare(before, today));

		System.out.println(dateOnlyComparator.compare(after, today));
		System.out.println(dateOnlyComparator.compare(today, after));
	}
}
