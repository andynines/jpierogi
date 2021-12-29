package com.andrewsenin.pierogi.ast;

public class FunctionExpression extends LineNumbered implements Expression {

	protected final java.util.List<String> parameters;
	protected final java.util.List<Expression> definition;

	public FunctionExpression(java.util.List<String> parameters, java.util.List<Expression> definition) {
		this(parameters, definition, 0);
	}

	public FunctionExpression(java.util.List<String> parameters, java.util.List<Expression> definition, int lineNumber) {
		super(lineNumber);
		this.parameters = parameters;
		this.definition = definition;
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof FunctionExpression)) {
			return false;
		}
		FunctionExpression other = (FunctionExpression) object;
		return parameters.equals(other.parameters) && definition.equals(other.definition);
	}

	public java.util.List<String> getParameters() {
		return parameters;
	}

	public java.util.List<Expression> getDefinition() {
		return definition;
	}
}