package com.andrewsenin.pierogi.ast;

public class OrExpression extends Binary implements Expression {

	public OrExpression(Expression left, Expression right) {
		this(left, right, 0);
	}

	public OrExpression(Expression left, Expression right, int lineNumber) {
		super(left, right, lineNumber);
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof OrExpression)) {
			return false;
		}
		OrExpression other = (OrExpression) object;
		return left.equals(other.left) && right.equals(other.right);
	}
}