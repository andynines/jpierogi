package com.andrewsenin.pierogi.ast;

public class OrExpression extends Expression {

	private final Expression left;
	private final Expression right;

	public OrExpression(Expression left, Expression right) {
		this(left, right, 0);
	}

	public OrExpression(Expression left, Expression right, int lineNumber) {
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
		if (!(expression instanceof OrExpression)) {
			return false;
		}
		OrExpression other = (OrExpression) expression;
		return left.equals(other.left) && right.equals(other.right);
	}

	public Expression getLeft() {
		return left;
	}

	public Expression getRight() {
		return right;
	}

}
