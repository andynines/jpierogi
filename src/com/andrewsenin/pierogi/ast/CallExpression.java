package com.andrewsenin.pierogi.ast;

public class CallExpression extends LineNumbered implements Expression {

	protected final String symbol;
	protected final java.util.List<Expression> arguments;

	public CallExpression(String symbol, java.util.List<Expression> arguments) {
		this(symbol, arguments, 0);
	}

	public CallExpression(String symbol, java.util.List<Expression> arguments, int lineNumber) {
		super(lineNumber);
		this.symbol = symbol;
		this.arguments = arguments;
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof CallExpression)) {
			return false;
		}
		CallExpression other = (CallExpression) object;
		return symbol.equals(other.symbol) && arguments.equals(other.arguments);
	}

	public String getSymbol() {
		return symbol;
	}

	public java.util.List<Expression> getArguments() {
		return arguments;
	}
}