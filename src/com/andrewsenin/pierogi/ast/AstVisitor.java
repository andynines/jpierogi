package com.andrewsenin.pierogi.ast;

public interface AstVisitor<T> {
	T visit(NilExpression nilExpression);
	T visit(TrueExpression trueExpression);
	T visit(FalseExpression falseExpression);
	T visit(NumberExpression numberExpression);
	T visit(StringExpression stringExpression);
	T visit(ListExpression listExpression);
	T visit(CallExpression callExpression);
	T visit(IdentifierExpression identifierExpression);
	T visit(DefinitionExpression definitionExpression);
	T visit(IfExpression ifExpression);
	T visit(FunctionExpression functionExpression);
	T visit(GroupExpression groupExpression);
	T visit(NegationExpression negationExpression);
	T visit(NotExpression notExpression);
	T visit(AdditionExpression additionExpression);
	T visit(SubtractionExpression subtractionExpression);
	T visit(MultiplicationExpression multiplicationExpression);
	T visit(DivisionExpression divisionExpression);
	T visit(ExponentExpression exponentExpression);
	T visit(LessThanExpression lessThanExpression);
	T visit(GreaterThanExpression greaterThanExpression);
	T visit(LessEqualExpression lessEqualExpression);
	T visit(GreaterEqualExpression greaterEqualExpression);
	T visit(EqualsExpression equalsExpression);
	T visit(NotEqualExpression notEqualExpression);
	T visit(AndExpression andExpression);
	T visit(OrExpression orExpression);
	T visit(ConcatenationExpression concatenationExpression);
	T visit(ConsExpression consExpression);
}