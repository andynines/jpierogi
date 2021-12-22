package com.andrewsenin.pierogi.ast;

public class GroupExpression extends Expression {

	private final Expression inside;

	public GroupExpression(Expression inside) {
		this(inside, 0);
	}

	public GroupExpression(Expression inside, int lineNumber) {
		super(lineNumber);
		this.inside = inside;
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

	public Expression getInside() {
		return inside;
	}

}
