package com.andrewsenin.pierogi.parser;

import com.andrewsenin.pierogi.ast.*;
import com.andrewsenin.pierogi.io.ErrorType;
import com.andrewsenin.pierogi.io.IoManager;
import com.andrewsenin.pierogi.io.UnwindingException;
import com.andrewsenin.pierogi.lexer.Token;
import com.andrewsenin.pierogi.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Parser { // TODO: include line numbers on every expression

    private final List<Token> tokens;
    private final IoManager ioManager;
    private final List<Expression> expressions = new ArrayList<>();
    private int currentTokenIndex = 0;
    private boolean hadError = false;

    public Parser(List<Token> tokens, IoManager ioManager) {
        this.tokens = tokens;
        this.ioManager = ioManager;
    }

    public List<Expression> parseTokens() {
        while (!isAtEnd()) {
            expressions.add(parseNextExpression());
        }
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

    private Token consumeCurrentToken() {
        if (!isAtEnd()) currentTokenIndex++;
        return peekPreviousToken();
    }

    private UnwindingException reportError(ErrorType errorType, Token token) {
        hadError = true;
        return ioManager.reportStaticError(errorType, token.getLexeme(), token.getLineNumber());
    }

    private Token consumeCurrentIfMatchesElseError(TokenType tokenType, ErrorType errorType) {
        if (matchesCurrentTokenType(tokenType)) return consumeCurrentToken();
        throw reportError(errorType, peekCurrentToken());
    }

    private boolean consumeCurrentIfMatchesAny(TokenType... tokenTypes) {
        for (TokenType tokenType : tokenTypes)
            if (matchesCurrentTokenType(tokenType)) {
                consumeCurrentToken();
                return true;
            }
        return false;
    }

    private Expression parseNextExpression() {
        consumeUntilNotNewline(); // TODO: fix this newline travesty
        Expression expression = parseDefinition();
        consumeUntilNotNewline();
        return expression;
    }

    private Expression parseDefinition() {
        Token possibleIdentifierToken = peekCurrentToken();
        Expression left = parseControl();
        if (consumeCurrentIfMatchesAny(TokenType.EQUAL)) {
            Expression value = parseDefinition();
            if (left instanceof IdentifierExpression) {
                return new DefinitionExpression(((IdentifierExpression) left).getSymbol(), value);
            }
            throw reportError(ErrorType.MISSING_IDENTIFIER, possibleIdentifierToken);
        }
        return left;
    }

    private Expression parseControl() {
        if (consumeCurrentIfMatchesAny(TokenType.IF)) {
            int lineNumber = peekPreviousToken().getLineNumber();
            Expression condition = parseNextExpression();
            List<Expression> consequent = parseBlock();
            consumeCurrentIfMatchesElseError(TokenType.ELSE, ErrorType.UNEXPECTED_TOKEN);
            List<Expression> alternative = parseBlock();
            return new IfExpression(condition, consequent, alternative, lineNumber);
        }
        if (consumeCurrentIfMatchesAny(TokenType.BACKSLASH)) {
            List<String> parameters = new ArrayList<>();
            while (true) {
                if (matchesCurrentTokenType(TokenType.LEFT_BRACE)) return new FunctionExpression(parameters, parseBlock());
                consumeCurrentIfMatchesElseError(TokenType.IDENTIFIER, ErrorType.MISSING_IDENTIFIER);
                parameters.add(peekPreviousToken().getLexeme());
                if (consumeCurrentIfMatchesAny(TokenType.COMMA)) continue;
                if (matchesCurrentTokenType(TokenType.LEFT_BRACE)) return new FunctionExpression(parameters, parseBlock());
                throw reportError(ErrorType.UNEXPECTED_TOKEN, peekCurrentToken());
            }
        }
        return parseLogic();
    }

    private Expression parseLogic() {
        Expression left = parseEquality();
        while (consumeCurrentIfMatchesAny(TokenType.AND, TokenType.OR)) {
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
        while (consumeCurrentIfMatchesAny(TokenType.EQUAL_EQUAL, TokenType.SLASH_EQUAL)) {
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
        while (consumeCurrentIfMatchesAny(TokenType.LESS_THAN, TokenType.GREATER_THAN, TokenType.LESS_EQUAL, TokenType.GREATER_EQUAL)) {
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
        while (consumeCurrentIfMatchesAny(TokenType.COLON)) {
            int lineNumber = peekPreviousToken().getLineNumber();
            Expression right = parseConstruction();
            left = new ConsExpression(left, right, lineNumber);
        }
        return left;
    }

    private Expression parseConcatenation() {
        Expression left = parseTerm();
        while (consumeCurrentIfMatchesAny(TokenType.DOT_DOT)) {
            int lineNumber = peekCurrentToken().getLineNumber();
            Expression right = parseTerm();
            left = new ConcatenationExpression(left, right, lineNumber);
        }
        return left;
    }

    private Expression parseTerm() {
        Expression left = parseFactor();
        while (consumeCurrentIfMatchesAny(TokenType.PLUS, TokenType.MINUS)) {
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
        while (consumeCurrentIfMatchesAny(TokenType.ASTERISK, TokenType.SLASH)) {
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
        while (consumeCurrentIfMatchesAny(TokenType.CARET)) {
            Expression power = parseUnary();
            base = new ExponentExpression(base, power, peekPreviousToken().getLineNumber());
        }
        return base;
    }

    private Expression parseUnary() {
        if (consumeCurrentIfMatchesAny(TokenType.MINUS)) return new NegationExpression(parseUnary(), peekPreviousToken().getLineNumber());
        if (consumeCurrentIfMatchesAny(TokenType.NOT)) return new NotExpression(parseUnary(), peekPreviousToken().getLineNumber());
        return parseCall();
    }

    private Expression parseCall() {
        Expression left = parseLiteral();
        while (true) {
            if (consumeCurrentIfMatchesAny(TokenType.LEFT_PARENTHESIS)) {
                left = parseArgumentList(left);
            } else {
                break;
            }
        }
        return left;
    }

    private Expression parseLiteral() {
        if (consumeCurrentIfMatchesAny(TokenType.NIL)) return new NilExpression();
        if (consumeCurrentIfMatchesAny(TokenType.TRUE)) return new TrueExpression();
        if (consumeCurrentIfMatchesAny(TokenType.FALSE)) return new FalseExpression();
        if (consumeCurrentIfMatchesAny(TokenType.NUMBER)) return new NumberExpression((double) peekPreviousToken().getLiteralValue());
        if (consumeCurrentIfMatchesAny(TokenType.STRING)) return new StringExpression((String) peekPreviousToken().getLiteralValue());
        if (consumeCurrentIfMatchesAny(TokenType.IDENTIFIER)) return new IdentifierExpression(peekPreviousToken().getLexeme(), peekPreviousToken().getLineNumber());
        if (consumeCurrentIfMatchesAny(TokenType.LEFT_PARENTHESIS)) {
            Expression inside = parseNextExpression();
            consumeCurrentIfMatchesElseError(TokenType.RIGHT_PARENTHESIS, ErrorType.UNMATCHED_PARENTHESIS);
            return new GroupExpression(inside);
        }
        if (consumeCurrentIfMatchesAny(TokenType.LEFT_SQUARE_BRACKET)) return parseList();
        throw reportError(ErrorType.UNEXPECTED_TOKEN, peekCurrentToken());
    }

    private void consumeUntilNotNewline() {
        while (consumeCurrentIfMatchesAny(TokenType.NEWLINE));
    }

    private Expression parseList() {
        List<Expression> contents = new ArrayList<>();
        while (true) {
            if (consumeCurrentIfMatchesAny(TokenType.RIGHT_SQUARE_BRACKET)) return new ListExpression(contents);
            Expression nextItemExpression = parseNextExpression();
            contents.add(nextItemExpression);
            if (consumeCurrentIfMatchesAny(TokenType.COMMA)) continue;
            if (consumeCurrentIfMatchesAny(TokenType.RIGHT_SQUARE_BRACKET)) return new ListExpression(contents);
            throw reportError(ErrorType.UNEXPECTED_TOKEN, peekCurrentToken());
        }
    }

    private List<Expression> parseBlock() {
        consumeCurrentIfMatchesElseError(TokenType.LEFT_BRACE, ErrorType.UNEXPECTED_TOKEN);
        List<Expression> expressions = new ArrayList<>();
        do {
            expressions.add(parseNextExpression());
        } while (!consumeCurrentIfMatchesAny(TokenType.RIGHT_BRACE));
        return expressions;
    }

    private Expression parseArgumentList(Expression callee) {
        List<Expression> arguments = new ArrayList<>();
        if (!matchesCurrentTokenType(TokenType.RIGHT_PARENTHESIS)) {
            do {
                arguments.add(parseNextExpression());
            } while (consumeCurrentIfMatchesAny(TokenType.COMMA));
        }
        consumeCurrentIfMatchesElseError(TokenType.RIGHT_PARENTHESIS, ErrorType.UNMATCHED_PARENTHESIS);
        return new CallExpression(callee, arguments, peekPreviousToken().getLineNumber());
    }
}
