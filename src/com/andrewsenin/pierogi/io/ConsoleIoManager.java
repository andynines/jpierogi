package com.andrewsenin.pierogi.io;

import com.andrewsenin.pierogi.ast.Expression;
import com.andrewsenin.pierogi.lexer.Token;

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
    public UnwindingException reportError(ErrorType errorType, String nearestLexeme, int lineNumber) {
        printErrorMessage(errorType, nearestLexeme, lineNumber);
        return new StaticError();
    }

    @Override
    public UnwindingException reportError(ErrorType errorType, Token nearestToken, int lineNumber) {
        printErrorMessage(errorType, nearestToken.getLexeme(), lineNumber);
        return new StaticError();
    }

    @Override
    public UnwindingException reportError(ErrorType errorType, Expression nearestExpression, int lineNumber) {
        printErrorMessage(errorType, nearestExpression.toString(), lineNumber);
        return new RuntimeError();
    }

    private void printErrorMessage(ErrorType errorType, String near, int lineNumber) {
        System.err.println("Error on line " + lineNumber + ": " + errorType.name() + " near \"" + near + "\"");
    }
}
