package com.andrewsenin.pierogi.ast;

public class MultiplicationExpression extends Binary implements Expression {

	public MultiplicationExpression(Expression left, Expression right) {
		this(left, right, 0);
	}

	public MultiplicationExpression(Expression left, Expression right, int lineNumber) {
		super(left, right, lineNumber);
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof MultiplicationExpression)) {
			return false;
		}
		MultiplicationExpression other = (MultiplicationExpression) object;
		return left.equals(other.left) && right.equals(other.right);
	}
}