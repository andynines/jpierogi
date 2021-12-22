package com.andrewsenin.pierogi.datatypes;

public class NativeLambda implements NativeType {
    @Override
    public String makePrintRepresentation() {
        return "<lambda>";
    }
}
