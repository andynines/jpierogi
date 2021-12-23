package com.andrewsenin.pierogi.datatypes;

public class NativeNil implements NativeType {

    @Override
    public boolean equals(NativeType other) {
        return other instanceof NativeNil;
    }

    @Override
    public String makePrintRepresentation() {
        return "nil";
    }
}
