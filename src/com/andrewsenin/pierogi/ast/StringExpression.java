package com.andrewsenin.pierogi.ast;

public class StringExpression extends Expression {

	private final String value;

	public StringExpression(String value) {
		this(value, 0);
	}

	public StringExpression(String value, int lineNumber) {
		super(lineNumber);
		this.value = value;
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Expression expression) {
		if (!(expression instanceof StringExpression)) {
			return false;
		}
		StringExpression other = (StringExpression) expression;
		return value.equals(other.value);
	}

	public String getValue() {
		return value;
	}

}
