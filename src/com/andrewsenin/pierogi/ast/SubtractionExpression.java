package com.andrewsenin.pierogi.ast;

public class SubtractionExpression extends Binary implements Expression {

	public SubtractionExpression(Expression left, Expression right) {
		this(left, right, 0);
	}

	public SubtractionExpression(Expression left, Expression right, int lineNumber) {
		super(left, right, lineNumber);
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof SubtractionExpression)) {
			return false;
		}
		SubtractionExpression other = (SubtractionExpression) object;
		return left.equals(other.left) && right.equals(other.right);
	}
}