package com.andrewsenin.pierogi.ast;

public class EqualsExpression extends Binary implements Expression {

	public EqualsExpression(Expression left, Expression right) {
		this(left, right, 0);
	}

	public EqualsExpression(Expression left, Expression right, int lineNumber) {
		super(left, right, lineNumber);
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof EqualsExpression)) {
			return false;
		}
		EqualsExpression other = (EqualsExpression) object;
		return left.equals(other.left) && right.equals(other.right);
	}
}