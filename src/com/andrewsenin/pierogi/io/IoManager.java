package com.andrewsenin.pierogi.io;

import com.andrewsenin.pierogi.ast.Expression;
import com.andrewsenin.pierogi.lexer.Token;

public interface IoManager {
    void print(String message);

    void println(String message);

    String getInput();

    UnwindingException reportError(ErrorType errorType, String nearestLexeme, int lineNumber);

    UnwindingException reportError(ErrorType errorType, Token nearestToken, int lineNumber);

    UnwindingException reportError(ErrorType errorType, Expression nearestExpression, int lineNumber);
}