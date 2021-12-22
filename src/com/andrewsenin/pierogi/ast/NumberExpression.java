package com.andrewsenin.pierogi.ast;

public class NumberExpression extends Expression {

	private final Double value;

	public NumberExpression(Double value) {
		this(value, 0);
	}

	public NumberExpression(Double value, int lineNumber) {
		super(lineNumber);
		this.value = value;
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Expression expression) {
		if (!(expression instanceof NumberExpression)) {
			return false;
		}
		NumberExpression other = (NumberExpression) expression;
		return value.equals(other.value);
	}

	public Double getValue() {
		return value;
	}

}
