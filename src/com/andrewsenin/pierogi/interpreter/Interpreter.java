package com.andrewsenin.pierogi.interpreter;

import com.andrewsenin.pierogi.ast.*;
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
        return new NativeNil();
    }

    @Override
    public NativeType visit(MultiplicationExpression multiplicationExpression) {
        return new NativeNil();
    }

    @Override
    public NativeType visit(DivisionExpression divisionExpression) {
        // TODO: throw error on division by zero
        return new NativeNil();
    }

    @Override
    public NativeType visit(ExponentExpression exponentExpression) {
        return new NativeNil();
    }

    @Override
    public NativeType visit(LessThanExpression lessThanExpression) {
        return new NativeNil();
    }

    @Override
    public NativeType visit(GreaterThanExpression greaterThanExpression) {
        return new NativeNil();
    }

    @Override
    public NativeType visit(LessEqualExpression lessEqualExpression) {
        return new NativeNil();
    }

    @Override
    public NativeType visit(GreaterEqualExpression greaterEqualExpression) {
        return new NativeNil();
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
        return new NativeNil();
    }

    @Override
    public NativeType visit(OrExpression orExpression) {
        // TODO: add short-circuiting
        return new NativeNil();
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
        NativeType value = definitionExpression.getDefinition().accept(this);
        environment.addBinding(definitionExpression.getSymbol(), value);
        return value;
    }
}
