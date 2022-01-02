package com.andrewsenin.pierogi.interpreter;

import com.andrewsenin.pierogi.ast.*;
import com.andrewsenin.pierogi.datatypes.*;
import com.andrewsenin.pierogi.io.ErrorType;
import com.andrewsenin.pierogi.io.FunctionIoManagerWrapper;
import com.andrewsenin.pierogi.io.IoManager;
import com.andrewsenin.pierogi.io.UnwindingException;
import com.andrewsenin.pierogi.lexer.Lexer;
import com.andrewsenin.pierogi.lexer.Token;
import com.andrewsenin.pierogi.parser.Parser;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

public class Interpreter implements AstVisitor<NativeData> {

    private final IoManager ioManager;
    private final Environment environment = new Environment();

    public Interpreter(IoManager ioManager) {
        this.ioManager = ioManager;
    }

    public List<NativeData> interpret(String source) {
        Lexer lexer = new Lexer(source, ioManager);
        List<Token> tokens = lexer.lexSource();
        Parser parser = new Parser(tokens, ioManager);
        List<Expression> expressions = parser.parseTokens();
        List<NativeData> values = new ArrayList<>();
        expressions.forEach(expression -> values.add(evaluate(expression)));
        return values;
    }

    // TODO: order all expressions in a uniform way here and in the generators

    @Override
    public NativeData visit(NilExpression nilExpression) {
        return new NativeNil();
    }

    @Override
    public NativeData visit(TrueExpression trueExpression) {
        return new NativeBool(true);
    }

    @Override
    public NativeData visit(FalseExpression falseExpression) {
        return new NativeBool(false);
    }

    @Override
    public NativeData visit(NumberExpression numberExpression) {
        return new NativeNumber(numberExpression.getValue());
    }

    @Override
    public NativeData visit(StringExpression stringExpression) {
        return new NativeString(stringExpression.getValue());
    }

    @Override
    public NativeData visit(IdentifierExpression identifierExpression) {
        String symbol = identifierExpression.getSymbol();
        if (!environment.hasDefinitionFor(symbol)) {
            throw ioManager.reportRuntimeError(ErrorType.UNDEFINED_SYMBOL, symbol, identifierExpression.getLineNumber());
        }
        return environment.lookUpValueOf(symbol);
    }

    @Override
    public NativeData visit(ListExpression listExpression) {
        Deque<NativeData> itemValues = new LinkedList<>();
        listExpression.getContents().forEach(itemExpression -> itemValues.add(evaluate(itemExpression)));
        return new NativeList(itemValues);
    }

    @Override
    public NativeData visit(GroupExpression groupExpression) {
        return evaluate(groupExpression.getInside());
    }

    @Override
    public NativeData visit(NegationExpression negationExpression) {
        NativeData insideValue = evaluate(negationExpression.getInside());
        if (!(insideValue instanceof NativeNumber)) {
            throw reportErrorAtExpression(ErrorType.INCOMPATIBLE_TYPES, negationExpression);
        }
        return new NativeNumber(-((NativeNumber) insideValue).getValue());
    }

    @Override
    public NativeData visit(NotExpression notExpression) {
        NativeData insideValue = evaluate(notExpression.getInside());
        if (!(insideValue instanceof NativeBool)) {
            throw reportErrorAtExpression(ErrorType.INCOMPATIBLE_TYPES, notExpression);
        }
        return new NativeBool(!((NativeBool) insideValue).getValue());
    }

    @Override
    public NativeData visit(AdditionExpression additionExpression) {
        return evaluateBinaryNumericExpression(additionExpression, (a, b) -> new NativeNumber(a.getValue() + b.getValue()));
    }

    @Override
    public NativeData visit(SubtractionExpression subtractionExpression) {
        return evaluateBinaryNumericExpression(subtractionExpression, (a, b) -> new NativeNumber(a.getValue() - b.getValue()));
    }

    @Override
    public NativeData visit(MultiplicationExpression multiplicationExpression) {
        return evaluateBinaryNumericExpression(multiplicationExpression, (a, b) -> new NativeNumber(a.getValue() * b.getValue()));
    }

    @Override
    public NativeData visit(DivisionExpression divisionExpression) {
        NativeNumber result = (NativeNumber) evaluateBinaryNumericExpression(divisionExpression, (a, b) -> new NativeNumber(a.getValue() / b.getValue()));
        if (result.isInvalid()) {
            throw reportErrorAtExpression(ErrorType.DIVISION_BY_ZERO, divisionExpression);
        }
        return result;
    }

    @Override
    public NativeData visit(ExponentExpression exponentExpression) {
        return evaluateBinaryNumericExpression(exponentExpression, (a, b) -> new NativeNumber(Math.pow(a.getValue(), b.getValue())));
    }

    @Override
    public NativeData visit(LessThanExpression lessThanExpression) {
        return evaluateBinaryNumericExpression(lessThanExpression, (a, b) -> new NativeBool(a.getValue() < b.getValue()));
    }

    @Override
    public NativeData visit(GreaterThanExpression greaterThanExpression) {
        return evaluateBinaryNumericExpression(greaterThanExpression, (a, b) -> new NativeBool(a.getValue() > b.getValue()));
    }

    @Override
    public NativeData visit(LessEqualExpression lessEqualExpression) {
        return evaluateBinaryNumericExpression(lessEqualExpression, (a, b) -> new NativeBool(a.getValue() <= b.getValue()));
    }

    @Override
    public NativeData visit(GreaterEqualExpression greaterEqualExpression) {
        return evaluateBinaryNumericExpression(greaterEqualExpression, (a, b) -> new NativeBool(a.getValue() >= b.getValue()));
    }

    @Override
    public NativeData visit(EqualsExpression equalsExpression) {
        return new NativeBool(evaluate(equalsExpression.getLeft()).equals(evaluate(equalsExpression.getRight())));
    }

    @Override
    public NativeData visit(NotEqualExpression notEqualExpression) {
        return new NativeBool(!evaluate(notEqualExpression.getLeft()).equals(evaluate(notEqualExpression.getRight())));
    }

    @Override
    public NativeData visit(AndExpression andExpression) {
        NativeData leftValue = evaluate(andExpression.getLeft());
        if (!(leftValue instanceof NativeBool)) {
            throw reportErrorAtExpression(ErrorType.INCOMPATIBLE_TYPES, andExpression);
        }
        if (!((NativeBool) leftValue).getValue()) {
            return new NativeBool(false);
        }
        NativeData rightValue = evaluate(andExpression.getRight());
        if (!(rightValue instanceof NativeBool)) {
            throw reportErrorAtExpression(ErrorType.INCOMPATIBLE_TYPES, andExpression);
        }
        return new NativeBool(((NativeBool) rightValue).getValue());
    }

    @Override
    public NativeData visit(OrExpression orExpression) {
        NativeData leftValue = evaluate(orExpression.getLeft());
        if (!(leftValue instanceof NativeBool)) {
            throw reportErrorAtExpression(ErrorType.INCOMPATIBLE_TYPES, orExpression);
        }
        if (((NativeBool) leftValue).getValue()) {
            return new NativeBool(true);
        }
        NativeData rightValue = evaluate(orExpression.getRight());
        if (!(rightValue instanceof NativeBool)) {
            throw reportErrorAtExpression(ErrorType.INCOMPATIBLE_TYPES, orExpression);
        }
        return new NativeBool(((NativeBool) rightValue).getValue());
    }

    @Override
    public NativeData visit(ConcatenationExpression concatenationExpression) {
        NativeData leftValue = evaluate(concatenationExpression.getLeft());
        NativeData rightValue = evaluate(concatenationExpression.getRight());
        if (!(leftValue instanceof NativeString && rightValue instanceof NativeString)) {
            throw reportErrorAtExpression(ErrorType.INCOMPATIBLE_TYPES, concatenationExpression);
        }
        return new NativeString(((NativeString) leftValue).getValue() + ((NativeString) rightValue).getValue());
    }

    @Override
    public NativeData visit(ConsExpression consExpression) {
        NativeData leftValue = evaluate(consExpression.getLeft());
        NativeData rightValue = evaluate(consExpression.getRight());
        if (!(rightValue instanceof NativeList)) {
            throw reportErrorAtExpression(ErrorType.INCOMPATIBLE_TYPES, consExpression);
        }
        Deque<NativeData> newItems = new LinkedList<>(((NativeList) rightValue).getItems());
        newItems.addFirst(leftValue);
        return new NativeList(newItems);
    }

    @Override
    public NativeData visit(DefinitionExpression definitionExpression) {
        NativeData value = evaluate(definitionExpression.getDefinition());
        environment.addBinding(definitionExpression.getSymbol(), value);
        return value;
    }

    @Override
    public NativeData visit(IfExpression ifExpression) {
        NativeData conditionValue = evaluate(ifExpression.getCondition());
        if (!(conditionValue instanceof NativeBool)) {
            throw reportErrorAtExpression(ErrorType.INCOMPATIBLE_TYPES, ifExpression);
        }
        return evaluateBlock(((NativeBool) conditionValue).getValue() ? ifExpression.getConsequent() : ifExpression.getAlternative());
    }

    @Override
    public NativeData visit(FunctionExpression functionExpression) {
        return new NativeNil();
    }

    @Override
    public NativeData visit(CallExpression callExpression) {
        NativeData calleeValue = evaluate(callExpression.getCallee());
        if (!(calleeValue instanceof NativeFunction)) {
            throw reportErrorAtExpression(ErrorType.UNCALLABLE_VALUE, callExpression);
        }
        List<Expression> argumentExpressions = callExpression.getArguments();
        NativeFunction functionValue = (NativeFunction) calleeValue;
        if (argumentExpressions.size() != functionValue.getArity()) {
            throw ioManager.reportRuntimeError(ErrorType.INCORRECT_NUMBER_OF_ARGUMENTS, functionValue.makeValueRepresentation(), callExpression.getLineNumber());
        }
        List<NativeData> argumentValues = new ArrayList<>();
        argumentExpressions.forEach(expression -> argumentValues.add(evaluate(expression)));
        return functionValue.call(argumentValues, new FunctionIoManagerWrapper(ioManager, functionValue.makeValueRepresentation(), callExpression.getLineNumber()));
    }

    private NativeData evaluate(Expression expression) {
        return expression.accept(this);
    }

    private <T extends LineNumbered & Expression>
    UnwindingException reportErrorAtExpression(ErrorType errorType, T expression) {
        return ioManager.reportStaticError(errorType, expression.getClass().getSimpleName(), expression.getLineNumber());
    }

    private <T extends Binary & Expression>
    NativeData evaluateBinaryNumericExpression(T expression, BiFunction<NativeNumber, NativeNumber, NativeData> operation) {
        NativeData leftValue = evaluate(expression.getLeft());
        NativeData rightValue = evaluate(expression.getRight());
        if (!(leftValue instanceof NativeNumber && rightValue instanceof NativeNumber)) {
            throw reportErrorAtExpression(ErrorType.INCOMPATIBLE_TYPES, expression);
        }
        return operation.apply((NativeNumber) leftValue, (NativeNumber) rightValue);
    }

    private NativeData evaluateBlock(List<Expression> block) {
        environment.pushNewFrame();
        NativeData result = null;
        for (Expression expression : block) {
            result = evaluate(expression);
        }
        environment.popCurrentFrame();
        return result;
    }
}
