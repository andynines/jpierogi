package com.andrewsenin.pierogi.ast;

public class CallExpression extends LineNumbered implements Expression {

	protected final Expression callee;
	protected final java.util.List<Expression> arguments;

	public CallExpression(Expression callee, java.util.List<Expression> arguments) {
		this(callee, arguments, 0);
	}

	public CallExpression(Expression callee, java.util.List<Expression> arguments, int lineNumber) {
		super(lineNumber);
		this.callee = callee;
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
		return callee.equals(other.callee) && arguments.equals(other.arguments);
	}

	public Expression getCallee() {
		return callee;
	}

	public java.util.List<Expression> getArguments() {
		return arguments;
	}
}