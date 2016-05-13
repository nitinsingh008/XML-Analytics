package com.concept.crew.util;

import java.io.Serializable;


public final class Pair<L, R> implements Named, Serializable {

	private static final long serialVersionUID = 1L;

	private final String name;
	private final L left;
	private final R right;

	public Pair(L left, R right) {
		this("Pair", left, right);
	}

	public Pair(String name, L left, R right) {
		this.name = name;
		this.left = left;
		this.right = right;
	}

	@Override
	public String getName() {
		return name;
	}

	public L getLeft() {
		return left;
	}

	public R getRight() {
		return right;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair<?, ?> other = (Pair<?, ?>) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "{" + name + "=[" + left + ", " + right + "]}";
	}
}
