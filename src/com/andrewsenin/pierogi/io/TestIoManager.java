package com.andrewsenin.pierogi.io;

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
    public String requestInput() {
        return input;
    }

    @Override
    public UnwindingException reportStaticError(ErrorType errorType, String near, int lineNumber) {
        mostRecentErrorType = errorType;
        mostRecentErrorLineNumber = lineNumber;
        mostRecentErrorLexeme = near;
        return new UnwindingException();
    }

    @Override
    public UnwindingException reportRuntimeError(ErrorType errorType) {
        return new UnwindingException();
    }

    @Override
    public UnwindingException reportRuntimeError(ErrorType errorType, String near, int lineNumber) {
        return new UnwindingException();
    }

    @Override
    public void recordFunctionScope(String functionName, int lineNumber) {
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
