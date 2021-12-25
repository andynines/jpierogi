package com.andrewsenin.pierogi.ast;

public class AdditionExpression extends Binary implements Expression {

	public AdditionExpression(Expression left, Expression right) {
		this(left, right, 0);
	}

	public AdditionExpression(Expression left, Expression right, int lineNumber) {
		super(left, right, lineNumber);
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof AdditionExpression)) {
			return false;
		}
		AdditionExpression other = (AdditionExpression) object;
		return left.equals(other.left) && right.equals(other.right);
	}
}