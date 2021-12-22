package com.andrewsenin.pierogi.ast;

public class ExponentExpression extends Expression {

	private final Expression base;
	private final Expression power;

	public ExponentExpression(Expression base, Expression power) {
		this(base, power, 0);
	}

	public ExponentExpression(Expression base, Expression power, int lineNumber) {
		super(lineNumber);
		this.base = base;
		this.power = power;
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Expression expression) {
		if (!(expression instanceof ExponentExpression)) {
			return false;
		}
		ExponentExpression other = (ExponentExpression) expression;
		return base.equals(other.base) && power.equals(other.power);
	}

	public Expression getBase() {
		return base;
	}

	public Expression getPower() {
		return power;
	}

}
