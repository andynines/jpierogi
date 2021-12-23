package com.andrewsenin.pierogi.ast;

public abstract class Unary extends LineNumbered {

	protected final Expression inside;

	public Unary(Expression inside) {
		this(inside, 0);
	}

	public Unary(Expression inside, int lineNumber) {
		super(lineNumber);
		this.inside = inside;
	}

	public Expression getInside() {
		return inside;
	}
}