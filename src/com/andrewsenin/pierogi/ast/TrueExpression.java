package com.andrewsenin.pierogi.ast;

public class TrueExpression extends LineNumbered implements Expression {



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
	public boolean equals(Object object) {
		if (!(object instanceof TrueExpression)) {
			return false;
		}
		TrueExpression other = (TrueExpression) object;
		return true;
	}


}