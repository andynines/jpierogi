package com.andrewsenin.pierogi.ast;

public class DefinitionExpression extends Expression {

	private final String symbol;
	private final Expression value;

	public DefinitionExpression(String symbol, Expression value) {
		this(symbol, value, 0);
	}

	public DefinitionExpression(String symbol, Expression value, int lineNumber) {
		super(lineNumber);
		this.symbol = symbol;
		this.value = value;
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Expression expression) {
		if (!(expression instanceof DefinitionExpression)) {
			return false;
		}
		DefinitionExpression other = (DefinitionExpression) expression;
		return symbol.equals(other.symbol) && value.equals(other.value);
	}

	public String getSymbol() {
		return symbol;
	}

	public Expression getValue() {
		return value;
	}

}
