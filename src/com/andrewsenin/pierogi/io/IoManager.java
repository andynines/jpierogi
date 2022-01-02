package com.andrewsenin.pierogi.io;

public interface IoManager {
    void print(String message);

    String requestInput();

    UnwindingException reportStaticError(ErrorType errorType, String near, int lineNumber);

    UnwindingException reportRuntimeError(ErrorType errorType);

    UnwindingException reportRuntimeError(ErrorType errorType, String near, int lineNumber);

    void recordFunctionScope(String functionName, int lineNumber);
}
