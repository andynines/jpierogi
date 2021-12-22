package com.andrewsenin.pierogi.io;

import com.andrewsenin.pierogi.ast.Expression;
import com.andrewsenin.pierogi.lexer.Token;

public class DummyIoManager implements IoManager {
    @Override
    public void print(String message) {
    }

    @Override
    public void println(String message) {
    }

    @Override
    public String getInput() {
        return "";
    }

    @Override
    public UnwindingException reportError(ErrorType errorType, String nearestLexeme, int lineNumber) {
        return new UnwindingException();
    }

    @Override
    public UnwindingException reportError(ErrorType errorType, Token nearestToken, int lineNumber) {
        return new UnwindingException();
    }

    @Override
    public UnwindingException reportError(ErrorType errorType, Expression nearestExpression, int lineNumber) {
        return new UnwindingException();
    }
}
