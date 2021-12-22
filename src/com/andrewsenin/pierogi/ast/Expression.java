package com.andrewsenin.pierogi.ast;

public abstract class Expression {

    private final int lineNumber;

    public Expression(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public abstract <T> T accept(AstVisitor<T> astVisitor);

    public abstract boolean equals(Expression expression);
}
