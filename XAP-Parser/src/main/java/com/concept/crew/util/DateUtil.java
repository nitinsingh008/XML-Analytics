package com.concept.crew.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;



/**
 * 
 * This has a thread safe <i>format</i> and <i>parse</i> methods
 * 
 */
public enum DateUtil {

	/**
	 * Format: ddMMyyyy<br>
	 * E.g.: 12312013
	 */
	DDMMYYYY("ddMMyyyy"),

	/**
	 * Format: dd-MMM-yyyy<br>
	 * E.g.: 31-DEC-2013
	 */
	DD_MMM_YYYY("dd-MMM-yyyy"),

	/**
	 * This formatter is set to UTC time zone.
	 * 
	 * Format: dd-MMM-yyyy<br>
	 * E.g.: 31-DEC-2013
	 */
	DD_MMM_YYYY_UTC(DD_MMM_YYYY.toPattern(), "UTC"),

	/**
	 * Format: dd-MMM-yyyy hh:mm:ss a<br>
	 * E.g.: 31-DEC-2013 11:00:00 AM
	 */
	DD_MMM_YYYY_HH_MM_SS_AP("dd-MMM-yyyy hh:mm:ss a"),

	/**
	 * Format: dd-MMM-yyyy hh:mm:ss a<br>
	 * E.g.: 31-DEC-2013 11:00:00 AM
	 */
	DD_MMM_YYYY_HH12_MM_SS("dd-MMM-yyyy hh:mm:ss a"),

	/**
	 * Format: dd-MMM-yyyy HH:mm:ss<br>
	 * E.g.: 31-DEC-2013 23:00:00
	 */
	DD_MMM_YYYY_HH24_MM_SS("dd-MMM-yyyy HH:mm:ss"),

	/**
	 * This formatter is set to UTC time zone.
	 * 
	 * Format: dd-MMM-yyyy HH:mm:ss<br>
	 * E.g.: 31-DEC-2013 23:00:00
	 */
	DD_MMM_YYYY_HH24_MM_SS_UTC(DD_MMM_YYYY_HH24_MM_SS.toPattern(), "UTC"),

	/**
	 * Format: MM/dd/yy<br>
	 * E.g.: 12/31/2013
	 */
	MMDDYY("MM/dd/yy"),

	/**
	 * Format: MM/dd/yyyy<br>
	 * E.g.: 12/31/2013
	 */
	MMDDYYYY("MM/dd/yyyy"),

	/**
	 * Format: MM-dd-yyyy<br>
	 * E.g.: 12-31-2013
	 */
	MM_DD_YYYY("MM-dd-yyyy"),

	/**
	 * Format: MM/dd/yyyy hh:mm:ss a<br>
	 * E.g.: 12/31/2013 11:00:00 AM
	 */
	MMDDYYYY_HH_MM_SS_AP("MM/dd/yyyy hh:mm:ss a"),

	/**
	 * Format: MM/dd/yyyy HH:mm:ss<br>
	 * E.g.: 12/31/2013 23:00:00
	 */
	MMDDYYYY_HH24_MM_SS("MM/dd/yyyy HH:mm:ss"),

	/**
	 * Format: yyyyMMdd<br>
	 * E.g.: 20131231
	 */
	YYYYMMDD("yyyyMMdd"),

	/**
	 * Format: yyyy-MM-dd<br>
	 * E.g.: 2013-12-31
	 */
	YYYY_MM_DD("yyyy-MM-dd"),

	/**
	 * Format: yyyy/MM/dd<br>
	 * E.g.: 2013/12/31
	 */
	YYYYMMDD_SLASH("yyyy/MM/dd"),

	/**
	 * Format: HH:mm:ss<br>
	 * E.g.: 23:00:00
	 */
	TIME_24HRS("HH:mm:ss"),

	/**
	 * Format: hh:mm:ss a<br>
	 * E.g.: 12:00:00 pm
	 */
	TIME_12HRS("hh:mm:ssa");

	/**
	 * Perpetual date constant: 12/31/2099
	 */
	public static final Date PERPETUAL_DATE = getPerpetualDate();

	/**
	 * Perpetual time constant: Number of milliseconds from January 1, 1970,
	 * 00:00:00 GMT to December 31, 2099 00:00:00 .
	 */
	public static final Long PERPETUAL_TIME = PERPETUAL_DATE.getTime();

	// Date comparator constants
	private static final DateOnlyComparator DATE_ONLY_COMPARATOR = new DateOnlyComparator();
	private static final DateTimeComparator DATE_TIME_COMPARATOR = new DateTimeComparator();

	/**
	 * Instance attributes
	 */
	private final ReentrantLock formatterLock = new ReentrantLock();
	private final ReentrantLock parserLock = new ReentrantLock();

	private final String pattern;
	private final TimeZone timeZone;
	private final SimpleDateFormat simpleDateFormatter;
	private final SimpleDateFormat simpleDateParser;

	private DateUtil(String pattern) {
		this(pattern, TimeZone.getDefault());
	}

	private DateUtil(String pattern, String ID) {
		this(pattern, TimeZone.getTimeZone(ID));
	}

	private DateUtil(String pattern, TimeZone timeZone) {
		this.pattern = pattern;
		this.timeZone = timeZone;
		this.simpleDateFormatter = createSimpleDateFormat();
		this.simpleDateParser = createSimpleDateFormat();
	}

	/**
	 * Parse a date pattern to create a new {@code java.util.Date} instance.
	 * 
	 * @param date
	 *            - A {@code String} pattern for date
	 * @return {@code java.util.Date}
	 * @throws ParseException
	 */
	public Date parse(String date) throws ParseException {
		parserLock.lock();
		try {
			return simpleDateParser.parse(date);
		} finally {
			parserLock.unlock();
		}
	}

	/**
	 * Formats a {@code java.util.Date} to desired {@code String} pattern.
	 * 
	 * @param date
	 *            - A {@code java.util.Date}
	 * @return formatted {@code String}
	 */
	public String format(Date date) {
		formatterLock.lock();
		try {
			return simpleDateFormatter.format(date);
		} finally {
			formatterLock.unlock();
		}
	}

	public String toPattern() {
		return pattern;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	/**
	 * Gives the current system time-stamp.
	 * 
	 * @return {@code java.sql.Timestamp}
	 */
	public static Timestamp getCurrentTimestamp() {
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		return currentTime;
	}

	/**
	 * Gives the current system time-stamp in the provided {@code SimpleDateFormat}. <i>Caller must ensure SimpleDateFormat passed
	 * in this method is thread safe</i>.
	 * 
	 * @param simpleDateFormat
	 *            - A {@code SimpleDateFormat}
	 * @return formatted {@code String}
	 */
	public static String getCurrentTimestamp(SimpleDateFormat simpleDateFormat) {
		return simpleDateFormat.format(getCurrentTimestamp());
	}

	/**
	 * Gives the current system time-stamp in the provided pre-defined {@code DateUtil} enum format.
	 * 
	 * @param format
	 *            - A {@code DateUtil}
	 * @return formatted {@code String}
	 */
	public static String getCurrentTimestamp(DateUtil format) {
		return format.format(getCurrentTimestamp());
	}

	/**
	 * Gives the current system date.
	 * 
	 * @return {@code java.util.Date}
	 */
	public static java.util.Date getCurrentDate() {
		java.util.Date currentDate = new java.util.Date();
		return currentDate;
	}

	/**
	 * Gives the current system date in the provided {@code SimpleDateFormat}.
	 * Caller must ensure SimpleDateFormat passed in this method is thread safe.
	 * 
	 * @param simpleDateFormat
	 *            - A {@code SimpleDateFormat}
	 * @return formatted {@code String}
	 */
	public static String getCurrentDate(SimpleDateFormat simpleDateFormat) {
		return simpleDateFormat.format(getCurrentDate());
	}

	/**
	 * Gives the current system date in the provided pre-defined {@code DateUtil} enum format.
	 * 
	 * @param format
	 *            - A {@code DateUtil}
	 * @return formatted {@code String}
	 */
	public static String getCurrentDate(DateUtil format) {
		return format.format(getCurrentDate());
	}

	/**
	 * Gives the current system sql-date.
	 * 
	 * @return {@code java.sql.Date}
	 */
	public static java.sql.Date getCurrentSqlDate() {
		java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
		return currentDate;
	}

	/**
	 * Gives the current system sql-date in the provided pre-defined {@code SimpleDateFormat}. <i>Caller must ensure SimpleDateFormat passed
	 * in this method is thread safe</i>.
	 * 
	 * @param simpleDateFormat
	 *            - A {@code SimpleDateFormat}
	 * @return formatted {@code String}
	 */
	public static String getCurrentSqlDate(SimpleDateFormat simpleDateFormat) {
		return simpleDateFormat.format(getCurrentSqlDate());
	}

	/**
	 * Gives the current system sql-date in the provided pre-defined {@code DateUtil} enum format.
	 * 
	 * @param format
	 *            - A {@code DateUtil}
	 * @return formatted {@code String}
	 */
	public static String getCurrentSqlDate(DateUtil format) {
		return format.format(getCurrentSqlDate());
	}

	/**
	 * Compares two date dis-regarding the time.
	 * 
	 * @param date1
	 *            - A {@code java.util.Date}
	 * @param date2
	 *            - {@code java.util.Date}
	 * @return {@code Integer} difference
	 */
	public static Integer compareDatesWithoutTime(Date date1, Date date2) {
		return DATE_ONLY_COMPARATOR.compare(date1, date2);
	}

	/**
	 * Compares two date including the time.
	 * 
	 * @param date1
	 *            - A {@code java.util.Date}
	 * @param date2
	 *            - {@code java.util.Date}
	 * @return {@code Integer} difference
	 */
	public static Integer compareDates(Date date1, Date date2) {
		return DATE_TIME_COMPARATOR.compare(date1, date2);
	}

	/**
	 * Checks if the given date is in the range (inclusive of start and end
	 * dates).
	 * 
	 * @param date
	 *            - A {@code java.util.Date}
	 * @param start
	 *            - A {@code java.util.Date}
	 * @param end
	 *            - {@code java.util.Date}
	 * @return {@code Boolean}
	 */
	public static Boolean dateBetweenRangeInclusive(Date date, Date start, Date end) {
		Boolean afterStart = DATE_ONLY_COMPARATOR.compare(date, start) >= 0;
		Boolean beforeEnd = DATE_ONLY_COMPARATOR.compare(date, end) <= 0;
		return afterStart && beforeEnd;
	}

	/**
	 * Checks if the given date is in the range (excluding start and end dates).
	 * 
	 * @param date
	 *            - A {@code java.util.Date}
	 * @param start
	 *            - A {@code java.util.Date}
	 * @param end
	 *            - {@code java.util.Date}
	 * @return {@code Boolean}
	 */
	public static Boolean dateBetweenRange(Date date, Date start, Date end) {
		Boolean afterStart = DATE_ONLY_COMPARATOR.compare(date, start) > 0;
		Boolean beforeEnd = DATE_ONLY_COMPARATOR.compare(date, end) < 0;
		return afterStart && beforeEnd;
	}

	/**
	 * This method takes 2 dates and computes the difference in milliseconds, in
	 * doing so <i>ignores Time and Time Zone</i> information.
	 * 
	 * @param date1
	 *            - A {@code java.util.Date}
	 * @param date2
	 *            - A {@code java.util.Date}
	 * 
	 * @return the period in milliseconds
	 */
	public static long dateDifferenceInMilliseconds(Date date1, Date date2) {
		long difference = Math.abs(toUTCDate(date1).getTime() - toUTCDate(date2).getTime());
		return difference;
	}

	/**
	 * This method takes 2 dates and computes the difference in seconds, in
	 * doing so <i>ignores Time and Time Zone</i> information.
	 * 
	 * @param date1
	 *            - A {@code java.util.Date}
	 * @param date2
	 *            - A {@code java.util.Date}
	 * 
	 * @return the period in seconds
	 */
	public static long dateDifferenceInSeconds(Date date1, Date date2) {
		return TimeUnit.MILLISECONDS.toSeconds(dateDifferenceInMilliseconds(date1, date2));
	}

	/**
	 * This method takes 2 dates and computes the difference in minutes, in
	 * doing so <i>ignores Time and Time Zone</i> information.
	 * 
	 * @param date1
	 *            - A {@code java.util.Date}
	 * @param date2
	 *            - A {@code java.util.Date}
	 * 
	 * @return the period in minutes
	 */
	public static long dateDifferenceInMinutes(Date date1, Date date2) {
		return TimeUnit.MILLISECONDS.toMinutes(dateDifferenceInMilliseconds(date1, date2));
	}

	/**
	 * This method takes 2 dates and computes the difference in hours, in doing
	 * so <i>ignores Time and Time Zone</i> information.
	 * 
	 * @param date1
	 *            - A {@code java.util.Date}
	 * @param date2
	 *            - A {@code java.util.Date}
	 * 
	 * @return the period in hours
	 */
	public static long dateDifferenceInHours(Date date1, Date date2) {
		return TimeUnit.MILLISECONDS.toHours(dateDifferenceInMilliseconds(date1, date2));
	}

	/**
	 * This method takes 2 dates and computes the difference in days, in doing
	 * so <i>ignores Time and Time Zone</i> information.
	 * 
	 * @param date1
	 *            - A {@code java.util.Date}
	 * @param date2
	 *            - A {@code java.util.Date}
	 * 
	 * @return the period in days
	 */
	public static long dateDifferenceInDays(Date date1, Date date2) {
		return TimeUnit.MILLISECONDS.toDays(dateDifferenceInMilliseconds(date1, date2));
	}

	/**
	 * This method takes 2 dates and computes the difference in milliseconds, in
	 * doing so <i>ignores Time Zone</i> information.
	 * 
	 * @param date1
	 *            - A {@code java.util.Date}
	 * @param date2
	 *            - A {@code java.util.Date}
	 * 
	 * @return the period in milliseconds
	 */
	public static long timeDifferenceInMilliseconds(Date date1, Date date2) {
		long difference = Math.abs(toUTCTime(date1).getTime() - toUTCTime(date2).getTime());
		return difference;
	}

	/**
	 * This method takes 2 dates and computes the difference in seconds, in
	 * doing so <i>ignores Time Zone</i> information.
	 * 
	 * @param date1
	 *            - A {@code java.util.Date}
	 * @param date2
	 *            - A {@code java.util.Date}
	 * 
	 * @return the period in seconds
	 */
	public static long timeDifferenceInSeconds(Date date1, Date date2) {
		return TimeUnit.MILLISECONDS.toSeconds(timeDifferenceInMilliseconds(date1, date2));
	}

	/**
	 * This method takes 2 dates and computes the difference in minutes, in
	 * doing so <i>ignores Time Zone</i> information.
	 * 
	 * @param date1
	 *            - A {@code java.util.Date}
	 * @param date2
	 *            - A {@code java.util.Date}
	 * 
	 * @return the period in minutes
	 */
	public static long timeDifferenceInMinutes(Date date1, Date date2) {
		return TimeUnit.MILLISECONDS.toMinutes(timeDifferenceInMilliseconds(date1, date2));
	}

	/**
	 * This method takes 2 dates and computes the difference in hours, in doing
	 * so <i>ignores Time Zone</i> information.
	 * 
	 * @param date1
	 *            - A {@code java.util.Date}
	 * @param date2
	 *            - A {@code java.util.Date}
	 * 
	 * @return the period in hours
	 */
	public static long timeDifferenceInHours(Date date1, Date date2) {
		return TimeUnit.MILLISECONDS.toHours(timeDifferenceInMilliseconds(date1, date2));
	}

	/**
	 * This method takes 2 dates and computes the difference in days, in doing
	 * so <i>ignores Time Zone</i> information.
	 * 
	 * @param date1
	 *            - A {@code java.util.Date}
	 * @param date2
	 *            - A {@code java.util.Date}
	 * 
	 * @return the period in days
	 */
	public static long timeDifferenceInDays(Date date1, Date date2) {
		return TimeUnit.MILLISECONDS.toDays(timeDifferenceInMilliseconds(date1, date2) + 1000L);
	}

	/**
	 * 
	 * @param date
	 * @return 12 hrs formatted time
	 */
	public static String getTimestamp(Date date) {
		if (GeneralUtil.isNotNull(date)) {
			return TIME_12HRS.format(date);
		}

		return StringUtil.emptyString();
	}

	/**
	 * 
	 * @param date
	 * @param format
	 * @return formatted date
	 */
	public static String convertToString(Date date, String format) {
		if (GeneralUtil.allNotNull(format, date)) {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(date);
		}

		return StringUtil.emptyString();
	}

	/**
	 * 
	 * @param date
	 *            - A {@code java.util.Date}
	 * @return A time stripped {@code java.util.Date}
	 */
	public static Date removeTime(Date date) {
		String stripped = YYYYMMDD.format(date);
		try {
			return YYYYMMDD.parse(stripped);
		} catch (ParseException e) {
			throw new RuntimeException("Parsing date failed", e);
		}
	}

	/**
	 * 
	 * @param date
	 *            - Any {@code java.util.Date}
	 * @return An UTC {@code java.util.Date}
	 */
	public static Date toUTCDate(Date date) {
		String stripped = DD_MMM_YYYY.format(date);
		return toUTCDate(stripped);
	}

	/**
	 * 
	 * @param date
	 *            - Any {@code java.util.Date}
	 * @return An UTC {@code java.util.Date}
	 */
	public static Date toUTCTime(Date date) {
		String stripped = DD_MMM_YYYY_HH24_MM_SS.format(date);
		return toUTCTime(stripped);
	}

	/**
	 * 
	 * @param dd_mmm_yyyy
	 *            - An input {@code String} of "<i>dd-MMM-yyyy</i>" format.<br>
	 *            E.g.: 31-DEC-2013<br>
	 * @return An UTC {@code java.util.Date}
	 */
	public static Date toUTCDate(String dd_mmm_yyyy) {
		try {
			return DD_MMM_YYYY_UTC.parse(dd_mmm_yyyy);
		} catch (ParseException e) {
			throw new RuntimeException("Parsing date failed", e);
		}
	}

	/**
	 * 
	 * @param dd_mmm_yyyy_hh24_mm_ss
	 *            - An input {@code String} of "<i>dd-MMM-yyyy HH:mm:ss</i>"
	 *            format.<br>
	 *            E.g.: 31-DEC-2013 23:00:00<br>
	 * @return An UTC {@code java.util.Date}
	 */
	public static Date toUTCTime(String dd_mmm_yyyy_hh24_mm_ss) {
		try {
			return DD_MMM_YYYY_HH24_MM_SS_UTC.parse(dd_mmm_yyyy_hh24_mm_ss);
		} catch (ParseException e) {
			throw new RuntimeException("Parsing date failed", e);
		}
	}

	/**
	 * Helper
	 * 
	 * @return
	 */
	private static Date getPerpetualDate() {
		try {
			return MMDDYYYY.parse("12/31/2099");
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @return
	 */
	public SimpleDateFormat createSimpleDateFormat() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		simpleDateFormat.setTimeZone(timeZone);
		return simpleDateFormat;
	}

	public static enum DB {
		ORACLE("SELECT SYSDDATE FROM DUAL", "SELECT SYSTIMESTAMP FROM DUAL");

		private final String sysDateSQL;
		private final String sysTimestampSQL;

		private DB(String sysDateSQL, String sysTimestampSQL) {
			this.sysDateSQL = sysDateSQL;
			this.sysTimestampSQL = sysTimestampSQL;
		}

		public Date getCurrentDate(final Connection dbConnection) throws SQLException {
			PreparedStatement prepareStatement = null;
			ResultSet resultSet = null;
			try {
				prepareStatement = dbConnection.prepareStatement(sysDateSQL);
				resultSet = prepareStatement.executeQuery();
				resultSet.next();
				return resultSet.getDate(1);
			} finally {
				ResultSetHelper.close(resultSet);
				StatementHelper.close(prepareStatement);
				ConnectionHelper.close(dbConnection);
			}
		}

		public Timestamp getCurrentTimestamp(final Connection dbConnection) throws SQLException {
			PreparedStatement prepareStatement = null;
			ResultSet resultSet = null;
			try {
				prepareStatement = dbConnection.prepareStatement(sysTimestampSQL);
				resultSet = prepareStatement.executeQuery();
				resultSet.next();
				return resultSet.getTimestamp(1);
			} finally {
				ResultSetHelper.close(resultSet);
				StatementHelper.close(prepareStatement);
				ConnectionHelper.close(dbConnection);
			}
		}
	}
	
	public static final class DateComparator implements Comparator<Date> {

		@Override
		public int compare(Date date1, Date date2) {
			if (GeneralUtil.allNotNull(date1, date2)) {
				return DateUtil.compareDatesWithoutTime(date1, date2);
			}

			return GeneralUtil.isNull(date1) ? 1 : -1;
		}
	}
	
	public static final class DateTransformer implements Transformer<Date, String> {
		@Override
		public String tansform(Date value) {
			if (GeneralUtil.isNull(value))
				return StringUtil.emptyString();

			return DateUtil.YYYY_MM_DD.format(value);
		}
	}



	@Override
	public String toString() {
		return super.toString() + "[" + pattern + ", " + timeZone.getDisplayName() + "]";
	}
}
