package com.andrewsenin.pierogi.ast;

public class TrueExpression extends Expression {

	

	public TrueExpression() {
		this(0);
	}

	public TrueExpression(int lineNumber) {
		super(lineNumber);
		
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Expression expression) {
		if (!(expression instanceof TrueExpression)) {
			return false;
		}
		TrueExpression other = (TrueExpression) expression;
		return true;
	}


}
