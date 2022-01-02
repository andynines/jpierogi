package com.andrewsenin.pierogi.io;

import java.util.Scanner;

public class ConsoleIoManager implements IoManager {

    private final Scanner inputScanner = new Scanner(System.in);

    @Override
    public void print(String message) {
        System.out.print(message);
    }

    @Override
    public String requestInput() {
        return inputScanner.nextLine();
    }

    @Override
    public UnwindingException reportStaticError(ErrorType errorType, String near, int lineNumber) {
        printErrorMessage(errorType, near, lineNumber);
        return new StaticError();
    }

    @Override
    public UnwindingException reportRuntimeError(ErrorType errorType) {
        System.err.println(errorType.name());
        return new StaticError();
    }

    @Override
    public UnwindingException reportRuntimeError(ErrorType errorType, String near, int lineNumber) {
        printErrorMessage(errorType, near, lineNumber);
        return new RuntimeError();
    }

    @Override
    public void recordFunctionScope(String functionName, int lineNumber) {
        System.err.println("In " + functionName + " on line " + lineNumber);
    }

    private void printErrorMessage(ErrorType errorType, String near, int lineNumber) {
        System.err.println("Error on line " + lineNumber + ": " + errorType.name() + " near " + near);
    }
}
