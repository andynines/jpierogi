package com.andrewsenin.pierogi.ast;

public class GreaterEqualExpression extends Binary implements Expression {

	public GreaterEqualExpression(Expression left, Expression right) {
		this(left, right, 0);
	}

	public GreaterEqualExpression(Expression left, Expression right, int lineNumber) {
		super(left, right, lineNumber);
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Expression expression) {
		if (!(expression instanceof GreaterEqualExpression)) {
			return false;
		}
		GreaterEqualExpression other = (GreaterEqualExpression) expression;
		return left.equals(other.left) && right.equals(other.right);
	}
}