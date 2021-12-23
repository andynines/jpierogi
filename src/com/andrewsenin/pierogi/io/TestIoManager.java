package com.andrewsenin.pierogi.io;

import com.andrewsenin.pierogi.ast.Expression;
import com.andrewsenin.pierogi.lexer.Token;

public class TestIoManager implements IoManager {

    private String output = "";
    private String input = "";
    private ErrorType mostRecentErrorType = null;
    private int mostRecentErrorLineNumber = 0;
    private String mostRecentErrorLexeme = "";

    @Override
    public void print(String message) {
        output += message;
    }

    @Override
    public void println(String message) {
        print(message);
        print("\n");
    }

    @Override
    public String getInput() {
        return input;
    }

    @Override
    public UnwindingException reportError(ErrorType errorType, String nearestLexeme, int lineNumber) {
        mostRecentErrorType = errorType;
        mostRecentErrorLineNumber = lineNumber;
        mostRecentErrorLexeme = nearestLexeme;
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

    public String getOutput() {
        return output;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public ErrorType getMostRecentErrorType() {
        return mostRecentErrorType;
    }

    public int getMostRecentErrorLineNumber() {
        return mostRecentErrorLineNumber;
    }

    public String getMostRecentErrorLexeme() {
        return mostRecentErrorLexeme;
    }
}
