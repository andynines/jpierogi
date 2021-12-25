package com.andrewsenin.pierogi.ast;

public class ExponentExpression extends Binary implements Expression {

	public ExponentExpression(Expression left, Expression right) {
		this(left, right, 0);
	}

	public ExponentExpression(Expression left, Expression right, int lineNumber) {
		super(left, right, lineNumber);
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ExponentExpression)) {
			return false;
		}
		ExponentExpression other = (ExponentExpression) object;
		return left.equals(other.left) && right.equals(other.right);
	}
}