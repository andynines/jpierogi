package com.andrewsenin.pierogi.ast;

public interface Expression {

    <T> T accept(AstVisitor<T> astVisitor);

    boolean equals(Expression expression);
}
