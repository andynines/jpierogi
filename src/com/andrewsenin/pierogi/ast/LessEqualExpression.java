package com.andrewsenin.pierogi.ast;

public class LessEqualExpression extends Binary implements Expression {

	public LessEqualExpression(Expression left, Expression right) {
		this(left, right, 0);
	}

	public LessEqualExpression(Expression left, Expression right, int lineNumber) {
		super(left, right, lineNumber);
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof LessEqualExpression)) {
			return false;
		}
		LessEqualExpression other = (LessEqualExpression) object;
		return left.equals(other.left) && right.equals(other.right);
	}
}