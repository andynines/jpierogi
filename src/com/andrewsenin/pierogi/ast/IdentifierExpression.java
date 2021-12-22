package com.andrewsenin.pierogi.ast;

public class IdentifierExpression extends Expression {

	private final String symbol;

	public IdentifierExpression(String symbol) {
		this(symbol, 0);
	}

	public IdentifierExpression(String symbol, int lineNumber) {
		super(lineNumber);
		this.symbol = symbol;
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Expression expression) {
		if (!(expression instanceof IdentifierExpression)) {
			return false;
		}
		IdentifierExpression other = (IdentifierExpression) expression;
		return symbol.equals(other.symbol);
	}

	public String getSymbol() {
		return symbol;
	}

}
