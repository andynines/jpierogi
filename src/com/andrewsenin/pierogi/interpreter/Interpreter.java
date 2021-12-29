package com.andrewsenin.pierogi.interpreter;

import com.andrewsenin.pierogi.ast.*;
import com.andrewsenin.pierogi.datatypes.*;
import com.andrewsenin.pierogi.io.ErrorType;
import com.andrewsenin.pierogi.io.FunctionIoManagerWrapper;
import com.andrewsenin.pierogi.io.IoManager;
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
            throw ioManager.reportError(ErrorType.UNDEFINED_SYMBOL, identifierExpression, identifierExpression.getLineNumber());
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
            throw ioManager.reportError(ErrorType.INCOMPATIBLE_TYPES, negationExpression, negationExpression.getLineNumber());
        }
        return ((NativeNumber) insideValue).negate();
    }

    @Override
    public NativeData visit(NotExpression notExpression) {
        NativeData insideValue = evaluate(notExpression.getInside());
        if (!(insideValue instanceof NativeBool)) {
            throw ioManager.reportError(ErrorType.INCOMPATIBLE_TYPES, notExpression, notExpression.getLineNumber());
        }
        return ((NativeBool) insideValue).negate();
    }

    @Override
    public NativeData visit(AdditionExpression additionExpression) {
        return evaluateBinaryNumericExpression(additionExpression, NativeNumber::add);
    }

    @Override
    public NativeData visit(SubtractionExpression subtractionExpression) {
        return evaluateBinaryNumericExpression(subtractionExpression, NativeNumber::subtract);
    }

    @Override
    public NativeData visit(MultiplicationExpression multiplicationExpression) {
        return evaluateBinaryNumericExpression(multiplicationExpression, NativeNumber::multiply);
    }

    @Override
    public NativeData visit(DivisionExpression divisionExpression) {
        NativeNumber result = (NativeNumber) evaluateBinaryNumericExpression(divisionExpression, NativeNumber::divide);
        if (result.isZeroDivisionResult()) {
            throw ioManager.reportError(ErrorType.DIVISION_BY_ZERO, divisionExpression, divisionExpression.getLineNumber());
        }
        return result;
    }

    @Override
    public NativeData visit(ExponentExpression exponentExpression) {
        return evaluateBinaryNumericExpression(exponentExpression, NativeNumber::raise);
    }

    @Override
    public NativeData visit(LessThanExpression lessThanExpression) {
        return evaluateBinaryNumericExpression(lessThanExpression, NativeNumber::lessThan);
    }

    @Override
    public NativeData visit(GreaterThanExpression greaterThanExpression) {
        return evaluateBinaryNumericExpression(greaterThanExpression, NativeNumber::greaterThan);
    }

    @Override
    public NativeData visit(LessEqualExpression lessEqualExpression) {
        return evaluateBinaryNumericExpression(lessEqualExpression, NativeNumber::lessEqual);
    }

    @Override
    public NativeData visit(GreaterEqualExpression greaterEqualExpression) {
        return evaluateBinaryNumericExpression(greaterEqualExpression, NativeNumber::greaterEqual);
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
            throw ioManager.reportError(ErrorType.INCOMPATIBLE_TYPES, andExpression, andExpression.getLineNumber());
        }
        if (!((NativeBool) leftValue).isTrue()) {
            return new NativeBool(false);
        }
        NativeData rightValue = evaluate(andExpression.getRight());
        if (!(rightValue instanceof NativeBool)) {
            throw ioManager.reportError(ErrorType.INCOMPATIBLE_TYPES, andExpression, andExpression.getLineNumber());
        }
        return new NativeBool(((NativeBool) rightValue).isTrue());
    }

    @Override
    public NativeData visit(OrExpression orExpression) {
        NativeData leftValue = evaluate(orExpression.getLeft());
        if (!(leftValue instanceof NativeBool)) {
            throw ioManager.reportError(ErrorType.INCOMPATIBLE_TYPES, orExpression, orExpression.getLineNumber());
        }
        if (((NativeBool) leftValue).isTrue()) {
            return new NativeBool(true);
        }
        NativeData rightValue = evaluate(orExpression.getRight());
        if (!(rightValue instanceof NativeBool)) {
            throw ioManager.reportError(ErrorType.INCOMPATIBLE_TYPES, orExpression, orExpression.getLineNumber());
        }
        return new NativeBool(((NativeBool) rightValue).isTrue());
    }

    @Override
    public NativeData visit(ConcatenationExpression concatenationExpression) {
        NativeData leftValue = evaluate(concatenationExpression.getLeft());
        NativeData rightValue = evaluate(concatenationExpression.getRight());
        if (!(leftValue instanceof NativeString && rightValue instanceof NativeString)) {
            throw ioManager.reportError(ErrorType.INCOMPATIBLE_TYPES, concatenationExpression, concatenationExpression.getLineNumber());
        }
        return ((NativeString) leftValue).concatenate((NativeString) rightValue);
    }

    @Override
    public NativeData visit(ConsExpression consExpression) {
        NativeData leftValue = evaluate(consExpression.getLeft());
        NativeData rightValue = evaluate(consExpression.getRight());
        if (!(rightValue instanceof NativeList)) {
            throw ioManager.reportError(ErrorType.INCOMPATIBLE_TYPES, consExpression, consExpression.getLineNumber());
        }
        return ((NativeList) rightValue).cons(leftValue);
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
            throw ioManager.reportError(ErrorType.INCOMPATIBLE_TYPES, ifExpression.getCondition(), ((LineNumbered) ifExpression.getCondition()).getLineNumber());
        }
        return evaluateBlock(((NativeBool) conditionValue).isTrue() ? ifExpression.getConsequent() : ifExpression.getAlternative());
    }

    @Override
    public NativeData visit(FunctionExpression functionExpression) {
        return new NativeNil();
    }

    @Override
    public NativeData visit(CallExpression callExpression) {
        String symbol = callExpression.getSymbol();
        if (!environment.hasDefinitionFor(symbol)) {
            throw ioManager.reportError(ErrorType.UNDEFINED_SYMBOL, callExpression, callExpression.getLineNumber());
        }
        NativeData lookupResult = environment.lookUpValueOf(symbol);
        if (!(lookupResult instanceof NativeFunction)) {
            throw ioManager.reportError(ErrorType.UNCALLABLE_SYMBOL, callExpression, callExpression.getLineNumber());
        }
        List<NativeData> arguments = new ArrayList<>();
        callExpression.getArguments().forEach(expression -> arguments.add(evaluate(expression)));
        return ((NativeFunction) lookupResult).call(arguments, new FunctionIoManagerWrapper(ioManager));
    }

    private NativeData evaluate(Expression expression) {
        return expression.accept(this);
    }

    private <T extends Binary & Expression>
    NativeData evaluateBinaryNumericExpression(T expression, BiFunction<NativeNumber, NativeNumber, NativeData> operation) {
        NativeData leftValue = evaluate(expression.getLeft());
        NativeData rightValue = evaluate(expression.getRight());
        if (!(leftValue instanceof NativeNumber && rightValue instanceof NativeNumber)) {
            throw ioManager.reportError(ErrorType.INCOMPATIBLE_TYPES, expression, expression.getLineNumber());
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
