package com.andrewsenin.pierogi.ast;

public abstract class Binary extends LineNumbered {

	protected final Expression left;
	protected final Expression right;

	public Binary(Expression left, Expression right) {
		this(left, right, 0);
	}

	public Binary(Expression left, Expression right, int lineNumber) {
		super(lineNumber);
		this.left = left;
		this.right = right;
	}

	public Expression getLeft() {
		return left;
	}

	public Expression getRight() {
		return right;
	}
}