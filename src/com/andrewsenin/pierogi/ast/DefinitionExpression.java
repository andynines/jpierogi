package com.andrewsenin.pierogi.ast;

public class DefinitionExpression extends LineNumbered implements Expression {

	protected final String symbol;
	protected final Expression definition;

	public DefinitionExpression(String symbol, Expression definition) {
		this(symbol, definition, 0);
	}

	public DefinitionExpression(String symbol, Expression definition, int lineNumber) {
		super(lineNumber);
		this.symbol = symbol;
		this.definition = definition;
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
		return symbol.equals(other.symbol) && definition.equals(other.definition);
	}

	public String getSymbol() {
		return symbol;
	}

	public Expression getDefinition() {
		return definition;
	}
}