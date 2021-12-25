package com.andrewsenin.pierogi.ast;

public class IfExpression extends LineNumbered implements Expression {

	protected final Expression condition;
	protected final java.util.List<Expression> consequent;
	protected final java.util.List<Expression> alternative;

	public IfExpression(Expression condition, java.util.List<Expression> consequent, java.util.List<Expression> alternative) {
		this(condition, consequent, alternative, 0);
	}

	public IfExpression(Expression condition, java.util.List<Expression> consequent, java.util.List<Expression> alternative, int lineNumber) {
		super(lineNumber);
		this.condition = condition;
		this.consequent = consequent;
		this.alternative = alternative;
	}

	@Override
	public <T> T accept(AstVisitor<T> astVisitor) {
		return astVisitor.visit(this);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof IfExpression)) {
			return false;
		}
		IfExpression other = (IfExpression) object;
		return condition.equals(other.condition) && consequent.equals(other.consequent) && alternative.equals(other.alternative);
	}

	public Expression getCondition() {
		return condition;
	}

	public java.util.List<Expression> getConsequent() {
		return consequent;
	}

	public java.util.List<Expression> getAlternative() {
		return alternative;
	}
}