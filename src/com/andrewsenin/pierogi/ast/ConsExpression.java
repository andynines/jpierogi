package com.andrewsenin.pierogi.ast;

public class ConsExpression extends Binary implements Expression {

	public ConsExpression(Expression left, Expression right) {
		this(left, right, 0);
	}

	public ConsExpression(Expression left, Expression right, int lineNumber) {
		super(left, right, lineNumber);
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Expression expression) {
		if (!(expression instanceof ConsExpression)) {
			return false;
		}
		ConsExpression other = (ConsExpression) expression;
		return left.equals(other.left) && right.equals(other.right);
	}
}