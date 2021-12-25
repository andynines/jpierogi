package com.andrewsenin.pierogi.ast;

public class NotExpression extends Unary implements Expression {

	public NotExpression(Expression inside) {
		this(inside, 0);
	}

	public NotExpression(Expression inside, int lineNumber) {
		super(inside, lineNumber);
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof NotExpression)) {
			return false;
		}
		NotExpression other = (NotExpression) object;
		return inside.equals(other.inside);
	}
}