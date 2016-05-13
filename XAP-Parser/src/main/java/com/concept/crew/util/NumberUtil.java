package com.concept.crew.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Random;

import com.concept.crew.util.Transformers.DoubleToString;
import com.concept.crew.util.Transformers.LongToString;
import com.concept.crew.util.Transformers.ObjectToBigDecimal;
import com.concept.crew.util.Transformers.ObjectToDouble;
import com.concept.crew.util.Transformers.ObjectToInteger;
import com.concept.crew.util.Transformers.ObjectToLong;
import com.concept.crew.util.Transformers.ObjectToShort;
import com.concept.crew.util.Transformers.ObjectToString;





public final class NumberUtil {

	public static final MathContext PRECISION_SIX = new MathContext(6);

	public static final Random RANDOM = new Random();

	// rounding off to 8th decimal place
	public static final Double DECIMAL_ROUND_OFF_POINT = Math.pow(10, 8);

	public static <T extends Number> Boolean isNegative(T number) {
		return ((number.doubleValue() * Double.POSITIVE_INFINITY) == Double.NEGATIVE_INFINITY);
	}

	public static Boolean isNumeric(String number) {
		Double transformed = new Transformers.ObjectToDouble().tansform(number);
		if (GeneralUtil.isNull(transformed))
			return Boolean.FALSE;

		return Boolean.TRUE;
	}

	public static int randInt(int min, int max) {
		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = RANDOM.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	public static final Long convertToLong(Object input) {
		return new ObjectToLong().tansform(input);
	}

	public static final Double convertToDouble(Object input) {
		return new ObjectToDouble().tansform(input);
	}

	public static final String convertToString(Double input) {
		return new DoubleToString().tansform(input);
	}

	public static final String convertToString(Long input) {
		return new LongToString().tansform(input);
	}

	public static final BigDecimal convertToBigDecimal(Object input) {
		return new ObjectToBigDecimal().tansform(input);
	}

	public static final String convertToString(Object input) {
		return new ObjectToString().tansform(input);
	}

	public static final Short convertToShort(Object input) {
		return new ObjectToShort().tansform(input);
	}

	public static final Integer convertToInteger(Object input) {
		return new ObjectToInteger().tansform(input);
	}
}
