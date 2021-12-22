package com.andrewsenin.pierogi.ast;

public class ListExpression extends Expression {

	private final java.util.List<Expression> contents;

	public ListExpression(java.util.List<Expression> contents) {
		this(contents, 0);
	}

	public ListExpression(java.util.List<Expression> contents, int lineNumber) {
		super(lineNumber);
		this.contents = contents;
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Expression expression) {
		if (!(expression instanceof ListExpression)) {
			return false;
		}
		ListExpression other = (ListExpression) expression;
		return contents.equals(other.contents);
	}

	public java.util.List<Expression> getContents() {
		return contents;
	}

}
