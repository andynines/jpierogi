package com.andrewsenin.pierogi.lexer;

public enum TokenType {
    LEFT_PARENTHESIS,
    RIGHT_PARENTHESIS,
    LEFT_SQUARE_BRACKET,
    RIGHT_SQUARE_BRACKET,
    LEFT_BRACE,
    RIGHT_BRACE,
    COLON,
    COMMA,
    DOT,
    MINUS,
    PLUS,
    CARET,
    SLASH,
    BACKSLASH,
    ASTERISK,

    EQUAL,
    EQUAL_EQUAL,
    SLASH_EQUAL,
    GREATER_THAN,
    GREATER_EQUAL,
    LESS_THAN,
    LESS_EQUAL,
    DOT_DOT,

    AND,
    OR,
    NOT,
    TRUE,
    FALSE,
    NIL,

    IDENTIFIER,
    STRING,
    NUMBER,

    EOF
}
