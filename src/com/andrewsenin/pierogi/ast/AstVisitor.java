package com.andrewsenin.pierogi.ast;

public abstract class AstVisitor<T> {
	public abstract T visit(NilExpression nilExpression);
	public abstract T visit(TrueExpression trueExpression);
	public abstract T visit(FalseExpression falseExpression);
	public abstract T visit(NumberExpression numberExpression);
	public abstract T visit(StringExpression stringExpression);
	public abstract T visit(ListExpression listExpression);
	public abstract T visit(IdentifierExpression identifierExpression);
	public abstract T visit(DefinitionExpression definitionExpression);
	public abstract T visit(GroupExpression groupExpression);
	public abstract T visit(NegationExpression negationExpression);
	public abstract T visit(NotExpression notExpression);
	public abstract T visit(AdditionExpression additionExpression);
	public abstract T visit(SubtractionExpression subtractionExpression);
	public abstract T visit(MultiplicationExpression multiplicationExpression);
	public abstract T visit(DivisionExpression divisionExpression);
	public abstract T visit(ExponentExpression exponentExpression);
	public abstract T visit(LessThanExpression lessThanExpression);
	public abstract T visit(GreaterThanExpression greaterThanExpression);
	public abstract T visit(LessEqualExpression lessEqualExpression);
	public abstract T visit(GreaterEqualExpression greaterEqualExpression);
	public abstract T visit(EqualsExpression equalsExpression);
	public abstract T visit(NotEqualExpression notEqualExpression);
	public abstract T visit(AndExpression andExpression);
	public abstract T visit(OrExpression orExpression);
	public abstract T visit(ConcatenationExpression concatenationExpression);
	public abstract T visit(ConsExpression consExpression);
}