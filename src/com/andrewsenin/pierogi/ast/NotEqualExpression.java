package com.andrewsenin.pierogi.ast;

public class NotEqualExpression extends Binary implements Expression {

	public NotEqualExpression(Expression left, Expression right) {
		this(left, right, 0);
	}

	public NotEqualExpression(Expression left, Expression right, int lineNumber) {
		super(left, right, lineNumber);
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof NotEqualExpression)) {
			return false;
		}
		NotEqualExpression other = (NotEqualExpression) object;
		return left.equals(other.left) && right.equals(other.right);
	}
}