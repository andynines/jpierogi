package com.andrewsenin.pierogi.datatypes;

public class NativeNil implements NativeData {

    @Override
    public boolean equals(NativeData other) {
        return other instanceof NativeNil;
    }

    @Override
    public String makePrintRepresentation() {
        return "nil";
    }
}
