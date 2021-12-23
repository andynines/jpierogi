package com.andrewsenin.pierogi.ast;

public class NegationExpression extends Unary implements Expression {

	public NegationExpression(Expression inside) {
		this(inside, 0);
	}

	public NegationExpression(Expression inside, int lineNumber) {
		super(inside, lineNumber);
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Expression expression) {
		if (!(expression instanceof NegationExpression)) {
			return false;
		}
		NegationExpression other = (NegationExpression) expression;
		return inside.equals(other.inside);
	}
}