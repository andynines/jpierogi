package com.andrewsenin.pierogi.ast;

public class ConcatenationExpression extends Binary implements Expression {

	public ConcatenationExpression(Expression left, Expression right) {
		this(left, right, 0);
	}

	public ConcatenationExpression(Expression left, Expression right, int lineNumber) {
		super(left, right, lineNumber);
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Expression expression) {
		if (!(expression instanceof ConcatenationExpression)) {
			return false;
		}
		ConcatenationExpression other = (ConcatenationExpression) expression;
		return left.equals(other.left) && right.equals(other.right);
	}
}