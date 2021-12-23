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
        // TODO: throw error if symbol isn't found
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
        return evaluateBinaryNumericOperation(additionExpression, NativeNumber::add);
    }

    @Override
    public NativeType visit(SubtractionExpression subtractionExpression) {
        return evaluateBinaryNumericOperation(subtractionExpression, NativeNumber::subtract);
    }

    @Override
    public NativeType visit(MultiplicationExpression multiplicationExpression) {
        return evaluateBinaryNumericOperation(multiplicationExpression, NativeNumber::multiply);
    }

    @Override
    public NativeType visit(DivisionExpression divisionExpression) {
        // TODO: throw error on division by zero
        return evaluateBinaryNumericOperation(divisionExpression, NativeNumber::divide);
    }

    @Override
    public NativeType visit(ExponentExpression exponentExpression) {
        return evaluateBinaryNumericOperation(exponentExpression, NativeNumber::exponentiate);
    }

    @Override
    public NativeType visit(LessThanExpression lessThanExpression) {
        return evaluateBinaryNumericOperation(lessThanExpression, NativeNumber::lessThan);
    }

    @Override
    public NativeType visit(GreaterThanExpression greaterThanExpression) {
        return evaluateBinaryNumericOperation(greaterThanExpression, NativeNumber::greaterThan);
    }

    @Override
    public NativeType visit(LessEqualExpression lessEqualExpression) {
        return evaluateBinaryNumericOperation(lessEqualExpression, NativeNumber::lessEqual);
    }

    @Override
    public NativeType visit(GreaterEqualExpression greaterEqualExpression) {
        return evaluateBinaryNumericOperation(greaterEqualExpression, NativeNumber::greaterEqual);
    }

    @Override
    public NativeType visit(EqualsExpression equalsExpression) {
        return new NativeBool(equalsExpression.getLeft().accept(this).equals(equalsExpression.getRight().accept(this)));
    }

    @Override
    public NativeType visit(NotEqualExpression notEqualExpression) {
        return new NativeBool(!notEqualExpression.getLeft().accept(this).equals(notEqualExpression.getRight().accept(this)));
    }

    @Override
    public NativeType visit(AndExpression andExpression) {
        NativeType leftValue = andExpression.getLeft().accept(this);
        if (!(leftValue instanceof NativeBool)) {
            throw ioManager.reportError(ErrorType.INCOMPATIBLE_TYPES, andExpression, andExpression.getLineNumber());
        }
        if (!((NativeBool) leftValue).isTrue()) {
            return new NativeBool(false);
        }
        NativeType rightValue = andExpression.getRight().accept(this);
        if (!(rightValue instanceof NativeBool)) {
            throw ioManager.reportError(ErrorType.INCOMPATIBLE_TYPES, andExpression, andExpression.getLineNumber());
        }
        return new NativeBool(((NativeBool) rightValue).isTrue());
    }

    @Override
    public NativeType visit(OrExpression orExpression) {
        NativeType leftValue = orExpression.getLeft().accept(this);
        if (!(leftValue instanceof NativeBool)) {
            throw ioManager.reportError(ErrorType.INCOMPATIBLE_TYPES, orExpression, orExpression.getLineNumber());
        }
        if (((NativeBool) leftValue).isTrue()) {
            return new NativeBool(true);
        }
        NativeType rightValue = orExpression.getRight().accept(this);
        if (!(rightValue instanceof NativeBool)) {
            throw ioManager.reportError(ErrorType.INCOMPATIBLE_TYPES, orExpression, orExpression.getLineNumber());
        }
        return new NativeBool(((NativeBool) rightValue).isTrue());
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

    private <T extends Binary & Expression, R extends NativeType>
    R evaluateBinaryNumericOperation(T expression, BiFunction<NativeNumber, NativeNumber, R> operation) {
        NativeType leftValue = expression.getLeft().accept(this);
        NativeType rightValue = expression.getRight().accept(this);
        if (!(leftValue instanceof NativeNumber && rightValue instanceof NativeNumber)) {
            throw ioManager.reportError(ErrorType.INCOMPATIBLE_TYPES, expression, expression.getLineNumber());
        }
        return operation.apply((NativeNumber) leftValue, (NativeNumber) rightValue);
    }
}
