package com.andrewsenin.pierogi.ast;

public class FalseExpression extends LineNumbered implements Expression {



	public FalseExpression() {
		this(0);
	}

	public FalseExpression(int lineNumber) {
		super(lineNumber);
		
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof FalseExpression)) {
			return false;
		}
		FalseExpression other = (FalseExpression) object;
		return true;
	}


}