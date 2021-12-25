package com.andrewsenin.pierogi.ast;

public class DivisionExpression extends Binary implements Expression {

	public DivisionExpression(Expression left, Expression right) {
		this(left, right, 0);
	}

	public DivisionExpression(Expression left, Expression right, int lineNumber) {
		super(left, right, lineNumber);
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof DivisionExpression)) {
			return false;
		}
		DivisionExpression other = (DivisionExpression) object;
		return left.equals(other.left) && right.equals(other.right);
	}
}