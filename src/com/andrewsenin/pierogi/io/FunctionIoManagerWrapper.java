package com.andrewsenin.pierogi.io;

import com.andrewsenin.pierogi.ast.Expression;
import com.andrewsenin.pierogi.lexer.Token;

public class FunctionIoManagerWrapper implements IoManager {

    private final IoManager ioManager;

    public FunctionIoManagerWrapper(IoManager ioManager) {
        this.ioManager = ioManager;
    }

    @Override
    public void print(String message) {
        ioManager.print(message);
    }

    @Override
    public String requestInput() {
        return ioManager.requestInput();
    }

    @Override
    public UnwindingException reportError(ErrorType errorType, String nearestLexeme, int lineNumber) {
        return ioManager.reportError(errorType, nearestLexeme, lineNumber);
    }

    @Override
    public UnwindingException reportError(ErrorType errorType, Token nearestToken, int lineNumber) {
        return ioManager.reportError(errorType, nearestToken, lineNumber);
    }

    @Override
    public UnwindingException reportError(ErrorType errorType, Expression nearestExpression, int lineNumber) {
        return ioManager.reportError(errorType, nearestExpression, lineNumber);
    }
}
