package com.andrewsenin.pierogi.ast;

public class LessThanExpression extends Binary implements Expression {

	public LessThanExpression(Expression left, Expression right) {
		this(left, right, 0);
	}

	public LessThanExpression(Expression left, Expression right, int lineNumber) {
		super(left, right, lineNumber);
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof LessThanExpression)) {
			return false;
		}
		LessThanExpression other = (LessThanExpression) object;
		return left.equals(other.left) && right.equals(other.right);
	}
}