package com.andrewsenin.pierogi.io;

public class DummyIoManager implements IoManager {
    @Override
    public void print(String message) {
    }

    @Override
    public String requestInput() {
        return "";
    }

    @Override
    public UnwindingException reportStaticError(ErrorType errorType, String near, int lineNumber) {
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
}
