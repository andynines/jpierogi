package com.andrewsenin.pierogi.ast;

public abstract class LineNumbered {

    private final int lineNumber;

    public LineNumbered(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
