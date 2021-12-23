package com.andrewsenin.pierogi.ast;

public class GroupExpression extends Unary implements Expression {

	public GroupExpression(Expression inside) {
		this(inside, 0);
	}

	public GroupExpression(Expression inside, int lineNumber) {
		super(inside, lineNumber);
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Expression expression) {
		if (!(expression instanceof GroupExpression)) {
			return false;
		}
		GroupExpression other = (GroupExpression) expression;
		return inside.equals(other.inside);
	}
}