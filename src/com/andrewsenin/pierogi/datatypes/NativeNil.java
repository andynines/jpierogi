package com.andrewsenin.pierogi.datatypes;

public class NativeNil implements NativeType {
    @Override
    public String makePrintRepresentation() {
        return "nil";
    }
}
