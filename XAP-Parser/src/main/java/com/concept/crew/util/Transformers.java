package com.concept.crew.util;

import java.math.BigDecimal;




public final class Transformers {

	public static final class AsItIsTransformer<T> implements Transformer<T, T> {
		@Override
		public T tansform(T t) {
			return t;
		}
	}

	public static final class ObjectToLong implements NamedTransformer<Object, Long> {

		@Override
		public String getName() {
			return "ObjectToLong";
		}

		@Override
		public Long tansform(Object object) {
			if (GeneralUtil.isNull(object))
				return null;

			try {
				return Long.parseLong(object.toString());
			} catch (NumberFormatException ignore) {
			}

			return null;
		}
	}

	public static final class ObjectToDouble implements NamedTransformer<Object, Double> {

		@Override
		public String getName() {
			return "ObjectToDouble";
		}

		@Override
		public Double tansform(Object object) {
			if (GeneralUtil.isNull(object))
				return null;

			try {
				return Double.parseDouble(object.toString());
			} catch (NumberFormatException ignore) {
			}

			return null;
		}
	}

	public static final class LongToString implements NamedTransformer<Long, String> {

		@Override
		public String getName() {
			return "LongToString";
		}

		@Override
		public String tansform(Long object) {
			if (GeneralUtil.isNull(object))
				return null;

			try {
				return Long.toString(object);
			} catch (NumberFormatException ignore) {
			}

			return null;
		}
	}

	public static final class DoubleToString implements NamedTransformer<Double, String> {

		@Override
		public String getName() {
			return "LongToString";
		}

		@Override
		public String tansform(Double object) {
			if (GeneralUtil.isNull(object))
				return null;

			try {
				return Double.toString(object);
			} catch (NumberFormatException ignore) {
			}

			return null;
		}
	}

	public static final class ObjectToBigDecimal implements NamedTransformer<Object, BigDecimal> {

		@Override
		public String getName() {
			return "ObjectToBigDecimal";
		}

		@Override
		public BigDecimal tansform(Object object) {
			if (GeneralUtil.isNull(object))
				return null;

			try {
				return new BigDecimal(object.toString());
			} catch (NumberFormatException ignore) {
			}

			return null;
		}
	}

	public static final class ObjectToShort implements NamedTransformer<Object, Short> {

		@Override
		public String getName() {
			return "ObjectToShort";
		}

		@Override
		public Short tansform(Object object) {
			if (GeneralUtil.isNull(object))
				return null;

			try {
				return new Short(object.toString());
			} catch (NumberFormatException ignore) {
			}

			return null;
		}
	}

	public static final class ObjectToString implements NamedTransformer<Object, String> {

		@Override
		public String getName() {
			return "ObjectToString";
		}

		@Override
		public String tansform(Object object) {
			if (GeneralUtil.isNull(object)) {
				return null;
			} else {
				return object.toString();
			}
		}

	}

	public static final class ObjectToInteger implements NamedTransformer<Object, Integer> {

		@Override
		public String getName() {
			return "ObjectToInteger";
		}

		@Override
		public Integer tansform(Object object) {
			if (GeneralUtil.isNull(object)) {
				return null;
			} else {
				try {
					return Integer.parseInt(object.toString());
				} catch (Exception e) {

				}
				return null;
			}
		}

	}
}
