package com.andrewsenin.pierogi.ast;

public class GreaterThanExpression extends Expression {

	private final Expression left;
	private final Expression right;

	public GreaterThanExpression(Expression left, Expression right) {
		this(left, right, 0);
	}

	public GreaterThanExpression(Expression left, Expression right, int lineNumber) {
		super(lineNumber);
		this.left = left;
		this.right = right;
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Expression expression) {
		if (!(expression instanceof GreaterThanExpression)) {
			return false;
		}
		GreaterThanExpression other = (GreaterThanExpression) expression;
		return left.equals(other.left) && right.equals(other.right);
	}

	public Expression getLeft() {
		return left;
	}

	public Expression getRight() {
		return right;
	}

}
