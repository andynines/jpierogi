package com.andrewsenin.pierogi.ast;

public class GreaterThanExpression extends Binary implements Expression {

	public GreaterThanExpression(Expression left, Expression right) {
		this(left, right, 0);
	}

	public GreaterThanExpression(Expression left, Expression right, int lineNumber) {
		super(left, right, lineNumber);
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof GreaterThanExpression)) {
			return false;
		}
		GreaterThanExpression other = (GreaterThanExpression) object;
		return left.equals(other.left) && right.equals(other.right);
	}
}