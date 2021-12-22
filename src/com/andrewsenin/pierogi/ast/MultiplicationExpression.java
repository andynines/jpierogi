package com.andrewsenin.pierogi.ast;

public class MultiplicationExpression extends Expression {

	private final Expression left;
	private final Expression right;

	public MultiplicationExpression(Expression left, Expression right) {
		this(left, right, 0);
	}

	public MultiplicationExpression(Expression left, Expression right, int lineNumber) {
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
		if (!(expression instanceof MultiplicationExpression)) {
			return false;
		}
		MultiplicationExpression other = (MultiplicationExpression) expression;
		return left.equals(other.left) && right.equals(other.right);
	}

	public Expression getLeft() {
		return left;
	}

	public Expression getRight() {
		return right;
	}

}
