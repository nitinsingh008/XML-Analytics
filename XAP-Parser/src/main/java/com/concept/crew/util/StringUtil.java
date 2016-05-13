package com.concept.crew.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.regex.Pattern;


public final class StringUtil {

	private static final String EMPTY_STRING = "";

	public static String emptyString() {
		return EMPTY_STRING;
	}

	public static boolean isEmpty(String in) {
		return (GeneralUtil.isNull(in) || in.isEmpty());
	}

	/**
	 * @deprecated use {@link StringUtil.isNotEmpty(in)} instead.
	 */
	public static boolean notEmpty(String in) {
		return !isEmpty(in);
	}

	public static boolean isNotEmpty(String in) {
		return !isEmpty(in);
	}

	public static boolean isEmpty(StringBuilder in) {
		return (GeneralUtil.isNull(in) || in.length() == 0);
	}

	public static boolean isNotEmpty(StringBuilder in) {
		return !isEmpty(in);
	}

	public static <T> String suffix(String delimiter, T... objects) {
		return join(Arrays.asList(objects), delimiter);
	}

	public static Boolean anyOneIsNull(String... objects) {
		for (String object : objects) {
			if (isEmpty(object))
				return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	public static <T> String join(Iterable<T> iterable, String delimiter) {
		StringBuilder builder = new StringBuilder("");
		for (T t : iterable) {
			builder.append(t);
			builder.append(delimiter);
		}

		int length = builder.length();
		if (length > 0)
			return builder.substring(0, length - delimiter.length());

		return StringUtil.emptyString();
	}

	public static Boolean toBoolean(String flag) {
		if ("Y".equalsIgnoreCase(flag))
			return Boolean.TRUE;
		if ("T".equalsIgnoreCase(flag))
			return Boolean.TRUE;
		if ("ON".equalsIgnoreCase(flag))
			return Boolean.TRUE;
		if ("A".equalsIgnoreCase(flag))
			return Boolean.TRUE;
		if ("true".equalsIgnoreCase(flag))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public static String[] splitFixedWidth(String input, int length) {
		String regEx = "(?s)(?<=\\G.{" + length + "})";
		return input.split(regEx);
	}

	public static String[] split(String input, Set<String> delimiters) {
		return input.split(buildRegularExpression(delimiters), -1);
	}

	public static String toCamelCase(String input, Set<Character> delimiters) {
		if (isEmpty(input)) {
			return emptyString();
		}

		boolean flip = Boolean.FALSE;
		char[] charArray = input.toCharArray();
		StringBuilder camelCaseString = new StringBuilder("" + Character.toUpperCase(charArray[0]));
		for (int position = 1; position < charArray.length; position++) {
			char ch = input.charAt(position);
			if (delimiters.contains(ch)) {
				camelCaseString.append(ch);
				flip = Boolean.TRUE;
			} else if (flip) {
				camelCaseString.append(Character.toUpperCase(ch));
				flip = Boolean.FALSE;
			} else {
				camelCaseString.append(Character.toLowerCase(ch));
			}
		}

		return camelCaseString.toString();
	}

	public static String toCamelCase(String input) {
		return toCamelCase(input, CollectionsUtil.newSet(' ', '_'));
	}

	public static String buildRegularExpression(Set<String> delimiters) {
		StringBuffer regex = new StringBuffer("");
		regex.append("[");
		for (String delimiter : delimiters) {
			regex.append("[");
			regex.append(Pattern.quote(delimiter));
			regex.append("]");
		}
		regex.append("]");

		return regex.toString();
	}
	
	public static final class StringComparator implements Comparator<String> {

		@Override
		public int compare(String string1, String string2) {
			if (StringUtil.isEmpty(string1) && StringUtil.isEmpty(string2))
				return 0;
			
			if (StringUtil.isEmpty(string1)){
				string1 = "N/A";
			}
			
			if (StringUtil.isEmpty(string2)){
				string2 = "N/A";
			}
			
			if (string2.equals(string1))
				return 0;

			return string1.compareTo(string2);
		}
	}
	
	public static final class StringTransformer implements Transformer<String, String> {
		@Override
		public String tansform(String value) {
			if (StringUtil.isEmpty(value))
				return "N/A";

			return value;
		}
	}
}
