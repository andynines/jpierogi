package com.andrewsenin.pierogi;

import com.andrewsenin.pierogi.ast.Expression;
import com.andrewsenin.pierogi.datatypes.NativeType;
import com.andrewsenin.pierogi.interpreter.Interpreter;
import com.andrewsenin.pierogi.io.*;
import com.andrewsenin.pierogi.lexer.Token;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class LanguageExecutable implements IoManager {

    private static final String EXECUTABLE_NAME = "pierogi";
    private static final String INPUT_PROMPT = EXECUTABLE_NAME + ">";

    private final Scanner inputScanner = new Scanner(System.in);
    private final Interpreter interpreter;

    public static void main(String[] args) {
        if (args.length > 1) {
            System.err.println("Usage: " + EXECUTABLE_NAME + " [script]");
            System.exit(1);
        }
        LanguageExecutable languageExecutable = new LanguageExecutable();
        if (args.length == 1) {
            File sourceFile = new File(args[0]);
            assertFileExists(sourceFile);
            languageExecutable.interpretFile(sourceFile);
        } else {
            languageExecutable.enterReplSession();
        }
    }

    public LanguageExecutable() {
        interpreter = new Interpreter(this);
    }

    @Override
    public void print(String message) {
        System.out.print(message);
    }

    @Override
    public void println(String message) {
        System.out.println(message);
    }

    @Override
    public String getInput() {
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

    private static void assertFileExists(File file) {
        if (!file.exists()) {
            System.err.println("File " + file.toString() + " does not exist");
            System.exit(1);
        }
    }

    private void interpretFile(File sourceFile) {
        String source = readFileContents(sourceFile);

    }

    private void enterReplSession() {
        while (true) {
            System.out.print(INPUT_PROMPT);
            String source = getInput();
            if (source.equals("exit")) {
                break;
            }
            List<NativeType> values = interpreter.interpret(source);
            values.forEach(value -> println(value.makePrintRepresentation()));
        }
    }

    private void printErrorMessage(ErrorType errorType, String near, int lineNumber) {
        System.err.println("Error on line " + lineNumber + ": " + errorType.name() + " near \"" + near + "\"");
    }

    private static String readFileContents(File file) {
        String contents = null;
        try {
            contents = Files.readString(Path.of(String.valueOf(file)));
        } catch (IOException e) {
            System.err.println("Failed to read source file " + file);
            System.exit(1);
        }
        return contents;
    }
}
