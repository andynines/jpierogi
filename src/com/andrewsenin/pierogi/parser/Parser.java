package com.andrewsenin.pierogi.parser;

import com.andrewsenin.pierogi.ast.*;
import com.andrewsenin.pierogi.io.ErrorType;
import com.andrewsenin.pierogi.io.IoManager;
import com.andrewsenin.pierogi.lexer.Token;
import com.andrewsenin.pierogi.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private static class UnexpectedTokenException extends RuntimeException {
    }

    private final List<Token> tokens;
    private final IoManager ioManager;
    private final List<Expression> expressions = new ArrayList<>();
    private int currentTokenIndex = 0;

    public Parser(List<Token> tokens, IoManager ioManager) {
        this.tokens = tokens;
        this.ioManager = ioManager;
    }

    public List<Expression> parseTokens() {
        while (!isAtEnd()) expressions.add(parseNextExpression());
        return expressions;
    }

    private boolean isAtEnd() {
        return peekCurrentToken().getType() == TokenType.EOF;
    }

    private Token peekPreviousToken() {
        return tokens.get(currentTokenIndex - 1);
    }

    private Token peekCurrentToken() {
        return tokens.get(currentTokenIndex);
    }

    private boolean matchesCurrentTokenType(TokenType tokenType) {
        if (isAtEnd()) return false;
        return peekCurrentToken().getType() == tokenType;
    }

    private boolean matchesNextTokenType(TokenType tokenType) {
        if (currentTokenIndex + 1 >= tokens.size()) return false;
        return tokens.get(currentTokenIndex + 1).getType() == tokenType;
    }

    private Token consumeCurrentToken() {
        if (!isAtEnd()) currentTokenIndex++;
        return peekPreviousToken();
    }

    private UnexpectedTokenException reportError(Token token, ErrorType errorType) {
        ioManager.reportError(errorType, token, token.getLineNumber());
        return new UnexpectedTokenException();
    }

    private Token consume(TokenType tokenType, ErrorType errorType) {
        if (matchesCurrentTokenType(tokenType)) return consumeCurrentToken();
        throw reportError(peekCurrentToken(), errorType);
    }

    private boolean consumeIfMatchesAny(TokenType... tokenTypes) {
        for (TokenType tokenType : tokenTypes)
            if (matchesCurrentTokenType(tokenType)) {
                consumeCurrentToken();
                return true;
            }
        return false;
    }

    private Expression parseNextExpression() {
        return parseDefinition();
    }

    private Expression parseDefinition() {
        Expression left = parseLogic();
        if (consumeIfMatchesAny(TokenType.EQUAL)) {
            Expression value = parseDefinition();
            if (left instanceof IdentifierExpression) {
                return new DefinitionExpression(((IdentifierExpression) left).getSymbol(), value);
            }
            throw new RuntimeException(); // TODO: properly record error
        }
        return left;
    }

    private Expression parseLogic() {
        Expression left = parseEquality();
        while (consumeIfMatchesAny(TokenType.AND, TokenType.OR)) {
            TokenType tokenType = peekPreviousToken().getType();
            int lineNumber = peekPreviousToken().getLineNumber();
            Expression right = parseEquality();
            if (tokenType == TokenType.AND) left = new AndExpression(left, right, lineNumber);
            if (tokenType == TokenType.OR) left = new OrExpression(left, right, lineNumber);
        }
        return left;
    }

    private Expression parseEquality() {
        Expression left = parseComparison();
        while (consumeIfMatchesAny(TokenType.EQUAL_EQUAL, TokenType.SLASH_EQUAL)) {
            TokenType tokenType = peekPreviousToken().getType();
            int lineNumber = peekPreviousToken().getLineNumber();
            Expression right = parseComparison();
            if (tokenType == TokenType.EQUAL_EQUAL) left = new EqualsExpression(left, right, lineNumber);
            if (tokenType == TokenType.SLASH_EQUAL) left = new NotEqualExpression(left, right, lineNumber);
        }
        return left;
    }

    private Expression parseComparison() {
        Expression left = parseConstruction();
        while (consumeIfMatchesAny(TokenType.LESS_THAN, TokenType.GREATER_THAN, TokenType.LESS_EQUAL, TokenType.GREATER_EQUAL)) {
            TokenType tokenType = peekPreviousToken().getType();
            int lineNumber = peekPreviousToken().getLineNumber();
            Expression right = parseConstruction();
            if (tokenType == TokenType.LESS_THAN) left = new LessThanExpression(left, right, lineNumber);
            if (tokenType == TokenType.GREATER_THAN) left = new GreaterThanExpression(left, right, lineNumber);
            if (tokenType == TokenType.LESS_EQUAL) left = new LessEqualExpression(left, right, lineNumber);
            if (tokenType == TokenType.GREATER_EQUAL) left = new GreaterEqualExpression(left, right, lineNumber);
        }
        return left;
    }

    private Expression parseConstruction() {
        Expression left = parseConcatenation();
        while (consumeIfMatchesAny(TokenType.COLON)) {
            int lineNumber = peekPreviousToken().getLineNumber();
            Expression right = parseConstruction();
            left = new ConsExpression(left, right, lineNumber);
        }
        return left;
    }

    private Expression parseConcatenation() {
        Expression left = parseTerm();
        while (consumeIfMatchesAny(TokenType.DOT_DOT)) {
            int lineNumber = peekCurrentToken().getLineNumber();
            Expression right = parseTerm();
            left = new ConcatenationExpression(left, right, lineNumber);
        }
        return left;
    }

    private Expression parseTerm() {
        Expression left = parseFactor();
        while (consumeIfMatchesAny(TokenType.PLUS, TokenType.MINUS)) {
            TokenType tokenType = peekPreviousToken().getType();
            int lineNumber = peekPreviousToken().getLineNumber();
            Expression right = parseFactor();
            if (tokenType == TokenType.PLUS) left = new AdditionExpression(left, right, lineNumber);
            if (tokenType == TokenType.MINUS) left = new SubtractionExpression(left, right, lineNumber);
        }
        return left;
    }

    private Expression parseFactor() {
        Expression left = parsePower();
        while (consumeIfMatchesAny(TokenType.ASTERISK, TokenType.SLASH)) {
            TokenType tokenType = peekPreviousToken().getType();
            int lineNumber = peekPreviousToken().getLineNumber();
            Expression right = parsePower();
            if (tokenType == TokenType.ASTERISK) left = new MultiplicationExpression(left, right, lineNumber);
            if (tokenType == TokenType.SLASH) left = new DivisionExpression(left, right, lineNumber);
        }
        return left;
    }

    private Expression parsePower() {
        Expression base = parseUnary();
        while (consumeIfMatchesAny(TokenType.CARET)) {
            Expression power = parseUnary();
            base = new ExponentExpression(base, power, peekPreviousToken().getLineNumber());
        }
        return base;
    }

    private Expression parseUnary() {
        if (consumeIfMatchesAny(TokenType.MINUS)) return new NegationExpression(parseUnary(), peekPreviousToken().getLineNumber());
        if (consumeIfMatchesAny(TokenType.NOT)) return new NotExpression(parseUnary(), peekPreviousToken().getLineNumber());
        return parseLiteral();
    }

    private Expression parseLiteral() {
        if (consumeIfMatchesAny(TokenType.NIL)) return new NilExpression();
        if (consumeIfMatchesAny(TokenType.TRUE)) return new TrueExpression();
        if (consumeIfMatchesAny(TokenType.FALSE)) return new FalseExpression();
        if (consumeIfMatchesAny(TokenType.NUMBER)) return new NumberExpression((double) peekPreviousToken().getLiteralValue());
        if (consumeIfMatchesAny(TokenType.STRING)) return new StringExpression((String) peekPreviousToken().getLiteralValue());
        if (consumeIfMatchesAny(TokenType.IDENTIFIER)) return new IdentifierExpression(peekPreviousToken().getLexeme(), peekPreviousToken().getLineNumber());
        if (consumeIfMatchesAny(TokenType.LEFT_PARENTHESIS)) {
            Expression inside = parseNextExpression();
            consume(TokenType.RIGHT_PARENTHESIS, ErrorType.UNMATCHED_PARENTHESIS);
            return new GroupExpression(inside);
        }
        if (consumeIfMatchesAny(TokenType.LEFT_SQUARE_BRACKET)) return parseList();
        throw reportError(peekCurrentToken(), ErrorType.UNRECOGNIZED_TOKEN);
    }

    private Expression parseList() {
        List<Expression> contents = new ArrayList<>();
        while (true) {
            if (consumeIfMatchesAny(TokenType.RIGHT_SQUARE_BRACKET)) return new ListExpression(contents);
            Expression nextItemExpression = parseNextExpression();
            contents.add(nextItemExpression);
            if (consumeIfMatchesAny(TokenType.COMMA)) continue;
            if (consumeIfMatchesAny(TokenType.RIGHT_SQUARE_BRACKET)) return new ListExpression(contents);
            throw reportError(peekCurrentToken(), ErrorType.UNRECOGNIZED_TOKEN);
        }
    }
}
