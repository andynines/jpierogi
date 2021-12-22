package com.andrewsenin.pierogi.ast;

public class NegationExpression extends Expression {

	private final Expression inside;

	public NegationExpression(Expression inside) {
		this(inside, 0);
	}

	public NegationExpression(Expression inside, int lineNumber) {
		super(lineNumber);
		this.inside = inside;
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

	public Expression getInside() {
		return inside;
	}

}
