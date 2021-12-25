package com.andrewsenin.pierogi.ast;

public class AndExpression extends Binary implements Expression {

	public AndExpression(Expression left, Expression right) {
		this(left, right, 0);
	}

	public AndExpression(Expression left, Expression right, int lineNumber) {
		super(left, right, lineNumber);
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof AndExpression)) {
			return false;
		}
		AndExpression other = (AndExpression) object;
		return left.equals(other.left) && right.equals(other.right);
	}
}