package com.andrewsenin.pierogi.lexer;

public class Token {

    private final TokenType type;
    private final String lexeme;
    private final Object literalValue;
    private final int lineNumber;

    public Token(TokenType type, String lexeme, Object literalValue, int lineNumber) {
        this.type = type;
        this.lexeme = lexeme;
        this.literalValue = literalValue;
        this.lineNumber = lineNumber;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public Object getLiteralValue() {
        return literalValue;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
