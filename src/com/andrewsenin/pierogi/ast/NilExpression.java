package com.andrewsenin.pierogi.ast;

public class NilExpression extends LineNumbered implements Expression {



	public NilExpression() {
		this(0);
	}

	public NilExpression(int lineNumber) {
		super(lineNumber);
		
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof NilExpression)) {
			return false;
		}
		NilExpression other = (NilExpression) object;
		return true;
	}


}