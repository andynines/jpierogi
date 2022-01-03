package com.andrewsenin.pierogi;

import com.andrewsenin.pierogi.datatypes.NativeData;
import com.andrewsenin.pierogi.interpreter.BuiltinsFrame;
import com.andrewsenin.pierogi.interpreter.Environment;
import com.andrewsenin.pierogi.interpreter.Frame;
import com.andrewsenin.pierogi.interpreter.Interpreter;
import com.andrewsenin.pierogi.io.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LanguageExecutable {

    private static final String EXECUTABLE_NAME = "pierogi";
    private static final String INPUT_PROMPT = EXECUTABLE_NAME + ">";

    private final IoManager ioManager;
    private final Interpreter interpreter;

    public static void main(String[] args) {
        if (args.length > 1) {
            System.err.println("Usage: " + EXECUTABLE_NAME + " [script]");
            System.exit(1);
        }
        IoManager ioManager = new ConsoleIoManager();
        Interpreter interpreter = new Interpreter(ioManager, new Environment(new Frame(new BuiltinsFrame())));
        LanguageExecutable languageExecutable = new LanguageExecutable(ioManager, interpreter);
        if (args.length == 1) {
            File sourceFile = new File(args[0]);
            assertFileExists(sourceFile);
            languageExecutable.interpretFile(sourceFile);
        } else {
            languageExecutable.enterReplSession();
        }
    }

    public LanguageExecutable(IoManager ioManager, Interpreter interpreter) {
        this.ioManager = ioManager;
        this.interpreter = interpreter;
    }

    private static void assertFileExists(File file) {
        if (!file.exists()) {
            System.err.println("File " + file.toString() + " does not exist");
            System.exit(1);
        }
    }

    private void interpretFile(File sourceFile) {
        try {
            String source = readFileContents(sourceFile);
            interpreter.interpret(source);
        } catch (UnwindingException ignored) {
        }
    }

    private void enterReplSession() {
        while (true) {
            ioManager.print(INPUT_PROMPT);
            String source = ioManager.requestInput();
            // TODO: handle ctrl+d
            // TODO: allow unmatched braces/parens
            try {
                List<NativeData> values = interpreter.interpret(source);
                values.forEach(value -> ioManager.print(value.makeValueRepresentation() + "\n"));
            } catch (UnwindingException ignored) {
            }
        }
    }

    private static String readFileContents(File file) {
        // TODO: this should be handled by the IoManager
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
