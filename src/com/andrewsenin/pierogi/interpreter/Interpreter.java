package com.andrewsenin.pierogi.interpreter;

import com.andrewsenin.pierogi.ast.*;
import com.andrewsenin.pierogi.ast.NilExpression;
import com.andrewsenin.pierogi.datatypes.*;
import com.andrewsenin.pierogi.io.ErrorType;
import com.andrewsenin.pierogi.io.IoManager;
import com.andrewsenin.pierogi.lexer.Lexer;
import com.andrewsenin.pierogi.lexer.Token;
import com.andrewsenin.pierogi.parser.Parser;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

public class Interpreter extends AstVisitor<NativeType> {

    private final IoManager ioManager;
    private final Environment environment = new Environment();

    public Interpreter(IoManager ioManager) {
        this.ioManager = ioManager;
    }

    // TODO: eliminate all uses of Object. See lexer, parser

    public List<NativeType> interpret(String source) {
        Lexer lexer = new Lexer(source, ioManager);
        List<Token> tokens = lexer.lexSource();
        Parser parser = new Parser(tokens, ioManager);
        List<Expression> expressions = parser.parseTokens();
        List<NativeType> values = new ArrayList<>();
        expressions.forEach(expression -> values.add(expression.accept(this)));
        return values;
    }

    @Override
    public NativeType visit(NilExpression nilExpression) {
        return new NativeNil();
    }

    @Override
    public NativeType visit(TrueExpression trueExpression) {
        return new NativeBool(true);
    }

    @Override
    public NativeType visit(FalseExpression falseExpression) {
        return new NativeBool(false);
    }

    @Override
    public NativeType visit(NumberExpression numberExpression) {
        return new NativeNumber(numberExpression.getValue());
    }

    @Override
    public NativeType visit(StringExpression stringExpression) {
        return new NativeString(stringExpression.getValue());
    }

    @Override
    public NativeType visit(IdentifierExpression identifierExpression) {
        return environment.lookUpValueOf(identifierExpression.getSymbol());
    }

    @Override
    public NativeType visit(ListExpression listExpression) {
        Deque<NativeType> itemValues = new LinkedList<>();
        listExpression.getContents().forEach(itemExpression -> itemValues.add(itemExpression.accept(this)));
        return new NativeList(itemValues);
    }

    @Override
    public NativeType visit(GroupExpression groupExpression) {
        return groupExpression.getInside().accept(this);
    }

    @Override
    public NativeType visit(NegationExpression negationExpression) {
        NativeType insideValue = negationExpression.getInside().accept(this);
        if (!(insideValue instanceof NativeNumber)) {
            throw ioManager.reportError(ErrorType.INCOMPATIBLE_TYPES, negationExpression, negationExpression.getLineNumber());
        }
        return ((NativeNumber) insideValue).negate();
    }

    @Override
    public NativeType visit(NotExpression notExpression) {
        NativeType insideValue = notExpression.getInside().accept(this);
        if (!(insideValue instanceof NativeBool)) {
            throw ioManager.reportError(ErrorType.INCOMPATIBLE_TYPES, notExpression, notExpression.getLineNumber());
        }
        return ((NativeBool) insideValue).negate();
    }

    @Override
    public NativeType visit(AdditionExpression additionExpression) {
        NativeType leftValue = additionExpression.getLeft().accept(this);
        NativeType rightValue = additionExpression.getRight().accept(this);
        if (!(leftValue instanceof NativeNumber && rightValue instanceof NativeNumber)) {
            throw ioManager.reportError(ErrorType.INCOMPATIBLE_TYPES, additionExpression, additionExpression.getLineNumber());
        }
        return ((NativeNumber) leftValue).add((NativeNumber) rightValue);
    }

    @Override
    public NativeType visit(SubtractionExpression subtractionExpression) {
        return evaluateBinaryNumericOperation(subtractionExpression.getLeft(), subtractionExpression.getRight(), (x, y) -> x - y, subtractionExpression.getLineNumber());
    }

    @Override
    public NativeType visit(MultiplicationExpression multiplicationExpression) {
        return evaluateBinaryNumericOperation(multiplicationExpression.getLeft(), multiplicationExpression.getRight(), (x, y) -> x * y, multiplicationExpression.getLineNumber());
    }

    @Override
    public NativeType visit(DivisionExpression divisionExpression) {
        // TODO: throw error on division by zero
        return evaluateBinaryNumericOperation(divisionExpression.getLeft(), divisionExpression.getRight(), (x, y) -> x / y, divisionExpression.getLineNumber());
    }

    @Override
    public NativeType visit(ExponentExpression exponentExpression) {
        return evaluateBinaryNumericOperation(exponentExpression.getBase(), exponentExpression.getPower(), Math::pow, exponentExpression.getLineNumber());
    }

    @Override
    public NativeType visit(LessThanExpression lessThanExpression) {
        return evaluateBinaryNumericOperation(lessThanExpression.getLeft(), lessThanExpression.getRight(), (x, y) -> x < y, lessThanExpression.getLineNumber());
    }

    @Override
    public NativeType visit(GreaterThanExpression greaterThanExpression) {
        return evaluateBinaryNumericOperation(greaterThanExpression.getLeft(), greaterThanExpression.getRight(), (x, y) -> x > y, greaterThanExpression.getLineNumber());
    }

    @Override
    public NativeType visit(LessEqualExpression lessEqualExpression) {
        return evaluateBinaryNumericOperation(lessEqualExpression.getLeft(), lessEqualExpression.getRight(), (x, y) -> x <= y, lessEqualExpression.getLineNumber());
    }

    @Override
    public NativeType visit(GreaterEqualExpression greaterEqualExpression) {
        return evaluateBinaryNumericOperation(greaterEqualExpression.getLeft(), greaterEqualExpression.getRight(), (x, y) -> x >= y, greaterEqualExpression.getLineNumber());
    }

    @Override
    public NativeType visit(EqualsExpression equalsExpression) {
        NativeType leftValue = equalsExpression.getLeft().accept(this);
        NativeType rightValue = equalsExpression.getRight().accept(this);
        return new NativeBool(leftValue.equals(rightValue));
    }

    @Override
    public NativeType visit(NotEqualExpression notEqualExpression) {
        NativeType leftValue = notEqualExpression.getLeft().accept(this);
        NativeType rightValue = notEqualExpression.getRight().accept(this);
        return new NativeBool(!leftValue.equals(rightValue));
    }

    @Override
    public NativeType visit(AndExpression andExpression) {
        // TODO: add short-circuiting
        return evaluateBinaryLogicalOperation(andExpression.getLeft(), andExpression.getRight(), (x, y) -> x && y, andExpression.getLineNumber());
    }

    @Override
    public NativeType visit(OrExpression orExpression) {
        // TODO: add short-circuiting
        return evaluateBinaryLogicalOperation(orExpression.getLeft(), orExpression.getRight(), (x, y) -> x || y, orExpression.getLineNumber());
    }

    @Override
    public NativeType visit(ConcatenationExpression concatenationExpression) {
        NativeType leftValue = concatenationExpression.getLeft().accept(this);
        NativeType rightValue = concatenationExpression.getRight().accept(this);
        if (!(leftValue instanceof NativeString && rightValue instanceof NativeString)) {
            throw ioManager.reportError(ErrorType.INCOMPATIBLE_TYPES, concatenationExpression, concatenationExpression.getLineNumber());
        }
        return ((NativeString) leftValue).concatenate((NativeString) rightValue);
    }

    @Override
    public NativeType visit(ConsExpression consExpression) {
        NativeType leftValue = consExpression.getLeft().accept(this);
        NativeType rightValue = consExpression.getRight().accept(this);
        if (!(rightValue instanceof NativeList)) {
            throw ioManager.reportError(ErrorType.INCOMPATIBLE_TYPES, consExpression, consExpression.getLineNumber());
        }
        return ((NativeList) rightValue).cons(leftValue);
    }

    @Override
    public NativeType visit(DefinitionExpression definitionExpression) {
        NativeType value = definitionExpression.getValue().accept(this);
        environment.addBinding(definitionExpression.getSymbol(), value);
        return value;
    }

    private <T> Object evaluateBinaryNumericOperation(Expression left, Expression right, BiFunction<Double, Double, T> operation, int lineNumber) {
        Object leftValue = left.accept(this);
        Object rightValue = right.accept(this);
        if (!(leftValue instanceof Double) || !(rightValue instanceof Double)) {
            ioManager.reportError(ErrorType.INCOMPATIBLE_TYPES, left, lineNumber);
        } // TODO: throw exception from above report
        return operation.apply((Double) leftValue, (Double) rightValue);
    }

    private Object evaluateBinaryLogicalOperation(Expression left, Expression right, BiFunction<Boolean, Boolean, Boolean> operation, int lineNumber) {
        Object leftValue = left.accept(this);
        Object rightValue = right.accept(this);
        if (!(leftValue instanceof Boolean) || !(rightValue instanceof Boolean)) {
            ioManager.reportError(ErrorType.INCOMPATIBLE_TYPES, left, lineNumber);
        } // TODO: throw exception
        return operation.apply((Boolean) leftValue, (Boolean) rightValue);
    }
}
